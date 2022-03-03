package org.openlvc.disco.connection.rpr.custom.dcss.types.fixed;

import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.connection.rpr.types.fixed.WrappedHlaFixedRecord;

public class SubsurfaceResponseData extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAfloat32BE temperature;
	private HLAfloat32BE salinity;
	private HLAfloat32BE currentSpeed;
	private HLAfloat32BE currentDirection;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SubsurfaceResponseData()
	{
		this.temperature = new HLAfloat32BE();
		this.salinity = new HLAfloat32BE();
		this.currentSpeed = new HLAfloat32BE();
		this.currentDirection = new HLAfloat32BE();
		
		this.add( this.temperature,
		          this.salinity,
		          this.currentSpeed,
		          this.currentDirection );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	
	public HLAfloat32BE getTemperature() { return this.temperature; }
	public HLAfloat32BE getSalinity() { return this.salinity; }
	public HLAfloat32BE getCurrentSpeed() { return this.currentSpeed; }
	public HLAfloat32BE getCurrentDirection() { return this.currentDirection; }
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
