/*
 *   Copyright 2015 Calytrix Technologies
 *
 *   This file is part of Calytrix LVC Cost Counter
 *
 *   NOTICE:  All information contained herein is, and remains
 *            the property of Calytrix Technologies Pty Ltd.
 *            The intellectual and technical concepts contained
 *            herein are proprietary to Calytrix Technologies Pty Ltd.
 *            Dissemination of this information or reproduction of
 *            this material is strictly forbidden unless prior written
 *            permission is obtained from Calytrix Technologies Pty Ltd.
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */
import org.openlvc.disco.configuration.Log4jConfiguration;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * This class contains project-wide common test setup logic. Before
 * any TestNG suite is run, the methods here are called.
 */
public class CommonSetup
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	/** The root directory from which any files that need to be loaded should be prefixed */
	public static final String TEST_ROOT_DIR = System.getProperty( "test.root.dir" );

	// System properties that will control the log level used for testing
	//private static final String TEST_LOG_LEVEL = System.getProperty( "test.loglevel", "OFF" );	
	//private static final String FILE_LOG_LEVEL = System.getProperty( "test.fileLogLevel","no" );
	
	// set during commonBeforeSuiteSetup()
	private static String CONSOLE_LOG_LEVEL = "OFF";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	@BeforeSuite(alwaysRun=true)
	public static void commonBeforeSuiteSetup()
	{
		///////////////////////////////////////////////////////////////////////////////
		// set the global log level based off the level provided on the command line //
		///////////////////////////////////////////////////////////////////////////////
		// set the log level if it has been specified in the system properties
		CONSOLE_LOG_LEVEL = System.getProperty( "test.loglevel", "OFF" );
		if( CONSOLE_LOG_LEVEL.equals("${test.loglevel}") )
			CONSOLE_LOG_LEVEL = "OFF";
		
		Log4jConfiguration logConfiguration = new Log4jConfiguration( "disco.test" );
		logConfiguration.activateConfiguration();

		/////////////////////////////////////////
		// project specific pre-test run setup //
		/////////////////////////////////////////
	}

	@AfterSuite(alwaysRun=true)
	public static void commonAfterSuiteCleanup()
	{

	}

	/**
	 * If file-based logging is turned on, this method will redirect output for
	 * each individual test into a separate log file so that you can easily locate
	 * tfhe logging caused by one test method even if you have run multiples.
	 */
	public static void testStarting( String className, String methodName )
	{
	}
}

