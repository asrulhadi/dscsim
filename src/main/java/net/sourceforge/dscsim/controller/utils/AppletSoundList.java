package net.sourceforge.dscsim.controller.utils;

import java.net.URL;
import java.applet.AudioClip;

import javax.sound.sampled.Clip;

//Loads and holds a bunch of audio files whose locations are specified
//relative to a fixed base URL.
public class AppletSoundList extends java.util.Hashtable {
    private URL baseURL;
    
    static AppletSoundList _oInstance = null;

    private AppletSoundList(URL baseURL) {
        super(5); //Initialize Hashtable with capacity of 5 entries.
        this.baseURL = baseURL;
    }
    
    public synchronized static AppletSoundList createSingleton(URL baseURL) {
    		_oInstance = new AppletSoundList(baseURL);
    		return _oInstance;
    }
    
    public synchronized static void destroySingleton() {
    		_oInstance = null;
    }

    public static AppletSoundList getInstance() {
    		return _oInstance;
    }
    public void startLoading(String relativeURL) {
        new AppletSoundLoader(this,baseURL, relativeURL, false);
    }

    public void load(String relativeURL) {
        new AppletSoundLoader(this, baseURL, relativeURL, true);
    }
    
    public AudioClip getClip(String relativeURL) {
        return (AudioClip)get(relativeURL);
    }
    
    public Clip getRealClip(String relativeURL) {
        return (Clip)get(relativeURL);
    }

    public void putClip(Clip clip, String relativeURL) {
        put(relativeURL, clip);
    }
}
