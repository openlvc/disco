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

public enum DamageStatusEnum32 implements ExtendedDataElement<DamageStatusEnum32>
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	NoDamage( new RPRunsignedInteger32BE(0) ),
	SlightDamage( new RPRunsignedInteger32BE(1) ),
	ModerateDamage( new RPRunsignedInteger32BE(2) ),
	Destroyed( new RPRunsignedInteger32BE(3) );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RPRunsignedInteger32BE value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private DamageStatusEnum32( RPRunsignedInteger32BE value )
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
	public DamageStatusEnum32 valueOf( ByteWrapper value ) throws DecoderException
	{
		RPRunsignedInteger32BE temp = new RPRunsignedInteger32BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}

	@Override
	public DamageStatusEnum32 valueOf( byte[] value ) throws DecoderException
	{
		RPRunsignedInteger32BE temp = new RPRunsignedInteger32BE();
		temp.decode( value );
		return valueOf( temp.getValue() );
	}


	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	public static DamageStatusEnum32 valueOf( long value )
	{
		for( DamageStatusEnum32 temp : DamageStatusEnum32.values() )
			if( temp.value.getValue() == value )
				return temp;
		
		throw new IllegalArgumentException( "Unknown enumerator value: "+value+" (DamageStatusEnum32)" );
	}
}
