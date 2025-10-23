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
package org.openlvc.disco.pdu.field;

import java.util.Optional;

import org.openlvc.disco.DiscoException;
import org.openlvc.disco.application.utils.DrmState;
import org.openlvc.disco.configuration.DiscoConfiguration;
import org.openlvc.disco.configuration.Flag;
import org.openlvc.disco.pdu.DisSizes;
import org.openlvc.disco.pdu.record.WorldCoordinate;
import org.openlvc.disco.utils.Mat3x3;
import org.openlvc.disco.utils.Quaternion;
import org.openlvc.disco.utils.Vec3;

public enum DeadReckoningAlgorithm
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Other ( (short)0 ),
	Static( (short)1 ),
	FPW   ( (short)2 ),
	RPW   ( (short)3 ),
	RVW   ( (short)4 ),
	FVW   ( (short)5 ),
	FPB   ( (short)6 ),
	RPB   ( (short)7 ),
	RVB   ( (short)8 ),
	FVB   ( (short)9 );

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private short value;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	private DeadReckoningAlgorithm( short value )
	{
		this.value = value;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	public short value()
	{
		return this.value;
	}

	@Override
	public String toString()
	{
		switch( this.value )
		{
			case 1: return "Static";
			case 2: return "FPW";
			case 3: return "RPW";
			case 4: return "RVW";
			case 5: return "FVW";
			case 6: return "FPB";
			case 7: return "RPB";
			case 8: return "RVB";
			case 9: return "FVB";
			default: // drop through
		}
		
		// Missing
		if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new IllegalArgumentException( value+" not a valid Dead Reckoning Algorithm" );
		else
			return "Other";
	}

	public ReferenceFrame getReferenceFrame()
	{
		switch( this )
		{
			case Static:
			case FPW:
			case FVW:
			case RPW:
			case RVW:
				return ReferenceFrame.WorldCoordinates;

			case FPB:
			case FVB:
			case RPB:
			case RVB:
				return ReferenceFrame.BodyCoordinates;

			default: // drop through
		}

		// Ill-defined or unknown
		if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new DiscoException( "Unknown reference frame for dead-reckoning algorithm: %s (%d)".formatted(this,
			                                                                                                    this.value()) );
		return ReferenceFrame.Other;
	}

	/**
	 * Returns the state of the dead-reckoning model when extrapolated for the given duration
	 * using this dead-reckoning algorithm.
	 * 
	 * @param initialState the initial state to apply the model to
	 * @param dt time to extrapolate forwards, in s
	 * @return a {@link DrmState} with the state of the model after the given duration
	 * 
	 * @see #computeFixedStateAfter(DeadReckoningAlgorithm, DrmState, double)
	 */
	public DrmState computeStateAfter( DrmState initialState, double dt )
	{
		if( this == Static )
			return initialState;
		
		switch( this.getReferenceFrame() )
		{
			case WorldCoordinates:
				return DeadReckoningAlgorithm.computeFixedStateAfter( this, initialState, dt );

			case BodyCoordinates:
				return DeadReckoningAlgorithm.computeRotatingStateAfter( this, initialState, dt );

			default:
			case Other:
				// fallback to static
				// TODO warn?
				return initialState;
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static final int getByteLength()
	{
		return DisSizes.UI8_SIZE;
	}

	public static DeadReckoningAlgorithm fromValue( short value )
	{
		switch( value )
		{
			case 2: return FPW;
			case 4: return RVW;
			case 1: return Static;
			case 8: return RVB;
			case 3: return RPW;
			case 5: return FVW;
			case 6: return FPB;
			case 7: return RPB;
			case 9: return FVB;
			default: // drop through
		}
		
		// Missing
		if( DiscoConfiguration.isSet(Flag.Strict) )
			throw new IllegalArgumentException( value+" not a valid Dead Reckoning Algorithm" );
		else
			return Other;
	}

	// q_DR in the spec
	private static Quaternion makeRotationQuaternion( Vec3 angularVelocity, double dt )
	{
		Vec3 rotAxis = new Vec3( angularVelocity ).normalize();

		double halfBeta = (angularVelocity.length() * dt) / 2.0;
		double sinHalfBeta = Math.sin( halfBeta );

		double w = Math.cos( halfBeta );
		double x = rotAxis.x * sinHalfBeta;
		double y = rotAxis.y * sinHalfBeta;
		double z = rotAxis.z * sinHalfBeta;

		return new Quaternion( w, x, y, z );
	}	

	/**
	 * Returns the state of the dead-reckoning model when extrapolated for the given duration,
	 * using the specified fixed/world reference (World Coordinate) dead-reckoning algorithm.
	 * 
	 * @param algorithm MUST be one of FPW, FVW, RPW, or RVW
	 * @param initialState the initial state to apply the model to
	 * @param dt time to extrapolate forwards, in s
	 * @return a {@link DrmState} with the state of the model after the given duration
	 * 
	 * @see #computeRotatingStateAfter(DeadReckoningAlgorithm, DrmState, double)
	 */
	private static DrmState computeFixedStateAfter( DeadReckoningAlgorithm algorithm, DrmState initialState, double dt )
	{
		// use the old copies if we don't make any changes
		Optional<Vec3> position = Optional.empty();
		Optional<Vec3> velocity = Optional.empty();
		Optional<Quaternion> orientation = Optional.empty();

		// effects due to velocity
		switch( algorithm )
		{
			// currently all models
			case FPW:
			case FVW:
			case RPW:
			case RVW:
				// displacement
				Vec3 disp = new Vec3( initialState.velocity() );
				disp.multiply( dt );

				if( position.isEmpty() )
					position = Optional.of( new Vec3(initialState.position()) );
				position.get().add( disp );
				break;

			default:
				break;
		}

		// effects due to acceleration
		switch( algorithm )
		{
			// 'V'-type models
			case FVW:
			case RVW:
				// change in velocity
				Vec3 dv = new Vec3( initialState.acceleration() );
				dv.multiply( dt );

				if( velocity.isEmpty() )
					velocity = Optional.of( new Vec3( initialState.velocity() ) );
				velocity.get().add( dv );

				// displacement
				Vec3 disp = new Vec3( dv );
				disp.multiply( dt / 2.0 );

				if( position.isEmpty() )
					position = Optional.of( new Vec3( initialState.position() ) );
				position.get().add( disp );
				break;

			default:
				break;
		}

		// effects due to angular velocity
		switch( algorithm )
		{
			// 'R'-type models
			case RPW:
			case RVW:
				// rotation
				Quaternion rotationQuaternion = DeadReckoningAlgorithm.makeRotationQuaternion( initialState.angularVelocity(), dt );
				orientation = Optional.of( orientation.orElseGet(initialState::orientation).multiply(rotationQuaternion) );
				break;

			default:
				break;
		}

		return new DrmState( position.orElse(initialState.position()),
		                     velocity.orElse(initialState.velocity()),
		                     new Vec3(initialState.acceleration()),
		                     orientation.orElse(initialState.orientation()),
		                     new Vec3(initialState.angularVelocity()) );
	}

	/**
	 * Returns the state of the dead-reckoning model when extrapolated for the given duration,
	 * using the specified rotating/body reference (Body Coordinate) dead-reckoning algorithm.
	 * 
	 * @param algorithm MUST be one of FPB, FVB, RPB, or RVB
	 * @param initialState the initial state to apply the model to
	 * @param dt time to extrapolate forwards, in s
	 * @return a {@link DrmState} with the state of the model after the given duration
	 * 
	 * @see #computeFixedStateAfter(DeadReckoningAlgorithm, DrmState, double)
	 */
	private static DrmState computeRotatingStateAfter( DeadReckoningAlgorithm algorithm, DrmState initialState, double dt )
	{
		Optional<Vec3> position = Optional.empty();
		Optional<Quaternion> orientation = Optional.empty();

		// precompute values shared across calculations

		// V_b; same as v_b
		Vec3 V_b = initialState.velocity();

		Vec3 w = initialState.angularVelocity();

		double w_mag = w.length();
		double w_mag2 = w.sqrdLength();
		double w_mag3 = w_mag * w_mag2;

		double wdt = w_mag * dt;

		// vectors are columns
		Mat3x3 skew = new Mat3x3( new Vec3(0, w.z, -w.y),
								  new Vec3(-w.z, 0, w.x),
								  new Vec3(w.y, -w.x, 0) );
		Mat3x3 wwT = w.outer( w );

		double sin_wdt = Math.sin( wdt );
		double cos_wdt = Math.cos( wdt );

		// effects due to velocity
		switch( algorithm )
		{
			// currently all models
			case FPB:
			case FVB:
			case RPB:
			case RVB:
				// R_1
				Mat3x3 R_1 = new Mat3x3( wwT ).multiply( (wdt - sin_wdt) / w_mag3 );
				R_1.add( Mat3x3.Identity().multiply(sin_wdt / w_mag) );
				R_1.add( new Mat3x3(skew).multiply((1 - cos_wdt) / w_mag2) );

				// displacement
				Vec3 disp = R_1.multiply( V_b ).rotate( initialState.orientation() );

				if( position.isEmpty() )
					position = Optional.of( new Vec3(initialState.position()) );
				position.get().add( disp );
				break;

			default:
				break;
		}

		// effects due to acceleration
		switch( algorithm )
		{
			// 'V'-type models
			case FVB:
			case RVB:
				double w_mag4 = w_mag2 * w_mag2;

				// R_2
				double cpwdtsm1 = cos_wdt + wdt*sin_wdt - 1;
				Mat3x3 R_2 = new Mat3x3( wwT ).multiply( (0.5*w_mag2*dt*dt - cpwdtsm1) / w_mag4 );
				R_2.add( Mat3x3.Identity().multiply(cpwdtsm1 / w_mag2) );
				R_2.add( new Mat3x3(skew).multiply((sin_wdt - wdt*cos_wdt) / w_mag3) );

				// A_b; time derivative of V_b, computed as a_b - skew * V_b
				Vec3 A_b = new Vec3( initialState.acceleration() );
				A_b.subtract( new Mat3x3(skew).multiply(V_b) );
				
				// TODO compute new velocity if needed

				// displacement
				Vec3 disp = R_2.multiply( A_b ).rotate( initialState.orientation() );

				if( position.isEmpty() )
					position = Optional.of( new Vec3( initialState.position() ) );
				position.get().add( disp );
				break;

			default:
				break;
		}

		// effects due to angular velocity
		switch( algorithm )
		{
			// 'R'-type models
			case RPB:
			case RVB:
				// rotation
				Quaternion rotationQuaternion = DeadReckoningAlgorithm.makeRotationQuaternion( initialState.angularVelocity(), dt );
				orientation = Optional.of( orientation.orElseGet(initialState::orientation).multiply(rotationQuaternion) );
				break;

			default:
				break;
		}
		
		return new DrmState( position.orElse(initialState.position()),
		                     new Vec3(initialState.velocity()),
		                     new Vec3(initialState.acceleration()),
		                     orientation.orElse(initialState.orientation()),
		                     new Vec3(initialState.angularVelocity()) );
	}

	/**
	 * The reference frame of a dead-reckoning algorithm - either world or body coordinates.
	 */
	public enum ReferenceFrame
	{
		Other,
		
		/**
		 * The 'World coordinate system' as defined in the DIS spec (1.6.3.1, IEEE 1278.1-2012), a
		 * NIMA TR 8350.2 and WGS 84 based right-handed geocentric Cartesian coordinate system.
		 * 
		 * See {@link WorldCoordinate} for more information.
		 */
		WorldCoordinates,

		/**
		 * The 'Entity coordinate system' as defined in the DIS spec (1.6.3.2, IEEE 1278.1-2012),
		 * a right-handed Cartesian coordinate system centred and oriented on the associated
		 * entity, with a distance of 1 unit corresponding to 1m. <br/>
		 * <br/>
		 * Axes (see Figure 2 of IEEE 1278.1-2012 for a graphical depiction):
		 * <ul>
		 * <li>{@code x}: positive is to the front of the entity</li>
		 * <li>{@code y}: positive is to the right of the entity</li>
		 * <li>{@code z}: positive is below the entity</li>
		 * </ul>
		 */
		BodyCoordinates;
	}
}
