/*
 * Created on 26.02.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.sourceforge.dscsim.controller.utils;

import org.apache.log4j.Logger;

/**
 * @author katharina
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AppLogger {

	/**
	 * The logger for this class
	 */
	private static Logger _logger = Logger.getLogger(AppLogger.class);
	
	public static void error(Exception oEx) {
		_logger.error("", oEx);
//		System.out.print("DSCSIM ERROR>");		
//		oEx.printStackTrace(System.out);
	}
	
	public static void error(String strMsg) {
		_logger.error(strMsg);
//		System.out.println("DSCSIM ERROR>" + strMsg);
		//Log4jWrapper.errorx(strMsg);
	}
	
	public static void debug(String strMsg) {		
		//System.out.println("DSCSIM DEBUG>" + strMsg);
		//Log4jWrapper.debug(strMsg);
	}
	
	public static void debug2(String strMsg) {
		_logger.debug(strMsg);
//		System.out.println("DSCSIM DEBUG2>" + strMsg);
//		//Log4jWrapper.debug(strMsg);
	}
	
	public static void info(String strMsg) {
		_logger.info(strMsg);
//		System.out.println("DSCSIM INFO>" + strMsg);
//		//Log4jWrapper.debug(strMsg);
	}
}
