package net.sourceforge.dscsim.orga;
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
 * the Initial Developer are Copyright (C) 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/**
 * Creates the HTML pages with the necessary data to set up the dscsim
 * instances.
 */
public class FrequencyAssign {

	private static String[] stationNames = { "Aurelia", "Halligalli","Hera", "Eva", "Klausi", "Sunny", "Andrea",
			  "Baracuda", "Up_and_Away", "Eierkopf", "Mon_Amour", "Schatzi", "Knochenbrecher",
			  "Eva2", "Carmen", "Sehnsucht", "Rubin", "Diamant", "Flash", "Speedy_Gonzales",
			  "Prinzessin", "Mad_Max", "Knutschfleck", "Bounty", "Marianne", "Erika", "Zauberer",
			  "Hexe", "Schnelle_Welle", "Knallkopp", "Karin", "Simba", "Sonnenschein", "Flunder",
			  "Beulchen", "Schnepfe", "Negerkuss", "Fluppe", "Kesse_Maus", "Klapperstorch"};
	private long startMmsi;
	private int startRz;
	private int port;
	private boolean isPortSet;
	private int magicNumber;
	private boolean isMagicNumberSet;
	private String groupMmsi;
	private boolean isGroupMmsiSet;
	private String nucleusIP;
	private boolean isNucleusIPSet;
	private File workingDir; 

	public FrequencyAssign() {
		startMmsi = 211000000;
		startRz=5382;
		port = 777;
		magicNumber = 98367987;
		groupMmsi = "021101600";
		nucleusIP = "localhost";
	}

	public void makeFiles() throws IOException {
		Set usedNumbers = new HashSet();
		Random rand = new Random(startRz);
		
		PrintWriter index = new PrintWriter( new FileWriter( new File(workingDir, "index.html" ) ) );
		index.println( "<html><head><title>&Uuml;bersicht Frequenzzuteilung dscsim</title></head>");
		index.println( "<body><table border=\"1\"><tr><th>MMSI</th><th>Name des Schiffes</th><th width=\"80%\">Bemerkung</th></tr>");
		for(int i=0; i<stationNames.length; i++) {
			String stationName = stationNames[i];
			int rNumber;
			Integer rNumberObject;
			do {
				rNumber = rand.nextInt();
				rNumberObject = new Integer( rNumber % 10000 );
			} while( (usedNumbers.contains(rNumberObject)) || ( rNumber < 0 ) );
			long mmsi = startMmsi+((rNumber%10000)*10);
			String callSign = "DB"+((5376+rNumber)%10000);
			String setup="individual.mmsi=" + mmsi +
					" dscsim.call_sign=" + callSign +
					" dscsim.station_name=" + stationName;
			if( isGroupMmsiSet ) {
				setup += " group.mmsi=" + groupMmsi;
			}
			if( isMagicNumberSet ) {
				setup += " dscsim.udp_airwave.magicnumber="+ this.magicNumber;
			}
			if( isNucleusIPSet ) {
				setup += " dscsim.udp_airwave.peerhost=" + this.nucleusIP;
			}
			if( isPortSet ) {
				setup += " dscsim.udp_airwave.startport=" + this.port;
			}
			PrintWriter out = new PrintWriter(  new FileWriter( new File(workingDir,""+mmsi+".html")));
			index.println( "<tr height=\"30\"><td><a href=\""+mmsi+".html\">"+mmsi+"</a></td><td>"+stationName+"</td><td>&nbsp</td></tr>");
			out.println( "<html><head><title>dscsim-Frequenzzuteilung "+mmsi+"</title></head>");
			out.println("<body>");
			out.println( "<H1><a href=\"http://dscsim.sourceforge.net\">dscsim</a>-Frequenzzuteilungsurkunde</H1>" );
			out.println( "<table>" );
			out.println( "<tr><td>Name des Schiffes:</td><td><B>"+stationName+"</B></td></tr>");
			out.println( "<tr><td>MMSI: </td><td><B>"+mmsi+"</B></td></tr>");
			out.println( "<tr><td>Gruppen-MMSI: </td><td><B>"+groupMmsi+"</B></td></tr>");
			out.println( "<tr><td>Rufzeichen: </td><td><B>"+callSign+"</B></td></tr>");
			out.println( "</table>");
			out.println( "<P/>");
			out.println( "dscsim-Setupstring:<p/>");
			out.println( "<TT>"+setup+"</TT><p/>");
			out.println("</body></html>");
			out.println( "(Geben Sie den obigen String beim ersten Starten von dscsim ein.");
			out.println( "Benutzen Sie m&ouml;glichst die Cut-and-Paste-Funktionalit&auml;t, ");
			out.println( "um Tippfehler zu vermeiden.)</P>");
			out.println( "Weitere Infos und Download von dscsim unter <i>" +
					"<a href=\"http://dscsim.sourceforge.net\">http://dscsim.sourceforge.net</a></i>" );
			out.close();
			
		}
		index.println( "</table></body></html>");
		index.close();
	}

	/**
	 * @param groupMmsi the groupMmsi to set
	 */
	public void setGroupMmsi(String groupMmsi) {
		this.groupMmsi = groupMmsi;
		this.isGroupMmsiSet = true;
	}

	/**
	 * @param magicNumber the magicNumber to set
	 */
	public void setMagicNumber(int magicNumber) {
		this.magicNumber = magicNumber;
		this.isMagicNumberSet = true;
	}

	/**
	 * @param nucleusIP the nucleusIP to set
	 */
	public void setNucleusIP(String nucleusIP) {
		this.nucleusIP = nucleusIP;
		this.isNucleusIPSet = true;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
		this.isPortSet = true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception  {
		// TODO Auto-generated method stub
		new FrequencyAssign().makeFiles();

	}

	public void setDir(File workingDir) {
		this.workingDir = workingDir;
	}

}
