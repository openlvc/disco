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
package org.openlvc.disco.connection.rpr.objects;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.connection.rpr.types.array.RTIobjectIdArray;
import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;
import org.openlvc.disco.connection.rpr.types.enumerated.RPRboolean;
import org.openlvc.disco.pdu.PDU;

public class JammerBeam extends EmitterBeamRpr
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger32BE jammingModeSequence;
	private RPRboolean highDensityJam;
	private RTIobjectIdArray jammedObjectIdentifiers;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public JammerBeam()
	{
		super();

		this.jammingModeSequence = new RPRunsignedInteger32BE();
		this.highDensityJam = new RPRboolean();
		this.jammedObjectIdentifiers = new RTIobjectIdArray();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS Decoding Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void fromPdu( PDU incoming )
	{
		// Done in AbstractEmitterMapper
		throw new DiscoException( "EmitterBeams (Jammer) cannot be deserialized directly from PDUs" );
	}

	@Override
	public PDU toPdu()
	{
		// Done in AbstractEmitterMapper
		throw new DiscoException( "EmitterBeams (Jammer) cannot be serialized directly to PDUs" );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public RPRunsignedInteger32BE getJammingModeSequence()
	{
		return jammingModeSequence;
	}

	public RPRboolean getHighDensityJam()
	{
		return highDensityJam;
	}
	
	@Override
	public RPRboolean getHighDensityTrackJam()
	{
		return highDensityJam;
	}

	@Override
	public RTIobjectIdArray getTargets()
	{
		return this.jammedObjectIdentifiers;
	}

	public RTIobjectIdArray getJammedObjectIdentifiers()
	{
		return jammedObjectIdentifiers;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
