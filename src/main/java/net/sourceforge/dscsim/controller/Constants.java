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
 * The Initial Developer of the Original Code is William Pennoyer. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007, 2008, 20010.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.controller;

import java.awt.event.KeyEvent;
import java.util.SortedSet;

/**
 * In controller related constants are define here.
 * @author William Pennoyer
 */
public interface Constants {
	
	/**
	 * Base location for resources.
	 */
	public static final String RESOURCE_BASE = "etc/";
	
	/**
	 * Base location for controller runtime data.
	 */
	public static final String STORE_BASE = "data/";	
	
	/*windows applet eclipse begin*/
	
	/**
	 * Full name of screen file for ship stations.
	 */
	public static final String SCREEN_FILE =  RESOURCE_BASE + "ship.xml";
	
	/**
	 * Full name of screen file for coastal stations.
	 */
	public static final String SCREEN_FILE_COAST =  RESOURCE_BASE + "shore.xml";
	
	/**
	 * Full name of controller's background image.
	 */
	public static final String SKIN_ICM504 = RESOURCE_BASE + "dscsim_controller.jpg";
	
	/**
	 * Name of properities file for storing application specific information.
	 */
	public static final String APPL_STRINGS = RESOURCE_BASE + "string.properties";
	
	/**
	 * Name of properties file for setup information.
	 */
	public static final String SETUP_STRINGS = RESOURCE_BASE + "setup.properties";
	
	/**
	 * Default input pattern.
	 */
	public static final String INPUT_PATTERN = "__________________________";
	
	/**
	 * Default for maximum list length.
	 */
	public static final int    MAX_LIST_LEN  = 7;
	
	/**
	 * Key action id for no key.
	 */
	public static final String NO_KEY = "NKEY";
	
	/**
	 * Key action id from key pressed.
	 */
	public static final String PRESSED = "PRSD";
	
	/**
	 * Key action id for key released.
	 */
	public static final String RELEASED = "RLSD";
	
	/**
	 * Key ids for number pad on controller.
	 */
	public static final String KP_0 = "KP_0";		
	public static final String KP_1 = "KP_1";
	public static final String KP_2 = "KP_2";
	public static final String KP_3 = "KP_3";
	public static final String KP_4 = "KP_4";
	public static final String KP_5 = "KP_5";
	public static final String KP_6 = "KP_6";
	public static final String KP_7 = "KP_7";
	public static final String KP_8 = "KP_8";
	public static final String KP_9 = "KP_9";
	public static final String KP_Aa = "KP_Aa";
	public static final String KP_BS = "KP_BS";
	
	/**
	 * Set of Key ids for searching.
	 */
	public static final SortedSet _oKeySet = DscUtils.createKeySet();
	
	/**
	 * Array with key ids.
	 */
	public static final String arrKeyPad[] = {
			KP_1, KP_2, KP_3, 
			KP_4, KP_5, KP_6, 
			KP_7, KP_8, KP_9, 
			KP_Aa, KP_0, KP_BS	
	};
			
	/**
	 * Key ids for the function keys.
	 */
	public static final String FK_SOS  = "FK_SOS";
	public static final String FK_CLR  = "FK_CLR";
	public static final String FK_CALL = "FK_CALL";
	public static final String FK_ENT  = "FK_ENT";
	
	/**
	 * Array for SOS key only.
	 */
	public static final String distressPad[] = {
			FK_SOS
	};
	
	/**
	 * Array containing clear, call and enter keys.
	 */
	public static final String arrFuncPad[] = {
			FK_CLR, FK_CALL, FK_ENT	
	};

	/**
	 * Key ids for movement keys - up,down, left and right.
	 */
	public static final String MV_UP = "MV_UP";
	public static final String MV_DOWN = "MV_DOWN";
	public static final String MV_LEFT = "MV_LEFT";
	public static final String MV_RIGHT = "MV_RIGHT";

	/**
	 * Array for horizontal movement keys.
	 */
	public static final String arrHorArrowPad[] = {
			MV_LEFT, MV_RIGHT	
	};
	
	/**
	 * Array for vertical movement keys.
	 */
	public static final String arrVerArrowPad[] = {
			MV_UP, MV_DOWN	
	};
	
	/**
	 * Mappings for keyboard keys to controller gui keys.
	 */	
	public static final String keyCode2Buttons [][] = {
			{String.valueOf(KeyEvent.VK_0), KP_0},
			//
			{String.valueOf(KeyEvent.VK_1), KP_1},
			{String.valueOf(KeyEvent.VK_A), KP_1},
			{String.valueOf(KeyEvent.VK_B), KP_1},			
			{String.valueOf(KeyEvent.VK_C), KP_1},
			//
			{String.valueOf(KeyEvent.VK_2), KP_2},
			{String.valueOf(KeyEvent.VK_D), KP_2},
			{String.valueOf(KeyEvent.VK_E), KP_2},
			{String.valueOf(KeyEvent.VK_F), KP_2},
			//
			{String.valueOf(KeyEvent.VK_3), KP_3},
			{String.valueOf(KeyEvent.VK_G), KP_3},
			{String.valueOf(KeyEvent.VK_H), KP_3},
			{String.valueOf(KeyEvent.VK_I), KP_3},
			//
			{String.valueOf(KeyEvent.VK_4), KP_4},
			{String.valueOf(KeyEvent.VK_J), KP_4},
			{String.valueOf(KeyEvent.VK_K), KP_4},
			{String.valueOf(KeyEvent.VK_L), KP_4},
			
			//
			{String.valueOf(KeyEvent.VK_5), KP_5},
			{String.valueOf(KeyEvent.VK_M), KP_5},
			{String.valueOf(KeyEvent.VK_N), KP_5},
			{String.valueOf(KeyEvent.VK_O), KP_5},
			
			//
			{String.valueOf(KeyEvent.VK_6), KP_6},
			{String.valueOf(KeyEvent.VK_P), KP_6},
			{String.valueOf(KeyEvent.VK_Q), KP_6},
			{String.valueOf(KeyEvent.VK_R), KP_6},
						
			//
			{String.valueOf(KeyEvent.VK_7), KP_7},
			{String.valueOf(KeyEvent.VK_S), KP_7},
			{String.valueOf(KeyEvent.VK_T), KP_7},
			{String.valueOf(KeyEvent.VK_U), KP_7},
			
			//
			{String.valueOf(KeyEvent.VK_8), KP_8},
			{String.valueOf(KeyEvent.VK_V), KP_8},
			{String.valueOf(KeyEvent.VK_W), KP_8},
			{String.valueOf(KeyEvent.VK_X), KP_8},
			
			//
			{String.valueOf(KeyEvent.VK_9), KP_9},
			{String.valueOf(KeyEvent.VK_Y), KP_9},
			{String.valueOf(KeyEvent.VK_Z), KP_9},
			
			//
			{String.valueOf(KeyEvent.VK_SHIFT), KP_Aa},
			{String.valueOf(KeyEvent.VK_BACK_SPACE), KP_BS},
			{String.valueOf(KeyEvent.VK_UP), MV_UP},
			{String.valueOf(KeyEvent.VK_DOWN), MV_DOWN},
			{String.valueOf(KeyEvent.VK_LEFT), MV_LEFT},
			{String.valueOf(KeyEvent.VK_RIGHT), MV_RIGHT},
			{String.valueOf(KeyEvent.VK_F1), FK_SOS},
			{String.valueOf(KeyEvent.VK_ESCAPE), FK_CLR},
			{String.valueOf(KeyEvent.VK_F2), FK_CALL},
			{String.valueOf(KeyEvent.VK_ENTER), FK_ENT}			
	};
	
	/**
	 * Channel 16 constant.
	 */
	public static final String CH_16 						= "16";
	/**
	 * Channel 70 constant.
	 */
	public static final String CH_70 						= "70";

	/**
	 * Call type individual.
	 */
	public static final String CALL_TYPE_INDIVIDUAL 		= "TYPE_INDIVIDUAL";
	/**
	 * Call type individual acknowlege.
	 */
	public static final String CALL_TYPE_INDIVIDUAL_ACK 	= "TYPE_INDIVIDUAL_ACK";
	/**
	 * Call type group.
	 */
	public static final String CALL_TYPE_GROUP	 			= "TYPE_GROUP";
	
	/**
	 * Call type group acknowledge.
	 */
	public static final String CALL_TYPE_GROUP_ACK	 		= "TYPE_GROUP_ACK";
	
	/**
	 * Call type all ships.
	 */
	public static final String CALL_TYPE_ALL_SHIPS 		= "TYPE_ALL_SHIPS";
	
	/**
	 * Call type all ships acknowledge.
	 */
	public static final String CALL_TYPE_ALL_SHIPS_ACK 	= "TYPE_ALL_SHIPS_ACK";
	
	/**
	 * Call type distress.
	 */
	public static final String CALL_TYPE_DISTRESS			= "TYPE_DISTRESS";
	
	/**
	 * Call type distress acknowledgement
	 */
	public static final String CALL_TYPE_DISTRESS_ACK		= "TYPE_DISTRESS_ACK";
	
	/**
	 * Call type position request.
	 */
	public static final String CALL_TYPE_POSITION_REQ		= "TYPE_POSITION_REQ";
	
	
	/**
	 * Designated called.
	 */
	public static final String CALL_NATURE_DESIGNATED		= "DESIG";
	
	/**
	 * Call nature undesignated.
	 */
	public static final String CALL_NATURE_UNDESIGNATED	= "UNDESIG";

	/**
	 * Catagory distress.
	 */
	public static final String CALL_CAT_DISTRESS			= "CAT_DISTRESS";
	
	/**
	 * Catagory urgency.
	 */
	public static final String CALL_CAT_URGENCY			= "CAT_URGENCY";
	
	/**
	 * Catagory saftey.
	 */
	public static final String CALL_CAT_SAFETY				= "CAT_SAFETY";
	/**
	 * Catagory routine.
	 */
	public static final String CALL_CAT_ROUTINE			= "CAT_ROUTINE";

	/**
	 * Catagory distress.
	 */
	public static final String CALL_CAT_DISTRESS_TEXT		= "Distress";
	
	/**
	 * Catagory urgency.
	 */
	public static final String CALL_CAT_URGENCY_TEXT 		= "Urgency";
	/**
	 * Catagory saftey.
	 */
	public static final String CALL_CAT_SAFETY_TEXT 		= "Saftey";
	
	/**
	 * Catagory routine.
	 */
	public static final String CALL_CAT_ROUTINE_TEXT		= "Routine";
	
	/**
	 * Compliance unable.
	 */
	public static final String COMPL_UNABLE 				= "COMPL_UNABLE";
	
	/**
	 * Able to comply.
	 */
	public static final String COMPL_ABLE					= "COMPL_ABLE";
	
	/**
	 * Constants for main menu of controller.
	 */
	public static final String UNABLE_NO_REASON			= "UNABLE_NO_REASON";
	public static final String UNABLE_CONGESTION			= "UNABLE_CONGESTION";
	public static final String UNABLE_BUSY					= "UNABLE_BUSY";
	public static final String UNABLE_QUEUE_INDIC			= "UNABLE_QUEUE_INDIC";
	public static final String UNABLE_STATION_BARRED		= "UNABLE_STATION_BARRED";
	public static final String UNABLE_NO_OPERATOR			= "UNABLE_NO_OPERATOR";
	public static final String UNABLE_OPERATOR_UNAVAIL		= "UNABLE_OPERATOR_UNAVAIL";
	public static final String UNABLE_EQUIP_DISABLE		= "UNABLE_EQUIP_DISABLE";
	public static final String UNABLE_CHANNEL_UNAVAIL		= "UNABLE_CHANNEL_UNAVAIL";
	public static final String UNABLE_MODE					= "UNABLE_NO_MODE";
		
	/**
	 * default values.
	 */
	public static final String KEY_MASTER_IPADDRESS 		= "master_address";
	public static final String KEY_MASTER_PORT				= "master_port";
	public static final String MASTER_IPADDRESS 			= "127.0.0.1";
	public static final String MASTER_PORT					= "9001";
	
	/**
	 * Strings for startup parameters.
	 */
	public static final String KEY_SCREEN_FILE				= "dscsim.screen.xml";
	public static final String KEY_INDIVIDUAL_MMSI			= "individual.mmsi";
	public static final String KEY_GROUP_MMSI				= "group.mmsi";
	public static final String KEY_PROVIDER_URL			= "dscsim.provider.url";
	public static final String KEY_PROVIDER_USER			= "dscsim.provider.user";
	public static final String KEY_PROVIDER_PWD			= "dscsim.provider.pwd";	
	public static final String KEY_PROVIDER_SUBJECT		= "dscsim.provider.subject";	
	public static final String KEY_UDP_AIRWAVE				= "dscsim.udp_airwave.peerhost";	
	public static final String KEY_STARTUP_FILE			= "startup.cfg=";
	public static final String KEY_IAC				    	= "dscsim.provider.protocol";
	public static final String IAC_UDP                     	= "UDP";
	public static final String IAC_YIMSG                   	= "YIMSG";
	public static final String IAC_JMS                   	= "JMS";
	public static final String IAC_AIRWAVE                 	= "AIRWAVE";	
	public static final String KEY_OP_MODE 				= "dscsim.syncmode.role";	
	public static final String OP_MODE_MASTER 				= "MASTER";
	public static final String OP_MODE_SLAVE 				= "SLAVE";
	public static final String OP_MODE_STANDALONE 			= "STANDALONE";
	public static final String KEY_IAC_SYNC                	= "dscsim.syncmode.protocol";
	public static final String IACS_TCP                    	= "TCP";
	public static final String IACS_YIMSG					= "YIMSG";
	

	
	public static final String MENU_ACT_NETWORK_SETUP		= "CONFIG_NETWORK";
	public static final String MENU_ACT_SIM_SETUP			= "CONFIG_SIMUL";

	/**
	 * Button OK.
	 */
	public static final String AWT_OK						= "OK";
	
	/**
	 * Button Cancel
	 */
	public static final String AWT_CAN					= "CANCEL";

	/**
	 * Intial GUI size.
	 */
	public static final int DEF_DIALOG_HEIGHT				= 250;
	public static final int DEF_DIALOG_WIDTH				= 450;
	public static final int READ_BUFF_SMALL				= 1024;
	
	/**
	 * Parameter name for simulation of GPS.
	 */
	public static final String SETUP_GPS					= "GPS";
	
	/**
	 * Virtual button string ids.
	 * */
	public static final String DSC_POWERED_ON				= "POWERED_ON";
	public static final String DSC_POWERED_OFF				= "POWERED_OFF";
	public static final String DSC_RESET					= "RESET";
		
	/**
	 * Virtual buttons - mostly originate from Radio.
	 */
	public static final Button BTN_POWER_ON 				= new Button(0,0,0,0, DSC_POWERED_ON);
	public static final Button BTN_POWER_OFF 				= new Button(0,0,0,0, DSC_POWERED_OFF);
	public static final Button BTN_RESET 					= new Button(0,0,0,0, DSC_RESET);

	
}
