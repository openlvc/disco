package org.openlvc.disco.connection.rpr.custom.dcss.types.fixed;

import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.connection.rpr.types.fixed.WrappedHlaFixedRecord;

public class SurfaceResponseData extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAfloat32BE windDirection;
	private HLAfloat32BE windSpeed;
	private HLAfloat32BE totalWaveHeight;
	private HLAfloat32BE windWaveHeight;
	private HLAfloat32BE windWavePeriod;
	private HLAfloat32BE windWaveDirection;
	private HLAfloat32BE primaryWavePeriod;
	private HLAfloat32BE primaryWaveDirection;
	private HLAfloat32BE swellHeight;
	private HLAfloat32BE swellPeriod;
	private HLAfloat32BE swellDirection;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public SurfaceResponseData()
	{
		this.windDirection = new HLAfloat32BE();
		this.windSpeed = new HLAfloat32BE();
		this.totalWaveHeight = new HLAfloat32BE();
		this.windWaveHeight = new HLAfloat32BE();
		this.windWavePeriod = new HLAfloat32BE();
		this.windWaveDirection = new HLAfloat32BE();
		this.primaryWavePeriod = new HLAfloat32BE();
		this.primaryWaveDirection = new HLAfloat32BE();
		this.swellHeight = new HLAfloat32BE();
		this.swellPeriod = new HLAfloat32BE();
		this.swellDirection = new HLAfloat32BE();
		
		this.add( this.windDirection,
		          this.windSpeed,
		          this.totalWaveHeight,
		          this.windWaveHeight,
		          this.windWavePeriod,
		          this.windWaveDirection,
		          this.primaryWavePeriod,
		          this.primaryWaveDirection,
		          this.swellHeight,
		          this.swellPeriod,
		          this.swellDirection );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAfloat32BE getWindDirection() { return this.windDirection; }
	public HLAfloat32BE getWindSpeed() { return this.windSpeed; }
	public HLAfloat32BE getTotalWaveHeight() { return this.totalWaveHeight; }
	public HLAfloat32BE getWindWaveHeight() { return this.windWaveHeight; }
	public HLAfloat32BE getWindWavePeriod() { return this.windWavePeriod; }
	public HLAfloat32BE getWindWaveDirection() { return this.windWaveDirection; }
	public HLAfloat32BE getPrimaryWavePeriod() { return this.primaryWaveDirection; }
	public HLAfloat32BE getPrimaryWaveDirection() { return this.primaryWaveDirection; }
	public HLAfloat32BE getSwellHeight() { return this.swellHeight; }
	public HLAfloat32BE getSwellPeriod() { return this.swellPeriod; }
	public HLAfloat32BE getSwellDirection() { return this.swellDirection; }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
