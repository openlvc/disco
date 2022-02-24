package org.openlvc.disco.connection.rpr.custom.dcss.types.fixed;

import org.openlvc.disco.connection.rpr.custom.dcss.types.enumerated.CloudClassification;
import org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.fixed.WrappedHlaFixedRecord;

public class CloudLayer extends WrappedHlaFixedRecord
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private HLAfloat32BE top;
	private HLAfloat32BE base;
	private HLAfloat32BE coverage;
	private EnumHolder<CloudClassification> type;
	
	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public CloudLayer()
	{
		this.top = new HLAfloat32BE();
		this.base = new HLAfloat32BE();
		this.coverage = new HLAfloat32BE();
		this.type = new EnumHolder<>( CloudClassification.InvalidCloudClassification );
		
		this.add( this.top,
		          this.base,
		          this.coverage,
		          this.type );
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public HLAfloat32BE getTop() { return this.top; }
	public HLAfloat32BE getBase() { return this.base; }
	public HLAfloat32BE getCoverage() { return this.coverage; }
	public EnumHolder<CloudClassification> getType() { return this.type; }
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
