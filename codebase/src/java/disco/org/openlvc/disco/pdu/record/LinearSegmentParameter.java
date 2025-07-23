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
package org.openlvc.disco.pdu.record;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;
import org.openlvc.disco.pdu.field.appearance.GeneralObjectApperance;

/**
 * An abstract Vector record with 3 dimensions
 */
public class LinearSegmentParameter implements IPduComponent
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short number;
	private byte modifications;
	private GeneralObjectApperance generalApperance;
	private byte[] specificApperance; // Spec just says 32-bit record, no impl detail given
	private WorldCoordinate location;
	private EulerAngles orientation;
	private int length;               // Note: These are UI16 in v6, but 32F in v7
	private int width;
	private int height;
	private int depth;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public LinearSegmentParameter()
	{
		this.number = 0;
		this.modifications = 0;
		this.generalApperance = new GeneralObjectApperance();
		this.specificApperance = new byte[4];
		this.location = new WorldCoordinate();
		this.orientation = new EulerAngles();
		this.length = 0;
		this.width = 0;
		this.height = 0;
		this.depth = 0;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( this == other )
			return true;
		
		if( other instanceof LinearSegmentParameter )
		{
			LinearSegmentParameter otherParam = (LinearSegmentParameter)other;
			return this.number == otherParam.number &&
			       this.modifications == otherParam.modifications &&
			       Objects.equals( this.generalApperance, otherParam.generalApperance ) &&
			       Arrays.equals( this.specificApperance, otherParam.specificApperance ) &&
			       Objects.equals( this.location, otherParam.location ) &&
			       Objects.equals( this.orientation, otherParam.orientation ) &&
			       this.length == otherParam.length &&
			       this.width ==  otherParam.width &&
			       this.height == otherParam.height &&
			       this.depth == otherParam.depth;
		}
		
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash( this.number,
		                     this.modifications,
		                     this.generalApperance,
		                     this.specificApperance,
		                     this.location,
		                     this.orientation,
		                     this.length,
		                     this.width,
		                     this.height,
		                     this.depth );
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.number = dis.readUI8();
		this.modifications = dis.readByte();
		this.generalApperance.setBits( dis.readUI16() );
		dis.readFully( this.specificApperance );
		this.location.from( dis );
		this.orientation.from( dis );
		this.length = dis.readUI16();
		this.width = dis.readUI16();
		this.height = dis.readUI16();
		this.depth = dis.readUI16();
		
		// Padding (32-bits)
		dis.skip32();
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeUI8( this.number );
		dos.writeByte( this.modifications );
		dos.writeUI16( this.generalApperance.getBits() );
		dos.write( this.specificApperance );
		this.location.to( dos );
		this.orientation.to( dos );
		dos.writeUI16( this.length );
		dos.writeUI16( this.width );
		dos.writeUI16( this.height );
		dos.writeUI16( this.depth );
		
		// Padding (32-bits)
		dos.writePadding32();
	}
	
	@Override
	public final int getByteLength()
	{
		return 56; // v6 (would be 64 in v7)
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////	
	public short getNumber()
	{
		return number;
	}

	public void setNumber( short number )
	{
		this.number = number;
	}

	public byte getModifications()
	{
		return modifications;
	}

	public void setModifications( byte modifications )
	{
		this.modifications = modifications;
	}

	public GeneralObjectApperance getGeneralApperance()
	{
		return generalApperance;
	}

	public void setGeneralApperance( GeneralObjectApperance generalApperance )
	{
		this.generalApperance = generalApperance;
	}

	public byte[] getSpecificApperance()
	{
		return specificApperance;
	}

	public void setSpecificApperance( byte[] specificApperance )
	{
		this.specificApperance = specificApperance;
	}

	public WorldCoordinate getLocation()
	{
		return location;
	}

	public void setLocation( WorldCoordinate location )
	{
		this.location = location;
	}

	public EulerAngles getOrientation()
	{
		return orientation;
	}

	public void setOrientation( EulerAngles orientation )
	{
		this.orientation = orientation;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength( int length )
	{
		this.length = length;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth( int width )
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight( int height )
	{
		this.height = height;
	}

	public int getDepth()
	{
		return depth;
	}

	public void setDepth( int depth )
	{
		this.depth = depth;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
