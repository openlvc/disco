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
import java.util.ArrayList;
import java.util.List;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.field.StateUpdateIndicator;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.EventIdentifier;

public class EmissionPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId emittingEntityId;
	private EventIdentifier eventId;
	private StateUpdateIndicator stateUpdateIndicator; // uint8
	// padding 16-bits
	private List<EmitterSystem> emitterSystems;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public EmissionPdu()
	{
		super( PduType.Emission );
		
		this.emittingEntityId = new EntityId();
		this.eventId = new EventIdentifier();
		this.stateUpdateIndicator = StateUpdateIndicator.HeartbeatUpdate;
		this.emitterSystems = new ArrayList<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "Electromagnetic Emission PDU\n" );
		builder.append( "  > Emitting Entity: "+emittingEntityId.toString()+"\n" );
		builder.append( "  > Emitter Systems: "+emitterSystems.size()+"\n" );
		
		int count = 0;
		for( EmitterSystem system : emitterSystems )
		{
			++count;
			builder.append( "    ["+count+"]: Function="+system.getSystemType().getFunction().name() );
			builder.append( ", Beams="+system.getBeamCount()+"\n" );
			
			for( EmitterBeam beam : system.getBeams() )
				builder.append( "      {Beam} " ).append( beam.toString() ).append( "\n" );
		}
		
		return builder.toString();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		// read base values
		emittingEntityId.from( dis );
		eventId.from( dis );
		stateUpdateIndicator = StateUpdateIndicator.fromValue( dis.readUI8() );
		short systemCount = dis.readUI8();
		dis.skip16(); // padding
		
		// read in the systems
		emitterSystems.clear(); // do we want to clear every time? partial updates are part of spec
		for( int i = 0; i < systemCount; i++ )
		{
			EmitterSystem system = new EmitterSystem(emittingEntityId);
			system.from( dis );
			emitterSystems.add( system );
		}
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		emittingEntityId.to( dos );
		eventId.to( dos );
		dos.writeUI8( stateUpdateIndicator.value() );
		dos.writeUI8( (short)emitterSystems.size() );
		dos.writePadding16();
		
		for( EmitterSystem system : emitterSystems )
			system.to( dos );
	}

	@Override
	public final int getContentLength()
	{
		/* Static Section
		 * ---------------------
		 * 6  EntityId
		 * 6  EventId
		 * 1  StateUpdateIndicator
		 * 1  Number of Systems
		 * 2  Padding
		 * ---------------------
		 * 16 Total
		 */
		int size = 16;
		
		/* Dynamic Section */
		for( EmitterSystem system : emitterSystems )
			size += system.getByteLength();

		return size;
	}
	
	@Override
	public int getSiteId()
	{
		return emittingEntityId.getSiteId();
	}
	
	@Override
	public int getAppId()
	{
		return emittingEntityId.getAppId();
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityId getEmittingEntityId()
	{
		return emittingEntityId;
	}

	public void setEmittingEntityId( EntityId emittingEntityId )
	{
		this.emittingEntityId = emittingEntityId;
	}

	public EventIdentifier getEventId()
	{
		return eventId;
	}

	public void setEventId( EventIdentifier eventId )
	{
		this.eventId = eventId;
	}

	public StateUpdateIndicator getStateUpdateIndicator()
	{
		return stateUpdateIndicator;
	}

	public void setStateUpdateIndicator( StateUpdateIndicator stateUpdateIndicator )
	{
		this.stateUpdateIndicator = stateUpdateIndicator;
	}

	/////////////////////
	// Emitter Systems //
	/////////////////////
	public int getEmitterSystemCount()
	{
		return this.emitterSystems.size();
	}

	public List<EmitterSystem> getEmitterSystems()
	{
		return emitterSystems;
	}

	public void addEmitterSystem( EmitterSystem system )
	{
		this.emitterSystems.add( system );
	}
	
	public void removeEmitterSystem( EmitterSystem system )
	{
		this.emitterSystems.remove( system );
	}
	
	public void removeEmitterSystem( int index )
	{
		this.emitterSystems.remove( index );
	}

	public void setEmitterSystems( List<EmitterSystem> emitterSystems )
	{
		this.emitterSystems = emitterSystems;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Merging Methods   //////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
