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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.emissions.DesignatorPdu;
import org.openlvc.disco.pdu.emissions.EmissionPdu;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.radio.ReceiverPdu;
import org.openlvc.disco.pdu.radio.SignalPdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.simman.ActionRequestPdu;
import org.openlvc.disco.pdu.simman.ActionResponsePdu;
import org.openlvc.disco.pdu.simman.CommentPdu;
import org.openlvc.disco.pdu.simman.DataPdu;
import org.openlvc.disco.pdu.simman.SetDataPdu;
import org.openlvc.disco.pdu.warfare.DetonationPdu;
import org.openlvc.disco.pdu.warfare.FirePdu;

public enum PduType
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other             ( (short)0,   ProtocolFamily.Other ),
	EntityState       ( (short)1,   ProtocolFamily.Warfare,        EntityStatePdu.class ),
	Fire              ( (short)2,   ProtocolFamily.Warfare,        FirePdu.class ),
	Detonation        ( (short)3,   ProtocolFamily.Warfare,        DetonationPdu.class ),
	Collision         ( (short)4,   ProtocolFamily.Entity ),
	ServiceRequest    ( (short)5,   ProtocolFamily.Logistics ),
	ResupplyOffer     ( (short)6,   ProtocolFamily.Logistics ),
	ResupplyReceived  ( (short)7,   ProtocolFamily.Logistics ),
	ResupplyCancel    ( (short)8,   ProtocolFamily.Logistics ),
	RepairComplete    ( (short)9,   ProtocolFamily.Logistics ),
	RepairResponse    ( (short)10,  ProtocolFamily.Logistics ),
	
	CreateEntity      ( (short)11,  ProtocolFamily.SimMgmt ),
	RemoveEntity      ( (short)12,  ProtocolFamily.SimMgmt ),
	StartResume       ( (short)13,  ProtocolFamily.SimMgmt ),
	StopFreeze        ( (short)14,  ProtocolFamily.SimMgmt ),
	Acknowledge       ( (short)15,  ProtocolFamily.SimMgmt ),
	ActionRequest     ( (short)16,  ProtocolFamily.SimMgmt,        ActionRequestPdu.class ),
	ActionResponse    ( (short)17,  ProtocolFamily.SimMgmt,        ActionResponsePdu.class ),
	DataQuery         ( (short)18,  ProtocolFamily.SimMgmt ),
	SetData           ( (short)19,  ProtocolFamily.SimMgmt,        SetDataPdu.class ),
	Data              ( (short)20,  ProtocolFamily.SimMgmt,        DataPdu.class ),
	EventReport       ( (short)21,  ProtocolFamily.SimMgmt ),
	Comment           ( (short)22,  ProtocolFamily.SimMgmt,        CommentPdu.class ),
	
	Emission          ( (short)23,  ProtocolFamily.Emission,       EmissionPdu.class ),
	Designator        ( (short)24,  ProtocolFamily.Emission,       DesignatorPdu.class ),
	Transmitter       ( (short)25,  ProtocolFamily.Radio,          TransmitterPdu.class ),
	Signal            ( (short)26,  ProtocolFamily.Radio,          SignalPdu.class ),
	Receiver          ( (short)27,  ProtocolFamily.Radio,          ReceiverPdu.class ),
	IFF               ( (short)28,  ProtocolFamily.Emission ),
	UnderwaterAcoustic( (short)29,  ProtocolFamily.Emission ),
	SupplementalEmmission( (short)30, ProtocolFamily.Emission ), // ?
	
	IntercomSignal    ( (short)31,  ProtocolFamily.Radio ),
	IntercomControl   ( (short)32,  ProtocolFamily.Radio ),
	
	AggregateSate     ( (short)33,  ProtocolFamily.EntityMgmt ),
	IsGroupOf         ( (short)34,  ProtocolFamily.EntityMgmt ),
	
	TransferOwnership ( (short)35,  ProtocolFamily.EntityMgmt ),
	IsPartOf          ( (short)36,  ProtocolFamily.EntityMgmt ),
	
	MinefieldState    ( (short)37,  ProtocolFamily.Minefield ),
	MinefieldQuery    ( (short)38,  ProtocolFamily.Minefield ),
	MinefieldData     ( (short)39,  ProtocolFamily.Minefield ),
	MinefieldRspNACK  ( (short)40,  ProtocolFamily.Minefield ),
	
	EnvironmentalProc ( (short)41,  ProtocolFamily.SyntheticEnv ),
	GriddedData       ( (short)42,  ProtocolFamily.SyntheticEnv ),
	PointObjectState  ( (short)43,  ProtocolFamily.SyntheticEnv ),
	LinearObjectState ( (short)44,  ProtocolFamily.SyntheticEnv ),
	ArealObjectState  ( (short)45,  ProtocolFamily.SyntheticEnv ),
	
	TSPI              ( (short)46,  ProtocolFamily.LiveEntity ),
	Appearance        ( (short)47,  ProtocolFamily.LiveEntity ),
	ArticulatedParts  ( (short)48,  ProtocolFamily.LiveEntity ),
	LEFire            ( (short)49,  ProtocolFamily.LiveEntity ),
	LEDetonation      ( (short)50,  ProtocolFamily.LiveEntity ),

	CreateEntity_R    ( (short)51,  ProtocolFamily.SimMgmt_R ),
	RemoveEntity_R    ( (short)52,  ProtocolFamily.SimMgmt_R ),
	StartResume_R     ( (short)53,  ProtocolFamily.SimMgmt_R ),
	StopFreeze_R      ( (short)54,  ProtocolFamily.SimMgmt_R ),
	Acknowledge_R     ( (short)55,  ProtocolFamily.SimMgmt_R ),
	ActionRequest_R   ( (short)56,  ProtocolFamily.SimMgmt_R ),
	ActionResponse_R  ( (short)57,  ProtocolFamily.SimMgmt_R ),
	DataQuery_R       ( (short)58,  ProtocolFamily.SimMgmt_R ),
	SetData_R         ( (short)59,  ProtocolFamily.SimMgmt_R ),
	Data_R            ( (short)60,  ProtocolFamily.SimMgmt_R ),
	EventReport_R     ( (short)61,  ProtocolFamily.SimMgmt_R ),
	Comment_R         ( (short)62,  ProtocolFamily.SimMgmt_R ),
	Record_R          ( (short)63,  ProtocolFamily.SimMgmt_R ),
	SetRecord_R       ( (short)64,  ProtocolFamily.SimMgmt_R ),
	RecordQuery_R     ( (short)65,  ProtocolFamily.SimMgmt_R ),

	CollisionElastic  ( (short)66,  ProtocolFamily.Entity ),
	EntityStateUpdate ( (short)67,  ProtocolFamily.Entity ),
	DirectedEnergyFire( (short)68,  ProtocolFamily.Warfare ),
	EntityDamageStatus( (short)69,  ProtocolFamily.Warfare ),

	InfoOpsAction     ( (short)70,  ProtocolFamily.InformationOps ),
	InfoOpsReport     ( (short)71,  ProtocolFamily.InformationOps ),
	Attribute         ( (short)72,  ProtocolFamily.Entity );
	
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final Map<Short,PduType> CACHE = Arrays.stream( PduType.values() )
	                                                      .collect( Collectors.toMap(PduType::value, 
	                                                                                 type -> type) );
	
	private static final EnumSet<PduType> SUPPORTED_TYPES = EnumSet.copyOf( Arrays.stream( PduType.values() )
	                                                                              .filter( type -> type.isSupported() )
	                                                                              .collect( Collectors.toList() ) );
	                                                              

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private final short value;
	private Class<? extends PDU> type;
	private ProtocolFamily protocolFamily;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private PduType( short value, ProtocolFamily family )
	{
		this.value = value;
		this.type = null;
		this.protocolFamily = family;
	}
	
	private PduType( short value, ProtocolFamily family, Class<? extends PDU> type )
	{
		this.value = value;
		this.type = type;
		this.protocolFamily = family;
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
	
	/**
	 * @return true if we have an associated Disco class/type for this PduType. false otherwise.
	 */
	public boolean isSupported()
	{
		return this.type != null;
	}

	public ProtocolFamily getProtocolFamily()
	{
		return this.protocolFamily;
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
		else if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new IllegalArgumentException( value+" not a valid PDUType number" );
		else
			return Other;
	}

	/**
	 * @return A list of all the types that we can instantiate. This is only the PDU types for which
	 *         we have a Class and Constructor cached.
	 */
	public static List<PduType> getSupportedPduTypes()
	{
		LinkedList<PduType> list = new LinkedList<>();
		for( PduType type : values() )
		{
			if( type.isSupported() )
				list.add( type );
		}
		
		return list;
	}
	
	public static EnumSet<PduType> getSupportedPdus()
	{
		return SUPPORTED_TYPES;
	}
}
