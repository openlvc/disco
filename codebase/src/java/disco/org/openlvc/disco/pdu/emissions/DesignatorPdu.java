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
package org.openlvc.disco.pdu.emissions;

import java.io.IOException;

import org.openlvc.disco.pdu.DisInputStream;
import org.openlvc.disco.pdu.DisOutputStream;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.CodeName;
import org.openlvc.disco.pdu.field.DeadReckoningAlgorithm;
import org.openlvc.disco.pdu.field.PduType;
import org.openlvc.disco.pdu.record.EntityId;
import org.openlvc.disco.pdu.record.VectorRecord;
import org.openlvc.disco.pdu.record.WorldCoordinate;

public class DesignatorPdu extends PDU
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EntityId designatingEntityId;
	private CodeName codeName;
	private EntityId designatedEntityId;
	private int      code;
	private float    power;
	private float    wavelength;
	private VectorRecord spotRelativeToDesignatedEntity;
	private WorldCoordinate spotLocation;
	private DeadReckoningAlgorithm spotDRA;
	// Padding 24-bits
	private VectorRecord spotLinearAcceleration;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DesignatorPdu()
	{
		super( PduType.Designator );

		this.designatingEntityId = new EntityId();
		this.codeName = CodeName.NotSpecified;
		this.designatedEntityId = new EntityId();
		this.code = (short)0;
		this.power = 0.0f;
		this.wavelength = 0.0f;
		this.spotRelativeToDesignatedEntity = new VectorRecord();
		this.spotLocation = new WorldCoordinate();
		this.spotDRA = DeadReckoningAlgorithm.Static;
		// padding 24-bit
		this.spotLinearAcceleration = new VectorRecord();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// IPduComponent Methods   ////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void from( DisInputStream dis ) throws IOException
	{
		designatingEntityId.from( dis );
		codeName = CodeName.fromValue( dis.readUI16() );
		designatedEntityId.from( dis );
		code = dis.readUI16();
		power = dis.readFloat();
		wavelength = dis.readFloat();
		spotRelativeToDesignatedEntity.from( dis );
		spotLocation.from( dis );
		spotDRA = DeadReckoningAlgorithm.fromValue( dis.readUI8() );
		dis.skip24();
		spotLinearAcceleration.from( dis );
	}

	@Override
	public void to( DisOutputStream dos ) throws IOException
	{
		designatingEntityId.to( dos );
		dos.writeUI16( codeName.value() );
		designatedEntityId.to( dos );
		dos.writeUI16( code );
		dos.writeFloat( power );
		dos.writeFloat( wavelength );
		spotRelativeToDesignatedEntity.to( dos );
		spotLocation.to( dos );
		dos.writeUI8( spotDRA.value() );
		dos.writePadding24();
		spotLinearAcceleration.to( dos );
	}

	@Override
	public final int getContentLength()
	{
		return 76;

		/*
		int size = designatingEntityId.getByteLength();         // 6
		size += codeName.getByteLength();                       // 2
		size += designatedEntityId.getByteLength();             // 6
		size += 2;                                              // 2 (Code)
		size += 4;                                              // 4 (Power)
		size += 4;                                              // 4 (Wavelength)
		size += spotRelativeToDesignatedEntity.getByteLength(); // 12
		size += spotLocation.getByteLength();                   // 24
		size += spotDRA.getByteLength();                        // 1
		size += 3;                                              // 3 (Padding)
		size += spotLinearAcceleration.getByteLength();         // 12
		return size;
		*/
	}
	
	@Override
	public int getSiteId()
	{
		return designatingEntityId.getSiteId();
	}
	
	@Override
	public int getAppId()
	{
		return designatingEntityId.getAppId();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public EntityId getDesignatingEntityId()
	{
		return designatingEntityId;
	}

	public void setDesignatingEntityId( EntityId designatingEntityId )
	{
		this.designatingEntityId = designatingEntityId;
	}

	public CodeName getCodeName()
	{
		return codeName;
	}

	public void setCodeName( CodeName codeName )
	{
		this.codeName = codeName;
	}

	public EntityId getDesignatedEntityId()
	{
		return designatedEntityId;
	}

	public void setDesignatedEntityId( EntityId designatedEntityId )
	{
		this.designatedEntityId = designatedEntityId;
	}

	public int getCode()
	{
		return code;
	}

	public void setCode( int code )
	{
		this.code = code;
	}

	public float getPower()
	{
		return power;
	}

	public void setPower( float power )
	{
		this.power = power;
	}

	public float getWavelength()
	{
		return wavelength;
	}

	public void setWavelength( float wavelength )
	{
		this.wavelength = wavelength;
	}

	public VectorRecord getSpotRelativeToDesignatedEntity()
	{
		return spotRelativeToDesignatedEntity;
	}

	public void setSpotRelativeToDesignatedEntity( VectorRecord spotRelativeToDesignatedEntity )
	{
		this.spotRelativeToDesignatedEntity = spotRelativeToDesignatedEntity;
	}

	public WorldCoordinate getSpotLocation()
	{
		return spotLocation;
	}

	public void setSpotLocation( WorldCoordinate spotLocation )
	{
		this.spotLocation = spotLocation;
	}

	public DeadReckoningAlgorithm getSpotDRA()
	{
		return spotDRA;
	}

	public void setSpotDRA( DeadReckoningAlgorithm spotDRA )
	{
		this.spotDRA = spotDRA;
	}

	public VectorRecord getSpotLinearAcceleration()
	{
		return spotLinearAcceleration;
	}

	public void setSpotLinearAcceleration( VectorRecord spotLinearAcceleration )
	{
		this.spotLinearAcceleration = spotLinearAcceleration;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
