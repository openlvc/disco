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
package org.openlvc.disco.pdu.record;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;
import org.openlvc.disco.pdu.field.EmitterSystemFunction;

/**
 * DIS 7 - 6.2.23: Emitter System Record
 */
public class EmitterSystemType implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int name;         // [UID 75] uint16
	private EmitterSystemFunction function;
	private short number;     // uint8

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EmitterSystemType()
	{
		this.name = 0;
		this.function = EmitterSystemFunction.Unknown;
		this.number = (short)0;
	}
	
	public EmitterSystemType( int name, short function, short number )
	{
		this.name = name;
		this.function = EmitterSystemFunction.fromValue( function );
		this.number = number;
	}

	public EmitterSystemType( int name, EmitterSystemFunction function, short number )
	{
		this.name = name;
		this.function = function;
		this.number = number;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public boolean equals( Object object )
	{
		if( this == object )
			return true;

		if( object instanceof EmitterSystemType )
		{
			EmitterSystemType other = (EmitterSystemType)object;
			if( other.name == this.name &&
				other.function == this.function &&
				other.number == this.number )
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public EmitterSystemType clone()
	{
		return new EmitterSystemType( name, function, number );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		name = dis.readUI16();
		function = EmitterSystemFunction.fromValue( dis.readUI8() );
		number = dis.readUI8();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI16( name );
		dos.writeUI8( function.value() );
		dos.writeUI8( number );
    }
	
	@Override
    public final int getByteLength()
	{
		return 4;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public String toString()
	{
		return String.format( "Function=%s, Number=%d, Name=%d", function.name(), number, name );
	}

	public int getName()
	{
		return name;
	}

	public void setName( int name )
	{
		this.name = name;
	}

	public EmitterSystemFunction getFunction()
	{
		return function;
	}

	public void setFunction( EmitterSystemFunction function )
	{
		this.function = function;
	}

	public short getNumber()
	{
		return number;
	}

	public void setNumber( short number )
	{
		this.number = number;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
