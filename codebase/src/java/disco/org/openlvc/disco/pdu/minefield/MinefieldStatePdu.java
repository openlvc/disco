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
import org.openlvc.disco.pdu.field.ForceId;
import org.openlvc.disco.pdu.field.MinefieldProtocolMode;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.field.appearance.MinefieldAppearance;
import org.openlvc.disco.pdu.record.EntityType;
import org.openlvc.disco.pdu.record.EulerAngles;
import org.openlvc.disco.pdu.record.MinefieldId;
import org.openlvc.disco.pdu.record.PointCoordinate;
import org.openlvc.disco.pdu.record.WorldCoordinate;

public class MinefieldStatePdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private MinefieldId minefieldId;
	private int minefieldSequenceNumber;
	private ForceId forceId;
	private EntityType minefieldType; // TODO Make this minefield type?
	private WorldCoordinate minefieldLocation;
	private EulerAngles minefieldOrientation;
	private MinefieldAppearance appearance;
	private MinefieldProtocolMode protocolMode;
	private List<PointCoordinate> perimeterPoints;
	private List<EntityType> mineTypes;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public MinefieldStatePdu()
	{
		super( PduType.MinefieldState );
		this.minefieldId = new MinefieldId();
		this.minefieldSequenceNumber = 0;
		this.forceId = ForceId.Other;
		this.minefieldType = new EntityType();
		this.minefieldLocation = new WorldCoordinate();
		this.minefieldOrientation = new EulerAngles();
		this.appearance = new MinefieldAppearance();
		this.protocolMode = MinefieldProtocolMode.HeartbeatMode;
		this.perimeterPoints = new ArrayList<>();
		this.mineTypes = new ArrayList<>();
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
		this.minefieldSequenceNumber = dis.readUI16();
		this.forceId = ForceId.fromValue( dis.readUI8() );
		int numberOfPerimeterPoints = dis.readUI8();
		this.minefieldType.from( dis );
		int numberOfMineTypes = dis.readUI16();
		this.minefieldLocation.from( dis );
		this.minefieldOrientation.from( dis );
		this.appearance = new MinefieldAppearance( dis.readShort() );
		
		short protoAndReserved = dis.readShort();
		int protocolModeRaw = protoAndReserved & 0x03;
		this.protocolMode = MinefieldProtocolMode.fromValue( protocolModeRaw );
		
		this.perimeterPoints = new ArrayList<>( numberOfPerimeterPoints );
		for( int i = 0 ; i < numberOfPerimeterPoints ; ++i )
		{
			PointCoordinate point = new PointCoordinate();
			point.from( dis );
			this.perimeterPoints.add( point );
		}
		
		this.mineTypes = new ArrayList<>( numberOfMineTypes );
		for( int i = 0 ; i < numberOfMineTypes ; ++i )
		{
			EntityType mineType = new EntityType();
			mineType.from( dis );
			this.mineTypes.add( mineType );
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
	
	public int getMinefieldSequenceNumber()
	{
		return this.minefieldSequenceNumber;
	}
	
	public ForceId getForceId()
	{
		return this.forceId;
	}
	
	public EntityType getMinefieldType()
	{
		return this.minefieldType;
	}
	
	public WorldCoordinate getMinefieldLocation()
	{
		return this.minefieldLocation;
	}
	
	public EulerAngles getMinefieldOrientation()
	{
		return this.minefieldOrientation;
	}
	
	public MinefieldAppearance getAppearance()
	{
		return this.appearance;
	}
	
	public MinefieldProtocolMode getProtocolMode()
	{
		return this.protocolMode;
	}
	
	public List<PointCoordinate> getPerimiterPoints()
	{
		return new ArrayList<>( this.perimeterPoints );
	}
	
	public List<EntityType> getMineTypes()
	{
		return new ArrayList<>( this.mineTypes );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
