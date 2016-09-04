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
package org.openlvc.distributor.links.dis;

import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.LinkBase;
import org.openlvc.distributor.configuration.LinkConfiguration;

public class DisLink extends LinkBase implements ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private OpsCenter opsCenter;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DisLink( LinkConfiguration linkConfiguration )
	{
		super( linkConfiguration );
		
		// Loaded when link is brought up
		this.opsCenter = null;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void up()
	{
		if( isUp() )
			return;

		logger.debug( "Bringing up link: "+super.getName() );
		logger.debug( "Link Mode: DIS" );
		
		// Create the Disco configuration from our link configuration
		this.opsCenter = new OpsCenter( turnIntoDiscoConfiguration(linkConfiguration) );
		this.opsCenter.setListener( new PduListener() );
		this.opsCenter.open();

		logger.debug( "Link is up" );
		super.linkUp = true;
	}
	
	public void down()
	{
		if( isDown() )
			return;

		logger.debug( "Taking down link: "+super.getName() );
		
		this.opsCenter.close();
		logger.debug( "Link is down" );

		super.linkUp = false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getStatusSummary()
	{
		// if link has never been up, return configuration information
		if( opsCenter == null )
		{
			return String.format( "{ DIS, address:%s, port:%d, nic:%s }",
			                      linkConfiguration.getDisAddress(),
			                      linkConfiguration.getDisPort(),
			                      linkConfiguration.getDisNic() );
		}
		else
		{
			return opsCenter.getMetrics().getSummaryString();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private DiscoConfiguration turnIntoDiscoConfiguration( LinkConfiguration link )
	{
		DiscoConfiguration disco = new DiscoConfiguration();
		disco.getUdpConfiguration().setAddress( link.getDisAddress() );
		disco.getUdpConfiguration().setPort( link.getDisPort() );
		disco.getUdpConfiguration().setNetworkInterface( link.getDisNic() );

		disco.getDisConfiguration().setExerciseId( link.getDisExerciseId() );
		
		disco.getLoggingConfiguration().setLevel( link.getDisLogLevel() );
		disco.getLoggingConfiguration().setFile( link.getDisLogFile() );
		disco.getLoggingConfiguration().setFileOn( link.getDisLogToFile() );
		return disco;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
