/*
 *   Copyright 2016 Open LVC Project.
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
package org.openlvc.distributor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.distributor.configuration.LinkConfiguration;
import org.openlvc.distributor.filters.FilterFactory;
import org.openlvc.distributor.filters.FilterGroup;

/**
 * Base class for all {@link ILink} implementations. Manages the tasks and tracking that is
 * common across all links. This includes caching of configuration, logging, link status,
 * transient status, inbound/outbound filtering, etc...
 */
public abstract class LinkBase
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected LinkConfiguration linkConfiguration;
	protected Logger logger;
	protected boolean linkUp;
	protected boolean isTransient;
	
	protected FilterGroup inboundFilter;
	protected FilterGroup outboundFilter;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public LinkBase( LinkConfiguration linkConfiguration )
	{
		this.linkConfiguration = linkConfiguration;
		this.logger = LogManager.getFormatterLogger( "distributor."+linkConfiguration.getName() );
		this.linkUp = false;
		this.isTransient = false;
		
		// pull the filter information out of the config
		this.inboundFilter = null;
		this.outboundFilter = null;
		if( linkConfiguration.isInboundFiltering() )
		{
			this.inboundFilter = FilterFactory.parse( linkConfiguration.getInboundFilter() );
			this.logger.debug( "Inbound Filtering: "+inboundFilter );
		}
		else
		{
			this.logger.debug( "Inbound Filters: <none>" );
		}
		
		if( linkConfiguration.isOutboundFiltering() )
		{
			this.outboundFilter = FilterFactory.parse( linkConfiguration.getOutboundFilter() );
			this.logger.debug( "Outbound Filtering: "+outboundFilter );
		}
		else
		{
			this.logger.debug( "Outbound Filters: <none>" );
		}
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Abstract Methods   /////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public abstract void up();
	public abstract void down();
	
	public abstract void setReflector( Reflector exchange );
	public abstract void reflect( Message message );
	public abstract String getStatusSummary();

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Filtering Methods   ////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/** Return true if the PDU passes inbound filtering and should be passed to reflector */
	public boolean passesInboundFilter( PDU pdu )
	{
		return inboundFilter == null ? true : inboundFilter.matches(pdu);
	}
	
	/** Return true if the PDU passes outbound filtering and should be passed to us by reflector */
	public boolean passesOutboundFilter( PDU pdu )
	{
		return outboundFilter == null ? true : outboundFilter.matches(pdu);
	}


	/**
	 * Inbound filtering: <code>network -> reflector</code><p/>
	 * Defines which messages are forwarded to the reflector.
	 */
	public void setInboundFilter( FilterGroup filterGroup ) { this.inboundFilter = filterGroup; }

	/**
	 * Outbound filtering: <code>reflector -> network</code><p/>
	 * Defines which messages the reflector will forward to us.
	 */
	public void setOutboundFilter( FilterGroup filterGroup ) { this.outboundFilter = filterGroup; }
	
	public FilterGroup getInboundFilter()   { return this.inboundFilter; }
	public FilterGroup getOutboundFilter()  { return this.outboundFilter; }
	public boolean isInboundFiltering()     { return inboundFilter != null; }
	public boolean isOutboundFiltering()    { return outboundFilter != null; }
	protected String getInboundFilterDesc() { return inboundFilter == null ? "<none>" : inboundFilter.toString(); }
	protected String getOutboundFilterDesc(){ return outboundFilter == null ? "<none>" : outboundFilter.toString(); }
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getName()
	{
		return linkConfiguration.getName();
	}
	
	public LinkConfiguration getConfiguration()
	{
		return this.linkConfiguration;
	}
	
	public boolean isUp()
	{
		return this.linkUp;
	}
	
	public boolean isDown()
	{
		return this.linkUp == false;
	}
	
	public String getLinkStatus()
	{
		return this.linkUp ? "up" : "down";
	}
	
	public void setTransient( boolean isTransient )
	{
		this.isTransient = isTransient;
	}
	
	public boolean isTransient()
	{
		return this.isTransient;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
