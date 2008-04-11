/**
 * 
 */
package net.sourceforge.dscsim.httpserver.server;

import net.sourceforge.dscsim.httpserver.GroupManager;
import net.sourceforge.dscsim.httpserver.servlet.AirwaveHubServlet;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;


/**
 * @author oliver
 *
 */
public class DscsimServer {

	/**
	 * The logger for this class
	 */
	private static Logger LOGGER = Logger.getLogger(DscsimServer.class);

	/**
	 * The port
	 */
	private static final int SERVER_PORT = 8080;
	
	/**
	 * The jetty dscsimServer
	 */
	private Server server;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public void startServer() {
		GroupManager.reInitialize();
		if( server != null ) {
			throw new IllegalStateException( "Server already started" );
		} else {
			server = new Server(SERVER_PORT);
			Context root = new Context(server,"/",Context.SESSIONS);
			root.addServlet(new ServletHolder(new AirwaveHubServlet()), "/*");
			try {
				server.start();
				while (!server.isStarted()) {
					Thread.sleep(1000);
				}
				LOGGER.info("HTTP Server started" );
			} catch (Exception e) {
				LOGGER.error("Problem while starting server", e);
			}

		}
	}

	public void stopServer() {
		if( server == null ) {
			throw new IllegalStateException( "Server is not started" );
		} else {
			try {
				server.stop();
				while (!server.isStopped()) {
					Thread.sleep(1000);
				}
				LOGGER.info("HTTP Server stopped" );
			} catch (Exception e) {
				LOGGER.error("Problem while stopping server", e);
			}
			server = null;
		}
		
	}
}
