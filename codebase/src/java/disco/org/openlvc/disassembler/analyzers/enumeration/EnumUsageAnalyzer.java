/*
 *   Copyright 2017 Open LVC Project.
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
package org.openlvc.disassembler.analyzers.enumeration;

import org.apache.logging.log4j.Logger;
import org.openlvc.disassembler.analyzers.IAnalyzer;
import org.openlvc.disassembler.analyzers.IResults;
import org.openlvc.disassembler.configuration.AnalyzerType;
import org.openlvc.disassembler.configuration.Configuration;
import org.openlvc.disco.DiscoException;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.duplicator.SessionReader;

public class EnumUsageAnalyzer implements IAnalyzer
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private EnumUsageConfiguration configuration;
	private Logger logger;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	@Override
	public IResults execute( Configuration genericConfiguration ) throws DiscoException
	{
		// initialize outselves from the configuration
		initialize( genericConfiguration );
		
		// log some startup information
		logger.info( "Analyzing Duplicator session: "+configuration.getInFile().getAbsolutePath() );

		// run the analysis
		SessionReader session = new SessionReader( configuration.getInFile() );
		session.open();
		
		EnumUsageResults resultset = new EnumUsageResults( configuration );
		long startTime = System.currentTimeMillis();
		long pduCount = 0;
		for( PDU pdu : session )
		{
			switch( pdu.getType() )
			{
				case EntityState:
				case Fire:
				case Detonation:
					resultset.add( pdu );
					break;
				default:
					break;
			}
			
			if( ++pduCount % 100000 == 0 )
				logger.info( "Analyzed %,d pdus", pduCount );
		}
		
		resultset.setBenchmarkTime( System.currentTimeMillis()-startTime );
		session.close();
		
		return resultset;
	}
	
	private void initialize( Configuration configuration )
	{
		this.configuration = (EnumUsageConfiguration)configuration;
		this.logger = this.configuration.getDisassemblerLogger();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public AnalyzerType getAnalyzerType()
	{
		return AnalyzerType.EnumUsage;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
