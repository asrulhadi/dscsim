/*
 * Created on 07.01.2007
 * 
 * The contents of this file are subject to the Mozilla Public License Version 1.0
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
 
package net.sourceforge.dscsim.common;

import java.awt.Window;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Window for showing splash screen during startup.
 */
public class SplashScreen extends Window {

	/**
	 * Is singleton.
	 */
    private static SplashScreen instance;

    /**
     * The image to be shown.
     */
    private Image image;

    /**
     * Paint called.
     */
    private boolean paintCalled = false;
    
    /**
     * SplashScreen constructor.
     * @param parent
     * @param image
     */
    private SplashScreen(Frame parent, Image image) {
        super(parent);
        this.image = image;
        
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image,0);
        try {
            mt.waitForID(0);
        } catch(InterruptedException ie){}
        
        if (mt.isErrorID(0)) {
            setSize(0,0);
            System.err.println("Warning: Splash image could not be loaded.");
            synchronized(this) {
                paintCalled = true;
                notifyAll();
            }
            return;
        }
        
        int imgWidth = image.getWidth(this);
        int imgHeight = image.getHeight(this);
        setSize(imgWidth, imgHeight);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
        (screenDim.width - imgWidth) / 2,
        (screenDim.height - imgHeight) / 2
        );
        
        MouseAdapter disposeOnClick = new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                synchronized(SplashScreen.this) {
                    SplashScreen.this.paintCalled = true;
                    SplashScreen.this.notifyAll();
                }
                dispose();
            }
        };
        addMouseListener(disposeOnClick);
    }
    

    /**
     * Call paint.
     */
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Draw image.
     */
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);

        if (! paintCalled) {
            paintCalled = true;
            synchronized (this) { notifyAll(); }
        }
    }
    
    public static void splash(Image image) {
        if (instance == null && image != null) {
            Frame f = new Frame();
            
            instance = new SplashScreen(f, image);
            
            instance.setVisible(true);
            
            if (! EventQueue.isDispatchThread()
            && Runtime.getRuntime().availableProcessors() == 1) {
                synchronized (instance) {
                    while (! instance.paintCalled) {
                        try { instance.wait(); } catch (InterruptedException e) {}
                    }
                }
            }
        }
    }

    public static void splash(URL imageURL) {
        if (imageURL != null) {
            splash(Toolkit.getDefaultToolkit().createImage(imageURL));
        }
    }
    
    public static void disposeSplash() {
        if (instance != null) {
            instance.getOwner().dispose();
            instance = null;
        }
    }
    
    public static void invokeMain(String className, String[] args) {
        try {
            Class.forName(className)
            .getMethod("main", new Class[] {String[].class})
            .invoke(null, new Object[] {args});
        } catch (Exception e) {
            InternalError error = new InternalError("InternalError - Failed to invoke main method");
            error.initCause(e);
            throw error;
        }
    }
    
    public Image getImage(){
    	return this.image;
    }
    
    public static SplashScreen getInstance(){
    	return instance;
    }
}
