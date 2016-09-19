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
package org.openlvc.distributor.filters;

import org.openlvc.disco.pdu.entity.EntityStatePdu;
import org.openlvc.disco.pdu.field.ForceId;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups={"distributor","filters"})
public class FilterTest
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

	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Valid: testEntityForceIdFilter()   /////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	@Test
	public void testEntityForceIdFilter()
	{
		String filterString = "entity.force == Friendly";
		FilterGroup directFilter = FilterFactory.parse( filterString );
		filterString = "entity.force == Friendly || entity.force == Neutral";
		FilterGroup choiceFilter = FilterFactory.parse( filterString );
		
		EntityStatePdu espdu = new EntityStatePdu();
		
		espdu.setForceID( ForceId.Friendly );
		Assert.assertTrue( directFilter.matches(espdu), "Did not detect matching ForceID" );
		Assert.assertTrue( choiceFilter.matches(espdu), "Did not detect matching ForceID" );
		
		espdu.setForceID( ForceId.Neutral );
		Assert.assertFalse( directFilter.matches(espdu), "Did not detect mis-matching ForceID" );
		Assert.assertTrue( choiceFilter.matches(espdu), "Did not detect matching ForceID" );
		
		espdu.setForceID( ForceId.Opposing );
		Assert.assertFalse( directFilter.matches(espdu), "Did not detect mis-matching ForceID" );
		Assert.assertFalse( choiceFilter.matches(espdu), "Did not detect mis-matching ForceID" );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
