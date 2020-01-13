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
package org.openlvc.disco.pdu.field;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.IPduComponent;

/**
 * This field shall indicate the spread spectrum technique or combination of spread spectrum techniques 
 * in use.
 * <p/> 
 * The Spread Spectrum field shall consist of a 16 element boolean array. Each independent type of spread 
 * spectrum technique shall be represented by a single element of this array. If a particular spread 
 * spectrum technique is in use, the corresponding array element shall be set to one, otherwise it shall 
 * be set to zero. All unused array elements shall be set to zero.
 * <p/>
 * The supported spread spectrum techniques and their assignment to elements of the 16 element array are 
 * defined in Section 9 of EBV-DOC.
 * 
 * @see "Section 9 in EBV-DOC"
 */
public class SpreadSpectrum  implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private boolean frequencyHopping;
	private boolean pseudoNoise;
	private boolean timeHopping;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SpreadSpectrum()
	{
		this( false, false, false );
	}
	
	public SpreadSpectrum( boolean frequencyHopping, boolean pseudoNoise, boolean timeHopping )
	{
		this.frequencyHopping = frequencyHopping;
		this.pseudoNoise = pseudoNoise;
		this.timeHopping = timeHopping;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public SpreadSpectrum clone()
	{
		return new SpreadSpectrum( this.frequencyHopping, this.pseudoNoise, this.timeHopping );
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		boolean equal = false;
		
		if( other instanceof SpreadSpectrum )
		{
			SpreadSpectrum asSpreadSpectrum = (SpreadSpectrum)other;
			equal = asSpreadSpectrum.frequencyHopping == this.frequencyHopping &&
			        asSpreadSpectrum.pseudoNoise == this.pseudoNoise &&
			        asSpreadSpectrum.timeHopping == this.timeHopping;
		}
		
		return equal;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Interface   //////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		int value = dis.readUI16();
		this.frequencyHopping = (value & 0x01) == 0 ? false : true;
		this.pseudoNoise = (value & 0x02) == 0 ? false : true;
		this.timeHopping = (value & 0x04) == 0 ? false : true;
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		int value = 0;
		if( this.frequencyHopping )
			value |= 0x01;
		if( this.pseudoNoise )
			value |= 0x02;
		if( this.timeHopping )
			value |= 0x04;

		dos.writeUI16( value );
	}

	@Override
	public int getByteLength()
	{
		return DisSizes.UI16_SIZE;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean isFrequencyHopping()
	{
		return this.frequencyHopping;
	}

	public void setFrequencyHopping( boolean frequencyHopping )
	{
		this.frequencyHopping = frequencyHopping;
	}

	public boolean isPseudoNoise()
	{
		return this.pseudoNoise;
	}

	public void setPseudoNoise( boolean pseudoNoise )
	{
		this.pseudoNoise = pseudoNoise;
	}

	public boolean isTimeHopping()
	{
		return this.timeHopping;
	}

	public void setTimeHopping( boolean timeHopping )
	{
		this.timeHopping = timeHopping;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
