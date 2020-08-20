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
package org.openlvc.distender;

public class Configuration
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
	public Configuration( String[] args )
	{
		
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void printHelp()
	{
		System.out.println( "Distender - Performance testing and benchmarking for Disco" );
		System.out.println( "Usage: bin/distender [--args]" );
		System.out.println( "" );
		System.out.println( "General Settings" );
		System.out.println( "    Mode options: network, serialization" );
		System.out.println( "  --mode                string   (required)  Define the type of test to run" );
		System.out.println( "  --log-level           string   (optional)  [OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE]    (default: INFO)" );
		System.out.println( "  --log-file            string   (optional)  Set location of the log file               (default: logs/distender.log)" );
		System.out.println( "" );
		System.out.println( "Disco Settings" );
		System.out.println( "    Sender/Receiver Options: simple, single-thread, thread-pool" );
		System.out.println( "  --pdu-sender          string   (optional)  Name of PDU Sender type                    (default: single-thread)" );
		System.out.println( "  --pdu-receiver        string   (optional)  Name of PDU Receiver type                  (default: single-thread)" );
		System.out.println( "" );
		System.out.println( "" );
		System.out.println( "DIS Settings" );
		System.out.println( "  --dis-address         string   (optional)  Where to send DIS traffic, or BROADCAST    (default: BROADCAST)" );
		System.out.println( "  --dis-port            integer  (optional)  Port for DIS traffic                       (default: 3000)" );
		System.out.println( "  --dis-interface       string   (optional)  NIC to use. Address or a special symbol:   (default: SITE_LOCAL)" );
		System.out.println( "                                             LOOPBACK, LINK_LOCAL, SITE_LOCAL, GLOBAL" );
		System.out.println( "" );
		System.out.println( "--------------------" );
		System.out.println( "     Test Modes     " );
		System.out.println( "--------------------" );
		System.out.println( "        network: Network send/receive test, counting through-put and queue backlog pressure" );
		System.out.println( "  serialization: Test serialization/deserialization operations per-second" );
		System.out.println( "" );
	}
}
