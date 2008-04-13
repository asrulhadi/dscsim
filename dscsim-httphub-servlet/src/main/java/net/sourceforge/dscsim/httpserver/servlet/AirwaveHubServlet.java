package net.sourceforge.dscsim.httpserver.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.dscsim.httpserver.GroupManager;
import net.sourceforge.dscsim.httpserver.PacketQueue;
import net.sourceforge.dscsim.httpserver.QueueManager;
import net.sourceforge.dscsim.util.ByteArrayUtil;
import net.sourceforge.dscsim.util.ByteConverter;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class for Servlet: AirwaveHubServlet
 *
 */
 public class AirwaveHubServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;

   /**
	 * The logger for this class
	 */
	private static Logger LOGGER = Logger.getLogger(AirwaveHubServlet.class);
   
   /**
    * Name of the HTTP header parameter containing the magic number;
    */
   private static final String MAGIC_NUMBER_HEADER_NAME = "magicNumber";
   
   /**
    * Name of the HTTP header parameter containing the magic number;
    */
   private static final String AIRWAVE_UID_HEADER_NAME = "airwaveUID";
   
   /**
    * Name of the HTTP header parameter containing the last turnaround time
    * which was measured at the client side
    */
   private static final String TURNAROUND_TIME_HEADER_NAME = "turnaround";

   /**
    * The maximum number of packets to send to the client in one response
    */
   private static final int MAX_PACKETS_PER_RESONSE = 100;
   
   /**
    * The length of the prefix of the encoded downlink data packets (server->client)
    */
   private static final int DOWNLINK_PREFIX_LENGTH = 6;
   
   /**
    * The length of the prefix of the encoded uplink data packets (client->server) 
    */
   private static final int UPLINK_PREFIX_LENGTH = 2;

   /**
    * The version id of the data encoding
    */
   private static final byte PROTOCOL_VERSION = 1;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public AirwaveHubServlet() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.debug("Test");
		if( request.getHeader(MAGIC_NUMBER_HEADER_NAME) == null ||
			request.getHeader(AIRWAVE_UID_HEADER_NAME) == null ) {
			createErrorPage(response);
			return;
		}
		int magicNumber = request.getIntHeader(MAGIC_NUMBER_HEADER_NAME);
		int airwaveUid = request.getIntHeader(AIRWAVE_UID_HEADER_NAME);
		int clientSideTurnaround = request.getIntHeader(TURNAROUND_TIME_HEADER_NAME);
		
		GroupManager groupManager = GroupManager.getInstance();
		QueueManager queueManager = groupManager.getQueueManager(magicNumber);
		PacketQueue packetQueue = queueManager.getQueue(airwaveUid);

		packetQueue.setTurnaroundTime(clientSideTurnaround);
		long startPollTimeStamp = System.currentTimeMillis();
		byte[] dataPacket = packetQueue.poll();
		long endPollTimeStamp = System.currentTimeMillis();
		int waitDelay = (int)(endPollTimeStamp - startPollTimeStamp);

		List<byte[]> bAList = new ArrayList<byte[]>(10);
		if( dataPacket != null ) {
			bAList.add(dataPacket);
			int packets = 1;
			while((packetQueue.getSize()>0) && (packets<MAX_PACKETS_PER_RESONSE)) {
				bAList.add(packetQueue.poll());
				packets++;
			}
		}
		byte[] encodedDataPackets = ByteArrayUtil.encode(bAList, DOWNLINK_PREFIX_LENGTH);
		encodedDataPackets[0] = PROTOCOL_VERSION;
		encodedDataPackets[1] = packetQueue.getSendSequenceByte();
		ByteConverter.intToByteArray(waitDelay, encodedDataPackets, 2);
		
		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Cache-Control", "no-store");
		response.addHeader("Expires", "0");
		response.addHeader("pragma","no-cache");
		response.setContentType("application/octet-stream");
		response.setContentLength(encodedDataPackets.length);
		OutputStream os = response.getOutputStream();
		os.write(encodedDataPackets);
		os.flush();
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.debug("Test");
		if( request.getHeader(MAGIC_NUMBER_HEADER_NAME) == null ||
			request.getHeader(AIRWAVE_UID_HEADER_NAME) == null ) {
			createErrorPage(response);
			return;
		}
		int magicNumber = request.getIntHeader(MAGIC_NUMBER_HEADER_NAME);
		int airwaveUid = request.getIntHeader(AIRWAVE_UID_HEADER_NAME);

		InputStream is = request.getInputStream();
		byte[] dataPacket = new byte[request.getContentLength()];
		is.read(dataPacket);
		if(dataPacket[0] != PROTOCOL_VERSION) {
			LOGGER.warn("Wrong protocol version found in uplink data: "+dataPacket[0]+" (Airwave: "+airwaveUid+")");
			// TODO send error to client
		} else {
			List<byte[]> bAList = ByteArrayUtil.decode(dataPacket, UPLINK_PREFIX_LENGTH);
			
			GroupManager groupManager = GroupManager.getInstance();
			QueueManager queueManager = groupManager.getQueueManager(magicNumber);
			for( byte[] data : bAList ) {
				queueManager.pushToQueues(airwaveUid, data);
			}
			PacketQueue queue =	queueManager.getQueue(airwaveUid);
			byte sequenceByte = dataPacket[1];
			byte expectedSequenceByte = queue.getSetReceiveSequenceByte(sequenceByte);
			if( expectedSequenceByte != sequenceByte ) {
				LOGGER.info("Mismatch in uplink sequence byte for airwave "+airwaveUid+" - Expected: "+expectedSequenceByte+" but was: "+sequenceByte );
			}
		}
	}   	
	
	/**
	 * Creates an HTML error page
	 * @param response the response object
	 */
	private void createErrorPage(HttpServletResponse response) {
			response.setContentType("text/html");
			PrintWriter out;
			try {
				LOGGER.info("Writing error page");
				out = response.getWriter();
				out.println("<html><head><title>Error</title></head><body>An Error occured</body></html>");		
			} catch (IOException e) {
				LOGGER.error("Error while writing error page", e);
			}
	}
}