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
package org.openlvc.disco.connection.rpr.model;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.openlvc.disco.DiscoException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.RTIexception;

/**
 * This class provides methods for dealing with an FOM module(s) in the IEEE-1516 (2010) format.
 * It includes methods to parse a module or set of modules, and methods to cache the handles from
 * a live federation. By capturing this here we can put all the code for the specific HLA interface
 * in a single spot.
 */
public class FomHelpers
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// FOM Module Parsing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Take the given URL that points to an XML FOM Module and parse it, finding and extracting
	 * the "objectModel" element that contains all the FOM declarations.
	 * 
	 * @param file The file to parse
	 * @return The objectModel element contained within it
	 * @throws ParserException Any XML parsing problem or if the objectModel element isn't the root
	 */
	private Element parseXmlModule( URL file ) throws ParserException
	{
		// 1. Parse the FOM module and return an XML document
		Document document = new ParserWrapper().parse( file );
		document.normalize();

		// 2. Get the <objectModel> element
		Element rootElement = document.getDocumentElement();
		String name = rootElement.getNodeName();
		if( name.equals("objectModel") == false )
			throw new ParserException( "XML Document Element was not <objectModel>: found "+name );
		
		return rootElement;
	}

	/**
	 * Parse the contents of the given module and return a new {@link ObjectModel} instance
	 * representing it. This will load into a new model and will not perform any merging.
	 * 
	 * @param fomModule The module to load
	 * @return An object model instance for the module
	 * @throws ParserException If there is an XML parsing problem, or problem with the data
	 */
	private ObjectModel parseModule( URL fomModule ) throws ParserException
	{
		// 1. Parse the FOM module
		Element rootElement = parseXmlModule( fomModule );
		
		// 2. Create the model we want to load in to
		ObjectModel model = new ObjectModel();
		
		// 3. Load the module into the model
		this.load( model, rootElement );
		
		// 4. Done!
		return model;
	}
	
	/**
	 * Parse the content of the given module and merge it with the existing object model instance.
	 * This will merge the content according to the 1516e merging rules.
	 * 
	 * @param fomModule The location of the FOM module to load
	 * @param model The model that we should merge with
	 * @throws ParserException If there is an XML parsing problem, problem with the data, or any
	 *                         merge rules violations
	 */
	private void parseModule( URL fomModule, ObjectModel model ) throws ParserException
	{
		if( model.isLocked() )
			throw new ParserException( "Cannot extend a locked Object Model" );
		
		// 1. Parse the FOM module
		Element rootElement = parseXmlModule( fomModule );
		
		// 2. Load the module into the existing model
		this.load( model, rootElement );
	}

	/**
	 * Loop through the given FOM module root element and extract all the FOM information,
	 * putting it into the object model. This will handle merging of module data into the
	 * existing {@link ObjectModel} based on FOM merging rules in the 1516e specification.
	 * 
	 * @param model The model we want to put the FOM data into
	 * @param rootElement The element we are going to extract the data from
	 * @throws ParserException If there is any reuqired information missing from the FOM
	 */
	private void load( ObjectModel model, Element rootElement ) throws ParserException
	{
		// NOTE: The <objects>, <interactions> and <dataTypes> elements are all OPTIONAL
		//       within any given module. 
		
		//
		// 1. Load the datatypes
		//
		Element datatypesElement = XmlHelpers.getFirstChildElement( rootElement, "dataTypes" );
		if( datatypesElement != null )
			parseDatatypes( model, datatypesElement );

		//
		// 2. Load the object classes
		//
		Element objectsElement = XmlHelpers.getFirstChildElement( rootElement, "objects" );
		if( objectsElement != null )
		{
			// Find HLAobjectRoot and start from there
			Element objectRoot = XmlHelpers.getFirstChildElement( objectsElement, "objectClass" );
			String name = XmlHelpers.getChildValue(objectRoot,"name");
			if( name.equals("HLAobjectRoot") == false )
				throw new ParserException( "Did not find HLAobjectRoot as first <objectClass>" );
			
			// load the children
			parseObjects( model, model.getObjectRoot(), objectRoot );
		}
		
		//
		// 3. Load the interaction classes
		//
		Element interactionsElement = XmlHelpers.getFirstChildElement( rootElement, "interactions" );
		if( interactionsElement != null )
		{
			// Find HLAinteractionRoot and start from there
			Element interactionRoot = XmlHelpers.getFirstChildElement( interactionsElement, "interactionClass" );
			String name = XmlHelpers.getChildValue( interactionRoot, "name" );
			if( name.equals("HLAinteractionRoot") == false )
				throw new ParserException( "Did not find HLAinteractionRoot as first <interactionClass>" );
			
			// load all the interactions
			parseInteractions( model, model.getInteractionRoot(), interactionRoot );
		}
	}

	////////////////////////////////////////////////////
	/// Datatype Parsing   /////////////////////////////
	////////////////////////////////////////////////////
	private void parseDatatypes( ObjectModel model, Element datatypesElement )
	{
		
	}

	////////////////////////////////////////////////////
	/// Object Class Parsing   /////////////////////////
	////////////////////////////////////////////////////
	private void parseObjects( ObjectModel model, ObjectClass parent, Element element )
	{
		// for each child <objectClass> element, parse into an ObjectClass
		List<Element> children = XmlHelpers.getAllChildElements( element, "objectClass" );
		for( Element ocElement : children )
		{
			// 1. Parse out the attributes that are children elements of <objectClass>.
			//    If this is a model merge, we need to know if there are any, because you
			//    can't add attributes to a class in an extension.
			List<Element> attributeList = XmlHelpers.getAllChildElements( ocElement, "attribute" );
			
			// 2. Check to see if the model already contains an ObjectClass with the same name.
			//    If not, then this is a new class and we need to create the representation for it.
			String className = XmlHelpers.getChildValue( ocElement, "name" );
			ObjectClass objectClass = parent.getChild( className );
			if( objectClass == null )
			{
				// Nothing exists yet, create it.
				objectClass = model.createObjectClass( className, parent );
			}
			else
			{
				// Class exists, check merge rules
				// It is OK, as long as they're not trying to extend with more attributes.
				if( attributeList.isEmpty() == false )
				{
					String msg = "Merge Failure: Cannot add attributes to existing class.";
					msg += " Tried to add ["+children.size()+"] attributes to class ["+
					       objectClass.getQualifiedName()+"]";
					throw new ParserException( msg );
				}
			}

			// 3. For each attribute, add to class (merge check that would limit was done above)
			for( Element attributeElement : attributeList )
				parseAttribute( model, objectClass, attributeElement );
			

			// 4. Recurse into the child classes
			parseObjects( model, objectClass, ocElement );
		}
	}
	
	private void parseAttribute( ObjectModel model, ObjectClass parent, Element element )
	{
		// Get the main values are are interested in
		String name = XmlHelpers.getChildValue( element, "name" );
		// FIXME String sharing = XmlHelpers.getChildValue( element, "sharing" );
		// FIXME String datatype = XmlHelpers.getChildValue( element, "datatype" );
		// FIXME String transport = XmlHelpers.getChildValue( element, "transportation" );
		// FIXME String order = XmlHelpers.getChildValue( element, "order" );
		
		// Create an attribute class to hold these values
		AttributeClass attribute = new AttributeClass( name );
		// FIXME attribute.setSharing( Sharing.valueOf(sharing) );
		// FIXME attribute.setTransport( Transport.valueOf(transport) );
		// FIXME attribute.setOrder( Order.valueOf(order) );
		// FIXME attribute.setDatatype()...
		parent.addAttribute( attribute );
	}
	
	
	////////////////////////////////////////////////////
	/// Interaction Class Parsing   ////////////////////
	////////////////////////////////////////////////////
	private void parseInteractions( ObjectModel model, InteractionClass parent, Element element )
	{
		// for each child <interactionClass> element, parse into an InteractionClass
		List<Element> children = XmlHelpers.getAllChildElements( element, "interactionClass" );
		for( Element icElement : children )
		{
			// Check to see if we already have a child class for the given name
			// If we do, we need to look at merging rules.
			String className = XmlHelpers.getChildValue( icElement, "name" );
			// FIXME String sharing = XmlHelpers.getChildValue( icElement, "sharing" );
			// FIXME String transport = XmlHelpers.getChildValue( icElement, "transportation" );
			// FIXME String order = XmlHelpers.getChildValue( icElement, "order" );
			InteractionClass interactionClass = parent.getChild( className );
			if( interactionClass != null )
			{
				// Check merge rules
			}
			else
			{
				// No existing class, create one
				interactionClass = model.createInteractionClass( className, parent );
			}
			
			// Get each declared parameter in the class
			List<Element> templist = XmlHelpers.getAllChildElements( icElement, "parameter" );
			for( Element pcElement : templist )
				parseParameter( model, interactionClass, pcElement );
			
			// Recurse into the children
			parseInteractions( model, interactionClass, icElement );
		}
	}
	
	private void parseParameter( ObjectModel model, InteractionClass parent, Element element )
	{
		String name = XmlHelpers.getChildValue( element, "name" );
		// FIXME String datatype = XmlHelpers.getChildValue( element, "datatype" );
		
		ParameterClass parameter = new ParameterClass( name );
		// FIXME parameter.setDatatype()...
		parent.addParameter( parameter );
	}

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Handle Caching Methods   ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Loop through each of classes contained in the given FOM and get the handles for it
	 * and its attribute/parameters, as well as all children.
	 * 
	 * @param model   The model to cache information up for
	 * @param rtiamb  The RTIambassador to fetch the data from
	 * @throws DiscoException If an RTI exception is thrown. It will be wrapped in a DiscoException
	 */
	private void loadHandles( RTIambassador rtiamb, ObjectModel model )
		throws DiscoException
	{
		try
		{
    		// Starting with ObjectRoot, cache up all the handlez
    		this.loadHandles( rtiamb, model.getObjectRoot() );
    		
    		// Repeat, with InteractionRoot
    		this.loadHandles( rtiamb, model.getInteractionRoot() );
		}
		catch( RTIexception rtie )
		{
			throw new DiscoException( "Error while caching handles: "+rtie.getMessage(), rtie );
		}
	}
	
	private void loadHandles( RTIambassador rtiamb, ObjectClass objectClass )
		throws RTIexception
	{
		// get the class handle
		ObjectClassHandle ocHandle = rtiamb.getObjectClassHandle( objectClass.getQualifiedName() );
		objectClass.setHandle( ocHandle );
		
		// get the attribute handles
		for( AttributeClass attribute : objectClass.getDeclaredAttributes() )
		{
			try
			{
				attribute.setHandle( rtiamb.getAttributeHandle(ocHandle,attribute.getName()) );
			}
			catch( NameNotFound e )
			{
				// if the rti is throwing an error about not finding the privilege to delete
				// attribute, it may be using a slightly different name for it
				if ( attribute.getName().equals("HLAprivilegeToDelete") )
				{
					attribute.setHandle( rtiamb.getAttributeHandle(ocHandle,"HLAprivilegeToDeleteObject") );
					break;
				}
				else
					throw e;
			}
		}
		
		// recurse for each child
		for( ObjectClass child : objectClass.getChildren() )
			loadHandles( rtiamb, child );
	}
	
	private void loadHandles( RTIambassador rtiamb, InteractionClass interactionClass )
		throws RTIexception
	{
		// get the class handle
		InteractionClassHandle icHandle = rtiamb.getInteractionClassHandle( interactionClass.getQualifiedName() );
		interactionClass.setHandle( icHandle );
		
		// get the parameter handles
		for( ParameterClass parameter : interactionClass.getDeclaredParameters() )
			parameter.setHandle( rtiamb.getParameterHandle(icHandle,parameter.getName()) );
		
		// recurse for each child
		for( InteractionClass child : interactionClass.getChildren() )
			loadHandles( rtiamb, child );
	}


	////////////////////////////////////////////////////////////////////////////////////////////
	/// PubSub Helper Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void pubsubObject( RTIambassador rtiamb,
	                           PubSub request,
	                           ObjectClass clazz,
	                           String... attributeList ) throws RTIexception, DiscoException
	{
		// Conduct baseline checks for: handle presence, attribute set
		if( clazz.getHandle() == null )
			throw new DiscoException( "Cannot publish object class [%s]: Has no handle", clazz.getLocalName() );
		
		// Build the set of attributes to complete the request on.
		// If the attribute name list was empty, assume it means EVERYTHING (declared & inherited)
		Collection<AttributeClass> attributes = clazz.getAllAttributes();
		if( attributeList != null && attributeList.length > 0 )
		{
			attributes.clear();
			for( String name : attributeList )
			{
				AttributeClass temp = clazz.getAttribute( name );
				if( temp == null )
				{
					throw new DiscoException( "Cannot pubsub attribute [%] in class [%s] - doesn't exist",
					                          name, clazz.getLocalName() );
				}
				else if( temp.getHandle() == null )
				{
					throw new DiscoException( "Cannot pubsub attribute [%] in class [%s] - no handle found",
					                          name, clazz.getLocalName() );
				}
			}
		}

		// Turn the attributes into a handle set
		AttributeHandleSet ahs = rtiamb.getAttributeHandleSetFactory().create();
		for( AttributeClass attribute : attributes )
		{
			if(attribute.getHandle() == null )
				continue;
			
			ahs.add( attribute.getHandle() );
		}

		//
		// Publish
		//
		if( request.isPublish() )
			rtiamb.publishObjectClassAttributes( clazz.getHandle(), ahs );
		
		//
		// Subscribe
		//
		if( request.isSubscribe() )
			rtiamb.subscribeObjectClassAttributes( clazz.getHandle(), ahs );
	}
	
	private void pubsubInteraction( RTIambassador rtiamb, PubSub request, InteractionClass clazz )
		throws RTIexception
	{
		// Conduct baseline checks for: handle presence, attribute set
		if( clazz.getHandle() == null )
			throw new DiscoException( "Cannot pubsub interaction class [%s]: Has no handle", clazz.getLocalName() );
		
		// Publish
		if( request.isPublish() )
			rtiamb.publishInteractionClass( clazz.getHandle() );
		
		// Subscribe
		if( request.isSubscribe() )
			rtiamb.subscribeInteractionClass( clazz.getHandle() );
	}

	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	// ***************************************************************************************
	// FOM Parsing Methods
	// ***************************************************************************************
	/**
	 * Parse the contents of the given 1516e formatted module and return a new {@link ObjectModel}
	 * instance representing it. This will load the single module into a fresh model instance.
	 * If you need to merge multiple modules, see {@link #parse(URL[])}.
	 * 
	 * @param fomModule The module, in 1516e XML format.
	 * @return An {@link ObjectModel} representing the module.
	 * @throws ParserException If there is a problem with the XML, or with missing/incorrect data
	 *                         inside it. 
	 */
	public static ObjectModel parse( URL fomModule ) throws ParserException
	{
		return new FomHelpers().parseModule( fomModule );
	}

	/**
	 * Parse the contents of the given 1516e formatted modules, and merge them into a new
	 * {@link ObjectModel} instance representing the set. This will merge according to the
	 * HLA specification rules for module merging.
	 * 
	 * @param fomModules The FOM modules, in 1516e XML format.
	 * @return An {@link ObjectModel} representing the module.
	 * @throws ParserException If there is a problem with the XML, or with missing/incorrect data
	 *                         inside it, or with merging the modules. 
	 */
	public static ObjectModel parse( URL[] fomModules ) throws ParserException
	{
		if( fomModules.length == 0 )
			throw new ParserException( "parse(URL[]): No modules provided to parse." );
		
		FomHelpers parser = new FomHelpers();
		ObjectModel model = parser.parseModule( fomModules[0] );
		for( int i = 1; i < fomModules.length; i++ )
			parser.parseModule( fomModules[i], model );
		
		return model;
	}
	
	/**
	 * Parse the contents of the identified FOM module into the given {@link ObjectModel}. 
	 * This will attempt to merge the module into the model following the 1516e merging rules.
	 * 
	 * @param fomModule The location of the FOM module to load
	 * @param model The model that we should extend
	 */
	public static void parse( URL fomModule, ObjectModel model ) throws ParserException
	{
		new FomHelpers().parseModule( fomModule, model );
	}

	// ***************************************************************************************
	// Handle Cachine Methods
	// ***************************************************************************************
	/**
	 * Loop through each of classes contained in the given FOM and get the handles for it
	 * and its attribute/parameters, as well as all children.
	 * 
	 * @param rtiamb  The RTIambassador to fetch the data from
	 * @param model   The model to cache information up for
	 * @throws DiscoException If an RTI exception is thrown. It will be wrapped in a DiscoException
	 */
	public static void loadHandlesFromRti( RTIambassador rtiamb, ObjectModel model )
	{
		new FomHelpers().loadHandles( rtiamb, model );
	}

//	/**
//	 * Loop through the given class and all its parent classes loading up any handles that are
//	 * not already loaded using the given RTIambassador.
//	 * 
//	 * @param rtiamb The RTIambassador to fetch the data from
//	 * @param clazz  The class start loading handles from, going from here through all parents
//	 */
//	public static void loadHandlesFromRti( RTIambassador rtiamb, ObjectClass clazz )
//	{
//		new FomHelpers().loadHandles( rtiamb, clazz );
//	}
//	
//	/**
//	 * Loop through the given class and all its parent classes loading up any handles that are
//	 * not already loaded using the given RTIambassador.
//	 * 
//	 * @param rtiamb The RTIambassador to fetch the data from
//	 * @param clazz  The class start loading handles from, going from here through all parents
//	 */
//	public static void loadHandlesFromRti( RTIambassador rtiamb, InteractionClass clazz )
//	{
//		new FomHelpers().loadHandles( rtiamb, clazz );
//	}


	// ***************************************************************************************
	// PubSub Helpers
	// ***************************************************************************************
	public static void pubsub( RTIambassador rtiamb, PubSub request, ObjectClass clazz, String... attributes )
		throws DiscoException
	{
		try
		{
			new FomHelpers().pubsubObject( rtiamb, request, clazz, attributes );
		}
		catch( DiscoException de )
		{
			throw de;
		}
		catch( Exception e )
		{
			throw new DiscoException( "Error during pubsub for object class [%s]: "+ e.getMessage(), e );
		}
	}
	
	public static void pubsub( RTIambassador rtiamb, PubSub request, InteractionClass clazz )
		throws DiscoException
	{
		try
		{
			new FomHelpers().pubsubInteraction( rtiamb, request, clazz );
		}
		catch( DiscoException de )
		{
			throw de;
		}
		catch( Exception e )
		{
			throw new DiscoException( "Error during pubsub for interaction class [%s]: "+ e.getMessage(), e );
		}		
	}

}
