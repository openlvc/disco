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
package org.openlvc;

import java.sql.Timestamp;

import org.openlvc.disco.application.DisApplication;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.RprConfiguration.RtiProvider;
import org.openlvc.disco.connection.rpr.custom.dcss.mappers.IRCChannelMessageMapper;
import org.openlvc.disco.connection.rpr.custom.dcss.mappers.IRCRawMessageMapper;
import org.openlvc.disco.pdu.custom.IrcMessagePdu;
import org.openlvc.disco.pdu.custom.IrcUserPdu;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disrespector.Configuration;
import org.openlvc.disrespector.Disrespector;

public class EmitterPrinterTest
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

	public static void main2( String[] args ) throws Exception
	{
		Configuration configuration = new Configuration();
		configuration.setHlaFederateName( "Disrespector" );
		configuration.setHlaFederationName( "DCSS" );
		configuration.setHlaRtiProvider( RtiProvider.Pitch );
		configuration.setDisNic( "SITE_LOCAL" );
		configuration.setDisAddress( "BROADCAST" );
		configuration.setHlaLogLevel( "TRACE" );
		
		Disrespector disrespector = new Disrespector( configuration );
		disrespector.start();
		
		System.out.println( "Disrespector has been called" );
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public static DiscoConfiguration getRprConfiguration()
	{
		DiscoConfiguration configuration = new DiscoConfiguration();
		
		// RPR Connection Basics
		configuration.setConnection( "rpr" );
		configuration.getRprConfiguration().setFederateName( "Test" );
		configuration.getRprConfiguration().setFederationName( "DCSS2" );
		configuration.getRprConfiguration().setRtiProvider( RtiProvider.Pitch );
		
		// Extensions
		configuration.getRprConfiguration().registerExtensionModules( "hla/dcss/DCSS-BaseService.xml" );
		configuration.getRprConfiguration().registerExtensionModules( "hla/dcss/DCSS-IRC-Chat.xml" );
		configuration.getRprConfiguration().registerExtensionMappers( new IRCChannelMessageMapper() );
		configuration.getRprConfiguration().registerExtensionMappers( new IRCRawMessageMapper() );
		
		return configuration;
	}
	
	
	public static DiscoConfiguration getDisConfiguration()
	{
		DiscoConfiguration configuration = new DiscoConfiguration();
		configuration.getUdpConfiguration().setAddress( "BROADCAST" );
		configuration.getUdpConfiguration().setNetworkInterface( "SITE_LOCAL" );
		return configuration;
	}
	
	public static void main( String[] args ) throws Exception
	{
		DiscoConfiguration configuration = getDisConfiguration();
		configuration.getLoggingConfiguration().setLevel( "TRACE" );
		DisApplication app = new DisApplication( configuration );
		app.start();
		
		// Put an IrcUser out there
		EntityId oneId = new EntityId(1,1,1);
		IrcUserPdu one = new IrcUserPdu( oneId, "UserOne", "Local", "#excon" );
		EntityId twoId = new EntityId(2,2,2);
		IrcUserPdu two = new IrcUserPdu( twoId, "UserTwo", "Local", "#excon" );

		// Send the PDUs for the user
		app.send( one );
		app.send( two );
		
		// Register them for subsequent heartbeating
		app.getHeartbeater().registerPdu( one );
		app.getHeartbeater().registerPdu( two );
		
		// Every second, send a message, alternating sender between the two
		while( true )
		{
			Thread.sleep( 1000 );
			
			IrcMessagePdu pdu = new IrcMessagePdu();
			pdu.setRoomName( "#excon" );
			pdu.setSenderId( oneId );
			pdu.setSenderNick( "UserOne" );
			pdu.setMessage( "Message sent at "+new Timestamp(System.currentTimeMillis()).toString() );
			
			System.out.println( "Pump out message" );
			app.send( pdu );
		}
	}
}
