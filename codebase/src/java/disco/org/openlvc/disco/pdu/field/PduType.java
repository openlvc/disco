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
package org.openlvc.disco.pdu.field;

import java.util.HashMap;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.radio.ReceiverPdu;
import org.openlvc.disco.pdu.radio.SignalPdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.warfare.DetonationPdu;
import org.openlvc.disco.pdu.warfare.FirePdu;

public enum PduType
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other             ( (short)0 ),
	EntityState       ( (short)1, EntityStatePdu.class ),
	Fire              ( (short)2, FirePdu.class ),
	Detonation        ( (short)3, DetonationPdu.class ),
	Collision         ( (short)4 ),
	ServiceRequest    ( (short)5 ),
	ResupplyOffer     ( (short)6 ),
	ResupplyReceived  ( (short)7 ),
	ResupplyCancel    ( (short)8 ),
	RepairComplete    ( (short)9 ),
	RepairResponse    ( (short)10 ),
	CreateEntity      ( (short)11 ),
	RemoveEntity      ( (short)12 ),
	StartResume       ( (short)13 ),
	StopFreeze        ( (short)14 ),
	Acknowledge       ( (short)15 ),
	ActionRequest     ( (short)16 ),
	ActionResponse    ( (short)17 ),
	DataQuery         ( (short)18 ),
	SetData           ( (short)19 ),
	Data              ( (short)20 ),
	EventReport       ( (short)21 ),
	Comment           ( (short)22 ),
	Emission          ( (short)23 ),
	Designator        ( (short)24 ),
	Transmitter       ( (short)25, TransmitterPdu.class ),
	Signal            ( (short)26, SignalPdu.class ),
	Receiver          ( (short)27, ReceiverPdu.class ),
	IFF               ( (short)28 ),
	UnderwaterAcoustic( (short)29 ),
	SupplementalEmmission( (short)30 ), // ?
	
	IntercomSignal    ( (short)31 ),
	IntercomControl   ( (short)32 ),
	
	AggregateSate     ( (short)33 ),
	IsGroupOf         ( (short)34 ),
	
	TransferOwnership ( (short)35 ),
	IsPartOf          ( (short)36 ),
	
	MinefieldState    ( (short)37 ),
	MinefieldQuery    ( (short)38 ),
	MinefieldData     ( (short)39 ),
	MinefieldRspNACK  ( (short)40 ),
	
	EnvironmentalProc ( (short)41 ),
	GriddedData       ( (short)42 ),
	PointObjectState  ( (short)43 ),
	LinearObjectState ( (short)44 ),
	ArealObjectState  ( (short)45 ),
	
	TSPI              ( (short)46 ),
	Appearance        ( (short)47 ),
	ArticulatedParts  ( (short)48 ),
	
	LEFire            ( (short)49 ),
	LEDetonation      ( (short)50 ),
	CreateEntity_R    ( (short)51 ),
	RemoveEntity_R    ( (short)52 ),
	StartResume_R     ( (short)53 ),
	StopFreeze_R      ( (short)54 ),
	Acknowledge_R     ( (short)55 ),
	ActionRequest_R   ( (short)56 ),
	ActionResponse_R  ( (short)57 ),
	DataQuery_R       ( (short)58 ),
	SetData_R         ( (short)59 ),
	Data_R            ( (short)60 ),
	EventReport_R     ( (short)61 ),
	Comment_R         ( (short)62 ),
	Record_R          ( (short)63 ),
	SetRecord_R       ( (short)64 ),
	RecordQuery_R     ( (short)65 ),
	CollisionElastic  ( (short)66 ),
	EntityStateUpdate ( (short)67 ),
	DirectedEnergyFire( (short)68 ),
	EntityDamageStatus( (short)69 ),
	InfoOpsAction     ( (short)70 ),
	InfoOpsReport     ( (short)71 ),
	Attribute         ( (short)72 );
	
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static HashMap<Short,PduType> CACHE;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;
	private Class<? extends PDU> type;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private PduType( short value )
	{
		this.value = value;
		store( value );
	}
	
	private PduType( short value, Class<? extends PDU> type )
	{
		this.value = value;
		this.type = type;
		store( value );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
	{
		return this.value;
	}

	/**
	 * Returns the class that implements this PDU, or null if there is none.
	 */
	public Class<? extends PDU> getImplementationClass()
	{
		return this.type;
	}
	
	private void store( short value )
	{
		if( CACHE == null )
			CACHE = new HashMap<>();

		CACHE.put( value, this );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}

	public static PduType fromValue( short value )
	{
		PduType type = CACHE.get( value );
		if( type != null )
			return type;

		// Missing
		if( DiscoConfiguration.STRICT_MODE )
			throw new IllegalArgumentException( value+" not a valid PDUType number" );
		else
			return Other;
	}
}
