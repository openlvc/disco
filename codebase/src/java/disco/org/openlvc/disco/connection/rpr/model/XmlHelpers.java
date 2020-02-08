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

import java.util.ArrayList;
import java.util.List;

import org.openlvc.disco.DiscoException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlHelpers
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
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// XML Helper Methods ////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return a list of child element types of the provided node. If the node has no children
	 * or none of the children are element nodes, an empty list is returned.
	 */
	public static List<Element> getChildElements( Node node )
	{
		NodeList list = node.getChildNodes();
		ArrayList<Element> elements = new ArrayList<Element>();
		for( int i = 0; i < list.getLength(); i++ )
		{
			Node temp = list.item(i);
			if( temp.getNodeType() == Node.ELEMENT_NODE )
				elements.add( (Element)temp );
		}
		
		return elements;
	}

	/**
	 * Return the first child element under node that has given given tag name.
	 * If there is none that matches, return null.
	 */
	public static Element getFirstChildElement( Node node, String tag )
	{
		NodeList list = node.getChildNodes();
		for( int i = 0; i < list.getLength(); i++ )
		{
			Node temp = list.item(i);
			if( temp.getNodeType() != Node.ELEMENT_NODE )
				continue;
			
			Element tempElement = (Element)temp;
			if( tempElement.getTagName().equals(tag) )
				return tempElement;
		}
		
		return null;
	}

	/**
	 * Return a list of all the children elements of node with the given tag name.
	 * If there are no matching elements, an empty list is returned.
	 */
	public static List<Element> getAllChildElements( Node node, String tag )
	{
		NodeList list = node.getChildNodes();
		ArrayList<Element> elements = new ArrayList<Element>();
		for( int i = 0; i < list.getLength(); i++ )
		{
			Node temp = list.item(i);
			if( temp.getNodeType() != Node.ELEMENT_NODE )
				continue;
			
			Element tempElement = (Element)temp;
			if( tempElement.getTagName().equals(tag) )
				elements.add( tempElement );
		}
		
		return elements;
	}

	public static boolean isElement( Node node )
	{
		return node.getNodeType() == Node.ELEMENT_NODE;
	}

	/**
	 * This method searches the given element for the first child element with the identified
	 * tag-name. When it finds it, its text content is returned. If there is no child element
	 * with the given tag-name, an exception is thrown.
	 */
	public static String getChildValue( Element element, String name ) 
		throws DiscoException
	{
		return getChildValue( element, name, null );
	}

	/**
	 * To provide some better context to errors, this method takes an additional `typeName`
	 * parameter that will be used in any exception reporting. This allows pretty generic,
	 * difficult to find errors like "Element <interactionClass> missing child <order>" to
	 * become a bit more descriptive by including the name value of the interaction class
	 * (in this example).
	 * 
	 * @param element  Element to look for the child in
	 * @param name     Name of the child to look for and return its value
	 * @param typeName Name of the type we are looking in if known. `null` will cause the
	 *                 name to not be printed and is still valid.
	 * @return The value of the named sub-element inside the given element.
	 * @throws DiscoException if the value cannot be found
	 */
	public static String getChildValue( Element element, String name, String typeName )
		throws DiscoException
	{
		if( typeName == null )
			typeName = "unknown";

		String value = null;
		
		// check for the value of an attribute first
		if( element.hasAttribute(name) )
		{
			value = element.getAttribute( name );
		}
		else
		{
			// if no attribute is present, look for a child element
			Element child = getFirstChildElement( element, name );
			if( child == null )
			{
				String message =
					String.format( "Element <%s name=\"%s\"> missing child <%s> element",
				                   element.getTagName(), 
				                   typeName, 
				                   name );

				throw new DiscoException( message );
			}
			else
			{
				value = child.getTextContent().trim();
			}
		}

		if( value == null || value.isEmpty() )
		{
			String message =
				String.format( "Element <%s name=\"%s\"> empty child <%s> element",
			                   element.getTagName(), 
			                   typeName, 
			                   name );

			throw new DiscoException( message );
		}
		else
		{
			return value;
		}
	}

	/**
	 * Same as {@link #getChildValue(Element, String)} except that it returns null if there is
	 * no child rather than throwing an exception.
	 * 
	 * @see {@link #getChildValue(Element, String, String)}
	 */
	public static String getChildValueForgiving( Element element, String name, String typeName )
	{
		try
		{
			return getChildValue( element, name, typeName );
		}
		catch( DiscoException error )
		{
			// let's just ignore this
			return null;
		}
	}

	
	public static int getChildValueInt( Element element, String name, String typeName ) 
		throws DiscoException
	{
		if( typeName == null )
			typeName = "unknown";
		
		String asString = getChildValue( element, name, typeName );
		try
		{
			return Integer.parseInt( asString );
		}
		catch( Exception e )
		{
			String message = String.format( "Element <%s name=\"%s.%s\"> contains a non-numeric value [%s]", 
			                                element.getTagName(), 
			                                typeName,
			                                name,
			                                asString );
			throw new DiscoException( message );
		}
	}

}
