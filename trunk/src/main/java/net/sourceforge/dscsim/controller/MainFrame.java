package net.sourceforge.dscsim.controller;

import java.applet.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageProducer;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import net.sourceforge.dscsim.controller.utils.AppLogger;

import sun.applet.AppletAudioClip;
import sun.misc.BASE64Decoder;


/** This class is an AWT window frame that can be used to hold an Applet.
  * You can use this to convert your Applet into an application in one line
  * of code!
  *
  * <p>Simply write,
  *
  * new MainFrame(myApplet, myWidth, myHeight).show();
  *
  * to show your Applet as an application.
  *
  *  @author Martin Stepp (stepp)
  *  @version Aug 6, 2002
  */
public class MainFrame extends Frame implements Runnable, Constants
{
  private class MainFrameStub implements AppletContext, AppletStub {
    public InputStream getStream(String s) { return null; }
    public Iterator getStreamKeys()        { return null; }
    public void setStream(String s, InputStream i) {}
    
    public Applet getApplet(String s)
    {
      if(s.equals(name))
        return applet;
      else
        return null;
    }

    public Enumeration getApplets()
    {
      Vector vector = new Vector();
      vector.addElement(applet);
      return vector.elements();
    }

    public AudioClip getAudioClip(URL url)
    {
      return new AppletAudioClip(url);
    }

    public Image getImage(URL url)
    {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      try
      {
        ImageProducer imageproducer = (ImageProducer)url.getContent();
        return toolkit.createImage(imageproducer);
      }
      catch(IOException ioexception)
      {
        return null;
      }
    }

    public void showDocument(URL url)
    {
    }

    public void showDocument(URL url, String s)
    {
    }

    public void showStatus(String s)
    {
      if(label != null)
        label.setText(s);
    }

    public void appletResize(int i, int j)
    {
      Dimension dimension = getSize();
      dimension.width += i - appletSize.width;
      dimension.height += j - appletSize.height;
      setSize(dimension);
      appletSize = applet.getSize();
    }

    public AppletContext getAppletContext()
    {
      return this;
    }

    public URL getCodeBase()
    {
      return getDocumentBase();
/*      
      String s = System.getProperty("java.class.path");
      for(StringTokenizer stringtokenizer = new StringTokenizer(s, ":"); stringtokenizer.hasMoreElements();)
      {
        String s1 = (String)stringtokenizer.nextElement();
        String s2 = s1 + File.separatorChar + name + ".class";
        File file = new File(s2);
        if(file.exists())
        {
          String s3 = s1.replace(File.separatorChar, '/');
          try
          {
            return new URL("file:" + s3 + "/");
          }
          catch(MalformedURLException malformedurlexception)
          {
            return null;
          }
        }
      }

      return null;
*/
    }

    public URL getDocumentBase()
    {
      String s = System.getProperty("user.dir");
      String s1 = s.replace(File.separatorChar, '/');
      try
      {
        return new URL("file:" + s1 + "/");
      }
      catch(MalformedURLException malformedurlexception)
      {
        return null;
      }
    }

    public String getParameter(String s)
    {
      AppLogger.debug2("MainFrameStub.getParameter for " + s);
      return System.getProperty("parameter." + s.toLowerCase());
    }

    public boolean isActive()
    {
      return true;
    }
  }



  private MainFrameStub myStub = null;

  public MainFrame(Applet applet1, String as[], int i, int j)
  {
    args = null;
    barebones = true;
    label = null;
    build(applet1, as, i, j);
  }

  public MainFrame(Applet applet1, String as[])
  {
    args = null;
    barebones = true;
    label = null;
    build(applet1, as, -1, -1);
  }

  public MainFrame(Applet applet1, int i, int j)
  {
    args = null;
    barebones = true;
    label = null;
    build(applet1, null, i, j);
  }

  private void build(Applet applet1, String as[], int i, int j)
  {
    applet = applet1;
    args = as;

    myStub = new MainFrameStub();
    applet1.setStub(myStub);
    name = applet1.getClass().getName();
    setTitle(name);
    Properties properties = System.getProperties();
    properties.put("browser", "Acme.MainFrame");
    properties.put("browser.version", "11jul96");
    properties.put("browser.vendor", "Acme Laboratories");
    properties.put("browser.vendor.url", "http://www.acme.com/");
    if(as != null){
    	
    	if(as.length > 0 && as[0].startsWith(KEY_STARTUP_FILE)){
    		parseCfg(as, properties);   		
    	}else{
    		parseArgs(as, properties);
    	}
    }
    String s = myStub.getParameter("width");
    if(s != null)
      i = Integer.parseInt(s);
    String s1 = myStub.getParameter("height");
    if(s1 != null)
      j = Integer.parseInt(s1);
    if(i == -1 || j == -1)
    {
      System.err.println("Width and height must be specified.");
      return;
    }
    String s2 = myStub.getParameter("barebones");
    if(s2 != null && s2.equals("true"))
      barebones = true;
    //setLayout(new BorderLayout());
    add("Center", applet1);
    pack();
    validate();
    appletSize = applet1.getSize();
    applet1.setSize(i, j);
    setVisible(true);
    addWindowListener(new WindowAdapter() {

      public void windowClosing(WindowEvent windowevent)
      {
        System.exit(0);
      }

    });
    (new Thread(this)).start();
  }

  private static void parseCfg(String as[], Properties properties)
  {

    	
	try {
		
		int j = as[0].indexOf(61);
		String fileName = RESOURCE_BASE + as[0].substring(j + 1);
		
		FileInputStream fis = new FileInputStream(fileName);
		char buf[] = new char[READ_BUFF_SMALL];
		
		Reader r = new BufferedReader(new InputStreamReader(fis));
		
		int read = r.read(buf);
		
		String cfgStr = String.valueOf(buf, 0, read);
		StringTokenizer cfgTknzr = new StringTokenizer(cfgStr);

		while(cfgTknzr.hasMoreElements()){
			
			cfgStr =  cfgTknzr.nextToken();
		    AppLogger.debug2("MainFrame.parseCfg = " + cfgStr);	
		    j = cfgStr.indexOf(61);
		    
		    if(j>-1)
		    	properties.put("parameter." + cfgStr.substring(0, j).toLowerCase(), cfgStr.substring(j + 1));
		    
			
		}
	} catch (FileNotFoundException oEx) {
		AppLogger.error(oEx);
	} catch (IOException oEx) {
		AppLogger.error(oEx);
	}
	
        
      
    

  }
  
  private static void parseArgs(String as[], Properties properties)
  {
    for(int i = 0; i < as.length; i++)
    {
      AppLogger.debug2("MainFrame.parseArgs = " + as[i]);
      String s = as[i];
      int j = s.indexOf(61);
      if(j == -1)
        properties.put("parameter." + s.toLowerCase(), "");
      else
        properties.put("parameter." + s.substring(0, j).toLowerCase(), s.substring(j + 1));
    }

  }

  public void run()
  {
    myStub.showStatus(name + " initializing...");
    applet.init();
    validate();
    myStub.showStatus(name + " starting...");
    applet.start();
    validate();
    myStub.showStatus(name + " running...");
  }

  private String args[];
  private String name;
  private boolean barebones;
  private Applet applet;
  private Label label;
  private Dimension appletSize;
  private static final String PARAM_PROP_PREFIX = "parameter.";

}

