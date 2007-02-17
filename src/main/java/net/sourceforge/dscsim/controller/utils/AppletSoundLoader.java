package net.sourceforge.dscsim.controller.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;


class AppletSoundLoader extends Thread {
	
    private AppletSoundList soundList;
    private URL baseURL;
    private String relativeURL;

    public AppletSoundLoader(AppletSoundList soundList,
                             URL baseURL,
                             String relativeURL,
                             boolean wait) {
        this.soundList = soundList;
        this.baseURL = baseURL;
        this.relativeURL = relativeURL;
        
        if(wait==false){
        		setPriority(MIN_PRIORITY);
        		start();
        }else {
        		run();
        }
    }

    public void run() {
    					
    		try {              	           		 
    			File file = new File(relativeURL);	
    			AudioInputStream ain = AudioSystem.getAudioInputStream(file);
    			DataLine.Info info = new DataLine.Info(Clip.class,ain.getFormat( ));
    			Clip clip = (Clip) AudioSystem.getLine(info);
    			clip.open(ain);
    			soundList.putClip(clip, relativeURL);
		}
		catch(Exception oEx) {
		   AppLogger.error(oEx);
		}

    }
}
