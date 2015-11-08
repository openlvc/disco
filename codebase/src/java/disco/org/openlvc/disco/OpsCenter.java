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
import org.openlvc.disco.provider.ProviderFactory;

public class OpsCenter
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private DiscoConfiguration configuration;
	private IProvider provider;
	private PduSource pduSource;
	private PduSink pduSink;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public OpsCenter()
	{
		this.configuration = new DiscoConfiguration();
		this.provider = null;
		this.pduSource = null;
		this.pduSink = null;
	}

	public OpsCenter( DiscoConfiguration configuration )
	{
		this();
		this.configuration = configuration;
		this.provider = ProviderFactory.getProvider( configuration.getProvider() );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	/////////////////////////////////////////////////////////////////////////
	/// Lifecycle Methods   /////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////
	public void open() throws DiscoException
	{
		// final checks
		if( this.provider == null )
		{
			this.provider = ProviderFactory.getProvider( configuration.getProvider() );
		}
		
		// set up any last minute bits and pieces
		this.pduSource = new PduSource( this.provider );
		this.pduSink = new PduSink( this.provider );
	}
	
	public void close() throws DiscoException
	{
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Accessor and Mutator Methods ///////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	public void setReceiver( IPduReceiver receiver )
	{
		this.pduSource.setReceiver( receiver );
	}

	public void send( PDU pdu ) throws DiscoException
	{
		this.pduSink.send( pdu );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
