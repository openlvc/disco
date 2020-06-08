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
package org.openlvc.disco.pdu.emissions;

import java.io.IOException;
import java.util.List;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;
import org.openlvc.disco.pdu.record.EmitterSystemType;
import org.openlvc.disco.pdu.record.VectorRecord;

public class EmitterSystem implements IPduComponent, Cloneable
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EmitterSystemType systemType;
	private VectorRecord location;
	private List<EmitterBeam> beams;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public String toString()
	{
		return systemType.toString()+", Beams="+beams.size();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		dis.skip8();  // System Data Length
		short numberOfBeams = dis.readUI8();
		dis.skip16(); // Padding
		systemType.from( dis );
		location.from( dis );

		// Read the beams
		beams.clear(); // FIXME Partial beam updates should be supported
		for( int i = 0; i < numberOfBeams; i++ )
		{
			EmitterBeam beam = new EmitterBeam();
			beam.from( dis );
			beams.add( beam );
		}
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		dos.writeUI8( (short)getByteLength() );
		dos.writeUI8( (short)beams.size() );
		dos.writePadding16();
		systemType.to( dos );
		location.to( dos );
		
		for( EmitterBeam beam : beams )
			beam.to( dos );
	}
	
	@Override
	public final int getByteLength()
	{
		/* Static Section
		 * ---------------------
		 * 1  System Data Length
		 * 1  Number of Beams
		 * 2  Padding
		 * 4  Emitter System Type  
		 * 12 Location
		 * ---------------------
		 * 20 Total
		 */
		int size = 20;

		/* Dynamic Section */
		for( EmitterBeam beam : beams )
			size += beam.getByteLength();
		
		return size;
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EmitterSystemType getSystemType()
	{
		return systemType;
	}

	public void setSystemType( EmitterSystemType emitterSystemType )
	{
		this.systemType = emitterSystemType;
	}

	public VectorRecord getLocation()
	{
		return location;
	}

	public void setLocation( VectorRecord location )
	{
		this.location = location;
	}

	public int getBeamCount()
	{
		return this.beams.size();
	}
	
	public List<EmitterBeam> getBeams()
	{
		return beams;
	}

	public void addBeam( EmitterBeam beam )
	{
		this.beams.add( beam );
	}
	
	public void removeBeam( EmitterBeam beam )
	{
		this.beams.remove( beam );
	}
	
	public void removeBeam( int index )
	{
		this.beams.remove( index );
	}
	
	public void setBeams( List<EmitterBeam> beams )
	{
		this.beams = beams;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
