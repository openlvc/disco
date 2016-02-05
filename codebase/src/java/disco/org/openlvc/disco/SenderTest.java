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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.pdu.PDU;
import org.openlvc.disco.pdu.entity.EntityStatePdu;

public class SenderTest implements IPduListener
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private List<EntityStatePdu> entitiyList;
	private int entityCount;
	private int loopCount;
	
	private Timer timer;
	private AtomicLong sent;
	private long startTime;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SenderTest()
	{
		this.entityCount = 50000;
		this.loopCount = 100;
		this.sent = new AtomicLong( 0 );
		this.startTime = 0;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void run( String[] args ) throws Exception
	{
		DiscoConfiguration configuration = new DiscoConfiguration();
		//configuration.getNetworkConfiguration().setAddress( "239.1.2.3" );
		configuration.getUdpConfiguration().setPort( 3000 );
		configuration.getUdpConfiguration().setNetworkInterface( "LINK_LOCAL" );

		this.entitiyList = new ArrayList<EntityStatePdu>();
		
		OpsCenter opscenter = new OpsCenter( configuration );
		opscenter.setListener( this );
		opscenter.open();
		//opscenter.close();

		// pre-cretae all the objects
		for( int i = 0; i < entityCount; i++ )
		{
			EntityStatePdu pdu = new EntityStatePdu();
			pdu.setEntityID( 1, 1, 1 );
			pdu.setMarking( "Entity"+i );
			entitiyList.add( pdu );
		}
		
		// start a timer to print progress
		this.startTime = System.currentTimeMillis();
		this.timer = new Timer();
		this.timer.schedule( new Task(), 0, 1000 );
		for( int i = 0; i < loopCount; i++ )
		{
			System.out.println( "Completed "+i+" iterations" );
			for( EntityStatePdu pdu : entitiyList )
			{
				opscenter.send( pdu );
				sent.incrementAndGet();
			}
		}
		
		this.timer.cancel();
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void receiver( PDU pdu )
	{
		// no-op
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args ) throws Exception
	{
		new SenderTest().run( args );
	}
	
	private class Task extends TimerTask
	{
		public void run()
		{
			long time = System.currentTimeMillis();
			long duration = time - startTime;
			long count = sent.get();
			double processed = (double)count / (duration/1000.0);
			String processedString = String.format( "%.0f", processed );
			System.out.println( "Processed: "+count+", Per-Sec: "+processedString+"/s Duration: "+duration+"ms" );
		}
	}

}
