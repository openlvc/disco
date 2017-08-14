/*
 *   Copyright 2017 Open LVC Project.
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
import org.openlvc.disco.pdu.simman.CommentPdu;

/**
 * Class representing the FixedDatum that is used in a number of PDUs, including the
 * {@link CommentPdu}. Record contains a UINT32 for its ID and another UINT32 for its value.
 */
public class FixedDatum implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private int datumId;
	private int datumValue;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public FixedDatum()
	{
		this( 0, 0 );
	}

	public FixedDatum( int datumId, int datumValue )
	{
		this.datumId = datumId;
		this.datumValue = datumValue;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		if( other instanceof FixedDatum )
		{
			FixedDatum asFixedDatum = (FixedDatum)other;
			if( datumId == asFixedDatum.datumId &&
				datumValue == asFixedDatum.datumValue )
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public FixedDatum clone()
	{
		return new FixedDatum( datumId, datumValue );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
    public void from( DisInputStream dis ) throws IOException
    {
		this.datumId = (int)dis.readUI32();
		this.datumValue = (int)dis.readUI32();
    }

	@Override
    public void to( DisOutputStream dos ) throws IOException
    {
		dos.writeUI32( (long)this.datumId );
		dos.writeUI32( (long)this.datumValue );
    }
	
	@Override
    public final int getByteLength()
	{
		return 8; //DisSizes.UI32_SIZE * 2;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public int getDatumId()
	{
		return datumId;
	}

	public void setDatumId( int datumId )
	{
		this.datumId = datumId;
	}

	public int getDatumValue()
	{
		return datumValue;
	}

	public void setDatumValue( int datumValue )
	{
		this.datumValue = datumValue;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
