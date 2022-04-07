package org.openlvc.disco.connection.rpr.custom.dcss.types.fixed;

import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.PrecipitationType;
import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.fixed.WrappedHlaFixedRecord;

public class CloudResponseData extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAfloat32BE totalCloudCover;
	private HLAfloat32BE cloudCeiling;
	private CloudLayer highCloud;
	private CloudLayer midCloud;
	private CloudLayer lowCloud;
	private CloudLayer convecCloud;
	private HLAfloat32BE precipitationRate;
	private HLAfloat32BE convecPrecipitationRate;
	private EnumHolder<PrecipitationType> precipitationType;
	private HLAfloat32BE lightning;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public CloudResponseData()
	{
		this.totalCloudCover = new HLAfloat32BE();
		this.cloudCeiling = new HLAfloat32BE();
		this.highCloud = new CloudLayer();
		this.midCloud = new CloudLayer();
		this.lowCloud = new CloudLayer();
		this.convecCloud = new CloudLayer();
		this.precipitationRate = new HLAfloat32BE();
		this.convecPrecipitationRate = new HLAfloat32BE();
		this.precipitationType = new EnumHolder<>( PrecipitationType.InvalidPrecipitationType );
		this.lightning = new HLAfloat32BE();
		
		this.add( this.totalCloudCover,
		          this.cloudCeiling,
		          this.highCloud,
		          this.midCloud,
		          this.lowCloud,
		          this.convecCloud,
		          this.precipitationRate,
		          this.convecPrecipitationRate,
		          this.precipitationType,
		          this.lightning );
	}
	
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAfloat32BE getTotalCloudCover() { return this.totalCloudCover; }
	public HLAfloat32BE getCloudCeiling() { return this.cloudCeiling; }
	public CloudLayer getHighCloud() { return this.highCloud; }
	public CloudLayer getMidCloud() { return this.midCloud; }
	public CloudLayer getLowCloud() { return this.lowCloud; }
	public CloudLayer getConvecCloud() { return this.convecCloud; }
	public HLAfloat32BE getPrecipitationRate() { return this.precipitationRate; }
	public HLAfloat32BE getConvecPrecipitationRate() { return this.convecPrecipitationRate; }
	public EnumHolder<PrecipitationType> getPrecipitationType() { return this.precipitationType; }
	public HLAfloat32BE getLightning() { return this.lightning; }
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
