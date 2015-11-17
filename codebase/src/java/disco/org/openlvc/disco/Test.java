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
	@Override
	public void receiver( PDU pdu )
	{
		int espdu_count = 0;

		switch( pdu.getType() )
		{
			case EntityState:
				espdu_count++;
				if( espdu_count % 25 == 0 )
					System.out.println( "EntityState ("+espdu_count+")" );
				break;
			case Fire:
				System.out.println( "Fire!" );
				break;
			case Detonation:
				System.out.println( "Detonation!" );
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
		configuration.getNetworkConfiguration().setAddress( "239.1.2.3" );
		configuration.getNetworkConfiguration().setNetworkInterface( "LINK_LOCAL" );

		Test test = new Test();
		OpsCenter opscenter = new OpsCenter( configuration );
		opscenter.setReceiver( test );
		opscenter.open();
		//opscenter.close();
	}
}
