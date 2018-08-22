/*
 *   Copyright 2018 Open LVC Project.
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

import java.util.ArrayList;
import java.util.List;

/**
 * A queue of strings that wraps around a list of commands or a command line. This allows
 * you to move forward and backward in the list and also handles things like arguments with
 * spaces (as long as they're wrapped in "").
 * <p/>
 * 
 * For example, given the following: <code>--help --marking "My Entity" --logfile "logfile.txt"</code>,
 * the {@link CommandList} will report five (5) arguments:
 * <ol>
 *   <li><code>--help</code></li>
 *   <li><code>--marking</code></li>
 *   <li><code>My Entity</code></li>
 *   <li><code>--logfile</code></li>
 *   <li><code>logfile.txt</code></li>
 * </ol>
 * 
 * Note that it will bridge across arguments that are quoted with double-quotes (not single).
 * It will also strip those quotes from the resulting arguments. This works regardless of whether
 * there is a space in the quoted argument or not.
 */
public class CommandList
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private List<String> arguments;
	private int index;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public CommandList( String[] commandline )
	{
		this.arguments = new ArrayList<>();
		this.index = 0;

		for( int i = 0; i < commandline.length; i++ )
		{
			String current = commandline[i];
			if( current.startsWith("\"") )
			{
				// if we end with a ", then the whole thing is encapsulated without spaces
				// and we can just treat it like one parameter
				if( current.endsWith("\"") )
				{
					arguments.add( current.replace("\"", "") ); // strip the "
					continue;
				}
				
				// there are multiple arguments we need to string together
				while( i+1 < commandline.length )
				{
					String next = commandline[++i];
					current += " ";
					current += next; 
					if( next.endsWith("\"") )
						break;
				}
				
				current = current.replace( "\"", "" );
				arguments.add( current );
			}
			else
			{
				arguments.add( current );
			}
		}
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * @return the next argument, but don't increment the index.
	 */
	public String peek()
	{
		return this.arguments.get( index );
	}

	/**
	 * Return the next argument in the list and increment, throwing an exception if we
	 * are past the end of the list.
	 * 
	 * @return The next argument.
	 */
	public String next() throws IndexOutOfBoundsException
	{
		return this.arguments.get( index++ );
	}

	/**
	 * Move the index back to its previous value. If this takes it past the 0, we pin
	 * to the start.
	 */
	public void stepBack()
	{
		index--;
		if( index < 0 )
			index = 0;
	}

	/**
	 * @return True if we are at the end of the list, false otherwise.
	 */
	public boolean hasMore()
	{
		return index+1 <= arguments.size();
	}
	
	public int size()
	{
		return this.arguments.size();
	}
	
	public String toString()
	{
		return arguments.toString();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
//	public static void main( String[] args )
//	{
//		String[] cl = new String[] { "--help", "--marking", "\"AVA", "VR\"", "--logfile", "\"log.log\"" };
//		CommandList list = new CommandList( cl );
//		System.out.println( "Count: "+list.size() );
//		while( list.hasMore() )
//			System.out.println( "  Arg: "+list.next() );
//	}
	
}
