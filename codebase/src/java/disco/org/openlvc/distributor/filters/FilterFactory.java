/*
 *   Copyright 2016 Open LVC Project.
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
package org.openlvc.distributor.filters;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Parses a given filter string and returns a {@link FilterGroup} that represents the appropriate
 * filter construction for the contains clauses.
 */
public class FilterFactory
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
	/// Filter Construction Methods   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * For the given ExpressionGroup, convert each of the expressions into Filters and
	 * FilterGroups. Return a single group that encapsulates the expression. 
	 */
	private FilterGroup convert( ExpressionGroup expressionGroup )
	{
		FilterGroup filterGroup = new FilterGroup( expressionGroup.type );
		for( Expression expression : expressionGroup.expressions )
		{
			// if this expression is itself a group, recurse down
			if( expression instanceof ExpressionGroup )
				filterGroup.add( convert((ExpressionGroup)expression) );
			else
				filterGroup.add( createFrom((ExpressionString)expression) );
		}

		return filterGroup;
	}

	/**
	 * Creates an instance of the filter in the expression adn returns it
	 */
	private IFilter createFrom( ExpressionString expression )
	{
		String string = expression.string;

		// Get the operand for the expression so that we can then find its
		// local and split the expression on it
		Operator operand = Operator.fromString( expression.string );
		int index = string.indexOf( operand.text );
		String field = string.substring( 0, index );
		String value = string.substring( index+2 );
		return FilterRegistry.create( field, operand, value );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Expression Parsing Methods   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Break down the given sequence of chars into an ExpressionGroup. Any nested expressions
	 * will be nested as groups inside the group.
	 */
	private ExpressionGroup parse( Queue<Character> filterString )
	{
		// create a group to put the expressions in
		ExpressionGroup containerGroup = null;
		ExpressionGroup currentGroup = new ExpressionGroup();
		
		StringBuilder builder = new StringBuilder();
		while( filterString.isEmpty() == false )
		{
			char character = filterString.remove();
			if( Character.isWhitespace(character) )
				continue;
			
			if( character == '(' )
			{
				ExpressionGroup child = parse( filterString );
				currentGroup.addGroup( child );
			}
			else if( character == ')' )
			{
				break;
			}
			else if( isOrString(character,filterString) )
			{
				// we've reached the end of the previous expression, store it
				if( builder.length() > 0 )
				{
					currentGroup.addExpression( builder.toString() );
					builder = new StringBuilder();
				}

				// if the current mode is null, store it as its the first one we've hit
				if( currentGroup.type == null )
					currentGroup.type = FilterGroup.Type.OR;
				
				// this is a new expression group because the type is differnet
				if( currentGroup.type == FilterGroup.Type.AND )
				{
					// current filter type is AND, but this is OR
					// we have added the expression to the group, let's create a new
					// group and move on
					if( containerGroup == null )
						containerGroup = new ExpressionGroup( FilterGroup.Type.OR );
					
					containerGroup.addGroup( currentGroup );
					currentGroup = new ExpressionGroup();
					currentGroup.type = FilterGroup.Type.OR;
				}
				
				// remove the consumed characters
				filterString.remove(); // 'r' or '|'
			}
			else if( isAndString(character,filterString) )
			{
				// we've reached the end of the previous expression, store it
				if( builder.length() > 0 )
				{
					currentGroup.addExpression( builder.toString() );
					builder = new StringBuilder();
				}

				// if the current mode is null, store it as its the first one we've hit
				if( currentGroup.type == null )
					currentGroup.type = FilterGroup.Type.AND;
				
				// this is a new expression group because the type is differnet
				if( currentGroup.type == FilterGroup.Type.OR )
				{
					// current filter type is AND, but this is OR
					// we have added the expression to the group, let's create a new
					// group and move on
					if( containerGroup == null )
						containerGroup = new ExpressionGroup( FilterGroup.Type.AND );
					
					containerGroup.addGroup( currentGroup );
					currentGroup = new ExpressionGroup();
					currentGroup.type = FilterGroup.Type.AND;
				}
				
				// remove the consumed characters
				filterString.remove();
				
				// remove the consumed characters
				filterString.remove();
				if( character != '&' )
					filterString.remove(); // if a-n-d we have to remove the d as well
			}
			else
			{
				builder.append( character );
			}

		}

		// add the last expression to the group
		String temp = builder.toString().trim();
		if( !temp.equals("") )
			currentGroup.addExpression( temp );
		
		// if this is a single-expression, we don't have any qualifier, in which case it is AND
		if( currentGroup.type == null )
			currentGroup.type = FilterGroup.Type.AND;
		
		// return the correct group (either the one we directly added, or the container if there
		// was more than one expression type to deal with
		if( containerGroup == null )
		{
			return currentGroup;
		}
		else
		{
			if( containerGroup.type ==  null )
				containerGroup.type = currentGroup.type;
			
			if( currentGroup.expressions.isEmpty() == false )
				containerGroup.addGroup( currentGroup );
		}
		
		return containerGroup;
	}

	/**
	 * There are two types of joining tests: AND and OR. Assess the given character (which has
	 * been removed from the sequence of characters we are looking at) and its subsequent
	 * characters to see if this is an "AND" clause.
	 * <p/>
	 * Valid clauses are: "and", "&&"
	 * 
	 * @param first The character under assessment that triggered this check and isn't in the
	 *              queue remaining to be processed.
	 * @param queue The rest of the characters
	 * @return <code>true</code> if this is an 'and' clause, <code>false</code> otherwise.
	 */
	private boolean isAndString( Character first, Queue<Character> queue )
	{
		if( Character.toLowerCase(first) == 'a' )
		{
			Character[] chars = queue.stream().limit(3).toArray( Character[]::new );
			return Character.toLowerCase(chars[0]) == 'n' &&
			       Character.toLowerCase(chars[1]) == 'd' &&
			       Character.toLowerCase(chars[1]) == ' ';
		}
		else
		{
			return first == '&' && queue.peek() == '&';
		}
	}

	/**
	 * There are two types of joining tests: AND and OR. Assess the given character (which has
	 * been removed from the sequence of characters we are looking at) and its subsequent
	 * characters to see if this is an "OR" clause.
	 * <p/>
	 * Valid clauses are: "or", "||"
	 * 
	 * @param first The character under assessment that triggered this check and isn't in the
	 *              queue remaining to be processed.
	 * @param queue The rest of the characters
	 * @return <code>true</code> if this is an 'or' clause, <code>false</code> otherwise.
	 */
	private boolean isOrString( Character first, Queue<Character> queue )
	{
		if( Character.toLowerCase(first) == 'o' )
		{
			Character[] chars = queue.stream().limit(2).toArray( Character[]::new );
			return Character.toLowerCase(chars[0]) == 'r' &&
			       Character.toLowerCase(chars[1]) == ' ';
		}
		else
		{
			return first == '|' && queue.peek() == '|'; 
		}
	}


	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Parses the given filter string, turning it into a {@link FilterGroup} representing the
	 * contained expressions.
	 * <p/>
	 * If the string contains nested expressions, the group will contain nested groups
	 * 
	 * @param filterString The filter text
	 * @return a {@link FilterGroup} representing the contained expression. If the string is
	 *         empty, an empty group will be returned.
	 */
	public static FilterGroup parse( String filterString )
	{
		// convert the filter string into something we can consume over multiple recursions
		Queue<Character> queue = new LinkedList<>();
		for( char character : filterString.toCharArray() )
			queue.add( character );

		// do the expression parsing
		FilterFactory constructor = new FilterFactory();
		ExpressionGroup expressionGroup = constructor.parse( queue );

		// turn the expression group into a filter group
		return constructor.convert( expressionGroup );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Private Inner Classes   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private interface Expression
	{}
	
	private class ExpressionString implements Expression
	{
		protected String string;
		protected ExpressionString( String expressionString )
		{
			this.string = expressionString;
		}
		
		@Override
		public String toString()
		{
			return string;
		}
	}
	
	private class ExpressionGroup implements Expression
	{
		protected FilterGroup.Type type;
		protected List<Expression> expressions;
		protected ExpressionGroup()
		{
			this.type = null;
			this.expressions = new ArrayList<>();
		}
		
		protected ExpressionGroup( FilterGroup.Type type )
		{
			this();
			this.type = type;
		}
		
		public void addExpression( String string )
		{
			this.expressions.add( new ExpressionString(string) );
		}
		
		public void addGroup( ExpressionGroup group )
		{
			this.expressions.add( group );
		}
		
		@Override
		public String toString()
		{
			return "("+this.type+"/"+this.expressions+")";
		}
	}

}
