/*
 *   Copyright 2020 Open LVC Project.
 *
 *   This file is part of Open LVC Disco.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.openlvc.disco.connection.rpr;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.bus.ErrorHandler;
import org.openlvc.disco.bus.MessageBus;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.RprConfiguration;
import org.openlvc.disco.configuration.RprConfiguration.RtiProvider;
import org.openlvc.disco.connection.IConnection;
import org.openlvc.disco.connection.Metrics;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.HlaDiscover;
import org.openlvc.disco.connection.rpr.mappers.HlaEvent;
import org.openlvc.disco.connection.rpr.mappers.HlaInteraction;
import org.openlvc.disco.connection.rpr.mappers.HlaReflect;
import org.openlvc.disco.connection.rpr.model.FomHelpers;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.model.ObjectModel;
import org.openlvc.disco.connection.rpr.objects.ObjectInstance;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.utils.ReflectionUtils;
import org.openlvc.disco.utils.FileUtils;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.FederateAlreadyExecutionMember;
import hla.rti1516e.exceptions.FederateNameAlreadyInUse;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.RTIexception;

/**
 * This class represents a connection to an HLA federation that will be used as the transport.
 * We convert all PDUs to their RPR FOM equivalents (objects or interactions) and then send them
 * off to the federation.
 * <p/>
 * 
 * This class supported the RPR FOM Version 2, and uses HLA Evolved (1516-2010).
 */
public class RprConnection implements IConnection
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private OpsCenter opscenter;
	private Logger logger;
	private DiscoConfiguration discoConfiguration;
	private RprConfiguration rprConfiguration;

	// RTI Properties
	private RTIambassador rtiamb;
	private boolean rtiambConnected;
	private FederateAmbassador fedamb;

	// Internal Data Storage and Message Passing
	private ObjectModel objectModel;
	private ObjectStore objectStore;
	private MessageBus<PDU> pduBus;
	private MessageBus<HlaEvent> hlaBus;

	// Local Services
	private RprHeartbeater pduHeartbeater;
	
	// Metrics
	private Metrics metrics;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public RprConnection()
	{
		this.opscenter = null;          // set in configure()
		this.logger = null;             // set in configure()
		this.discoConfiguration = null; // set in configure()
		this.rprConfiguration = null;   // set in configure()
		
		// RTI Properties
		this.rtiamb = null;      // set in initializeFederation()
		this.rtiambConnected = false;
		this.fedamb = null;      // set in initializeFederation()
		
		// Internal Data Storage and Message Passing
		this.objectModel = null;  // set in initializeFederation()
		this.objectStore = null;  // set in open()
		this.pduBus = null;       // set in open()
		this.hlaBus = null;       // set in open()

		// Local Services
		this.pduHeartbeater = null; // set in open()
		
		// Metrics
		this.metrics = null;      // set in open()
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IConnection Implementation Methods   ///////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String getName()
	{
		return "RPR FOM Connection";
	}

	@Override
	public Collection<PduType> getSupportedPduTypes()
	{
		Collection<PduType> collection = new LinkedHashSet<>();
		for( Class<? extends AbstractMapper> type : rprConfiguration.getRegisteredFomMappers() )
		{
			AbstractMapper temp = ReflectionUtils.newInstance( type );
			collection.addAll( temp.getSupportedPdus() );
		}

		return collection;
	}
	
	/**
	 * Configure the provider as it is being deployed into the given {@link OpsCenter}.
	 * Find the RPR FOM modules we need to use and make sure we can parse them.
	 */
	@Override
	public void configure( OpsCenter opscenter ) throws DiscoException
	{
		this.opscenter = opscenter;
		this.logger = opscenter.getLogger();
		this.discoConfiguration = opscenter.getConfiguration();
		this.rprConfiguration = this.discoConfiguration.getRprConfiguration();
	}

	
	/**
	 * Open a connection to this provider and start it receiving.
	 * 
	 * This method cannot block.
	 */
	@Override
	public void open() throws DiscoException
	{
		// Step 0. Re-initialize local stores and busses
		//
		this.objectStore = new ObjectStore();
		this.pduBus = new MessageBus<>();
		this.pduBus.setThrowExceptionOnError( true ); // we want this so we can do discard counting
		this.hlaBus = new MessageBus<>();
		this.hlaBus.subscribe( new HlaBusExceptionLogger() );
		this.pduHeartbeater = new RprHeartbeater( this );

		this.metrics = new Metrics();

		// Step 1. Locate and parse all necessary FOM Modules
		//
		// Find and parse all the FOM modules we'll need, building up a structure we
		// can query in the event handlers
		URL[] modules = rprConfiguration.getRegisteredFomModules();
		if( modules.length == 0 )
			throw new DiscoException( "Did not find the RPR FOM Modules" );
		
		logger.debug( "Parsing FOM modules "+Arrays.toString(modules) );
		this.objectModel = FomHelpers.parse( modules );

		try
		{
			// Step 2. Initialize Federation
			//
			// Initalize and connect to the federation
			this.initializeFederation();

			// Step 3. Initialize Mappers
			//
			// Set up the mappers. Must come before initializeFederation() because we do
			// pub/sub in there, which will cause us to start receiving HLA traffic
			for( Class<? extends AbstractMapper> mapperType : rprConfiguration.getRegisteredFomMappers() )
			{
				logger.trace( "Registering mapper: "+mapperType.getSimpleName() );

				// 1. Create an instance of the mapper
				AbstractMapper mapper = ReflectionUtils.newInstance( mapperType );

				// 2. Subscribe to the HLA bus
				//    The mapper should do HLA pubsub inside initialize(). As soon as the mapper
				//    does this the the fedamb will get discovery callbacks. If we're not on the
				//    HLA event bus at that time we'll miss them, so we need to be ready.
				//       -BUT-
				//    We can't be on the DIS PDU bus until AFTER we do HLA publish, so hold off on that
				hlaBus.subscribe( mapper );

				// 3. Initialize the mapper
				//    This will cause it to cache handles and do HLA publication and subscription.
				//    We should be ready to catch and start dealing with HLA-side data.
				mapper.initialize( this );

				// 4. Subscribe for incoming PDUs
				//    Now that the mapper should have done HLA publication, we can connect to the PDU
				//    bus without fear that we'll start trying to push HLA info before publication
				pduBus.subscribe( mapper );

				logger.debug( "Registered mapper: " + mapper.getClass().getCanonicalName() );
			}

//    		AbstractMapper[] mappers = rprConfiguration.getRegisteredFomMappers();
//    		for( AbstractMapper mapper : mappers )
//    		{
//    			logger.trace( "Registering mapper: "+mapper.getClass().getCanonicalName() );
//    
//    			// 1. Subscribe to the HLA bus
//    			//    The mapper should do HLA pubsub inside initialize(). As soon as the mapper
//    			//    does this the the fedamb will get discovery callbacks. If we're not on the
//    			//    HLA event bus at that time we'll miss them, so we need to be ready.
//    			//       -BUT-
//    			//    We can't be on the DIS PDU bus until AFTER we do HLA publish, so hold off on that
//    			hlaBus.subscribe( mapper );
//    			
//    			// 2. Initialize the mapper
//    			//    This will cause it to cache handles and do HLA publication and subscription.
//    			//    We should be ready to catch and start dealing with HLA-side data.
//    			mapper.initialize( this );
//    			
//    			// 3. Subscribe for incoming PDUs
//    			//    Now that the mapper should have done HLA publication, we can connect to the PDU
//    			//    bus without fear that we'll start trying to push HLA info before publication
//    			pduBus.subscribe( mapper );
//    			
//    			logger.debug( "Registered mapper: "+mapper.getClass().getCanonicalName() );
//    		}
		}
		catch( DiscoException de )
		{
			// If an error was encountered while setting up the mappers, then cleanup
			// the federation immediately
			this.cleanupFederation();
			throw de;
		}
		
		// Step 4. Start Local Services
		this.pduHeartbeater.start();
	}

	/**
	 * Resign from the federation and disconnect from the RTI
	 */
	@Override
	public void close() throws DiscoException
	{
		// Stop local services
		if( this.pduHeartbeater != null )
			this.pduHeartbeater.stop();
		
		// Clean up the federation
		if( this.rtiambConnected )
			this.cleanupFederation();
	}


	/**
	 * Send the given PDU bytes to the network.
	 */
	@Override
	public void send( byte[] pdubytes ) throws DiscoException
	{
		throw new DiscoException( "The method IConnection::send(byte[]) is not supported by the RprFomConnection" );
	}

	/**
	 * Send the given DIS PDU to the network.
	 */
	@Override
	public void send( PDU pdu ) throws DiscoException
	{
		try
		{
			pduBus.publish( pdu );
			metrics.pduSent( pdu.getPduLength() );
		}
		catch( Exception e )
		{
			logger.warn( "(RprConnection) Exception sending DIS >> HLA: "+e.getMessage(), e );
			metrics.pduDiscarded();
		}
	}

	/**
	 * Return the {@link Metrics} gathered for this data source.
	 */
	@Override
	public Metrics getMetrics()
	{
		return this.metrics;
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Lifecycle Management Methods   /////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This method will perform all the basic setup for the HLA connection.
	 * It will:
	 * <ol>
	 *   <li>Connect to the RTI</li>
	 *   <li>Create the federation (if configured to)</li>
	 *   <li>Join the federation</li>
	 *   <li>Parse all relevant FOM modules</li>
	 *   <li>Cache handles for all classes/attributes/parameters from the model</li>
	 * </ol>
	 */
	private void initializeFederation()
	{
		//
		// Step 1. Connect to the Federation
		//
		try
		{
			logger.debug( "Connecting to RTI" );
			this.rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
			this.fedamb = new FederateAmbassador( this );
			this.rtiamb.connect( this.fedamb,
			                     CallbackModel.HLA_IMMEDIATE,
			                     rprConfiguration.getLocalSettings() );
			this.rtiambConnected = true;
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( "Error initializing HLA Connection: "+rtie.getMessage(), rtie );
		}
		
		//
		// Step 2. Create the Federation (if required)
		//
		String federation = rprConfiguration.getFederationName();
		if( this.rprConfiguration.isCreateFederation() )
		{
			try
			{
				logger.debug( "Creating Federation ["+federation+"]" );
				this.rtiamb.createFederationExecution( federation, getRprCreateModules() );
			}
			catch( FederationExecutionAlreadyExists feae )
			{
				logger.debug( "Federation [%s] already exists, continuing...", federation );
			}
			catch( RTIexception rtie )
			{
				throw new DiscoException( "Error creating HLA Federation ["+federation+"]: "+
				                          rtie.getMessage(), rtie );
			}
		}
		
		//
		// Step 3. Join the Federation
		//
		// Note: By default, if we fail to join a federation because the name we want is taken,
		//       then we try to randomize the name we use and have another go. Well, we give it
		//       the good old college try and swing a few times, but that's the idea.
		//
		String federate = rprConfiguration.getFederateName();
		String federateSuffix = "";
		String federateType = rprConfiguration.getFederateType();
		int attempts = 5;
		while( (--attempts) >= 0 )
		{
			try
			{
				String federateName = federate+federateSuffix;
				logger.debug( "Joining Federation ["+federation+"] as ["+federateName+"]" );
				this.rtiamb.joinFederationExecution( federateName,
				                                     federateType,
				                                     federation,
				                                     rprConfiguration.getRegisteredFomModules() );
				
				// everything good! let's bust out
				rprConfiguration.setFederateName( federateName );
				break;
			}
			catch( FederateAlreadyExecutionMember | FederateNameAlreadyInUse e )
			{
				// not randomizing, so give up
				if( rprConfiguration.isRandomizeFedName() == false || attempts == 0 )
					throw new DiscoException( "Error joining HLA federation ["+federation+"]: "+e.getMessage(), e );
				
				// we are randomizing -- have another whack!
				federateSuffix = ""+new Random().nextInt( 99999 );
				continue;
			}
			catch( RTIexception rtie )
			{
				throw new DiscoException( "Error joining HLA Federation ["+federation+"]: "+
				                          rtie.getMessage(), rtie );
			}
		}
		
		//
		// Step 4. Cache Handles
		//
		// Get all the handles for the classes/attributes we may need to know about
		logger.debug( "Loading handles for FOM" );
		FomHelpers.loadHandlesFromRti( this.rtiamb, this.objectModel );

		//
		// Step 5. Any special post-join things we should do
		//
		try
		{
			this.rtiamb.enableCallbacks();
			this.rtiamb.enableAsynchronousDelivery();
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( "Error enabling callbacks/async delivery",
			                          rtie.getMessage(), rtie );
		}
		
		//
		// Step 5. Publish and Subscribe -- this will start data flowing from the HLA
		//
		// ===> This has now been moved to Mappers; be sure to check them <===
		//this.publishAndSubscribe();
	}

	/**
	 * Resign and exit a federation cleanly
	 */
	private void cleanupFederation()
	{
		if( this.rtiamb == null )
		{
			return;
		}
		
		try
		{
			// Resign from the federation
			this.rtiamb.resignFederationExecution( ResignAction.DELETE_OBJECTS_THEN_DIVEST );
		}
		catch( RTIexception rtie )
		{
			logger.warn( "Error while resigning from HLA federatoin: "+rtie.getMessage() );
		}
		
		// Delete the federation, to be a good citizen. Will get told off if people are
		// still using it, so expect that.
		try
		{
			this.rtiamb.destroyFederationExecution( rprConfiguration.getFederationName() );
		}
		catch( RTIexception rtie )
		{
			// no-op
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		// Disconnect from the RTI and then we are allll cleaned up
		try
		{
			this.rtiamb.disconnect();
			this.rtiambConnected = false;
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( "Error disconnecting from RTI: "+rtie.getMessage(), rtie );
		}
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	///  HLA -> DIS Receive Methods   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	//
	// DISCOVER OBJECT
	//
	protected void receiveHlaDiscover( ObjectInstanceHandle theObject,
	                                   ObjectClassHandle theClass,
	                                   String objectName )
	{
		logger.debug( "[hla>>dis] (Discover) object=%s, class=%s", theObject, theClass );

		// Find the metadata information we have for the class
		ObjectClass objectClass = objectModel.getObjectClass( theClass );
		if( objectClass == null )
			logger.error( "[hla>>dis] (Discover) Received discover for unknown class: handle="+theClass );

		// Publish to the HLA bus so that someone picks this up and creates the local object
		hlaBus.publish( new HlaDiscover(theObject,objectClass,objectName) );
	}
	
	//
	// REFLECT ATTRIBUTE VALUES
	//
	protected void receiveHlaReflection( ObjectInstanceHandle objectHandle,
	                                     AttributeHandleValueMap attributes )
	{
		logger.trace( "[hla>>dis] (Reflect) object=%s, attribute=%d", objectHandle, attributes.size() );

		// Find the local object representation for this object handle
		ObjectInstance hlaObject = objectStore.getDiscoveredHlaObject( objectHandle );
		if( hlaObject == null )
		{
			logger.warn( "[hla>>dis] (Reflect) Reflection for object prior to discovery: "+objectHandle );
			return;
		}
		
		// Publish a reflection event to the bus
		hlaObject.setLastUpdatedTimeToNow();
		hlaBus.publish( new HlaReflect(hlaObject,attributes) );

		// Track metrics
		int size = attributes.values().stream().mapToInt(v -> v.length).sum();
		metrics.pduReceived(size);
	}

	//
	// DELETE OBJECT
	//
	protected void receiveHlaRemove( ObjectInstanceHandle objectHandle )
	{
		ObjectInstance hlaObject = objectStore.removeDiscoveredHlaObject( objectHandle );

		if( logger.isDebugEnabled() )
		{
			logger.debug( "[hla>>dis] (Remove) Removed object: handle=%s, name=%s",
			              objectHandle.toString(),
			              hlaObject == null ? "null" : hlaObject.getObjectName() );
		}
	}
	
	//
	// RECEIVE INTERACTION
	//
	protected void receiveHlaInteraction( InteractionClassHandle classHandle,
	                                      ParameterHandleValueMap parameters )
	{
		logger.trace( "[hla>>dis] (Interaction) class=%s, parameters=%d", classHandle, parameters.size() );

		// Find the class of interaction that this is
		InteractionClass theClass = objectModel.getInteractionClass( classHandle );
		if( theClass == null )
		{
			logger.warn( "[hla>>dis] (Interaction) Received for unknown class: "+classHandle );
			return;
		}

		// Publish an interaction event to the bus
		hlaBus.publish( new HlaInteraction(theClass,parameters) );
		
		// Track metrics
		int size = parameters.values().stream().mapToInt(v -> v.length).sum();
		metrics.pduReceived(size);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public RprConfiguration getRprConfiguration()
	{
		return this.rprConfiguration;
	}

	public Logger getLogger()
	{
		return this.logger;
	}

	public ObjectModel getFom()
	{
		return this.objectModel;
	}
	
	public ObjectStore getObjectStore()
	{
		return this.objectStore;
	}
	
	public RTIambassador getRtiAmb()
	{
		return this.rtiamb;
	}
	
	public OpsCenter getOpsCenter()
	{
		return this.opscenter;
	}

	private URL[] getRprCreateModules()
	{
		// When creating a federation we have to provide some switch values.
		// Let's take the join modules array and add the switches to the front.
		
		// Get the join modules
		URL[] joinModules = rprConfiguration.getRegisteredFomModules();

		// Get the create-only modules
		ClassLoader loader = getClass().getClassLoader();
		String fomPath = "hla/rpr2/RPR-Switches_v2.0.xml";
		URL url = loader.getResource( fomPath );
		
		// using mak rti, so can't load modules from jar
		if( rprConfiguration.getRtiProvider() == RtiProvider.Mak )
		{
			List<URL> urls = FileUtils.extractFilesFromJar( Arrays.asList(fomPath), new File("hla/rpr2"), loader );
			url = urls.get(0);
		}
		
		if( url == null )
			throw new DiscoException( "Could not find FOM module: "+url );
		
		URL[] createModules = new URL[] { url };
		
		// Slam the arrays together and return
		return Stream.concat( Arrays.stream(createModules), Arrays.stream(joinModules) )
		//return Stream.concat( Arrays.stream(joinModules), Arrays.stream(createModules) )
		             .toArray( URL[]::new );
	}
		

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Inner Class: HlaBusExceptionLogger   ///////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Class for capturing and logging exceptions in the processing of HLA bus handlers
	public class HlaBusExceptionLogger
	{
		@ErrorHandler
		public void hlaBusError( Throwable throwable, Object target )
		{
			logger.warn( "hla >> dis (Exception) "+throwable.getMessage(), throwable );
		}
	}

}
