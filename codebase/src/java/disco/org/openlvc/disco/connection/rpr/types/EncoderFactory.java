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

import org.openlvc.disco.DiscoException;

import hla.rti1516e.RtiFactoryFactory;
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
import hla.rti1516e.exceptions.RTIinternalError;

public class EncoderFactory
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static hla.rti1516e.encoding.EncoderFactory RTI_FACTORY = null;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public static HLAASCIIchar createHLAASCIIchar()
	{
		return factory().createHLAASCIIchar();
	}

	public static HLAASCIIchar createHLAASCIIchar( byte b )
	{
		return factory().createHLAASCIIchar(b);
	}

	public static HLAASCIIstring createHLAASCIIstring()
	{
		return factory().createHLAASCIIstring();
	}

	public static HLAASCIIstring createHLAASCIIstring( String s )
	{
		return factory().createHLAASCIIstring(s);
	}

	public static HLAboolean createHLAboolean()
	{
		return factory().createHLAboolean();
	}

	public static HLAboolean createHLAboolean( boolean b )
	{
		return factory().createHLAboolean(b);
	}

	public static HLAbyte createHLAbyte()
	{
		return factory().createHLAbyte();
	}

	public static HLAbyte createHLAbyte( byte b )
	{
		return factory().createHLAbyte(b);
	}

	public <T extends DataElement> HLAvariantRecord<T> createHLAvariantRecord( T discriminant )
	{
		return factory().createHLAvariantRecord( discriminant );
	}

	public static HLAfixedRecord createHLAfixedRecord()
	{
		return factory().createHLAfixedRecord();
	}

	public <T extends DataElement> HLAfixedArray<T>
	       createHLAfixedArray( DataElementFactory<T> factory, int size )
	{
		return factory().createHLAfixedArray( factory, size );
	}

	@SuppressWarnings("unchecked")
	public <T extends DataElement> HLAfixedArray<T> createHLAfixedArray( T... elements )
	{
		return factory().createHLAfixedArray( elements );
	}

	public static HLAfloat32BE createHLAfloat32BE()
	{
		return factory().createHLAfloat32BE();
	}

	public static HLAfloat32BE createHLAfloat32BE( float f )
	{
		return factory().createHLAfloat32BE(f);
	}

	public static HLAfloat32LE createHLAfloat32LE()
	{
		return factory().createHLAfloat32LE();
	}

	public static HLAfloat32LE createHLAfloat32LE( float f )
	{
		return factory().createHLAfloat32LE(f);
	}

	public static HLAfloat64BE createHLAfloat64BE()
	{
		return factory().createHLAfloat64BE();
	}

	public static HLAfloat64BE createHLAfloat64BE( double d )
	{
		return factory().createHLAfloat64BE(d);
	}

	public static HLAfloat64LE createHLAfloat64LE()
	{
		return factory().createHLAfloat64LE();
	}

	public static HLAfloat64LE createHLAfloat64LE( double d )
	{
		return factory().createHLAfloat64LE(d);
	}

	public static HLAinteger16BE createHLAinteger16BE()
	{
		return factory().createHLAinteger16BE();
	}

	public static HLAinteger16BE createHLAinteger16BE( short s )
	{
		return factory().createHLAinteger16BE(s);
	}

	public static HLAinteger16LE createHLAinteger16LE()
	{
		return factory().createHLAinteger16LE();
	}

	public static HLAinteger16LE createHLAinteger16LE( short s )
	{
		return factory().createHLAinteger16LE(s);
	}

	public static HLAinteger32BE createHLAinteger32BE()
	{
		return factory().createHLAinteger32BE();
	}

	public static HLAinteger32BE createHLAinteger32BE( int i )
	{
		return factory().createHLAinteger32BE(i);
	}

	public static HLAinteger32LE createHLAinteger32LE()
	{
		return factory().createHLAinteger32LE();
	}

	public static HLAinteger32LE createHLAinteger32LE( int i )
	{
		return factory().createHLAinteger32LE(i);
	}

	public static HLAinteger64BE createHLAinteger64BE()
	{
		return factory().createHLAinteger64BE();
	}

	public static HLAinteger64BE createHLAinteger64BE( long l )
	{
		return factory().createHLAinteger64BE(l);
	}

	public static HLAinteger64LE createHLAinteger64LE()
	{
		return factory().createHLAinteger64LE();
	}

	public static HLAinteger64LE createHLAinteger64LE( long l )
	{
		return factory().createHLAinteger64LE(l);
	}

	public static HLAoctet createHLAoctet()
	{
		return factory().createHLAoctet();
	}

	public static HLAoctet createHLAoctet( byte b )
	{
		return factory().createHLAoctet(b);
	}

	public static HLAoctetPairBE createHLAoctetPairBE()
	{
		return factory().createHLAoctetPairBE();
	}

	public static HLAoctetPairBE createHLAoctetPairBE( short s )
	{
		return factory().createHLAoctetPairBE(s);
	}

	public static HLAoctetPairLE createHLAoctetPairLE()
	{
		return factory().createHLAoctetPairLE();
	}

	public static HLAoctetPairLE createHLAoctetPairLE( short s )
	{
		return factory().createHLAoctetPairLE(s);
	}

	public static HLAopaqueData createHLAopaqueData()
	{
		return factory().createHLAopaqueData();
	}

	public static HLAopaqueData createHLAopaqueData( byte[] b )
	{
		return factory().createHLAopaqueData(b);
	}

	public static HLAunicodeChar createHLAunicodeChar()
	{
		return factory().createHLAunicodeChar();
	}

	public static HLAunicodeChar createHLAunicodeChar( short c )
	{
		return factory().createHLAunicodeChar(c);
	}

	public static HLAunicodeString createHLAunicodeString()
	{
		return factory().createHLAunicodeString();
	}

	public static HLAunicodeString createHLAunicodeString( String s )
	{
		return factory().createHLAunicodeString(s);
	}

	@SuppressWarnings("unchecked")
	public <T extends DataElement> HLAvariableArray<T>
	       createHLAvariableArray( DataElementFactory<T> factory, T... elements )
	{
		return factory().createHLAvariableArray( factory, elements );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	private static final hla.rti1516e.encoding.EncoderFactory factory()
	{
		if( RTI_FACTORY == null )
		{
			try
			{
				RTI_FACTORY = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
			}
			catch( RTIinternalError e )
			{
				throw new DiscoException( e );
			}
		}
		
		return RTI_FACTORY;
	}
}
