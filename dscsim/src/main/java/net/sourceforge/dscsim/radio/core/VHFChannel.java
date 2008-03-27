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
package net.sourceforge.dscsim.radio.core;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.dscsim.radiotransport.Frequency;

/**
 * Definition of VHF channels and their frequencies
 */
public class VHFChannel {
	
	/**
	 * Map to lookup all channels by their name
	 */
	private static Map channelMap = new TreeMap();

	// the normal (voice) channels
	public static final VHFChannel VHF_CHANNEL_01 = new VHFChannel("01",156050000,160650000);
	public static final VHFChannel VHF_CHANNEL_02 = new VHFChannel("02",156100000,160700000);
	public static final VHFChannel VHF_CHANNEL_03 = new VHFChannel("03",156150000,160750000);
	public static final VHFChannel VHF_CHANNEL_04 = new VHFChannel("04",156200000,160800000);
	public static final VHFChannel VHF_CHANNEL_05 = new VHFChannel("05",156250000,160850000);
	public static final VHFChannel VHF_CHANNEL_06 = new VHFChannel("06",156300000,156300000);
	public static final VHFChannel VHF_CHANNEL_07 = new VHFChannel("07",156350000,160950000);
	public static final VHFChannel VHF_CHANNEL_08 = new VHFChannel("08",156400000,156400000);
	public static final VHFChannel VHF_CHANNEL_09 = new VHFChannel("09",156450000,156450000);
	public static final VHFChannel VHF_CHANNEL_10 = new VHFChannel("10",156500000,156500000);
	public static final VHFChannel VHF_CHANNEL_11 = new VHFChannel("11",156550000,156550000);
	public static final VHFChannel VHF_CHANNEL_12 = new VHFChannel("12",156600000,156600000);
	public static final VHFChannel VHF_CHANNEL_13 = new VHFChannel("13",156650000,156650000);
	public static final VHFChannel VHF_CHANNEL_14 = new VHFChannel("14",156700000,156700000);
	public static final VHFChannel VHF_CHANNEL_15 = new VHFChannel("15",156750000,156750000);
	public static final VHFChannel VHF_CHANNEL_16 = new VHFChannel("16",156800000,156800000);
	public static final VHFChannel VHF_CHANNEL_17 = new VHFChannel("17",156850000,156850000);
	public static final VHFChannel VHF_CHANNEL_18 = new VHFChannel("18",156900000,161500000);
	public static final VHFChannel VHF_CHANNEL_19 = new VHFChannel("19",156950000,161550000);
	public static final VHFChannel VHF_CHANNEL_20 = new VHFChannel("20",157000000,161600000);
	public static final VHFChannel VHF_CHANNEL_21 = new VHFChannel("21",157050000,161650000);
	public static final VHFChannel VHF_CHANNEL_22 = new VHFChannel("22",157100000,161700000);
	public static final VHFChannel VHF_CHANNEL_23 = new VHFChannel("23",157150000,161750000);
	public static final VHFChannel VHF_CHANNEL_24 = new VHFChannel("24",157200000,161800000);
	public static final VHFChannel VHF_CHANNEL_25 = new VHFChannel("25",157250000,161850000);
	public static final VHFChannel VHF_CHANNEL_26 = new VHFChannel("26",157300000,161900000);
	public static final VHFChannel VHF_CHANNEL_27 = new VHFChannel("27",157350000,161950000);
	public static final VHFChannel VHF_CHANNEL_28 = new VHFChannel("28",157400000,162000000);
	public static final VHFChannel VHF_CHANNEL_60 = new VHFChannel("60",156025000,160625000);
	public static final VHFChannel VHF_CHANNEL_61 = new VHFChannel("61",156075000,160675000);
	public static final VHFChannel VHF_CHANNEL_62 = new VHFChannel("62",156125000,160125000);
	public static final VHFChannel VHF_CHANNEL_63 = new VHFChannel("63",156175000,160775000);
	public static final VHFChannel VHF_CHANNEL_64 = new VHFChannel("64",156225000,160825000);
	public static final VHFChannel VHF_CHANNEL_65 = new VHFChannel("65",156275000,160875000);
	public static final VHFChannel VHF_CHANNEL_66 = new VHFChannel("66",156325000,160925000);
	public static final VHFChannel VHF_CHANNEL_67 = new VHFChannel("67",156375000,156375000);
	public static final VHFChannel VHF_CHANNEL_68 = new VHFChannel("68",156425000,156425000);
	public static final VHFChannel VHF_CHANNEL_69 = new VHFChannel("69",156475000,156475000);
	// channel 70 is for DSC, see below
	public static final VHFChannel VHF_CHANNEL_71 = new VHFChannel("71",156575000,156575000);
	public static final VHFChannel VHF_CHANNEL_72 = new VHFChannel("72",156625000,156625000);
	public static final VHFChannel VHF_CHANNEL_73 = new VHFChannel("73",156675000,156675000);
	public static final VHFChannel VHF_CHANNEL_74 = new VHFChannel("74",156725000,156725000);
	public static final VHFChannel VHF_CHANNEL_75 = new VHFChannel("75",156775000,156775000);
	public static final VHFChannel VHF_CHANNEL_76 = new VHFChannel("76",156825000,156825000);
	public static final VHFChannel VHF_CHANNEL_77 = new VHFChannel("77",156875000,156875000);
	public static final VHFChannel VHF_CHANNEL_78 = new VHFChannel("78",156925000,161525000);
	public static final VHFChannel VHF_CHANNEL_79 = new VHFChannel("79",156975000,161575000);
	public static final VHFChannel VHF_CHANNEL_80 = new VHFChannel("80",157025000,161625000);
	public static final VHFChannel VHF_CHANNEL_81 = new VHFChannel("81",157075000,161675000);
	public static final VHFChannel VHF_CHANNEL_82 = new VHFChannel("82",157125000,161725000);
	public static final VHFChannel VHF_CHANNEL_83 = new VHFChannel("83",157175000,161775000);
	public static final VHFChannel VHF_CHANNEL_84 = new VHFChannel("84",157225000,161825000);
	public static final VHFChannel VHF_CHANNEL_85 = new VHFChannel("85",157275000,161875000);
	public static final VHFChannel VHF_CHANNEL_86 = new VHFChannel("86",157325000,161925000);
	public static final VHFChannel VHF_CHANNEL_87 = new VHFChannel("87",157375000,157375000);
	public static final VHFChannel VHF_CHANNEL_88 = new VHFChannel("88",157425000,157425000);

	// create the previous/next linking for all above (voice) channels
	static {
		VHFChannel previous = VHF_CHANNEL_88;
		for( Iterator i = channelMap.values().iterator(); i.hasNext(); ) {
			VHFChannel channel = (VHFChannel)i.next();
			channel._previousChannel = previous;
			previous._nextChannel = channel;
			previous = channel;
		}
	}
	
	// the remaining special purpose channels
	public static final VHFChannel VHF_CHANNEL_70 = new VHFChannel("70",156525000,156525000);
	public static final VHFChannel VHF_CHANNEL_AIS1 = new VHFChannel("AIS1",161975000,161975000);
	public static final VHFChannel VHF_CHANNEL_AIS2 = new VHFChannel("AIS2",162025000,162025000);
	
	/**
	 * The name of the channel
	 */
	private String _channelName;
	/**
	 * The next channel for normal voice channels
	 */
	private VHFChannel _previousChannel;
	
	/**
	 * The previous channel for normal voice channels
	 */
	private VHFChannel _nextChannel;
	
	/**
	 * The transmitter frequency of the ship station
	 */
	private Frequency _shipStationFrequency;
	
	/**
	 * The transmitter frequency of the coast station
	 */
	private Frequency _coastStationFrequency;

	/**
	 * Private constructor.
	 * @param name name of the channel; Normally the channel number using two
	 * digit representation (leading zeros)
	 * @param shipStationFrequency frequency for direction ship station to coast station (given in Hz)
	 * @param coastStationFrequency frequency for direction coast station to ship station (given in Hz)
	 */
	private VHFChannel(String name, int shipStationFrequency, int coastStationFrequency ){
		_channelName = name;
		_shipStationFrequency = new Frequency(shipStationFrequency);
		_coastStationFrequency = new Frequency(coastStationFrequency);
		_previousChannel = this;
		_nextChannel = this;
		channelMap.put(_channelName, this);
	}

	/**
	 * Gets the next channel.
	 * @return the next channel for normal voice channels. For channel 88 this will return 01
	 * (wrap around). For channels 70, AIS1 and AIS2 this returns the channel itself.
	 */
	public VHFChannel getNext() {
		return _nextChannel;
	}

	/**
	 * Gets the previous channel.
	 * @return the previous channel for normal voice channels. For channel 01 this will return 88
	 * (wrap around). For channels 70, AIS1 and AIS2 this returns the channel itself.
	 */
	public VHFChannel getPrevious() {
		return _previousChannel;
	}

	/**
	 * @return the _channelName
	 */
	public String getName() {
		return _channelName;
	}

	/**
	 * @return the _coastStationFrequency
	 */
	public Frequency getCoastStationFrequency() {
		return _coastStationFrequency;
	}

	/**
	 * @return the shipStationFrequency
	 */
	public Frequency getShipStationFrequency() {
		return _shipStationFrequency;
	}
	
	/**
	 * Gets the channel by name
	 * @param name the name of the channel
	 * @return the VHFChannel object for the given name
	 * @throws IllegalArgumentException if there is no VHF channel with the given name
	 */
	public static VHFChannel getByName(String name ){
		if( !channelMap.containsKey(name) ) {
			throw new IllegalArgumentException( "There is no VHF channel with name "+name);
		}
		return (VHFChannel)channelMap.get(name);
	}
}
