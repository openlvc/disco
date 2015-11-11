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
package org.openlvc.disco;

/**
 * The Source class is a generic representation of some medium that PDU's can be drawn
 * from and sent to. Whether that is a network connection, file, database or what does
 * not matter.
 */
public interface IDatasource
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public String getName();

	/**
	 * Configure the provider as it is being deployed into the given {@link OpsCenter}.
	 */
	public void configure( OpsCenter opscenter ) throws DiscoException;
	
	/**
	 * Open a connection to this provider and start it receiving.
	 * 
	 * This method cannot block.
	 */
	public void open() throws DiscoException;

	/**
	 * Close out the connection to this provider.
	 */
	public void close() throws DiscoException;

}
