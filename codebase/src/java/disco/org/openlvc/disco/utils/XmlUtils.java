/*
 *   Copyright 2025 Open LVC Project.
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
package org.openlvc.disco.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.openlvc.disco.DiscoException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class with utility methods to help parse XML content.
 * 
 * The methods that take a {@link Node} can be used standalone. The methods that do not take
 * a {@link Node} use a cached {@link Document} inside the instance (this must be set during
 * construction or via {@link #setDocument(Document)} afterwards).
 * 
 * 
 * Element/Attribute Fallback Querying
 * ------------------------------------
 * The various `getText` methods employ a fallback to make finding simple text that are either
 * expressed in element tags, or attributes easier to query for.
 * 
 * They first query for an element match, and if found, the text between the element tags is
 * returned. If not, the query expression is modified so that final part queries for an attribute
 * and the query is retried.
 * 
 * For example:
 * - If `/path/to/match` matches to an element, the text content in that element is returned.
 * - If there is no match, the query expression is changed to `/path/to/@match` and retried. 
 * - If there is still no match, an empty string is returned.
 *
 * This allows us to match XML in two common XML idioms. First, where the text value is between
 * to element tags:
 * ```
 * <path>
 *     <to>
 *         <match>Some Text</match>
 *     </to>
 * </path>
 * ```
 * 
 * Secondly, where the value is an attribute on its parent element:
 * ```
 * <path>
 *     <to match="Some Text"/>
 * </path>
 * ```
 *
 */
public class XmlUtils
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private XPathFactory factory;
	private Element root;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public XmlUtils()
	{
		this.factory = XPathFactory.newDefaultInstance();
	}

	public XmlUtils( Element rootElement )
	{
		this();
		this.root = rootElement;
	}

	/**
	 * Create a new set of XmlUtils, providing the document that node-less queries will be
	 * issued against.
	 * 
	 * @param xmlDocument The document that node-less queries will be issued against
	 */
	public XmlUtils( Document xmlDocument )
	{
		this( xmlDocument.getDocumentElement() );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * There are a number of methods that do not take a context node to query against.
	 * These methods try to reference an internally cached element. This method will
	 * extract the document element from the given document and set it as the root node.
	 * 
	 * @param document The document to use for context-less query methods.
	 */
	public void setDocument( Document document )
	{
		this.root = document.getDocumentElement();
	}

	/**
	 * Sets the root node to use as context for methods that don't take a context node.
	 * 
	 * @param element The element to use as the root for all context-less method calls
	 */
	public void setRootNode( Element element )
	{
		this.root = element;
	}

	///////////////////////////////////////////////////////////////////
	/// String Fetching Methods ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	/**
	 * Evaluate the XPath expression against the given node and return the result as a string.
	 * No fallbacks are used. The experssion must match. An empty string is returned if there
	 * is no match.
 	 * 
	 * @param expression The expression to evaluate
	 * @param context    The root node to query against
	 * @return           The string contents of an expression match, or an empty string if no match
	 * @throws DiscoException If the expression fails during evaluation
	 */
	public String getStringSimple( String expression, Node node )
		throws DiscoException
	{
		return evaluateAsString( expression, node );
	}

	/**
	 * @see #getString(String, Node)
	 */
	public String getStringSimple( String expression ) throws DiscoException
	{
		return getStringSimple( expression, root );
	}

	/**
	 * Using the given path expression, try and match it to a string value. If there is no match,
	 * modify the expression so the last part of the path is an attribute match and try again.
	 * 
	 * Uses _Element/Attribute Fallback Querying_. See {@link XmlUtils} class-level comments. 
	 * 
	 * @param expression The expression to evaluate
	 * @param context    The root node to query against
	 * @return           The value of the element or attribute text, or an empty string for no match
	 * @throws DiscoException If the expression fails during evaluation
	 */
	public String getString( String expression, Node node )
		throws DiscoException
	{
		// Try to resolve the expression to see if we get a match. If we do, return it.
		// If we don't, and the expression is already for an attribute, there is nothing else
		// we can do, so return the original (empty) match anyway
		String firstValue = evaluateAsString( expression, node );
		if( firstValue.isBlank() == false || expression.contains("@") )
			return firstValue;

		// No match, BUT, the expression was not requesting that the last part of the
		// path be an attribute, so let's try that shall we?
		int lastSlash = expression.lastIndexOf( '/' );
		expression = expression.substring(0,lastSlash+1) + "@" + expression.substring(lastSlash+1);
		return evaluateAsString( expression, node );
	}

	/**
	 * @see #getString(String, Node)
	 */
	public String getString( String expression ) throws DiscoException
	{
		return getString( expression, root );
	}

	/**
	 * Same as {@link #getString(String, Node)} except that if the query does not yield a result
	 * an exception is thrown.
	 * 
	 * @param expression The expression to evaluate
	 * @param context    The root node to query against
	 * @return           The value of the element or attribute text
	 * @throws DiscoException If the expression fails or results in no value
	 */
	public String getStringStrict( String expression, Node node )
		throws DiscoException
	{
		String value = getString( expression, node );
		if( value.isBlank() )
			throw new DiscoException( "XML error: Missing required path [%s] on node [%s]",
			                             expression, node.getNodeName() );
		else
			return value;
	}

	/**
	 * @see #getStringStrict(String, Node)
	 */
	public String getStringStrict( String expression )
		throws DiscoException
	{
		return getStringStrict( expression, root );
	}

	/**
	 * Using the given path expression, try and match it to a string value. If there is no match,
	 * modify the expression so the last part of the path is an attribute match and try again.
	 * 
	 * - If a value is found, use it to invoke the given {@link Consumer}.
	 * - If no value is found, the function is not invoked and the method returns.
	 *
	 * Uses _Element/Attribute Fallback Querying_. See {@link XmlUtils} class-level comments. 
	 * 
	 * @param expression The expression to evaluate
	 * @param context    The root node to query against
	 * @param settable   The object invoked if a value is found
	 * @throws DiscoException If the expression fails during evaluation
	 */
	public void getStringThenCall( String expression, Node node, Consumer<String> settable )
		throws DiscoException
	{
		String value = getString( expression, node );
		if( value.isBlank() == false )
			settable.accept( value );
	}

	/**
	 * @see #getStringThenCall(String, Node, Consumer)
	 */
	public void getStringThenCall( String expression, Consumer<String> settable )
		throws DiscoException
	{
		getStringThenCall( expression, root, settable );
	}
	
	///////////////////////////////////////////////////////////////////
	/// Boolean Fetching Methods //////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	/**
	 * Evaluate the given expression using the given node as the context root. Try to convert
	 * the result to a boolean. An optional is returned so that the caller can identify if
	 * there was no boolean-convertable match available.
	 * 
	 * Return values in the `Optional` are:
	 * - True for any string matching `true`, `on` or `yes`
	 * - False for any string matching `false`, `off` or `no`
	 * - If there is no match to those values, the optional will be empty
	 * 
	 * @param expression The expression to evaluate
	 * @param context    The root node to query against
	 * @return           An optional with either true or false (if a matching string is found),
	 *                   or no value/empty for no query match (or no string match to true/false). 
	 * @throws DiscoException If the expression fails during evaluation
	 */
	public Optional<Boolean> getBooleanSimple( String expression, Node node )
		throws DiscoException
	{
		return evaluateAsBoolean( expression, node );
	}

	/**
	 * @see #getBooleanSimple(String, Node)
	 */
	public Optional<Boolean> getBooleanSimple( String expression ) throws DiscoException
	{
		return getBooleanSimple( expression, root );
	}

	/**
	 * Using the given path expression, try and match it to a boolean value. If there is no match,
	 * modify the expression so the last part of the path is an attribute match and try again.
	 * 
	 * Return values in the `Optional` are:
	 * - True for any string matching `true`, `on` or `yes`
	 * - False for any string matching `false`, `off` or `no`
	 * - If there is no match to those values, the optional will be empty
	 * 
	 * Uses _Element/Attribute Fallback Querying_. See {@link XmlUtils} class-level comments. 
	 * 
	 * @param expression The expression to evaluate
	 * @param context    The root node to query against
	 * @return           The boolean value of the element or attribute, or an empty optional
	 * @throws DiscoException If the expression fails during evaluation
	 */
	public Optional<Boolean> getBoolean( String expression, Node node )
		throws DiscoException
	{
		// Try to resolve the expression to see if we get a match. If we do, return it.
		// If we don't, and the expression is already for an attribute, there is nothing else
		// we can do, so return the original (empty) match anyway
		Optional<Boolean> firstValue = evaluateAsBoolean( expression, node );
		if( firstValue.isEmpty() == false || expression.contains("@") )
			return firstValue;

		// No match, BUT, the expression was not requesting that the last part of the
		// path be an attribute, so let's try that shall we?
		int lastSlash = expression.lastIndexOf( '/' );
		expression = expression.substring(0,lastSlash+1) + "@" + expression.substring(lastSlash+1);
		return evaluateAsBoolean( expression, node );
	}

	/**
	 * @see #getBoolean(String, Node)
	 */
	public Optional<Boolean> getBoolean( String expression )
		throws DiscoException
	{
		return getBoolean( expression, root );
	}
	
	/**
	 * Tries to find a boolean result of the expression with {@link #getBooleanSimple(String, Node)}
	 * 
	 * - If a value is found, use it to invoke the given {@link Consumer}
	 * - If no value is found, the function is not invoked and the method returns.
	 *
	 * Uses _Element/Attribute Fallback Querying_. See {@link XmlUtils} class-level comments. 
	 * 
	 * @param expression The expression to evaluate
	 * @param context    The root node to query against
	 * @param settable   The object invoked if a value is found
	 * @throws DiscoException If the expression fails during evaluation 
	 */
	public void getBooleanThenCall( String expression, Node node, Consumer<Boolean> consumer )
		throws DiscoException
	{
		Optional<Boolean> value = getBoolean( expression, node );
		if( value.isPresent() )
			consumer.accept( value.get() );
	}

	/**
	 * @see #getBooleanThenCall(String, Node, Consumer)
	 */
	public void getBooleanThenCall( String expression, Consumer<Boolean> settable )
		throws DiscoException
	{
		getBooleanThenCall( expression, root, settable );
	}

	///////////////////////////////////////////////////////////////////
	/// Element Fetching Methods //////////////////////////////////////
	///////////////////////////////////////////////////////////////////
	/**
	 * Evaluate the given expression and return the first {@link Element} that matches. Only the 
	 * first one found will be returned. If there are no matches, <code>null</code> will be
	 * returned.
	 * 
	 * @param expression The path expression to evaluate
	 * @param node       The node to treat as the root for expression evaluation
	 * @return           The first XML element found, or null if there is none
	 * @throws DiscoException If the expression cannot be evaluated
	 */
	public Element getElement( String expression, Node node )
		throws DiscoException
	{
		return evaluateAsElement( expression, node );
	}

	/**
	 * @see #getElement(String, Node)
	 */
	public Element getElement( String expression )
		throws DiscoException
	{
		return getElement( expression, root );
	}	
	
	/**
	 * Evaluate the given expression and return the first {@link Element} that matches. 
	 * If there is no match, then throw an exception.
	 * 
	 * @param expression The path expression to evaluate
	 * @param node       The node to treat as the root for expression evaluation
	 * @return           The first located matching element
	 * @throws DiscoException If there is no matching element for the expression
	 */
	public Element getElementStrict( String expression, Node node )
		throws DiscoException
	{
		Element element = evaluateAsElement( expression, node );
		if( element == null )
			throw new DiscoException( "XML error: Missing required path [%s] on node [%s]",
			                             expression, node.getNodeName() );
		else
			return element;
	}

	/**
	 * @see #getElementStrict(String, Node)}
	 */
	public Element getElementStrict( String expression )
		throws DiscoException
	{
		return getElementStrict( expression, root );
	}	

	///////////////////////////////////////////////////////////////////
	/// Element List Fetching Methods /////////////////////////////////
	///////////////////////////////////////////////////////////////////
	/**
	 * Evaluate the given expression and return the list of {@link Element} that match.
	 * If there is no match, then return an empty list. To make use of this effectively,
	 * ensure that your query is built to match multiple elements.
	 * 
	 * @param expression The path expression to evaluate
	 * @param node       The node to treat as the root for expression evaluation
	 * @return           The list of elements matched by the expression
	 * @throws DiscoException If the expression cannot be evaluated
	 */
	public List<Element> getElementList( String expression, Node node )
		throws DiscoException
	{
		return evaluateAsElementList( expression, node );
	}

	/**
	 * @see #getElementList(String, Node)}
	 */
	public List<Element> getElementList( String expression )
		throws DiscoException
	{
		return getElementList( expression, root );
	}
	
	public List<Element> getElementChildren( Node node )
		throws DiscoException
	{
		return getElementList( "*", node );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Utility Methods   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Evaluate the given expression using the given node as the context root. Assume the
	 * result can be evaluated to a string and return it. If the expression doesn't match
	 * anything, an empty string is returned.
	 * 
	 * @param expression The expression to evaluate
	 * @param context    The root node to query against
	 * @return           A string with the text result of the query, or an empty string for no match
	 * @throws DiscoException If the expression fails during evaluation or context is null
	 */
	private final String evaluateAsString( String expression, Node context )
	{
   		try
		{
   	   		XPath path = factory.newXPath();
			XPathExpression xpression = path.compile( expression );

			return xpression.evaluate( context );
		}
		catch( XPathExpressionException e )
		{
			throw new DiscoException( e, "XML Error: xpath expression [%s]: %s",
			                             expression, e.getMessage() );
		}
   	}

	/**
	 * Evaluate the given expression using the given node as the context root. Assume
	 * the result can be converted to a boolean. An optional is returned so that the
	 * caller can identify if there was no boolean-convertable result available.
	 * 
	 * True will be returned for any string of "true", "on" or "yes".
	 * False will be returned for any string of "false", "off" or "no".
	 * If there is no match to those values, the optional will be empty.
	 * 
	 * @param expression The expression to evaluate
	 * @param context    The root node to query against
	 * @return           An optional with either true or false (if a matching string is found),
	 *                   or no value/empty for no query match (or no string match to true/false). 
	 * @throws DiscoException If the expression fails during evaluation
	 */
	private final Optional<Boolean> evaluateAsBoolean( String expression, Node context )
		throws DiscoException
	{
		String value = evaluateAsString( expression, context );
		if( value == null || value.trim().isEmpty() )
			return Optional.empty();
		
		value = value.toLowerCase();
		if( value.equals("true") || value.equals("on") || value.equals("yes") )
			return Optional.of( true );
		else if ( value.equals("false") || value.equals("off") || value.equals("no") )
			return Optional.of( false );

		// Was some other rando value
		return Optional.empty();
	}

	/**
	 * Evaluate the given expression using the given node as the context root. Return the
	 * first matching Element node that is found.
	 * 
	 * @param expression The expression to evaluate
	 * @param context    The root node to query against
	 * @return           The first matching element node, or null if there is no match (or the
	 *                   match isn't an element).
	 * @throws DiscoException If the expression fails during evaluation
	 */
	private final Element evaluateAsElement( String expression, Node context )
		throws DiscoException
	{
   		try
		{
   	   		XPath path = factory.newXPath();
			XPathExpression xpression = path.compile( expression );
			
			return (Element)xpression.evaluate( context, XPathConstants.NODE );
		}
		catch( XPathExpressionException e )
		{
			throw new DiscoException( e, "XML Error: xpath expression [%s]: %s",
			                             expression, e.getMessage() );
		}			
	}

	/**
	 * Evaluate the given expression using the given node as the context root. Expect to find
	 * a LIST of nodes which are expected to be elements. Return this list of elements. If the
	 * expression doesn't evaluate to anything, an empty list is returned.
	 * 
	 * @param expression The XPath expression that should result in a list of elements
	 * @param context    The root node to query against
	 * @return           The list of elements matched by the expression, or an empty list
	 *                   if there are none.
	 * @throws DiscoException If the expression fails during evaluation
	 */
	private final List<Element> evaluateAsElementList( String expression, Node context )
		throws DiscoException
	{
		try
		{
			XPath path = factory.newXPath();
			XPathExpression xpression = path.compile( expression );
			
			List<Element> list = new ArrayList<>();
			NodeList nodes = (NodeList)xpression.evaluate( context, XPathConstants.NODESET );
			for( int i = 0; i < nodes.getLength(); i++ )
			{
				Node node = nodes.item(i);
				if( node.getNodeType() == Node.ELEMENT_NODE )
					list.add( (Element)node );
			}
			
			return list;
		}
		catch( XPathExpressionException e )
		{
			throw new DiscoException( e, "XML Error: xpath expression [%s]: %s",
			                             expression, e.getMessage() );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	//==========================================================================================
	// XML File Parsing Helpers   //////////////////////////////////////////////////////////////
	//==========================================================================================
	/**
	 * Parse and return an XML document from the given file.
	 * 
	 * @param file The file to read from
	 * @return An XML document that has been parsed from the file
	 * @throws DiscoException If there is a file reading error or parsing error
	 */
	public static Document parseXml( File file ) throws DiscoException
	{
		Document document = null;
		try( InputStream fileStream = FileUtils.openFile(file) )
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Parse the XML string into a Document object
			document = builder.parse( fileStream );
		}
		catch( DiscoException le )
		{
			throw le;
		}
		catch( Exception e )
		{
			throw new DiscoException( e, "Error while parsing xml file [%s]: %s",
			                             file.getAbsolutePath(), e.getMessage() );
		}

		return document;
	}

	/**
	 * Parse and return an XML document from the given URL. 
	 * 
	 * @param stream The stream to read the XML contents from
	 * @return An XML document that has been parsed from the stream contents
	 * @throws DiscoException If there is an excepption while parsing the stream
	 */
	public static Document parseXml( URL url ) throws DiscoException
	{
		Document document = null;
		try( InputStream fileStream = url.openStream() )
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Parse the XML string into a Document object
			document = builder.parse( fileStream );
		}
		catch( DiscoException le )
		{
			throw le;
		}
		catch( Exception e )
		{
			throw new DiscoException( e, "Error while parsing xml file [%s]: %s",
			                             url.toExternalForm(), e.getMessage() );
		}

		return document;
	}
	
	/**
	 * Parse and return an XML document from the given stream. Does not close the stream when
	 * finished parsing or during error.
	 * 
	 * @param stream The stream to read the XML contents from
	 * @return An XML document that has been parsed from the stream contents
	 * @throws DiscoException If there is an excepption while parsing the stream
	 */
	public static Document parseStream( InputStream stream ) throws DiscoException
	{
		try
		{
			Document document = null;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Parse the XML string into a Document object
			document = builder.parse( stream );
			return document;
		}
		catch( Exception e )
		{
			throw new DiscoException( e, "Error while parsing xml stream: %s", e.getMessage() );
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch( Exception e )
			{
				throw new DiscoException( e, "Error while parsing xml stream: %s", e.getMessage() );
			}
		}
	}

}
