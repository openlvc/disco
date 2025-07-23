/*
 *   Copyright 2025 Open LVC Project.
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
package org.openlvc.disco.pdu.minefield;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityCoordinate;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.EntityType;
import org.openlvc.disco.pdu.record.MinefieldId;
import org.openlvc.disco.utils.BitField32;

public class MinefieldDataPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private MinefieldId minefieldId;
	private EntityId requestingEntityId;
	private int minefieldSequenceNumber;
	private short requestId;
	private short pduSequenceNumber;
	private short numberOfPdus;
	// number of mines in this pdu
	// number of sensor types
	private BitField32 dataFilter;
	private EntityType mineType;
	private List<Short> sensorTypes;
	private List<EntityCoordinate> mineLocations;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public MinefieldDataPdu()
	{
		super( PduType.MinefieldData );
		this.minefieldId = new MinefieldId();
		this.requestingEntityId = new EntityId();
		this.minefieldSequenceNumber = 0;
		this.requestId = 0;
		this.pduSequenceNumber = 0;
		this.numberOfPdus = 0;
		this.dataFilter = new BitField32( 0 );
		this.mineType = new EntityType();
		this.sensorTypes = new ArrayList<>();
		this.mineLocations = new ArrayList<>();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// PDU Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		this.minefieldId.from( dis );
		
		this.requestingEntityId.from( dis );
		this.minefieldSequenceNumber = dis.readUI16();
		this.requestId = dis.readUI8();
		this.pduSequenceNumber = dis.readUI8();
		this.numberOfPdus = dis.readUI8();
		
		int numberOfMinesInPdu = dis.readUI8();
		int numberOfSensorTypes = dis.readUI8();
		
		dis.skip8(); // Padding
		this.dataFilter.setInt( dis.readInt() );
		
		this.mineType.from( dis );
		this.sensorTypes = new ArrayList<>( numberOfSensorTypes );
		for( int i = 0 ; i < numberOfSensorTypes ; ++i )
		{
			short sensorType = dis.readShort();
			this.sensorTypes.add( sensorType );
		}
		
		int padding = (numberOfSensorTypes % 2) * 2;
		dis.skipBytes( padding );
		
		this.mineLocations = new ArrayList<>();
		for( int i = 0 ; i < numberOfMinesInPdu ; ++i )
		{
			EntityCoordinate location = new EntityCoordinate();
			location.from( dis );
			this.mineLocations.add( location );
		}
		
	}
	
	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final int getContentLength()
	{
		return 0;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public int getSiteId()
	{
		return minefieldId.getSiteId();
	}
	
	@Override
	public int getAppId()
	{
		return minefieldId.getAppId();
	}
	
	public MinefieldId getMinefieldId()
	{
		return this.minefieldId;
	}
	
	public EntityId getRequestingEntityId()
	{
		return this.requestingEntityId;
	}
	
	public int getMinefieldSequenceNumber()
	{
		return this.minefieldSequenceNumber;
	}
	
	public short getRequestId()
	{
		return this.requestId;
	}
	
	public short getPduSequenceNumber()
	{
		return this.pduSequenceNumber;
	}
	
	public short getNumberOfPdus()
	{
		return this.numberOfPdus;
	}
	
	public BitField32 getDataFilter()
	{
		return this.dataFilter;
	}
	
	public EntityType getMineType()
	{
		return this.mineType;
	}
	
	public List<Short> getSensorTypes()
	{
		return this.sensorTypes;
	}
	
	public List<EntityCoordinate> getMineLocations()
	{
		return this.mineLocations;
	}
	
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
