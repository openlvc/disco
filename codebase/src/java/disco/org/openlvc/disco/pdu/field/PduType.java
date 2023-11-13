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

import java.util.Set;

import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.utils.ValueLookup;

public class PduType
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final ValueLookup<Short> StandardValueLookup; 
	
	public static final short Other                 = 0;
	public static final short EntityState           = 1;
	public static final short Fire                  = 2;
	public static final short Detonation            = 3;
	public static final short Collision             = 4;
	public static final short ServiceRequest        = 5;
	public static final short ResupplyOffer         = 6;
	public static final short ResupplyReceived      = 7;
	public static final short ResupplyCancel        = 8;
	public static final short RepairComplete        = 9;
	public static final short RepairResponse        = 10;
 	
	public static final short CreateEntity          = 11;
	public static final short RemoveEntity          = 12;
	public static final short StartResume           = 13;
	public static final short StopFreeze            = 14;
	public static final short Acknowledge           = 15;
	public static final short ActionRequest         = 16;
	public static final short ActionResponse        = 17;
	public static final short DataQuery             = 18;
	public static final short SetData               = 19;
	public static final short Data                  = 20;
	public static final short EventReport           = 21;
	public static final short Comment               = 22;
	
	public static final short Emission              = 23;
	public static final short Designator            = 24;
	public static final short Transmitter           = 25;
	public static final short Signal                = 26;
	public static final short Receiver              = 27;
	public static final short IFF                   = 28;
	public static final short UnderwaterAcoustic    = 29;
	public static final short SupplementalEmmission = 30;
	
	public static final short IntercomSignal        = 31;
	public static final short IntercomControl       = 32;
	
	public static final short AggregateSate         = 33;
	public static final short IsGroupOf             = 34;
	
	public static final short TransferOwnership     = 35;
	public static final short IsPartOf              = 36;
	
	public static final short MinefieldState        = 37;
	public static final short MinefieldQuery        = 38;
	public static final short MinefieldData         = 39;
	public static final short MinefieldRspNACK      = 40;
	
	public static final short EnvironmentalProc     = 41;
	public static final short GriddedData           = 42;
	public static final short PointObjectState      = 43;
	public static final short LinearObjectState     = 44;
	public static final short ArealObjectState      = 45;
	
	public static final short TSPI                  = 46;
	public static final short Appearance            = 47;
	public static final short ArticulatedParts      = 48;
	public static final short LEFire                = 49;
	public static final short LEDetonation          = 50;

	public static final short CreateEntity_R        = 51;
	public static final short RemoveEntity_R        = 52;
	public static final short StartResume_R         = 53;
	public static final short StopFreeze_R          = 54;
	public static final short Acknowledge_R         = 55;
	public static final short ActionRequest_R       = 56;
	public static final short ActionResponse_R      = 57;
	public static final short DataQuery_R           = 58;
	public static final short SetData_R             = 59;
	public static final short Data_R                = 60;
	public static final short EventReport_R         = 61;
	public static final short Comment_R             = 62;
	public static final short Record_R              = 63;
	public static final short SetRecord_R           = 64;
	public static final short RecordQuery_R         = 65;

	public static final short CollisionElastic      = 66;
	public static final short EntityStateUpdate     = 67;
	public static final short DirectedEnergyFire    = 68;
	public static final short EntityDamageStatus    = 69;

	public static final short InfoOpsAction         = 70;
	public static final short InfoOpsReport         = 71;
	public static final short Attribute             = 72;
	
	static
	{
		StandardValueLookup = new ValueLookup<>();
		StandardValueLookup.addNamedValue( "Other",       Other );
		StandardValueLookup.addNamedValue( "EntityState", EntityState );
		StandardValueLookup.addNamedValue( "Fire",        Fire );
		StandardValueLookup.addNamedValue( "Detonation",  Detonation );
		StandardValueLookup.addNamedValue( "Collision",   Collision );
		StandardValueLookup.addNamedValue( "ServiceRequest", ServiceRequest );
		StandardValueLookup.addNamedValue( "ResupplyOffer",  ResupplyOffer );
		StandardValueLookup.addNamedValue( "ResupplyReceived", ResupplyReceived );
		StandardValueLookup.addNamedValue( "ResupplyCancel", ResupplyCancel );
		StandardValueLookup.addNamedValue( "RepairComplete", RepairComplete );
		StandardValueLookup.addNamedValue( "RepairResponse", RepairResponse );
	 	
		StandardValueLookup.addNamedValue( "CreateEntity", CreateEntity );
		StandardValueLookup.addNamedValue( "RemoveEntity", RemoveEntity );
		StandardValueLookup.addNamedValue( "StartResume", StartResume );
		StandardValueLookup.addNamedValue( "StopFreeze", StopFreeze );
		StandardValueLookup.addNamedValue( "Acknowledge", Acknowledge );
		StandardValueLookup.addNamedValue( "ActionRequest", ActionRequest );
		StandardValueLookup.addNamedValue( "ActionResponse", ActionResponse );
		StandardValueLookup.addNamedValue( "DataQuery", DataQuery );
		StandardValueLookup.addNamedValue( "SetData", SetData );
		StandardValueLookup.addNamedValue( "Data", Data );
		StandardValueLookup.addNamedValue( "EventReport", EventReport );
		StandardValueLookup.addNamedValue( "Comment", Comment );
		
		StandardValueLookup.addNamedValue( "Emission", Emission );
		StandardValueLookup.addNamedValue( "Designator", Designator );
		StandardValueLookup.addNamedValue( "Transmitter", Transmitter );
		StandardValueLookup.addNamedValue( "Signal", Signal );
		StandardValueLookup.addNamedValue( "Receiver", Receiver );
		StandardValueLookup.addNamedValue( "IFF", IFF );
		StandardValueLookup.addNamedValue( "UnderwaterAcoustic", UnderwaterAcoustic );
		StandardValueLookup.addNamedValue( "SupplementalEmmission", SupplementalEmmission );
		
		StandardValueLookup.addNamedValue( "IntercomSignal", IntercomSignal );
		StandardValueLookup.addNamedValue( "IntercomControl", IntercomControl );
		
		StandardValueLookup.addNamedValue( "AggregateSate", AggregateSate );
		StandardValueLookup.addNamedValue( "IsGroupOf", IsGroupOf );
		
		StandardValueLookup.addNamedValue( "TransferOwnership", TransferOwnership );
		StandardValueLookup.addNamedValue( "IsPartOf", IsPartOf );
		
		StandardValueLookup.addNamedValue( "MinefieldState", MinefieldState );
		StandardValueLookup.addNamedValue( "MinefieldQuery", MinefieldQuery );
		StandardValueLookup.addNamedValue( "MinefieldData", MinefieldData );
		StandardValueLookup.addNamedValue( "MinefieldRspNACK", MinefieldRspNACK );
		
		StandardValueLookup.addNamedValue( "EnvironmentalProc", EnvironmentalProc );
		StandardValueLookup.addNamedValue( "GriddedData", GriddedData );
		StandardValueLookup.addNamedValue( "PointObjectState", PointObjectState );
		StandardValueLookup.addNamedValue( "LinearObjectState", LinearObjectState );
		StandardValueLookup.addNamedValue( "ArealObjectState", ArealObjectState );
		
		StandardValueLookup.addNamedValue( "TSPI", TSPI );
		StandardValueLookup.addNamedValue( "Appearance", Appearance );
		StandardValueLookup.addNamedValue( "ArticulatedParts", ArticulatedParts );
		StandardValueLookup.addNamedValue( "Fire", Fire );
		StandardValueLookup.addNamedValue( "LEDetonation", LEDetonation );

		StandardValueLookup.addNamedValue( "CreateEntity_R", CreateEntity_R );
		StandardValueLookup.addNamedValue( "RemoveEntity_R", RemoveEntity_R );
		StandardValueLookup.addNamedValue( "StartResume_R", StartResume_R );
		StandardValueLookup.addNamedValue( "StopFreeze_R", StopFreeze_R );
		StandardValueLookup.addNamedValue( "Acknowledge_R", Acknowledge_R );
		StandardValueLookup.addNamedValue( "ActionRequest_R", ActionRequest_R );
		StandardValueLookup.addNamedValue( "ActionResponse_R", ActionResponse_R );
		StandardValueLookup.addNamedValue( "DataQuery_R", DataQuery_R );
		StandardValueLookup.addNamedValue( "SetData_R", SetData_R );
		StandardValueLookup.addNamedValue( "Data_R", Data_R );
		StandardValueLookup.addNamedValue( "EventReport_R", EventReport_R );
		StandardValueLookup.addNamedValue( "Comment_R", Comment_R );
		StandardValueLookup.addNamedValue( "Record_R", Record_R );
		StandardValueLookup.addNamedValue( "SetRecord_R", SetRecord_R );
		StandardValueLookup.addNamedValue( "RecordQuery_R", RecordQuery_R );

		StandardValueLookup.addNamedValue( "CollisionElastic", CollisionElastic );
		StandardValueLookup.addNamedValue( "EntityStateUpdate", EntityStateUpdate );
		StandardValueLookup.addNamedValue( "DirectedEnergyFire", DirectedEnergyFire );
		StandardValueLookup.addNamedValue( "EntityDamageStatus", EntityDamageStatus );

		StandardValueLookup.addNamedValue( "InfoOpsAction", InfoOpsAction );
		StandardValueLookup.addNamedValue( "InfoOpsReport", InfoOpsReport );
		StandardValueLookup.addNamedValue( "Attribute", Attribute );
		
	}
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static Set<Short> getStandardValues()
	{
		return StandardValueLookup.getValues();
	}
	
	public static int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}
	
	public static short fromName( String name )
	{
		Short value = StandardValueLookup.getValueForName( name );
		if( value == null )
			throw new IllegalArgumentException( name+" is not a valid name for PduType" );
		
		return value.shortValue();
	}
	
	public static String describe( Number value )
	{
		String name = StandardValueLookup.getNameForValue( value.shortValue() );
		return name != null ? name : String.format( "Unknown (%d)", value.shortValue() );
	}
}
