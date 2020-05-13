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
package org.openlvc.disco.connection.rpr.types.enumerated;

import org.openlvc.disco.connection.rpr.types.basic.RPRunsignedInteger32BE;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public enum ComplianceStateEnum32 implements ExtendedDataElement<ComplianceStateEnum32>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( new RPRunsignedInteger32BE(0) ),
	Detained( new RPRunsignedInteger32BE(1) ),
	Surrender( new RPRunsignedInteger32BE(2) ),
	UsingFists( new RPRunsignedInteger32BE(3) ),
	VerbalAbuse1( new RPRunsignedInteger32BE(4) ),
	VerbalAbuse2( new RPRunsignedInteger32BE(5) ),
	VerbalAbuse3( new RPRunsignedInteger32BE(6) ),
	PassiveResistance1( new RPRunsignedInteger32BE(7) ),
	PassiveResistance2( new RPRunsignedInteger32BE(8) ),
	PassiveResistance3( new RPRunsignedInteger32BE(9) ),
	NonLethalWeapon1( new RPRunsignedInteger32BE(10) ),
	NonLethalWeapon2( new RPRunsignedInteger32BE(11) ),
	NonLethalWeapon3( new RPRunsignedInteger32BE(12) ),
	NonLethalWeapon4( new RPRunsignedInteger32BE(13) ),
	NonLethalWeapon5( new RPRunsignedInteger32BE(14) ),
	NonLethalWeapon6( new RPRunsignedInteger32BE(15) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger32BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ComplianceStateEnum32( RPRunsignedInteger32BE value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public long getValue()
	{
		return this.value.getValue();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Data Element Methods   /////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getOctetBoundary()
	{
		return value.getOctetBoundary();
	}

	@Override
	public void encode( ByteWrapper byteWrapper ) throws EncoderException
	{
		value.encode( byteWrapper );
	}


	@Override
	public int getEncodedLength()
	{
		return value.getEncodedLength();
	}


	@Override
	public byte[] toByteArray() throws EncoderException
	{
		return value.toByteArray();
	}


	@Override
	public ComplianceStateEnum32 valueOf( ByteWrapper value ) throws DecoderException
	{
		RPRunsignedInteger32BE temp = new RPRunsignedInteger32BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public ComplianceStateEnum32 valueOf( byte[] value ) throws DecoderException
	{
		RPRunsignedInteger32BE temp = new RPRunsignedInteger32BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	public static ComplianceStateEnum32 valueOf( long value )
	{
		for( ComplianceStateEnum32 temp : ComplianceStateEnum32.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		// Don't be so strict
		// throw new UnsupportedException( "Unknown enumerator value: "+value+" (ComplianceStateEnum32)" );
		return Other;
	}
}
