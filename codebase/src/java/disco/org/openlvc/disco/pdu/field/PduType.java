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
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;
import org.openlvc.disco.pdu.DisSizes;

public class PduType
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final Map<Short,PduType> StandardValues = new HashMap<>();

	public static final PduType Other                 = registerStandardValue( 0, "Other" );
	public static final PduType EntityState           = registerStandardValue( 1, "EntityState" );
	public static final PduType Fire                  = registerStandardValue( 2, "Fire" );
	public static final PduType Detonation            = registerStandardValue( 3, "Detonation" );
	public static final PduType Collision             = registerStandardValue( 4, "Collision" );
	public static final PduType ServiceRequest        = registerStandardValue( 5, "ServiceRequest" );
	public static final PduType ResupplyOffer         = registerStandardValue( 6, "ResupplyOffer" );
	public static final PduType ResupplyReceived      = registerStandardValue( 7, "ResupplyReceived" );
	public static final PduType ResupplyCancel        = registerStandardValue( 8, "ResupplyCancel" );
	public static final PduType RepairComplete        = registerStandardValue( 9, "RepairComplete" );
	public static final PduType RepairResponse        = registerStandardValue( 10, "RepairResponse" );
 	
	public static final PduType CreateEntity          = registerStandardValue( 11, "CreateEntity" );
	public static final PduType RemoveEntity          = registerStandardValue( 12, "RemoveEntity" );
	public static final PduType StartResume           = registerStandardValue( 13, "StartResume" );
	public static final PduType StopFreeze            = registerStandardValue( 14, "StopFreeze" );
	public static final PduType Acknowledge           = registerStandardValue( 15, "Acknowledge" );
	public static final PduType ActionRequest         = registerStandardValue( 16, "ActionRequest" );
	public static final PduType ActionResponse        = registerStandardValue( 17, "ActionResponse" );
	public static final PduType DataQuery             = registerStandardValue( 18, "DataQuery" );
	public static final PduType SetData               = registerStandardValue( 19, "SetData" );
	public static final PduType Data                  = registerStandardValue( 20, "Data" );
	public static final PduType EventReport           = registerStandardValue( 21, "EventReport" );
	public static final PduType Comment               = registerStandardValue( 22, "Comment" );
	
	public static final PduType Emission              = registerStandardValue( 23, "Emission" );
	public static final PduType Designator            = registerStandardValue( 24, "Designator" );
	public static final PduType Transmitter           = registerStandardValue( 25, "Transmitter" );
	public static final PduType Signal                = registerStandardValue( 26, "Signal" );
	public static final PduType Receiver              = registerStandardValue( 27, "Receiver" );
	public static final PduType IFF                   = registerStandardValue( 28, "IFF" );
	public static final PduType UnderwaterAcoustic    = registerStandardValue( 29, "UnderwaterAcoustic" );
	public static final PduType SupplementalEmmission = registerStandardValue( 30, "SupplementalEmmission" );
	
	public static final PduType IntercomSignal        = registerStandardValue( 31, "IntercomSignal" );
	public static final PduType IntercomControl       = registerStandardValue( 32, "IntercomControl" );
	
	public static final PduType AggregateSate         = registerStandardValue( 33, "AggregateSate" );
	public static final PduType IsGroupOf             = registerStandardValue( 34, "IsGroupOf" );
	
	public static final PduType TransferOwnership     = registerStandardValue( 35, "TransferOwnership" );
	public static final PduType IsPartOf              = registerStandardValue( 36, "IsPartOf" );
	
	public static final PduType MinefieldState        = registerStandardValue( 37, "MinefieldState" );
	public static final PduType MinefieldQuery        = registerStandardValue( 38, "MinefieldQuery" );
	public static final PduType MinefieldData         = registerStandardValue( 39, "MinefieldData" );
	public static final PduType MinefieldRspNACK      = registerStandardValue( 40, "MinefieldRspNACK" );
	
	public static final PduType EnvironmentalProc     = registerStandardValue( 41, "EnvironmentalProc" );
	public static final PduType GriddedData           = registerStandardValue( 42, "GriddedData" );
	public static final PduType PointObjectState      = registerStandardValue( 43, "PointObjectState" );
	public static final PduType LinearObjectState     = registerStandardValue( 44, "LinearObjectState" );
	public static final PduType ArealObjectState      = registerStandardValue( 45, "ArealObjectState" );
	
	public static final PduType TSPI                  = registerStandardValue( 46, "TSPI" );
	public static final PduType Appearance            = registerStandardValue( 47, "Appearance" );
	public static final PduType ArticulatedParts      = registerStandardValue( 48, "ArticulatedParts" );
	public static final PduType LEFire                = registerStandardValue( 49, "LEFire" );
	public static final PduType LEDetonation          = registerStandardValue( 50, "LEDetonation" );

	public static final PduType CreateEntity_R        = registerStandardValue( 51, "CreateEntity_R" );
	public static final PduType RemoveEntity_R        = registerStandardValue( 52, "RemoveEntity_R" );
	public static final PduType StartResume_R         = registerStandardValue( 53, "StartResume_R" );
	public static final PduType StopFreeze_R          = registerStandardValue( 54, "StopFreeze_R" );
	public static final PduType Acknowledge_R         = registerStandardValue( 55, "Acknowledge_R" );
	public static final PduType ActionRequest_R       = registerStandardValue( 56, "ActionRequest_R" );
	public static final PduType ActionResponse_R      = registerStandardValue( 57, "ActionResponse_R" );
	public static final PduType DataQuery_R           = registerStandardValue( 58, "DataQuery_R" );
	public static final PduType SetData_R             = registerStandardValue( 59, "SetData_R" );
	public static final PduType Data_R                = registerStandardValue( 60, "Data_R" );
	public static final PduType EventReport_R         = registerStandardValue( 61, "EventReport_R" );
	public static final PduType Comment_R             = registerStandardValue( 62, "Comment_R" );
	public static final PduType Record_R              = registerStandardValue( 63, "Record_R" );
	public static final PduType SetRecord_R           = registerStandardValue( 64, "SetRecord_R" );
	public static final PduType RecordQuery_R         = registerStandardValue( 65, "RecordQuery_R" );

	public static final PduType CollisionElastic      = registerStandardValue( 66, "CollisionElastic" );
	public static final PduType EntityStateUpdate     = registerStandardValue( 67, "EntityStateUpdate" );
	public static final PduType DirectedEnergyFire    = registerStandardValue( 68, "DirectedEnergyFire" );
	public static final PduType EntityDamageStatus    = registerStandardValue( 69, "EntityDamageStatus" );

	public static final PduType InfoOpsAction         = registerStandardValue( 70, "InfoOpsAction" );
	public static final PduType InfoOpsReport         = registerStandardValue( 71, "InfoOpsReport" );
	public static final PduType Attribute             = registerStandardValue( 72, "Attribute" );
	
	// Custom Extensions
		/*
		IRCUser              ( (short)160, ProtocolFamily.DiscoCustom, IrcUserPdu.class ),
		IRCMessage           ( (short)161, ProtocolFamily.DiscoCustom, IrcMessagePdu.class ),
		IRCRawMessage        ( (short)162, ProtocolFamily.DiscoCustom, IrcRawMessagePdu.class ),
		
		DcssWallclockTime         ( (short)170, ProtocolFamily.DiscoCustom, DcssWallclockTimePdu.class ),
		DcssWeatherRequest        ( (short)171, ProtocolFamily.DiscoCustom, DcssWeatherRequestPdu.class ),
		DcssWeatherResponse       ( (short)172, ProtocolFamily.DiscoCustom, DcssWeatherResponsePdu.class ),
		DcssWeatherInstance       ( (short)173, ProtocolFamily.DiscoCustom, DcssWeatherInstancePdu.class ),
		
		InhibitedMidsPairing ( (short)180, ProtocolFamily.DiscoCustom, InhibitedMidsPairingPdu.class );
		*/
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;
	private String name;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private PduType( short value )
	{
		this( value, null );
	}
	
	private PduType( short value, String name )
	{
		this.value = value;
		this.name = name;
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
	{
		return this.value;
	}
	
	public String name()
	{
		return this.name;
	}

	@Override
	public String toString()
	{
		if( this.name != null )
			return this.name;
		else
			return String.valueOf( this.value );
	}
	
	@Override
	public boolean equals( Object other )
	{
		if( other == this )
			return true;
		
		if( !(other instanceof PduType) )
			return false;
		
		PduType otherType = (PduType)other;
		return otherType.value == this.value;
	}
	
	@Override
	public int hashCode()
	{
		return this.value;
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static Set<PduType> getStandardValues()
	{
		Set<PduType> values = new HashSet<>();
		values.addAll( StandardValues.values() );
		return values;
	}
	
	public static int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}

	public static PduType fromValue( short value )
	{
		PduType result = StandardValues.get( value );
		if( result == null )
		{
			if( DiscoConfiguration.isSet(Flag.Strict) )
				throw new IllegalArgumentException( value+" is not a valid value for PduType" );
			
			result = new PduType( value );
		}

		return result;
	}
	
	public static PduType fromName( String name )
	{
		Optional<PduType> result = StandardValues.values().stream()
		                                                  .filter( v -> v.name.equalsIgnoreCase(name) )
		                                                  .findFirst();

		if( result.isPresent() )
			return result.get();
		else
			throw new IllegalArgumentException( name+" is not a valid name for PduType" ); 
	}
	
	private static PduType registerStandardValue( Number value, String name )
	{
		PduType type = new PduType( value.shortValue(), name );
		StandardValues.put( value.shortValue(), type );
		return type;
	}
}
