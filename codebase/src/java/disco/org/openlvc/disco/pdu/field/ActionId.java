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
package org.openlvc.disco.pdu.field;

import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.utils.EnumLookup;

public enum ActionId
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other( 0 ),
	LocalStorageOfRequestInformation( 1 ),
	InformRanOutOfAmmunition( 2 ),
	InformKilledInAction( 3 ),
	InformDamage( 4 ),
	InformMobilityDisabled( 5 ),
	InformFireDisabled( 6 ),
	InformRanOutOfFuel( 7 ),
	RecallCheckpointData( 8 ),
	RecallInitialParameters( 9 ),
	InitiateTetherLead( 10 ),
	InitiateTetherFollow( 11 ),
	Untether( 12 ),
	InitiateServiceStationResupply( 13 ),
	InitiateTailgateResupply( 14 ),
	InitiateHitchLead( 15 ),
	InitiateHitchFollow( 16 ),
	Unhitch( 17 ),
	Mount( 18 ),
	Dismount( 19 ),
	StartDrc( 20 ),
	StopDrc( 21 ),
	DataQuery( 22 ),
	StatusRequest( 23 ),
	SendObjectStateData( 24 ),
	Reconstitute( 25 ),
	LockSiteConfiguration( 26 ),
	UnlockSiteConfiguration( 27 ),
	UpdateSiteConfiguration( 28 ),
	QuerySiteConfiguration( 29 ),
	TetheringInformation( 30 ),
	MountIntent( 31 ),
	AcceptSubscription( 33 ),
	Unsubscribe( 34 ),
	TeleportEntity( 35 ),
	ChangeAggregateState( 36 ),
	RequestStartPdu( 37 ),
	WakeupGetReadyForInitialization( 38 ),
	InitializeInternalParameters( 39 ),
	SendPlanData( 40 ),
	SynchronizeInternalClocks( 41 ),
	Run( 42 ),
	SaveInternalParameters( 43 ),
	SimulateMalfunction( 44 ),
	JoinExercise( 45 ),
	ResignExercise( 46 ),
	TimeAdvance( 47 ),
	TaccsfLosRequestType1( 100 ),
	TaccsfLosRequestType2( 101 );
	
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final EnumLookup<ActionId> DISVALUE_LOOKUP = new EnumLookup<>( ActionId.class, 
	                                                                              ActionId::value, 
	                                                                              ActionId.Other );
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private long value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private ActionId( long value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public long value()
	{
		return this.value;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static final int getByteLength()
	{
		return DisSizes.UI32_SIZE;
	}

	public static ActionId fromValue( long value )
	{
		return DISVALUE_LOOKUP.fromValue( value );
	}
}
