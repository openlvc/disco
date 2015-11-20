/*
 *   Copyright 2015 Open LVC Project.
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
package org.openlvc.disco;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.warfare.DetonationPdu;

public class Test implements IPduReceiver
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
	private int espdu_count = 0;
	private int fire_count = 0;
	private int det_count = 0;
	private int transmitter_count = 0;
	private int receiver_count = 0;
	private int signal_count = 0;

	@Override
	public void receiver( PDU pdu )
	{

		switch( pdu.getType() )
		{
			case EntityState:
//				espdu_count++;
//				if( (espdu_count % 1000) == 0 )
//					System.out.println( "EntityState ("+espdu_count+")" );
				break;
			case Fire:
				fire_count++;
				System.out.println( "Fire ("+fire_count+")" );
				break;
			case Detonation:
				det_count++;
				System.out.println( "Detonation ("+det_count+")" );
				DetonationPdu det = (DetonationPdu)pdu;
				System.out.println( "x: "+det.getLocationInEntityCoordinates().getX()+
				                    ", y: "+det.getLocationInEntityCoordinates().getY()+
				                    ", z: "+det.getLocationInEntityCoordinates().getZ() );
				break;
			case Transmitter:
//				transmitter_count++;
//				if( (transmitter_count % 10) == 0 )
//					System.out.println( "Transmitter ("+transmitter_count+")" );
				break;
			case Receiver:
//				receiver_count++;
//				if( (receiver_count % 10) == 0 )
//					System.out.println( "Receiver ("+receiver_count+")" );
				break;
			case Signal:
//				signal_count++;
//				if( (signal_count % 10) == 0 )
//					System.out.println( "Signal ("+signal_count+")" );
				break;
			default:
				break;
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args ) throws Exception
	{
		DiscoConfiguration configuration = new DiscoConfiguration();
		//configuration.getNetworkConfiguration().setAddress( "239.1.2.3" );
		configuration.getNetworkConfiguration().setPort( 3000 );
		configuration.getNetworkConfiguration().setNetworkInterface( "LINK_LOCAL" );

		Test test = new Test();
		OpsCenter opscenter = new OpsCenter( configuration );
		opscenter.setReceiver( test );
		opscenter.open();
		//opscenter.close();
	}
}
