/*
 * The contents of this file are subject to the Mozilla Public License Version 1.0
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'dscsim'.
 *
 * The Initial Developer of the Original Code is Oliver Hecker. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.radiotransport;

/**
 * Model for the propagation of the radio airwave
 *
 */
public class PropagationModel {
	
	/**
	 * Speed of light (m/s)
	 */
	private static final double SPEED_OF_LIGHT = 300000000.0;
	
	/**
	 * Approximate frequency for VHF (1/s)
	 */
	private static final double VHF_FREQUENCY = 150000000.0;
	
	/**
	 * Approximate wave length
	 */
	private static final double LAMBDA = SPEED_OF_LIGHT / VHF_FREQUENCY;
	
	/**
	 * Fixed part of G_dist
	 */
	private static final double G_DIST_FIX = LAMBDA * LAMBDA / (2 * Math.PI * Math.PI );

	/**
	 * The atmosperic factor (power decreases by factor 2 within this distance)
	 */
	private static final double G_ATMO_FIX = 100000;
	
	/**
	 * Computes the attenuation.
	 * The power received at the receiving antenna can be computed
	 * by P_receive = P_send * G_antsend * G_att * G_reicant where
	 * P_send is the transmitter power, G_antsend is the gain of the sender
	 * antenna, G_att is the attenuation computed by this method and
	 * G_reicant is the gain of the receiver antenna.
	 * <p>
	 * This method computes G_att [dB] as sum of three parts:
	 * <ul>
	 * <li>G_dist = lambda^2 / ( 4 * PI^2 * distance^2 ) ; which models
	 * the effect that the strength decreases with the distance to the
	 * power of 2
	 * <li>G_atmo = K_atmo / distance; which models the attenuation by
	 *  atmospheric absortion
	 *  <li>G_hori = ???? which models the shadowing effect of the
	 *  horizont
	 *  </ul> 
	 *  if the distance is 0 (same antenna), then the attenuation 0 dB
	 * 
	 * @param distance the distance between the two antennas in metres
	 * @return the attenuation G_att in decibel
	 */
	public static Decibel computeAttenuation(int distance) {
		if( distance == 0 ){
			return Decibel.fromDb(0.0);
		}
		double gHori = 1.0;  // no model for this at the moment
		double gDist = G_DIST_FIX / (distance * distance);
		double gAtmo = Math.pow(0.5, (distance/G_ATMO_FIX));
		double gAtt = gDist * gAtmo * gHori;
		return Decibel.fromLinearPower(gAtt);
	}
	
}
