package net.sourceforge.dscsim.httpserver.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.sourceforge.dscsim.httpserver.GroupManager;
import net.sourceforge.dscsim.httpserver.PacketQueue;
import net.sourceforge.dscsim.httpserver.QueueManager;

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
		if( request.getHeader(MAGIC_NUMBER_HEADER_NAME) == null ||
			request.getHeader(AIRWAVE_UID_HEADER_NAME) == null ) {
			createErrorPage(response);
			return;
		}
		int magicNumber = request.getIntHeader(MAGIC_NUMBER_HEADER_NAME);
		int airwaveUid = request.getIntHeader(AIRWAVE_UID_HEADER_NAME);
		
		GroupManager groupManager = GroupManager.getInstance();
		QueueManager queueManager = groupManager.getQueueManager(magicNumber);
		PacketQueue packetQueue = queueManager.getQueue(airwaveUid);
		byte[] dataPacket = packetQueue.poll();
		if( dataPacket == null ) {
			dataPacket = new byte[0];
		}
		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Cache-Control", "no-store");
		response.addHeader("Expires", "0");
		response.addHeader("pragma","no-cache");
		response.setContentType("application/octet-stream");
		response.setContentLength(dataPacket.length);
		OutputStream os = response.getOutputStream();
		os.write(dataPacket);
		os.flush();
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		
		GroupManager groupManager = GroupManager.getInstance();
		QueueManager queueManager = groupManager.getQueueManager(magicNumber);
		queueManager.pushToQueues(airwaveUid, dataPacket);
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