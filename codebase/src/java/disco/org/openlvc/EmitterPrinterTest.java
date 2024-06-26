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

import java.util.Set;

import org.openlvc.disco.application.DisApplication;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.RprConfiguration.RtiProvider;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
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

	@EventHandler
	public void receive( PDU pdu )
	{
		System.out.println( "RECEIVED: "+pdu.getType() );
	}

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
		configuration.setHlaFederationName( "Test" );
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
		configuration.getRprConfiguration().setFederationName( "Test2" );
		configuration.getRprConfiguration().setRtiProvider( RtiProvider.Pitch );
		
		return configuration;
	}
	
	
	public static DiscoConfiguration getDisConfiguration()
	{
		DiscoConfiguration configuration = new DiscoConfiguration();
//		configuration.getUdpConfiguration().setAddress( "BROADCAST" );
		configuration.getUdpConfiguration().setAddress( "239.4.9.19" );
		configuration.getUdpConfiguration().setNetworkInterface( "SITE_LOCAL" );
		return configuration;
	}
	
	public static void main( String[] args ) throws Exception
	{
		DiscoConfiguration configuration = getDisConfiguration();
		configuration.getLoggingConfiguration().setLevel( "INFO" );
		DisApplication app = new DisApplication( configuration );
		app.addSubscriber( new EmitterPrinterTest() );
		app.start();
		
		// Every two seconds, send a message to all connected channels
		while( true )
		{
			Thread.sleep( 2000 );
			
			Set<TransmitterPdu> set = app.getPduStore().getTransmitterStore().getTransmittersUpdatedSince( 0L );
			System.out.println( "Found: "+set.size() );
			for( TransmitterPdu pdu : set )
			{
				System.out.println( "Transmitter: "+pdu.getEntityId() );
			}
			
		}
	}
}
