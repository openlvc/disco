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
package org.openlvc.disco.utils;

public enum ByteUnit
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	BYTES
	{
		public long   toBytes(long value)      { return value; }
		public double toKilobytes(long value)  { return value / ONE; } 
		public double toMegabytes(long value)  { return value / TWO; } 
		public double toGigabytes(long value)  { return value / THREE; } 
		public double toTerabytes(long value)  { return value / FOUR; }

		public long   toBytes(double value)    { return (long)(value); }
		public long   toKilobytes(double value){ return (long)(value/ONE); }
		public long   toMegabytes(double value){ return (long)(value/TWO); } 
		public long   toGigabytes(double value){ return (long)(value/THREE); } 
		public long   toTerabytes(double value){ return (long)(value/FOUR); } 
	},
	
	KILOBYTES
	{
		public long   toBytes(long value)      { return value * (long)ONE; }
		public double toKilobytes(long value)  { return value; }
		public double toMegabytes(long value)  { return value / ONE; } 
		public double toGigabytes(long value)  { return value / TWO; } 
		public double toTerabytes(long value)  { return value / THREE; } 

		public long   toBytes(double value)    { return (long)(value * ONE); }
		public long   toKilobytes(double value){ return (long)value; }
		public long   toMegabytes(double value){ return (long)(value/ONE); } 
		public long   toGigabytes(double value){ return (long)(value/TWO); } 
		public long   toTerabytes(double value){ return (long)(value/THREE); } 
	},
	
	MEGABYTES
	{
		public long   toBytes(long value)      { return value * (long)TWO; }
		public double toKilobytes(long value)  { return value * (long)ONE; } 
		public double toMegabytes(long value)  { return value; } 
		public double toGigabytes(long value)  { return value / ONE; } 
		public double toTerabytes(long value)  { return value / TWO; } 

		public long   toBytes(double value)    { return (long)(value * TWO); }
		public long   toKilobytes(double value){ return (long)(value * ONE); }
		public long   toMegabytes(double value){ return (long)(value); } 
		public long   toGigabytes(double value){ return (long)(value/ONE); } 
		public long   toTerabytes(double value){ return (long)(value/TWO); } 
	},
	
	GIGABYTES
	{
		public long   toBytes(long value)      { return value * (long)THREE; }
		public double toKilobytes(long value)  { return value * (long)TWO; } 
		public double toMegabytes(long value)  { return value * (long)ONE; } 
		public double toGigabytes(long value)  { return value; } 
		public double toTerabytes(long value)  { return value / ONE; } 

		public long   toBytes(double value)    { return (long)(value * THREE); }
		public long   toKilobytes(double value){ return (long)(value * TWO); }
		public long   toMegabytes(double value){ return (long)(value * ONE); } 
		public long   toGigabytes(double value){ return (long)(value); } 
		public long   toTerabytes(double value){ return (long)(value/ONE); } 
	},
	
	TERABYTES
	{
		public long   toBytes(long value)      { return value * (long)FOUR; }
		public double toKilobytes(long value)  { return value * (long)THREE; } 
		public double toMegabytes(long value)  { return value * (long)TWO; } 
		public double toGigabytes(long value)  { return value * (long)ONE; } 
		public double toTerabytes(long value)  { return value; } 

		public long   toBytes(double value)    { return (long)(value * FOUR); }
		public long   toKilobytes(double value){ return (long)(value * THREE); }
		public long   toMegabytes(double value){ return (long)(value * TWO); } 
		public long   toGigabytes(double value){ return (long)(value / ONE); } 
		public long   toTerabytes(double value){ return (long)(value); } 
	};

	// Constants
	private static final double ONE    = 1000.0d;
	private static final double TWO    = 1000000.0d;
	private static final double THREE  = 1000000000000.0d;
	private static final double FOUR   = 1000000000000000000000000.0d;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public long   toBytes(long value)      { throw new AbstractMethodError(); }
	public double toKilobytes(long value)  { throw new AbstractMethodError(); } 
	public double toMegabytes(long value)  { throw new AbstractMethodError(); }
	public double toGigabytes(long value)  { throw new AbstractMethodError(); }
	public double toTerabytes(long value)  { throw new AbstractMethodError(); }
	public String toString(long value)     { return StringUtils.humanReadableSize(toBytes(value)); }

	public long   toBytes(double value)    { throw new AbstractMethodError(); }
	public long   toKilobytes(double value){ throw new AbstractMethodError(); } 
	public long   toMegabytes(double value){ throw new AbstractMethodError(); }
	public long   toGigabytes(double value){ throw new AbstractMethodError(); }
	public long   toTerabytes(double value){ throw new AbstractMethodError(); }
	public String toString(double value)   { return StringUtils.humanReadableSize(toBytes(value)); }

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
