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

public enum PduType
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other             ( (short)0 ),
	EntityState       ( (short)1 ),
	Fire              ( (short)2 ),
	Detonation        ( (short)3 ),
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
	Transmitter       ( (short)25 ),
	Signal            ( (short)26 ),
	Receiver          ( (short)27 ),
	
	AnnounceObject      ( (short)129 ),
	DeleteObject        ( (short)130 ),
	DescribeApplication ( (short)131 ),
	DescribeEvent       ( (short)132 ),
	DescribeObject      ( (short)133 ),
	RequestEvent        ( (short)134 ),
	RequestObject       ( (short)135 ),

	TimeSpacePositionFI ( (short)140 ),
	AppearanceFI        ( (short)141 ),
	ArticulatedPartsFI  ( (short)142 ),
	FireFI              ( (short)143 ),
	DetonationFI        ( (short)144 ),

	PointObjectState           ( (short)150 ),
	LinearObjectState          ( (short)151 ),
	ArealObjectState           ( (short)152 ),
	Environment                ( (short)153 ),
	TransferControlRequest     ( (short)155 ),
	TransferControl            ( (short)156 ),
	TransferControlAcknowledge ( (short)157 ),

	IntercomControl     ( (short)160 ),
	IntercomSignal      ( (short)161 ),

	Aggregate           ( (short)170 );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private PduType( short value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
	{
		return this.value;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public PduType fromValue( short value )
	{
		if( value == EntityState.value )
			return EntityState;
		else if( value == Fire.value )
			return Fire;
		else if( value == Signal.value )
			return Signal;
		else if( value == Transmitter.value )
			return Transmitter;
		else if( value == Detonation.value )
			return Detonation;
		else if( value == Designator.value )
			return Designator;
		else if( value == Receiver.value )
			return Receiver;
		else
		{
			for( PduType type : PduType.values() )
			{
				if( type.value == value )
					return type;
			}
			
			throw new IllegalArgumentException( value+" not a valid PDUType number" );
		}
	}
}
