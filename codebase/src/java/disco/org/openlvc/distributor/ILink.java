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

import org.openlvc.disco.pdu.PDU;
import org.openlvc.distributor.configuration.LinkConfiguration;
import org.openlvc.distributor.filters.FilterGroup;

/**
 * Represents a links to a particular site/network.
 * 
 * Links are purely logical constructors. Just a name used to tag a configuration set.
 * Links represent a conncetion that the Distributor will attempt to bring online. It will
 * take input from the link and reflect it back out to all others, subject to any defined
 * filtering rules.
 */
public interface ILink
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	// Properties
	public String getName();
	public LinkConfiguration getConfiguration();
	public String getLinkStatus();
	public boolean isUp();
	public boolean isDown();

	// Lifecycle
	public void up();      // bring the connection online
	public void down();    // close the connection

	// Transient links are not persisted in distributor after they are taken down
	// This is done primarily to support Relay created links
	public void setTransient( boolean isTransient );
	public boolean isTransient();
	
	// Message Passing
	public void setReflector( Reflector reflector );
	public void reflect( Message message );
	
	/** Get some short (one-line) status summary information */
	public String getConfigSummary();
	public String getStatusSummary();

	// Filtering
	/**
	 * Inbound filtering: <code>network -> reflector</code><p/>
	 * Defines which messages are forwarded to the reflector.
	 */
	public void setInboundFilter( FilterGroup filterGroup );

	/**
	 * Outbound filtering: <code>reflector -> network</code><p/>
	 * Defines which messages the reflector has that it should forward to us. If a message
	 * does not pass this filter, the reflector does not give it to us (but may give it to
	 * others).
	 */
	public void setOutboundFilter( FilterGroup filterGroup );

	/** Return true if the PDU passes inbound filtering and should be passed to reflector */
	public boolean passesInboundFilter( PDU pdu );
	
	/** Return true if the PDU passes outbound filtering and should be passed to us by reflector */
	public boolean passesOutboundFilter( PDU pdu );
	
	public FilterGroup getInboundFilter();
	public FilterGroup getOutboundFilter();
	public boolean isInboundFiltering();
	public boolean isOutboundFiltering();
	
}
