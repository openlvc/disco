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
package org.openlvc.disco.connection.rpr.custom.dcss.mappers;

import java.util.Arrays;
import java.util.Collection;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.bus.EventHandler;
import org.openlvc.disco.connection.rpr.custom.dcss.objects.WallclockTime;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.HlaDiscover;
import org.openlvc.disco.connection.rpr.mappers.HlaReflect;
import org.openlvc.disco.connection.rpr.model.AttributeClass;
import org.openlvc.disco.connection.rpr.model.ObjectClass;
import org.openlvc.disco.connection.rpr.model.ObjectModel;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;

public class WallclockTimeMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final String HLA_CLASS_NAME = "HLAobjectRoot.DCSSBaseObject.WallclockTime";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private ObjectClass hlaClass;
	private AttributeClass zuluTime;
	private AttributeClass simulationTime;
	private AttributeClass simulationElapsedTime;
	private AttributeClass scalingFactor;
	private AttributeClass simulationStartTime;
	private AttributeClass clockState;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA Initialization   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void initialize() throws DiscoException
	{
		ObjectModel fom = rprConnection.getFom();
		this.hlaClass = fom.getObjectClass( HLA_CLASS_NAME );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find class: "+HLA_CLASS_NAME );
		
		this.zuluTime = this.hlaClass.getAttribute( "ZuluTime" );
		this.simulationTime = this.hlaClass.getAttribute( "SimulationTime" );
		this.simulationElapsedTime = this.hlaClass.getAttribute( "SimulationElapsedTime" );
		this.scalingFactor = this.hlaClass.getAttribute( "ScalingFactor" );
		this.simulationStartTime = this.hlaClass.getAttribute( "SimulationStartTime" );
		this.clockState = this.hlaClass.getAttribute( "ClockState" );
		
		// Do publication and subscription
		super.publishAndSubscribe( hlaClass );
	}
	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.DcssWallclockTime );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleDiscover( HlaDiscover event )
	{
		if( hlaClass == event.theClass )
		{
			WallclockTime hlaObject = new WallclockTime();
			hlaObject.setObjectClass( event.theClass );
			hlaObject.setObjectHandle( event.theObject );
			objectStore.addDiscoveredHlaObject( hlaObject );

			if( logger.isDebugEnabled() )
			{
				logger.debug( "hla >> dis (Discover) Created [%s] for discovery of object handle [%s]",
				              event.theClass.getLocalName(), 
				              event.theObject );
			}
			
			// Request an attribute update for the object so that we can get everything we need
			super.requestAttributeUpdate( hlaObject );
		}
	}

	@EventHandler
	public void handleReflect( HlaReflect event )
	{
		if( (event.hlaObject instanceof WallclockTime) == false )
			return;
		
		try
		{
			// Update the local object representation from the received attributes
			deserializeFromHla( (WallclockTime)event.hlaObject, event.attributes );
		}
		catch( DecoderException de )
		{
			throw new DiscoException( de.getMessage(), de );
		}
		
		// Send the PDU off to the OpsCenter
		// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
		//         on the other side. This is inefficient and distasteful. Fix me.
		opscenter.getPduReceiver().receive( event.hlaObject.toPdu().toByteArray() );
	}
	
	private void deserializeFromHla( WallclockTime time, AttributeHandleValueMap map )
		throws DecoderException
	{
		if( map.containsKey(zuluTime.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(zuluTime.getHandle()) );
			time.getZuluTime().decode( wrapper );
		}
		
		if( map.containsKey(simulationTime.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(simulationTime.getHandle()) );
			time.getSimulationTime().decode( wrapper );
		}
		
		if( map.containsKey(simulationElapsedTime.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(simulationElapsedTime.getHandle()) );
			time.getSimulationElapsedTime().decode( wrapper );
		}
		
		if( map.containsKey(scalingFactor.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(scalingFactor.getHandle()) );
			time.getScalingFactor().decode( wrapper );
		}
		
		if( map.containsKey(simulationStartTime.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(simulationStartTime.getHandle()) );
			time.getSimulationStartTime().decode( wrapper );
		}
		
		if( map.containsKey(clockState.getHandle()) )
		{
			ByteWrapper wrapper = new ByteWrapper( map.get(clockState.getHandle()) );
			time.getClockState().decode( wrapper );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
