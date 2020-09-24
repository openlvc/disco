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
import org.openlvc.disco.connection.rpr.custom.dcss.interactions.WeatherResponse;
import org.openlvc.disco.connection.rpr.interactions.InteractionInstance;
import org.openlvc.disco.connection.rpr.mappers.AbstractMapper;
import org.openlvc.disco.connection.rpr.mappers.HlaInteraction;
import org.openlvc.disco.connection.rpr.model.InteractionClass;
import org.openlvc.disco.connection.rpr.model.ParameterClass;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.field.PduType;

import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.DecoderException;

public class WeatherResponseMapper extends AbstractMapper
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private InteractionClass weatherResponseClass;
	private ParameterClass responseData;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	private InteractionClass tryGetInteractionClass( String name )
	{
		InteractionClass ic = rprConnection.getFom().getInteractionClass( name );
		if( ic == null )
			throw new DiscoException( "Could not find interaction: "+name );
		
		return ic;
	}
	
	@Override
	protected void initialize() throws DiscoException
	{
		this.weatherResponseClass = tryGetInteractionClass( "HLAinteractionRoot.Service.WeatherBase.WeatherResponse" );
		
		// Named InstanceID in the request, InstanceId in the response :(
		this.responseData          = weatherResponseClass.getParameter( "ResponseData" ); 
		
		// Publish and Subscribe
		// NOTE: We really only subscribe to this
		this.publishAndSubscribe( this.weatherResponseClass );
	}

	@Override
	public Collection<PduType> getSupportedPdus()
	{
		return Arrays.asList( PduType.DcssWeatherResponse );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// HLA -> DIS Methods   ///////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void handleInteraction( HlaInteraction event )
	{
		InteractionInstance interaction = null;
		
		try
		{
			if( event.theClass == this.weatherResponseClass )
				interaction = this.deserializeWeatherResponseFromHla( event.parameters );
		}
		catch( DecoderException de )
		{
			throw new DiscoException( de.getMessage(), de );
		}
		
		if( interaction != null )
		{
			// Send the PDU off to the OpsCenter
			// FIXME - We serialize it to a byte[], but it will be turned back into a PDU
			//         on the other side. This is inefficient and distasteful. Fix me.
			PDU pdu = interaction.toPdu();
			opscenter.getPduReceiver().receive( pdu.toByteArray() );
		}
	}
	
	private WeatherResponse deserializeWeatherResponseFromHla( ParameterHandleValueMap map )
		throws DecoderException
	{
		WeatherResponse interaction = new WeatherResponse();
		deserializeInto( map, responseData, interaction.getResponseData() );
		
		return interaction;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
