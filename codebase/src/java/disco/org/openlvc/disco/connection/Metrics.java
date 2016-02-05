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
package org.openlvc.disco.connection;

/**
 * Generic object for recording baseline metrics in.
 */
public class Metrics
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private long pdusSent;
	private long pdusReceived;
	private long bytesSent;
	private long bytesReceived;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Metrics()
	{
		reset();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void pduSent( long bytes )
	{
		++pdusSent;
		bytesSent += bytes;
	}
	
	public void pduReceived( long bytes )
	{
		++pdusReceived;
		bytesReceived += bytes;
	}

	public void reset()
	{
		this.pdusSent = 0;
		this.pdusReceived = 0;
		this.bytesSent = 0;
		this.bytesReceived = 0;
	}
	
	public long getPdusSent()
	{
		return pdusSent;
	}

	public long getPdusReceived()
	{
		return pdusReceived;
	}

	public long getBytesSent()
	{
		return bytesSent;
	}

	public long getBytesReceived()
	{
		return bytesReceived;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
