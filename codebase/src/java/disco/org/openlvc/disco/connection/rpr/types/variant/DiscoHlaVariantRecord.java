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
package org.openlvc.disco.connection.rpr.types.variant;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.connection.rpr.types.enumerated.EnumHolder;
import org.openlvc.disco.connection.rpr.types.enumerated.ExtendedDataElement;

import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;
import hla.rti1516e.encoding.HLAvariantRecord;
import hla.rti1516e.exceptions.RTIinternalError;

public class DiscoHlaVariantRecord<T extends ExtendedDataElement<T>> implements HLAvariantRecord<T>
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected HLAvariantRecord<EnumHolder<T>> internal;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DiscoHlaVariantRecord( T discriminant )
	{
		try
		{
			this.internal = RtiFactoryFactory.getRtiFactory()
			                                 .getEncoderFactory()
			                                 .createHLAvariantRecord( new EnumHolder<>(discriminant) );
		}
		catch( RTIinternalError e )
		{
			throw new DiscoException( "Could not create HLAvariantRecord: "+e.getMessage(), e );
		}
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	// HLAvariantRecord
	@Override
	public void setVariant( T discriminant, DataElement dataElement )
	{
		internal.setVariant( new EnumHolder<>(discriminant), dataElement );
	}

	@Override
	public void setDiscriminant( T discriminant )
	{
		internal.setDiscriminant( new EnumHolder<>(discriminant) );
	}

	@Override
	public T getDiscriminant()
	{
		return internal.getDiscriminant().getEnum();
	}

	@Override
	public DataElement getValue()
	{
		return internal.getValue();
	}


	// DataElement
	public int getOctetBoundary()			  { return internal.getOctetBoundary(); }
	public int getEncodedLength()			  { return internal.getEncodedLength(); }
	public void encode( ByteWrapper wrapper )
		throws EncoderException				  { internal.encode(wrapper); }
	public byte[] toByteArray()
		throws EncoderException				  { return internal.toByteArray(); }
	public void decode( ByteWrapper wrapper )
		throws DecoderException				  { internal.decode(wrapper); }
	public void decode( byte[] bytes )
		throws DecoderException               { internal.decode(bytes); }

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
