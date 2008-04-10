/**
 * 
 */
package net.sourceforge.dscsim.httpserver.servlet;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import net.sourceforge.dscsim.httpserver.server.DscsimServer;
import net.sourceforge.dscsim.util.ByteArrayUtil;
import junit.framework.TestCase;

/**
 * @author oliver
 *
 */
public class AirwaveHubServletTest extends TestCase {
	
	private static final Logger LOGGER = Logger.getLogger(AirwaveHubServletTest.class); 
	
	private abstract class TestThread extends Thread {
		public Throwable failure = null;
		public void run() {
			try {
				doTest();
			} catch( Throwable t) {
				failure = t;
			}
		}
		public void evaluate() throws Throwable {
			if( failure != null ) {
				throw failure;
			}
		}
		public abstract void doTest();
	}

	private static void doTests(TestThread[] tta) throws Throwable {
		LOGGER.info("Starting parallel tests");
		for( int i=0; i<tta.length; i++ ) {
			tta[i].start();
		}
		LOGGER.info("Waiting for parallel tests to finish");
		for( int i=0; i<tta.length; i++ ) {
			tta[i].join();
		}
		LOGGER.info("Evaluating parallel test results");
		for( int i=0; i<tta.length; i++ ) {
			tta[i].evaluate();
		}
	}
	
	private static final String URL = "http://localhost:8080/";
	
	private DscsimServer server;
	
	private byte[] array1 = new byte[] { (byte)1, (byte)2, (byte)3 };
	private byte[] array2 = new byte[] { (byte)4, (byte)5, (byte)6, (byte)7 };
	
	private List<byte[]> a1List;
	private List<byte[]> a2List;
	private List<byte[]> a1a2List;
	
	private byte[] arrayEmpty = new byte[0];
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		server = new DscsimServer();
		server.startServer();
		a1List = new ArrayList<byte[]>();
		a1List.add(array1);
		a2List = new ArrayList<byte[]>();
		a2List.add(array2);
		a1a2List = new ArrayList<byte[]>();
		a1a2List.add(array1);
		a1a2List.add(array2);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		server.stopServer();
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.servlet.AirwaveHubServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	public void testDoGetWithoutParameters() {
		assertTrue( doGetRequestWithoutParameter().contains("An Error occured"));
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.servlet.AirwaveHubServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	public void testDoGetWithParameters() {
		assertTrue( Arrays.equals(arrayEmpty, doGetRequest(6543, 1234) ) );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.servlet.AirwaveHubServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	public void testDoPostWithoutParameters() {
		assertTrue( doPostRequestWithoutParameter().contains("An Error occured"));
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.servlet.AirwaveHubServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	public void testDoPostWithParameters() {
		assertTrue( Arrays.equals(arrayEmpty, doGetRequest(6543, 1234) ) );
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.servlet.AirwaveHubServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * and {@link net.sourceforge.dscsim.httpserver.servlet.AirwaveHubServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	public void testSimpleScenario() throws Throwable {
		TestThread[] tta = new TestThread[] {
			new TestThread() {
				public void doTest() {
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(1, 1) ) );
				}
			},
			new TestThread() {
				public void doTest() {
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(1, 2) ) );
				}
			},
			new TestThread() {
				public void doTest() {
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(2, 1) ) );
				}
			},
			new TestThread() {
				public void doTest() {
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(2, 2) ) );
				}
			}
		};
		doTests(tta);
		
	}
	
	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.servlet.AirwaveHubServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * and {@link net.sourceforge.dscsim.httpserver.servlet.AirwaveHubServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	public void testSimpleScenario2() throws Throwable {

		doPostRequest(1,1,ByteArrayUtil.encode(a1List, 0));
		doPostRequest(1,1,ByteArrayUtil.encode(a2List, 0));
		TestThread[] tta = new TestThread[] {
			new TestThread() {
				public void doTest() {
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(1, 1) ) );
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(1, 1) ) );
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(1, 1) ) );
				}
			},
			new TestThread() {
				public void doTest() {

					assertTrue( arrayListEquals(a1a2List, ByteArrayUtil.decode(doGetRequest(1, 2), 0)));
//					assertTrue( Arrays.equals(array1,     doGetRequest(1, 2) ) );
//					assertTrue( Arrays.equals(array2,     doGetRequest(1, 2) ) );
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(1, 2) ) );
				}
			},
			new TestThread() {
				public void doTest() {
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(2, 1) ) );
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(2, 1) ) );
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(2, 1) ) );
				}
			},
			new TestThread() {
				public void doTest() {
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(2, 2) ) );
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(2, 2) ) );
					assertTrue( Arrays.equals(arrayEmpty, doGetRequest(2, 2) ) );
				}
			}
		};
		doTests(tta);
		
	}

	/**
	 * Test method for {@link net.sourceforge.dscsim.httpserver.servlet.AirwaveHubServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * and {@link net.sourceforge.dscsim.httpserver.servlet.AirwaveHubServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
	 */
	public void testSimpleScenario3() throws Throwable {
		doPostRequest(2,1,ByteArrayUtil.encode(a1List, 0));
		doPostRequest(2,2,ByteArrayUtil.encode(a2List, 0));
		TestThread[] tta = new TestThread[] {
				new TestThread() {
					public void doTest() {
						assertTrue( Arrays.equals(arrayEmpty, doGetRequest(1, 1) ) );
						assertTrue( Arrays.equals(arrayEmpty, doGetRequest(1, 1) ) );
					}
				},
				new TestThread() {
					public void doTest() {
						assertTrue( Arrays.equals(arrayEmpty, doGetRequest(1, 2) ) );
						assertTrue( Arrays.equals(arrayEmpty, doGetRequest(1, 2) ) );
					}
				},
				new TestThread() {
					public void doTest() {
						assertTrue( arrayListEquals(a2List, ByteArrayUtil.decode(doGetRequest(2, 1), 0)));
//						assertTrue( Arrays.equals(array2,     doGetRequest(2, 1) ) );
						assertTrue( Arrays.equals(arrayEmpty, doGetRequest(2, 1) ) );
					}
				},
				new TestThread() {
					public void doTest() {
						assertTrue( arrayListEquals(a1List, ByteArrayUtil.decode(doGetRequest(2, 2), 0)));
//						assertTrue( Arrays.equals(array1,     doGetRequest(2, 2) ) );
						assertTrue( Arrays.equals(arrayEmpty, doGetRequest(2, 2) ) );
					}
				}
			};
			doTests(tta);
		
	}

	/**
	 * Do a get request on the server without any parameters.
	 * @return the response body as string
	 */
	private String doGetRequestWithoutParameter( ) {
		HttpClient httpclient = new HttpClient();
		GetMethod httpget = new GetMethod(URL);
		String retValue = null;
		try {
		  httpclient.executeMethod(httpget);
		  retValue = httpget.getResponseBodyAsString();
		} catch( Exception e ) {
		  e.printStackTrace();
		} finally {
		   httpget.releaseConnection();
		}
		return retValue;
	}
	
	/**
	 * Do a get request on the server without any parameters.
	 * @return the response body as string
	 */
	private String doPostRequestWithoutParameter( ) {
		HttpClient httpclient = new HttpClient();
		PostMethod httppost = new PostMethod(URL);
		String retValue = null;
		try {
		  httpclient.executeMethod(httppost);
		  retValue = httppost.getResponseBodyAsString();
		} catch( Exception e ) {
		  e.printStackTrace();
		} finally {
		   httppost.releaseConnection();
		}
		return retValue;
	}

	/**
	 * Do a get request with the given parameters.
	 * @param magicNumber the magicNumber request parameter
	 * @param uid the airwaveUID request parameter
	 * @return the response body as byte array
	 */
	private byte[] doGetRequest( int magicNumber, int uid ) {
		HttpClient httpclient = new HttpClient();
		GetMethod httpget = new GetMethod(URL);
		httpget.addRequestHeader("magicNumber", ""+magicNumber);
		httpget.addRequestHeader("airwaveUID", ""+uid);
		byte[] retValue = null;
		try {
		  httpclient.executeMethod(httpget);
		  retValue = httpget.getResponseBody();
		} catch( Exception e ) {
		  e.printStackTrace();
		} finally {
		   httpget.releaseConnection();
		}
		return retValue;
	}

	/**
	 * Do a post request with the given parameters.
	 * @param magicNumber the magicNumber request parameter
	 * @param uid the airwaveUID request parameter
	 * @param body the request body as byte array
	 * @return the response body as byte array
	 */
	private byte[] doPostRequest( int magicNumber, int uid, byte[] body ) {
		HttpClient httpclient = new HttpClient();
		PostMethod httppost = new PostMethod(URL);
		httppost.addRequestHeader("magicNumber", ""+magicNumber);
		httppost.addRequestHeader("airwaveUID", ""+uid);
		httppost.setRequestEntity( new ByteArrayRequestEntity(body) );
		byte[] retValue = null;
		try {
		  httpclient.executeMethod(httppost);
		  retValue = httppost.getResponseBody();
		} catch( Exception e ) {
		  e.printStackTrace();
		} finally {
		   httppost.releaseConnection();
		}
		return retValue;
	}

	/**
	 * Helper method to check if two lists of byte arrays are equal
	 */
	private static boolean arrayListEquals( List<byte[]> list1, List<byte[]> list2) {
		if( list1.size() != list2.size() ) {
			return false;
		}
		Iterator<byte[]> i = list1.iterator();
		Iterator<byte[]> j = list2.iterator();
		while( i.hasNext() ) {
			if( !Arrays.equals(i.next(), j.next() ) ){
				return false;
			}
		}
		return true;
	}

}
