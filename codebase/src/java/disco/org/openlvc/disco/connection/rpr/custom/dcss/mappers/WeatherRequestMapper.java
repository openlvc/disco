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
import org.openlvc.disco.connection.rpr.custom.dcss.interactions.WeatherRequestLocation;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.pdu.custom.DcssWeatherRequestPdu;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.ParameterHandleValueMap;

public class WeatherRequestMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private InteractionClass hlaClass;
	private ParameterClass instanceId;
	private ParameterClass weatherData;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	@Override
	protected void initialize() throws DiscoException
	{
		this.hlaClass = rprConnection.getFom().getInteractionClass( "HLAinteractionRoot.Service.WeatherBase.WeatherRequestLocation" );
		if( this.hlaClass == null )
			throw new DiscoException( "Could not find interaction: HLAinteractionRoot.Service.WeatherBase.WeatherRequestLocation" );

		this.instanceId     = hlaClass.getParameter( "InstanceID" );
		this.weatherData    = hlaClass.getParameter( "WeatherData" );
		
		// Publish and Subscribe
		// Note: We really only publish this
		super.publishAndSubscribe( hlaClass );
	}

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.DcssWeatherRequest );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// DIS -> HLA Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handlePdu( DcssWeatherRequestPdu pdu )
	{
		// Populate the Interaction
		WeatherRequestLocation interaction = new WeatherRequestLocation();
		interaction.setInteractionClass( hlaClass );
		interaction.fromPdu( pdu );

		ParameterHandleValueMap map = this.createParameters( this.hlaClass );
		interaction.setParameters( map );
		
		serializeInto( interaction.getInstanceId(), instanceId, map );
		serializeInto( interaction.getWeatherData(), weatherData, map );
		
		// Send the interaction
		super.sendInteraction( interaction, interaction.getParameters() );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
