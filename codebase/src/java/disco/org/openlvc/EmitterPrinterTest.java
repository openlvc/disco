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

import org.openlvc.disco.application.DisApplication;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.RprConfiguration.RtiProvider;
import org.openlvc.disco.pdu.emissions.EmitterBeam;
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
		configuration.setDisNic( "LOOPBACK" );
		configuration.setDisAddress( "127.255.255.255" );
		
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
		configuration.getRprConfiguration().setFederateName( "Test" );
		configuration.getRprConfiguration().setFederationName( "DCSS2" );
		configuration.getRprConfiguration().setRtiProvider( RtiProvider.Pitch );
		configuration.setConnection( "rpr" );
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
		DiscoConfiguration configuration = getRprConfiguration();
		//configuration.getLoggingConfiguration().setLevel( "TRACE" );
		DisApplication app = new DisApplication( configuration );
		app.start();
		
		while( true )
		{
			Thread.sleep( 2000 );
			
			long time = System.currentTimeMillis();
			
			//
			// Transmitters
			//
//			Set<TransmitterPdu> transmitters = 
//				app.getPduStore().getTransmitterStore().getTransmittersMatching( (tx) -> tx != null );
//			System.out.println( "Transmitters ("+transmitters.size()+"):" );
//			for( TransmitterPdu pdu : transmitters )
//				System.out.println( time+" [Transmitter] "+pdu.getEntityId()+"-"+pdu.getRadioID() );
//			
//			System.out.println( "" );
			
			//
			// Entities
			//
			//System.out.println( time+" [Entities]: "+app.getPduStore().getEntityStore().getAllMarkings().size() );
			
			//
			// Emitters
			//
			System.out.println( "Beam Count: "+app.getPduStore().getEmitterStore().getActiveBeams().size() );
			for( EmitterBeam beam : app.getPduStore().getEmitterStore().getActiveBeams() )
			{
				boolean noParams = beam.getParameterData() == null;
				long seconds = (System.currentTimeMillis()-beam.getEmitterSystem().getLastUpdatedTime()) / 1000;
				System.out.println( time+" [Beam] "+noParams+": "+beam.toString()+" (Age: "+seconds+"s)" );
			}
			System.out.println( " == End of Record ==" );
		}
	}
}
