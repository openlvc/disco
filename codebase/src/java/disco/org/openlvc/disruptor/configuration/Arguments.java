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
package org.openlvc.disruptor.configuration;

import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

public class Arguments
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final String ARG_CONFIG_FILE    = "--config-file";
	private static final String ARG_LOG_LEVEL      = "--log-level";
	private static final String ARG_OBJECTS        = "--objects";
	private static final String ARG_TICK_INTERVAL  = "--tick-interval";
	private static final String ARG_LOOPS          = "--loops";
	private static final String ARG_SIM_ADDRESS    = "--simulation-address";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Properties properties;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Arguments( String[] args ) throws Exception
	{
		this.properties = new Properties();
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
			
			if( argument.equals(ARG_CONFIG_FILE) )
				properties.put( Configuration.KEY_CONFIG_FILE, queue.remove() );
			else if( argument.equals(ARG_LOG_LEVEL) )
				properties.put( Configuration.KEY_LOG_LEVEL, queue.remove() );
			else if( argument.equals(ARG_OBJECTS) )
				properties.put( Configuration.KEY_OBJECT_COUNT, queue.remove() );
			else if( argument.equals(ARG_TICK_INTERVAL) )
				properties.put( Configuration.KEY_TICK_INTERVAL, queue.remove() );
			else if( argument.equals(ARG_LOOPS) )
				properties.put( Configuration.KEY_LOOPS, queue.remove() );
			else if( argument.equals(ARG_SIM_ADDRESS) )
				properties.put( Configuration.KEY_SIMULATION_ADDRESS, queue.remove() );
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
		if( properties.contains(Configuration.KEY_LOG_LEVEL) )
		{
			String loglevel = properties.getProperty( Configuration.KEY_LOG_LEVEL );
			// check the log level
			if( !loglevel.equals( "OFF" ) &&
				!loglevel.equals( "FATAL" ) &&
			    !loglevel.equals( "WARN" ) &&
			    !loglevel.equals( "INFO" ) &&
			    !loglevel.equals( "DEBUG" ) &&
			    !loglevel.equals( "TRACE" ) )
			{
				throw new Exception( "Unknown log level: " + loglevel );
			}
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

	protected Properties getProperties()
	{
		return this.properties;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getConfigFile()
	{
		return properties.getProperty( Configuration.KEY_CONFIG_FILE );
	}
	
	public String getLogLevel()
	{
		return properties.getProperty( Configuration.KEY_LOG_LEVEL );
	}
	
	public String getObjectCount()
	{
		return properties.getProperty( Configuration.KEY_OBJECT_COUNT );
	}
	
	public String getTickInterval()
	{
		return properties.getProperty( Configuration.KEY_TICK_INTERVAL );
	}
	
	public String getLoops()
	{
		return properties.getProperty( Configuration.KEY_LOOPS );
	}
	
	public String getSimulationAddress()
	{
		return properties.getProperty( Configuration.KEY_SIMULATION_ADDRESS );
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "             Log Level: "+getLogLevel()+"\n" );
		builder.append( "                 Loops: "+getLoops()+"\n" );
		builder.append( "          Object Count: "+getObjectCount()+"\n" );
		builder.append( "         Tick Interval: "+getTickInterval()+"\n" );
		builder.append( "    Simulation Address: "+getSimulationAddress()+"\n" );
		return builder.toString();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void printHelp()
	{
		System.out.println( "Disruptor - Load testing tool for DIS applications" );
		System.out.println( "Usage: bin/disruptor [--args]" );
		System.out.println( "" );
		System.out.println( "" );

		String formatString = "%22s   %s";
		System.out.println( String.format(formatString,ARG_OBJECTS       ,"integer  (optional)  Number of objects to create              (default: 100)") );
		System.out.println( String.format(formatString,ARG_LOOPS         ,"integer  (optional)  Numbber of sim-loops to run              (default: 300)") );
		System.out.println( String.format(formatString,ARG_TICK_INTERVAL ,"integer  (optional)  Millis between update tick cycle         (default: 1000)") );
		System.out.println( String.format(formatString,ARG_SIM_ADDRESS   ,"string   (optional)  Simulation Address                       (default: 1-1-20913)") );
		System.out.println( String.format(formatString,ARG_LOG_LEVEL     ,"string   (optional)  [OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE]  (default: INFO)") );
		System.out.println( "" );
	}

}
