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
 * The Initial Developer of the Original Code is William Pennoyer. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007, 2008, 20010.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.controller.network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;
import java.nio.channels.*;

import net.sourceforge.dscsim.controller.utils.AppLogger;



public class SyncListenerDispatcher implements Runnable, SyncListenerInterface {
	
	private ArrayList _dispatchQueue = new ArrayList();
	private ArrayList _subscribers = new ArrayList();
	
	private static SyncListenerDispatcher _oInstance = new SyncListenerDispatcher();
	
	private SyncListenerDispatcher(){
		(new Thread(this)).start();
	}
	
	public static SyncListenerDispatcher getInstance(){
		return _oInstance;
	}
	public  void addSubscriber(SyncListenerSubscriber oSubscriber){
		synchronized(_subscribers){
			_subscribers.add(oSubscriber);
		}
		
		
	}

	/* (non-Javadoc)
	 * @see network.SyncListenerInterface#removeSubscriber(network.SyncListenerSubscriber)
	 */
	public  void removeSubscriber(SyncListenerSubscriber oSubscriber){
		synchronized(_subscribers){
			_subscribers.remove(oSubscriber);	
		}
			
	}
	
	private void pushDispatchQueue(SyncMessage oMessage){
		
		synchronized(_dispatchQueue){
			
			_dispatchQueue.add(oMessage);
			
			AppLogger.debug("SyncListenerDispatcher.pushDispatchQueue queue size=" + _dispatchQueue.size());
			
			_dispatchQueue.notify();
		}
	
	}

	private  SyncMessage popDispatchQueue() {
		
		synchronized(_dispatchQueue){
			
			if(_dispatchQueue.size() > 0){
				return (SyncMessage)_dispatchQueue.remove(0);
			}
		
			try {
				_dispatchQueue.wait();
				
				if(_dispatchQueue.size()>0)
					return (SyncMessage)_dispatchQueue.remove(0);

			} catch (InterruptedException oEx) {
				AppLogger.error(oEx);
			}				
			
		}
		
		return null;
	}
	
	public void run(){
	
		Thread worker = new Thread(new SyncListener());
		worker.setName("Controller.SyncListenerDispatcher");
		worker.start();
		
		SyncMessage oSyncMsg = null;
		SyncListenerSubscriber oSubscriber = null;
		while(true){
			
			
			oSyncMsg = popDispatchQueue();
			
			
			if(oSyncMsg != null){
							
				
				synchronized (_subscribers){
					
					for(int i=0; i<_subscribers.size(); i++){
						
						oSubscriber = (SyncListenerSubscriber)_subscribers.get(i);

						try{
							
							oSubscriber.notifySycMessage(oSyncMsg);
							
						}catch(Throwable oEx){
							AppLogger.error("SyncListenerDispatcher.run" + oEx.getMessage());
						}
									
					}
							
				}
				

				
			}
			
		}
		
	}
	
	private class SyncListener implements Runnable {
		
		private long _workTime = 10000;
		private int _port = 8777;

		//private Selector readSelector;
		private ByteBuffer buffer = ByteBuffer.allocate(10000);
				
		


		public void run(){
			try{
				doRun();
			} catch(Exception oEx){
				net.sourceforge.dscsim.controller.utils.AppLogger.error(oEx);
			}
		}
		
		public void doRun () throws Exception {
					
			AppLogger.debug("SyncListener.doRun started!");

			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			Selector selector = Selector.open();

			AppLogger.debug2("SyncListener.doRun binding to port=" + _port);
			
			serverChannel.socket().bind (new InetSocketAddress (_port));
			serverChannel.configureBlocking (false);
			serverChannel.register (selector, SelectionKey.OP_ACCEPT);

			while (true) {
	      			  
				AppLogger.debug("SyncListener.doRun select");
				
				selector.select();
				Iterator it = selector.selectedKeys().iterator();
				
				while (it.hasNext()) {
			   	
					AppLogger.debug("SyncListener.doRun select");

					SelectionKey key = (SelectionKey) it.next();

					if (key.isAcceptable()) {
						AppLogger.debug2("SyncListener.doRun  isAcceptable");
					
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel channel = server.accept();
						channel.configureBlocking (false);
						channel.register (selector, SelectionKey.OP_READ);
					
					} else if (key.isReadable()) {
						AppLogger.debug2("SyncListener.doRun  isReadable");
						try{
							
							readDataFromSocket (key);
											
						}catch(IOException oEx){
							AppLogger.error("SyncPublisher.connect - IOException message =" + oEx.getMessage());
							key.channel().close();
						} catch(Exception oEx){
							AppLogger.error("SyncListener.doRun Exception message=" + oEx);
						}
						 
					} else if (!key.isValid()) {
					  	AppLogger.debug("SyncListener.doRun  isValid");

					}
			      
					it.remove();

			   }
			   
			 
			}
		}

		
		private void registerChannel (Selector selector, SelectableChannel channel, int ops)
		    throws IOException {
		  	
			if (channel == null) 
				return;
		
			channel.configureBlocking(false);
			channel.register(selector, ops);
		    
		}
		  
		protected int readDataFromSocket(SelectionKey key) throws Exception{
		
			SocketChannel client = null;
						
			client = (SocketChannel)key.channel();
			buffer.clear();
		
			int numBytesRead = client.read(buffer);
			//utils.AppLogger.debug("SyncListener.readDataFromSocket - bytes read = "+ numBytesRead);

			if(numBytesRead < 0){
		   		//AppLogger.debug("SyncListener.readDataFromSocket - disconnecting.");
		   		client.close();
		   		return -1;
			}
		   
	        ByteArrayInputStream oInputStr = new ByteArrayInputStream(buffer.array());
	        ObjectInputStream oObjStrm = new ObjectInputStream(oInputStr);
	        
			SyncMessage oSyncMessage = (SyncMessage)oObjStrm.readObject();
					
			AppLogger.debug2("SyncListener.readDataFromSocket - request received = "+oSyncMessage.toXml());

			pushDispatchQueue(oSyncMessage);
			
			return 0;
		
		}

	}
	

	
	public static void main(String args[]) throws Exception {
		
		(new Thread(new SyncListenerDispatcher())).start();
	 }

}





