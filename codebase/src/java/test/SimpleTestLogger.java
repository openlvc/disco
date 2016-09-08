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
import org.openlvc.disco.common.CommonSetup;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * This class is a TestNG test listener that will print test status information to the
 * console in a manner that is easy to read and can easily be scanned for problems.
 * 
 * By default, no information is logged by TestNG unless the verbosity level is turned up.
 * If you do turn it up by 1 level (from 1 to 2 - out of 10), way too much information is
 * dumped out. I just want to be able to easily scan to see whether tests pass or fail.
 * This listener takes care of that.
 * 
 * Sample report:
 *
 *      testThatHappilyPassed 
 *      testSomethingElseThatPassed
 * FAIL testThatFailed
 * SKIP testThatWasSkipped
 * ...
 *
 */
public class SimpleTestLogger implements ITestListener
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	
	public void onTestStart( ITestResult result )
	{
		String className = result.getTestClass().getRealClass().getSimpleName();
		String methodName = result.getMethod().getMethodName();
		CommonSetup.testStarting( className, methodName );
	}

	public void onTestSuccess( ITestResult result )
	{
		System.out.println( "     " + result.getName() );
	}

	public void onTestFailure( ITestResult result )
	{
		if( result.getName().equals("beforeClass") ||
			result.getName().equals("afterClass") ||
			result.getName().equals("beforeMethod") ||
			result.getName().equals("afterMethod") )
		{
			System.out.println( "FAIL " + result.getName() +
			                    " (" +result.getTestClass().getRealClass().getSimpleName()+ "): " +
			                    result.getThrowable() );
		}
		else
		{
			System.out.println( "FAIL " + result.getName() );
		}
	}

	public void onTestSkipped( ITestResult result )
	{
		System.out.println( "SKIP " + result.getName() +
		                    " (" +result.getTestClass().getRealClass().getSimpleName()+ ")" );

	}

	public void onTestFailedButWithinSuccessPercentage( ITestResult result )
	{
		System.out.println( "     " + result.getName() );
	}

	public void onStart( ITestContext context )
	{
	}

	public void onFinish( ITestContext context )
	{
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
