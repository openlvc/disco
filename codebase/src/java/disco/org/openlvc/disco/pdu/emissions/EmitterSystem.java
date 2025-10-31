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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.IPduComponent;
import org.openlvc.disco.pdu.record.EmitterSystemType;
import org.openlvc.disco.pdu.record.EntityId;
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
	private Map<Short,EmitterBeam> beams;
	
	// Off-Spec / Non-Spec Data 
	private EntityId emittingEntity;
	private long lastUpdated;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EmitterSystem()
	{
		this.systemType = new EmitterSystemType();
		this.location = new VectorRecord();
		this.beams = new HashMap<>();
		
		// Off-Spec / Non-Spec Data
		this.emittingEntity = null;
		this.lastUpdated = System.currentTimeMillis();
	}

	public EmitterSystem( EntityId emittingEntity )
	{
		this();

		// Off-Spec / Non-Spec Data
		this.emittingEntity = emittingEntity;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	public boolean equals( Object other )
	{
		if( other == null )
			return false;
		
		if( !(other instanceof EmitterSystem) )
			return false;
		
		EmitterSystem otherSystem = (EmitterSystem)other;
		return Objects.equals( this.systemType, otherSystem.systemType ) &&
		       Objects.equals( this.location, otherSystem.location ) &&
		       Objects.equals( this.beams, otherSystem.beams );
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash( this.systemType, 
		                     this.location,
		                     this.beams );
	}
	
	@Override
	public String toString()
	{
		return systemType.toString()+", Beams="+beams.size();
	}
	
	@Override
	public EmitterSystem clone()
	{
		EmitterSystem cloned = new EmitterSystem();
		cloned.systemType = this.systemType.clone();
		cloned.location = this.location.clone();
		this.beams.forEach( (key,value) -> cloned.beams.put(key, value.clone(cloned)) );
		cloned.lastUpdated = this.lastUpdated;
		
		return cloned;
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
			EmitterBeam beam = new EmitterBeam(this);
			beam.from( dis );
			beams.put( beam.getBeamNumber(), beam );
		}
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		// ref DIS-7 spec section 7.6.2 paragraph f.1
		// if length exceeds 255 this value is not used and should be set to 0
		short dataLength = getDataLength();
		dos.writeUI8( dataLength );
		dos.writeUI8( (short)beams.size() );
		dos.writePadding16();
		systemType.to( dos );
		location.to( dos );
		
		for( EmitterBeam beam : beams.values() )
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
		for( EmitterBeam beam : beams.values() )
			size += beam.getByteLength();
		
		return size;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Helper Methods   ///////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the set of contained {@link EmitterBeam}s that currently list the given
	 * {@link EntityId} as a target in their Track/Jam data field.
	 * 
	 * @param target We want to find any beams targeting this entity id
	 * @return A set of all the beams targeting the entity. Empty if there are none
	 */
	public final Set<EmitterBeam> getBeamsTargeting( EntityId target )
	{
		return beams.values().stream()
		                     .filter( beam -> beam.isTargeting(target) )
		                     .collect( Collectors.toSet() );
	}

	/**
	 * Returns the length of this structure in 32-bit words.
	 * <p/>
	 * If the 32-bit word length exceeds 255, then 0 is returned instead.
	 * <p/>
	 * This value is what is written to the data length field in the PDU. See DIS specification 
	 * s7.6.2.
	 * <p/>
	 * For the length in bytes, please see {@link #getByteLength()}
	 * 
	 * @return the length of this structure in 32-bit words. If this figure would be greater than 
	 *         255, then zero is returned
	 */
	public short getDataLength()
	{
		// ref DIS-7 spec section 7.6.2 paragraph f.5.i
		// if length exceeds 255 this value is not used and should be set to 0
		int byteLength = this.getByteLength();
		int dataLength = byteLength / 4;
		if( dataLength > 255 )
			dataLength = 0;
		
		return (short)dataLength;
	}

	/**
	 * Returns if the Complete-System emitter system represented by this PDU is active (if the
	 * system contains any active beams).
	 * 
	 * @return true iff the system contains an active beam
	 */
	public boolean isSystemActive()
	{
		return this.getBeams().parallelStream().anyMatch( beam -> beam.isBeamActive() );
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
	
	public Collection<EmitterBeam> getBeams()
	{
		return new HashSet<>( beams.values() );
	}

	public void clearBeams()
	{
		beams.values().clear();
	}

	/**
	 * Put the given Beam into this system, returning the object that is already stored
	 * for the beam number, or null if we don't have one for this number yet.
	 * 
	 * @param beam The beam to replace any existing beam with
	 * @return Null if there is no existing beam with that beam number, or the existing
	 *         Beam object that was replaced
	 */
	public EmitterBeam addBeam( EmitterBeam beam )
	{
		beam.setEmitterSystem( this );
		return this.beams.put( beam.getBeamNumber(), beam );
	}
	
	public EmitterBeam removeBeam( EmitterBeam beam )
	{
		return this.beams.remove( beam.getBeamNumber() );
	}
	
	public EmitterBeam removeBeam( short number )
	{
		return this.beams.remove( number );
	}

	public boolean containsBeam( short number )
	{
		return this.beams.containsKey( number );
	}

	////////////////////////////////////////////////////////////
	///  Off-Spec Properties   /////////////////////////////////
	////////////////////////////////////////////////////////////
	// Properties that aren't defined by the spec, but create
	// links that Disco wants to maintain.
	public EntityId getEmittingEntity()
	{
		return this.emittingEntity;
	}

	// Keep protected - should be called by the EmissionPdu when it adds it
	protected void setEmittingEntity( EntityId entity )
	{
		this.emittingEntity = entity;
	}
	
	public short getEmitterNumber()
	{
		return systemType.getNumber();
	}
	
	public EmitterSystemId getEmitterSystemId()
	{
		return new EmitterSystemId();
	}

	//
	// Off-Spec Support
	//
	/**
	 * @return The last time this system was updated (typically the wall-clock time when the system
	 *         was created. This is local wallclock time only and not related to the DIS timestamp.
	 */
	public long getLastUpdatedTime()
	{
		return this.lastUpdated;
	}

	public void setLastUpdatedTime( long time )
	{
		this.lastUpdated = time;
	}


	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	public class EmitterSystemId
	{
		@Override
		public int hashCode()
		{
			// Combination of
			//   SiteId      >> Trim to 8-bit  (range to 256)
			//   AppId       >> Trim to 4-bit  (range to 16)
			//   EntityId    >> Keep at 16-bit (these frequently randomly assigned in range)
			//   System Num  >> Trim to 4-bit  (caps max systems for entity at 16; seems fair)
			
			int index    = systemType.getNumber();
			int siteId   = emittingEntity.getSiteId()   & 0x00ff; // mask out above first 8-bits
			int appId    = emittingEntity.getAppId()    & 0x000f; // mask out above first 4-bits
			int entityId = emittingEntity.getEntityId() & 0xffff; // mask out above first 16-bits
			
			return (index  << 28)  |
			       (siteId << 20)  |
			       (appId  << 16)  |
			       (entityId);
		}
		
		@Override
		public String toString()
		{
			return emittingEntity.toString()+"-"+systemType.getNumber();
		}
	}
	
}
