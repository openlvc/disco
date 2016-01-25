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
package org.openlvc.disco.loadmaster.configuration;

import java.util.LinkedList;
import java.util.Queue;

public class Arguments
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final String ARG_LOG_LEVEL      = "--log-level";
	private static final String ARG_OBJECTS        = "--objects";
	private static final String ARG_TICK_INTERVAL  = "--tick";
	private static final String ARG_SIM_ADDRESS    = "--simulation-address";
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String loglevel;                    // optional
	private int objectCount;                    // optional
	private long tickInterval;                  // optional 1000ms
	private String simulationAddress;           // optional (1-65534)-(1-65535)-(1-65533) 

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Arguments( String[] args ) throws Exception
	{
		// Set defaults
		this.loglevel = "INFO";
		this.objectCount = 100;
		this.tickInterval = 1000;
		this.simulationAddress = "1-1-20913"; // exerciseId=1, siteId=1, appId=20913 (T[20] I[9] M[13])
		
		scan( args );
		validate();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	private void scan( String[] args ) throws Exception
	{
		// put everything into a queue so we can just drain it rather than tracking position
		Queue<String> queue = new LinkedList<>();
		for( String temp : args )
			queue.add( temp );
		
		// iterate through the queue and pull out the args
		while( queue.isEmpty() == false )
		{
			String argument = queue.remove();
			
			if( argument.equals(ARG_LOG_LEVEL) )
				setLogLevel( queue.remove() );
			else if( argument.equals(ARG_OBJECTS) )
				setObjectCount( queue.remove() );
			else if( argument.equals(ARG_TICK_INTERVAL) )
				setTickInterval( queue.remove() );
			else if( argument.equals(ARG_SIM_ADDRESS) )
				setSimulationAddress( queue.remove() );
			else
				throw new Exception( "Unknown argument: "+argument );
		}
	}

	/**
	 * Once we've collected all the input, check that there isn't anything wrong (like a
	 * permanent license with an expiry date).
	 */
	private void validate() throws Exception
	{
		// check the log level
		if( !loglevel.equals("OFF") &&
			!loglevel.equals("FATAL") &&
			!loglevel.equals("WARN") &&
			!loglevel.equals("INFO") &&
			!loglevel.equals("DEBUG") &&
			!loglevel.equals("TRACE") )
		{
			throw new Exception( "Unknown log level: "+loglevel );
		}
	}

	protected String getToNextArgument( Queue<String> queue )
	{
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		while( queue.peek() != null && !queue.peek().startsWith("--") )
		{
			if( first )
				first = false;
			else
				builder.append( " " );
			
			builder.append( queue.remove() );
		}
		
		return builder.toString();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getLogLevel()
	{
		return this.loglevel;
	}
	
	private void setLogLevel( String loglevel )
	{
		this.loglevel = loglevel; 
	}
	
	public int getObjectCount()
	{
		return this.objectCount;
	}
	
	private void setObjectCount( String objectCount )
	{
		this.objectCount = Integer.parseUnsignedInt( objectCount );
	}
	
	public long getTickInterval()
	{
		return this.tickInterval;
	}
	
	private void setTickInterval( String tickInterval )
	{
		this.tickInterval = Long.parseUnsignedLong( tickInterval );
	}

	public String getSimulationAddress()
	{
		return this.simulationAddress;
	}

	private void setSimulationAddress( String simulationAddress )
	{
		this.simulationAddress = simulationAddress;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "          Object Count: "+objectCount+"\n" );
		builder.append( "         Tick Interval: "+tickInterval+"\n" );
		builder.append( "    Simulation Address: "+simulationAddress+"\n" );
		builder.append( "             Log Level: "+loglevel+"\n" );
		return builder.toString();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void printHelp()
	{
		
		System.out.println( "Calytrix Cost Counter Licensing Application" );
		System.out.println( "" );

		String formatString = "%22s   %s";
		System.out.println( String.format(formatString,ARG_OBJECTS       ,"integer  (optional)  Number of objects to create              (default: 100)") );
		System.out.println( String.format(formatString,ARG_TICK_INTERVAL ,"string   (optional)  Millis between update tick cycle         (default: 1000)") );
		System.out.println( String.format(formatString,ARG_SIM_ADDRESS   ,"string   (optional)  Simulation Address                       (default: 1-1-20913)") );
		System.out.println( String.format(formatString,ARG_LOG_LEVEL     ,"string   (optional)  [OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE]  (default: INFO)") );
		System.out.println( "" );
	}

}
