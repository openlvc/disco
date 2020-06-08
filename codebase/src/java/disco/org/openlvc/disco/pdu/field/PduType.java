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

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.emissions.DesignatorPdu;
import org.openlvc.disco.pdu.emissions.EmissionPdu;
import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.radio.ReceiverPdu;
import org.openlvc.disco.pdu.radio.SignalPdu;
import org.openlvc.disco.pdu.radio.TransmitterPdu;
import org.openlvc.disco.pdu.record.PduHeader;
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
	SetData           ( (short)19, SetDataPdu.class ),
	Data              ( (short)20, DataPdu.class ),
	EventReport       ( (short)21 ),
	Comment           ( (short)22, CommentPdu.class ),
	Emission          ( (short)23, EmissionPdu.class ),
	Designator        ( (short)24, DesignatorPdu.class ),
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
	private Constructor<? extends PDU> constructor;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private PduType( short value )
	{
		this.value = value;
		this.type = null;
		this.constructor = null;
	}
	
	private PduType( short value, Class<? extends PDU> type )
	{
		this.value = value;
		this.type = type;
		this.constructor = null;
		if( type != null )
		{
			try
			{
				this.constructor = type.getConstructor( PduHeader.class );
			}
			catch( Exception e )
			{
				throw new DiscoException( "PDU Class is missing constructor `Class(PduHeader)`",
				                          e );
			}
		}
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
	
	public Constructor<? extends PDU> getImplementationConstructor()
	{
		return this.constructor;
	}

	/**
	 * @return `true` if we have an associated Disco class/type for this PduType. `false` otherwise.
	 */
	public boolean isSupported()
	{
		return this.constructor != null;
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
