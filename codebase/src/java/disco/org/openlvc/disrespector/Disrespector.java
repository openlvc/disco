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
package org.openlvc.disrespector;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.IPduListener;
import org.openlvc.disco.OpsCenter;
import org.openlvc.disco.UnsupportedException;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.PDU;

/**
 * Main container class for the Disrespector. Here we store two connections - one attached to a DIS
 * network and the other to a HLA/RPR network. We have listeners in place that simply exchange
 * the data received between each of them.
 */
public class Disrespector
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;

	// DIS Network
	private OpsCenter disCenter;
	
	// HLA Network
	private OpsCenter hlaCenter;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Disrespector()
	{
		this.configuration = new Configuration( new String[]{} );
		DiscoConfiguration.getVersion();
	}
	
	public Disrespector( Configuration configuration )
	{
		this.configuration = configuration;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void start()
	{
		this.disCenter = new OpsCenter( configuration.getDisConfiguration() );
		this.disCenter.setPduListener( new DisToHla() );
		
		this.hlaCenter = new OpsCenter( configuration.getHlaConfiguration() );
		this.hlaCenter.setPduListener( new HlaToDis() );
		
		// Start the connections
		this.hlaCenter.open();
		this.disCenter.open();
	}
	
	public void stop()
	{
		// Close the DIS side first
		try
		{
			this.disCenter.close();
		}
		catch( DiscoException de )
		{
			de.printStackTrace();
		}
		
		// Close the HLA side
		try
		{
			this.hlaCenter.close();
		}
		catch( DiscoException de )
		{
			de.printStackTrace();
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public Configuration getConfiguration()
	{
		return this.configuration;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private class DisToHla implements IPduListener
	{
		@Override
		public void receive( PDU pdu )
		{
			try
			{
				hlaCenter.send( pdu );
			}
			catch( UnsupportedException ue )
			{
				disCenter.getLogger().debug( " [UNSUPPORTED: "+ue.getMessage()+"]" );
			}
		}
	}
	
	private class HlaToDis implements IPduListener
	{
		@Override
		public void receive( PDU pdu )
		{
			try
			{
				disCenter.send( pdu );
			}
			catch( UnsupportedException ue )
			{
				hlaCenter.getLogger().debug( " [UNSUPPORTED: "+ue.getMessage()+"]" );
			}
		}
	} 

}
