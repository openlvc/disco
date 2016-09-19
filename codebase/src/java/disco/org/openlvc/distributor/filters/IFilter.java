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
package org.openlvc.distributor.filters;

import org.openlvc.disco.pdu.PDU;
import org.openlvc.distributor.ILink;
import org.openlvc.distributor.Reflector;

/**
 * Defines the interface that PDU filtes must implement. These test a given PDU to see if they
 * meet the conditions to pass the filter. If they do not, they will be potentially bounced.
 * Filters can be arranged into groups which have different assessment syntax (and/or), so whether
 * a PDU is actually filtered depends on all the filters against which it is being applied and the
 * context given to that filter.
 * <p/>
 * In practical terms, unless the configuration is constructed from code, the actual filter
 * definition strings will be inside the configuration file. They are loaded from there as a
 * String which is then passed to the {@link FilterFactory} to turn the string into a
 * {@link FilterGroup} (which may have one or more filters nested inside it). During this process,
 * implementations of this interface are instantiated via the {@link FilterRegistry} which
 * maintains a static set of all the filters that are available.
 * <p/>
 * In code, the form for a single filter declaration is: <code>name operator value</code>. For
 * exmaple: <code>entity.type == 1.1.*.1.2.3.4</code>. The name (<code>entity.type</code>) is
 * looked up in the registry, returning an implementing class to which the operator <code>==</code>
 * and value <code>1.1.*.1.2.3.4</code> is passed, configuring the filter. This is all loaded into
 * a {@link FilterGroup} with others (including the and/or information) and returned.
 * <p/>
 * At runtime, each of the {@link ILink}'s will apply filtering to their incoming messages to
 * prevent them from being offered for reflection, and the {@link Reflector} will also apply
 * outgoing filters to prevent PDUs from passing to certain links.
 * <p/>
 * Phew! That is about it.
 * 
 * @see FilterFactory
 * @see FilterRegistry
 */
public interface IFilter
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * Return <code>true</code> if the given PDU matches the condition set in this filter.
	 */
	public boolean matches( PDU pdu );

	/**
	 * Essentially <code>toString()</code> for a filter. Returns the query string that the
	 * filter encapsultates.
	 */
	public String getFilterString();
}
