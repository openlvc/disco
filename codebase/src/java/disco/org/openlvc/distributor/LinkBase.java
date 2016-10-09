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
	
	protected FilterGroup receiveFilter;
	protected FilterGroup sendFilter;

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
		this.receiveFilter = null;
		this.sendFilter = null;
		if( linkConfiguration.isReceiveFiltering() )
			this.receiveFilter = FilterFactory.parse( linkConfiguration.getReceiveFilter() );
		
		if( linkConfiguration.isSendFiltering() )
			this.sendFilter = FilterFactory.parse( linkConfiguration.getSendFilter() );
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
	/** Return true if the PDU passes receive filtering and should be passed to reflector */
	public boolean passesReceiveFilter( PDU pdu )
	{
		return receiveFilter == null ? true : receiveFilter.matches(pdu);
	}
	
	/** Return true if the PDU passes send filtering and should be passed to us by reflector */
	public boolean passesSendFilter( PDU pdu )
	{
		return sendFilter == null ? true : sendFilter.matches(pdu);
	}


	/**
	 * Inbound filtering: <code>network -> reflector</code><p/>
	 * Defines which messages are forwarded to the reflector.
	 */
	public void setReceiveFilter( FilterGroup filterGroup ) { this.receiveFilter = filterGroup; }

	/**
	 * Outbound filtering: <code>reflector -> network</code><p/>
	 * Defines which messages the reflector will forward to us.
	 */
	public void setSendFilter( FilterGroup filterGroup ) { this.sendFilter = filterGroup; }
	
	public FilterGroup getReceiveFilter()   { return this.receiveFilter; }
	public FilterGroup getSendFilter()      { return this.sendFilter; }
	public boolean isReceiveFiltering()     { return receiveFilter != null; }
	public boolean isSendFiltering()        { return sendFilter != null; }
	protected String getReceiveFilterDesc() { return receiveFilter == null ? "<none>" : receiveFilter.toString(); }
	protected String getSendFilterDesc()    { return sendFilter == null ? "<none>" : sendFilter.toString(); }
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public String getName()
	{
		return linkConfiguration.getName();
	}
	
	@Override
	public String toString()
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
