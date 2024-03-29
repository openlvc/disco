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
package org.openlvc.disco;

import java.util.function.BooleanSupplier;

import org.openlvc.disco.common.CommonSetup;
import org.openlvc.disco.common.TestPduListener;
import org.openlvc.disco.common.TimeoutException;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.testng.Assert;

public class AbstractTest
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final String CUSTOM_MODULE_PATH = "resources/testdata/hla/Custom-FOM-Module.xml";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	protected OpsCenter newOpsCenter()
	{
		DiscoConfiguration configuration = new DiscoConfiguration();
		configuration.getLoggingConfiguration().setLevel( CommonSetup.CONSOLE_LOG_LEVEL );
		
		OpsCenter opscenter = new OpsCenter( configuration );
		opscenter.setPduListener( new TestPduListener() );
		return opscenter;
	}

	/**
	 * Create a new {@link OpsCenter} using the given configuration and a default
	 * {@link TestPduListener} and return it.
	 * 
	 * @param configuration The configuration to use
	 * @return An initialized OpsCenter instance using the given configuration
	 */
	protected OpsCenter newOpsCenter( DiscoConfiguration configuration )
	{
		// force the log level to what the test suite wants it to be
		configuration.getLoggingConfiguration().setLevel( CommonSetup.CONSOLE_LOG_LEVEL );

		// create and return the opscenter
		OpsCenter opscenter = new OpsCenter( configuration );
		opscenter.setPduListener( new TestPduListener() );
		return opscenter;
	}

	
	/**
	 * <p>Run the given lamba, which is just a wrapper for a basic method call.
	 * Make sure we receive an exception. If we don't get an exception, or we
	 * do - but it isn't of any of the times in the provided list of valid exceptions,
	 * then we'll fail the test.</p>
	 * 
	 * <p>If we do get an exception and it is in the list, we'll return happily</p> 
	 * If we do, and it is in the list of valid
	 *  
	 * @param lambda Wrapper for anonymous function to execute
	 * @param validExceptions The list of exceptions we deem as valid
	 */
	protected void assertException( VoidMethod lambda, Class<?>... validExceptions )
	{
		Exception exception = null;
		try
		{
			lambda.operation();
		}
		catch( Exception e )
		{
			// make sure this is what we want
			for( Class<?> possible : validExceptions )
			{
				if( possible.isInstance(e) )
					return;
			}
			
			exception = e;
		}
		
		// If we get here, the method was either a success (bad), or there was an exception
		// but it didn't match any of the ones we wanted. Note it.
		
		StringBuilder builder = new StringBuilder();
		for( int i = 0; i < validExceptions.length; i++ )
		{
			builder.append( validExceptions[i].getName() );
			if( i < validExceptions.length-1 )
				builder.append( ", " );
		}

		if( exception == null )
		{
			Assert.fail( "No exception when we expected one of: "+builder.toString() );
		}
		else
		{
			Assert.fail( "Wrong Exception. Received ["+exception.getClass().getSimpleName()+
			             "], expected: "+builder.toString(), exception );
		}
	}

	/**
	 * Wait up to the given milliseconds for the supplier to return true. To use this, you can
	 * pass in a lambda that returns a boolean. For example:
	 * <p/>
	 * <pre>waitFor( () -> {return somethingd(); }, millis );</pre>
	 * 
	 * <p/>
	 * This method will try up to 10 times, sleeping for 1/10th of the millis value each attempt
	 * 
	 * 
	 * @param supplier The supplier to keep checking against
	 * @param millis How long we should wait
	 * @throws TimeoutException If the supplier doesn't return true before time expires
	 */
	protected void waitFor( BooleanSupplier supplier, long millis ) throws TimeoutException
	{
		long chunk = millis/10;
		while( chunk > 0 )
		{
			if( supplier.getAsBoolean() )
				return;

			// was false, wait a bit
			--chunk;
			snooze( chunk );
		}
		
		throw new TimeoutException( "Waiting too long for condition to be true" );
	}

	/**
	 * Same as {@link #waitFor(BooleanSupplier, long)} but with a default wait time (2 seconds).
	 * @param supplier The supplier to check against
	 */
	protected void waitFor( BooleanSupplier supplier )
	{
		waitFor( supplier, 2000 );
	}
	
	protected void snooze( long millis )
	{
		try
		{
			Thread.sleep( millis );
		}
		catch( InterruptedException ie )
		{
			// no-op, just return
		}
	}


	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
