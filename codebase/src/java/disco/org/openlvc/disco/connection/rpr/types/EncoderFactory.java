/*
 *   Copyright 2020 Open LVC Project.
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
package org.openlvc.disco.connection.rpr.types;

import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.HLAASCIIchar;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAboolean;
import hla.rti1516e.encoding.HLAbyte;
import hla.rti1516e.encoding.HLAfixedArray;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.encoding.HLAfloat32LE;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.encoding.HLAfloat64LE;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.encoding.HLAinteger16LE;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.encoding.HLAinteger32LE;
import hla.rti1516e.encoding.HLAinteger64BE;
import hla.rti1516e.encoding.HLAinteger64LE;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.encoding.HLAoctetPairBE;
import hla.rti1516e.encoding.HLAoctetPairLE;
import hla.rti1516e.encoding.HLAopaqueData;
import hla.rti1516e.encoding.HLAunicodeChar;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.encoding.HLAvariableArray;
import hla.rti1516e.encoding.HLAvariantRecord;

public class EncoderFactory
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

	public HLAASCIIchar createHLAASCIIchar()
	{
		return new org.openlvc.disco.connection.rpr.types.simple.HLAASCIIchar();
	}

	public HLAASCIIchar createHLAASCIIchar( byte b )
	{
		return new org.openlvc.disco.connection.rpr.types.simple.HLAASCIIchar( b );
	}

	public HLAASCIIstring createHLAASCIIstring()
	{
		return new org.openlvc.disco.connection.rpr.types.simple.HLAASCIIstring();
	}

	public HLAASCIIstring createHLAASCIIstring( String s )
	{
		return new org.openlvc.disco.connection.rpr.types.simple.HLAASCIIstring( s );
	}

	public HLAboolean createHLAboolean()
	{
		return new org.openlvc.disco.connection.rpr.types.enumerated.HLAboolean();
	}

	public HLAboolean createHLAboolean( boolean b )
	{
		return new org.openlvc.disco.connection.rpr.types.enumerated.HLAboolean( b );
	}

	public HLAbyte createHLAbyte()
	{
		return new org.openlvc.disco.connection.rpr.types.simple.HLAbyte();
	}

	public HLAbyte createHLAbyte( byte b )
	{
		return new org.openlvc.disco.connection.rpr.types.simple.HLAbyte( b );
	}

	public <T extends DataElement> HLAvariantRecord<T> createHLAvariantRecord( T discriminant )
	{
		throw new RuntimeException( "Not Yet Implemented" );
	}

	public HLAfixedRecord createHLAfixedRecord()
	{
		return new org.openlvc.disco.connection.rpr.types.fixed.HLAfixedRecord();
	}

	public <T extends DataElement> HLAfixedArray<T>
	       createHLAfixedArray( DataElementFactory<T> factory, int size )
	{
		return new org.openlvc.disco.connection.rpr.types.array.HLAfixedArray<T>( factory, size );
	}

	@SuppressWarnings("unchecked")
	public <T extends DataElement> HLAfixedArray<T> createHLAfixedArray( T... elements )
	{
		return new org.openlvc.disco.connection.rpr.types.array.HLAfixedArray<T>( elements );
	}

	public HLAfloat32BE createHLAfloat32BE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE();
	}

	public HLAfloat32BE createHLAfloat32BE( float f )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAfloat32BE( f );
	}

	public HLAfloat32LE createHLAfloat32LE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAfloat32LE();
	}

	public HLAfloat32LE createHLAfloat32LE( float f )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAfloat32LE( f );
	}

	public HLAfloat64BE createHLAfloat64BE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAfloat64BE();
	}

	public HLAfloat64BE createHLAfloat64BE( double d )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAfloat64BE( d );
	}

	public HLAfloat64LE createHLAfloat64LE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAfloat64LE();
	}

	public HLAfloat64LE createHLAfloat64LE( double d )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAfloat64LE( d );
	}

	public HLAinteger16BE createHLAinteger16BE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger16BE();
	}

	public HLAinteger16BE createHLAinteger16BE( short s )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger16BE( s );
	}

	public HLAinteger16LE createHLAinteger16LE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger16LE();
	}

	public HLAinteger16LE createHLAinteger16LE( short s )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger16LE( s );
	}

	public HLAinteger32BE createHLAinteger32BE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger32BE();
	}

	public HLAinteger32BE createHLAinteger32BE( int i )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger32BE( i );
	}

	public HLAinteger32LE createHLAinteger32LE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger32LE();
	}

	public HLAinteger32LE createHLAinteger32LE( int i )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger32LE( i );
	}

	public HLAinteger64BE createHLAinteger64BE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger64BE();
	}

	public HLAinteger64BE createHLAinteger64BE( long l )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger64BE( l );
	}

	public HLAinteger64LE createHLAinteger64LE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger64LE();
	}

	public HLAinteger64LE createHLAinteger64LE( long l )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAinteger64LE( l );
	}

	public HLAoctet createHLAoctet()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAoctet();
	}

	public HLAoctet createHLAoctet( byte b )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAoctet( b );
	}

	public HLAoctetPairBE createHLAoctetPairBE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAoctetPairBE();
	}

	public HLAoctetPairBE createHLAoctetPairBE( short s )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAoctetPairBE( s );
	}

	public HLAoctetPairLE createHLAoctetPairLE()
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAoctetPairLE();
	}

	public HLAoctetPairLE createHLAoctetPairLE( short s )
	{
		return new org.openlvc.disco.connection.rpr.types.basic.HLAoctetPairLE( s );
	}

	public HLAopaqueData createHLAopaqueData()
	{
		throw new RuntimeException( "Not Yet Implemented" );
	}

	public HLAopaqueData createHLAopaqueData( byte[] b )
	{
		throw new RuntimeException( "Not Yet Implemented" );
	}

	public HLAunicodeChar createHLAunicodeChar()
	{
		throw new RuntimeException( "Not Yet Implemented" );
	}

	public HLAunicodeChar createHLAunicodeChar( short c )
	{
		throw new RuntimeException( "Not Yet Implemented" );
	}

	public HLAunicodeString createHLAunicodeString()
	{
		return new org.openlvc.disco.connection.rpr.types.simple.HLAunicodeString();
	}

	public HLAunicodeString createHLAunicodeString( String s )
	{
		return new org.openlvc.disco.connection.rpr.types.simple.HLAunicodeString( s );
	}

	@SuppressWarnings("unchecked")
	public <T extends DataElement> HLAvariableArray<T>
	       createHLAvariableArray( DataElementFactory<T> factory, T... elements )
	{
		return new org.openlvc.disco.connection.rpr.types.array.HLAvariableArray<T>( factory, elements );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
