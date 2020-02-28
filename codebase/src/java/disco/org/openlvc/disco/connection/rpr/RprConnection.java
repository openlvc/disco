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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.RprConfiguration;
import org.openlvc.disco.connection.IConnection;
import org.openlvc.disco.connection.Metrics;
import org.openlvc.disco.connection.rpr.mappers.RprConverter;
import org.openlvc.disco.connection.rpr.model.FomHelpers;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.model.ObjectModel;
import org.openlvc.disco.connection.rpr.model.PubSub;
import org.openlvc.disco.pdu.PDU;

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
	protected Logger logger;
	private DiscoConfiguration discoConfiguration;
	private RprConfiguration rprConfiguration;

	// RTI Properties
	private RTIambassador rtiamb;
	private FederateAmbassador fedamb;

	// Internal Tracking
	protected ObjectModel objectModel;
	protected RprConverter rprConverter;
	
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
		this.fedamb = null;      // set in initializeFederation()
		
		// Internal Tracking
		this.objectModel = null;  // set in initializeFederation()
		this.rprConverter = null; // set in initializeFederation()
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

	/**
	 * Configure the provider as it is being deployed into the given {@link OpsCenter}.
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
		// Initalize and connect to the federation
		this.initializeFederation();
	}


	/**
	 * Close out the connection to this provider.
	 */
	@Override
	public void close() throws DiscoException
	{
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
		rprConverter.sendDisToHla( pdu, rtiamb );
	}

	/**
	 * Return the {@link Metrics} gathered for this data source.
	 */
	@Override
	public Metrics getMetrics()
	{
		return new Metrics();
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
	 *   <li>Publish and Subscribe to all relevant classes</li>
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
			this.rtiamb.connect( this.fedamb, CallbackModel.HLA_IMMEDIATE );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( "Error initializing HLA Connection: "+rtie.getMessage(), rtie );
		}
		
		//
		// Step 2. Parse the modules we are interested in
		//
		// We do this now to make sure everything is OK for our initialization before
		// we even worry about the RTI
		URL[] modules = getRprModules();
		if( modules.length == 0 )
			throw new DiscoException( "Did not find the RPR FOM Modules" );
		
		logger.debug( "Parsing FOM models "+Arrays.toString(modules) );
		this.objectModel = FomHelpers.parse( modules );

		
		//
		// Step 3. Create the Federation (if required)
		//
		String federation = rprConfiguration.getFederationName();
		if( this.rprConfiguration.isCreateFederation() )
		{
			try
			{
				logger.debug( "Creating Federation ["+federation+"]" );
				this.rtiamb.createFederationExecution( federation, modules );
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
		// Step 4. Join the Federation
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
				this.rtiamb.joinFederationExecution( federateName, federateType, federation, modules );
				
				// everything good! let's bust out
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
		// Step 5. Cache Handles
		//
		// Get all the handles for the classes/attributes we may need to know about
		logger.debug( "Loading handles for FOM" );
		FomHelpers.loadHandlesFromRti( this.rtiamb, this.objectModel );

		//
		// Step 6. Set up our RPR Conversion Engine.
		//         We must do this BEFORE pub/sub, otherwise we'll get discoveries for
		//         objects we don't know how to convert.
		//
		this.rprConverter = new RprConverter( objectModel, logger );

		//
		// Step 7. Publish and Subscribe
		//
		this.publishAndSubscribe();
	}

	private void publishAndSubscribe()
	{
		// PubSub Object Classes
		List<String> classes = new ArrayList<>();
		classes.add( "HLAobjectRoot.EmbeddedSystem.RadioTransmitter" );
		classes.add( "HLAobjectRoot.BaseEntity.PhysicalEntity" );
		for( String qualifiedName : classes )
		{
			logger.debug( "PubSub for "+qualifiedName );
			ObjectClass objectClass = objectModel.getObjectClass( qualifiedName );
			FomHelpers.pubsub( rtiamb, PubSub.Both, objectClass );
		}

		// PubSub Interaction Classes
		classes = Arrays.asList( "HLAinteractionRoot.RadioSignal.EncodedAudioRadioSignal" );
		for( String qualifiedName : classes )
		{
			logger.debug( "PubSub for "+qualifiedName );
			InteractionClass interactionClass = objectModel.getInteractionClass( qualifiedName );
			FomHelpers.pubsub( rtiamb, PubSub.Both, interactionClass );
		}
	}

	/**
	 * Resign and exit a federation cleanly
	 */
	private void cleanupFederation()
	{
		try
		{
    		// Resign from the federation
    		this.rtiamb.resignFederationExecution( ResignAction.DELETE_OBJECTS_THEN_DIVEST );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( "Error resigning from HLA federation: "+rtie.getMessage(), rtie );
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
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( "Error disconnecting from RTI: "+rtie.getMessage(), rtie );
		}
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	///  HLA -> DIS Receive Methods   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	protected void receiveHlaDiscover( ObjectInstanceHandle object, ObjectClassHandle clazz )
	{
		try
		{
			rprConverter.discoverHlaObject( object, clazz );
		}
		catch( Exception e )
		{
			logger.error( "(HLA->DIS) Error during object discover: "+e.getMessage(), e );
		}
	}
	
	protected void receiveHlaReflection( ObjectInstanceHandle objectHandle,
	                                     AttributeHandleValueMap attributes )
	{
		try
		{
			// convert the object to a PDU
			rprConverter.sendHlaToDis( objectHandle, attributes, opscenter );
		}
		catch( Exception e )
		{
			logger.error( "(HLA->DIS) Error during object reflection: "+e.getMessage(), e );
		}
	}

	protected void receiveHlaRemove( ObjectInstanceHandle objectHandle )
	{
		try
		{
			rprConverter.removeHlaObject( objectHandle );
		}
		catch( Exception e )
		{
			logger.error( "(HLA->DIS) Error during object removal: "+e.getMessage(), e );
		}
	}
	
	protected void receiveHlaInteraction( InteractionClassHandle classHandle,
	                                      ParameterHandleValueMap parameters )
	{
		try
		{
			// convert the interaction to a PDU
			rprConverter.sendHlaToDis( classHandle, parameters, opscenter );
		}
		catch( Exception e )
		{
			logger.error( "(HLA->DIS) Error during object reflection: "+e.getMessage(), e );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	private URL[] getRprModules()
	{
		// Get references to all the FOM modules we want to load
		ClassLoader loader = getClass().getClassLoader();
		return new URL[]
		{
		 	//loader.getResource( "hla/rpr2/RPR-DiscoDefaults.xml" ),
		 	loader.getResource( "hla/rpr2/HLAstandardMIM.xml" ),
			loader.getResource( "hla/rpr2/RPR-Foundation_v2.0_draft21.xml" ),
			loader.getResource( "hla/rpr2/RPR-Base_v2.0_draft21.xml" ),
			loader.getResource( "hla/rpr2/RPR-Communication_v2.0_draft21.xml" ),
			loader.getResource( "hla/rpr2/RPR-Physical_v2.0_draft21.xml" ),
		};
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
