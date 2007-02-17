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
 * The Initial Developer of the Original Code is Oliver Hecker. Portions created by
 * the Initial Developer are Copyright (C) 2006, 2007.
 * All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 */
package net.sourceforge.dscsim.radiotransport.udp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

import org.apache.log4j.Logger;

import net.sourceforge.dscsim.controller.utils.AppLogger;

/**
 * Holds the address and identifier of an UDPAirwave for keeping track of
 * the known UDPAirwave objects throughout the world (even on other hosts)
 */
public class UDPAirwaveEntry {

	/**
	 * The logger for this class
	 */
	private static Logger _logger = Logger.getLogger(UDPAirwaveEntry.class);
	
	/**
	 * Timeout if no packets can be received from airwave
	 */
	private static final long TIMEOUT_NO_RECEIVE = 60000;
	
	/**
	 * Timeout if no confirmation can be received from airwave
	 */
	private static final long TIMEOUT_NO_CONFIRM = 60000;
	
	/**
	 * Timeout until airwave should be removed from whitepages 
	 * if no connection is possible
	 */
	private static final long TIMEOUT_UNTIL_DEAD = 600000;

	/**
	 * Repeat intervall for repeating sending "white pages" packets
	 */
	private static final long REPEAT_WHITEPAGES = 15000;
	
	/**
	 * local abstract base class of the possible states of the
	 * UDPAirwaveEntry object 
	 */
	private abstract class State {
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public abstract String toString();
//		{
//			return this.getClass().getName();
//		}

		/**
		 * States are equal if they are of the same class
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if( obj == null ){
				return false;
			}
			return this.getClass().equals(obj.getClass());
		}

		/**
		 * Called when object is added to the set of known other UDPAirwave objects
		 */
		public void known() {
		}
		
		/**
		 * Called when a packet is received from the remote UDPAirwave
		 */
		public void packetReceived() {
			_timeStampPacketReceived = now();
			sendConfirm();
//			sendWhitepages();
			setState( new StateWAIT_FOR_CONFIRM() );
		}
		
		/**
		 * Called when a whitepages packet is received from the remote UDPAirwave
		 */
		public void whitePagesReceived() {
			_timeStampPacketReceived = now();
			sendConfirm();
		}

		/**
		 * Called when a confirmation is received from the remote UDPAirwave
		 */
		public void confirmReceived() {
			_timeStampPacketReceived = now();
			_timeStampConfirmationReceived = now();
			setState( new StateESTABLISHED() );
			sendWhitepagesToAll();
		}

		/**
		 * Called to check time dependend actions
		 */
		public abstract void timerCheck();
		
		/**
		 * Called when exiting the state
		 */
		public void exitAction() {
		}

		/**
		 * Called when entering the state
		 */
		public void entryAction() {
		}
		
		/**
		 * Adds this entry to the set of UDPAirwaveEntries to be propagated
		 * to other airwaves
		 * @param the set of UDPAirwaveEntries to be propagated to all
		 * other airwaves.
		 */
		public void addToWhitePages(Set whitePages) {
			// default implementation does nothing
		}
		
		/**
		 * Check if connectivity to this UDPAirwave is confirmed
		 */
		public boolean isConfirmed() {
			return false;
		}
		
		/**
		 * Check if packets from the UDPAirwave are received (so it is sending
		 * packets to us)
		 */
		public boolean isSendingPackets() {
			return false;
		}
		
		/**
		 * Indicates if the remote UDPAirwave is marked as unreachable
		 */
		public boolean isUnreachable() {
			return false;
		}
		
		/**
		 * Indicates if the remote UDPAirwave is marked as dead
		 */
		public boolean isDead() {
			return false;
		}
		
	}
	
	/**
	 * initial state of the object
	 */
	private class StateINIT extends State {
		public void known() {
			sendWhitepages();
			setState( new StateWAIT_FOR_PACKET() );
		}

		public void timerCheck() {}

		public String toString() {
			return "INIT";
		}
	}
	
	/**
	 * state for waiting for receiving a packet
	 */
	private class StateWAIT_FOR_PACKET extends State {

		public void timerCheck() {
			if( isOlderThan(_timeStampStateChange,TIMEOUT_NO_RECEIVE) ) {
				setState(new StateNO_CONNECTION() );
			}
			if( isOlderThan(_timeStampWhitePagesSent,REPEAT_WHITEPAGES) ) {
				sendWhitepages();
			}
		}

		public String toString() {
			return "WAIT_FOR_PACKET";
		}
	}

	/**
	 * state for waiting for confirmation
	 */
	private class StateWAIT_FOR_CONFIRM extends State {
		public void packetReceived() {
			_timeStampPacketReceived = now();
			sendConfirm();
			sendWhitepages();
		}
		public void entryAction() {
			super.entryAction();
			sendWhitepages();
		}
		public void timerCheck() {
			if( isOlderThan(_timeStampPacketReceived,TIMEOUT_NO_RECEIVE) ) {
				setState(new StateWAIT_FOR_PACKET() );
			}
			if( isOlderThan(_timeStampStateChange,TIMEOUT_NO_CONFIRM) ) {
				setState(new StateDOES_NOT_CONFIRM() );
			}
			if( isOlderThan(_timeStampWhitePagesSent,REPEAT_WHITEPAGES) ) {
				sendWhitepages();
			}
		}
		public boolean isSendingPackets() {
			return true;
		}
		public String toString() {
			return "WAIT_FOR_CONFIRM";
		}
	}

	/**
	 * state for established communication between both airwaves
	 */
	private class StateESTABLISHED extends State {
		public void confirmReceived() {
			_timeStampPacketReceived = now();
			_timeStampConfirmationReceived = now();
		}
		/* (non-Javadoc)
		 * @see net.sourceforge.dscsim.radiotransport.udp.UDPAirwaveEntry.State#packetReceived()
		 */
		public void packetReceived() {
			_timeStampPacketReceived = now();
		}
		public void timerCheck() {
			if( isOlderThan(_timeStampConfirmationReceived,TIMEOUT_NO_CONFIRM) ) {
				setState(new StateWAIT_FOR_CONFIRM() );
			}
		}
		public void addToWhitePages(Set whitePages) {
			whitePages.add(UDPAirwaveEntry.this);
		}
		public boolean isConfirmed() {
			return true;
		}
		public boolean isSendingPackets() {
			return true;
		}
		public String toString() {
			return "ESTABLISHED";
		}
	}

	/**
	 * state for airwave which does not confirm
	 */
	private class StateDOES_NOT_CONFIRM extends State {
		public void packetReceived() {
			_timeStampPacketReceived = now();
		}
		public void timerCheck() {
			if( isOlderThan(_timeStampPacketReceived,TIMEOUT_NO_RECEIVE) ) {
				setState(new StateNO_CONNECTION() );
			}
		}
		public boolean isSendingPackets() {
			return true;
		}
		public String toString() {
			return "DOES_NOT_CONFIRM";
		}
	}
	
	/**
	 * state for airwave which does not send any packets
	 */
	private class StateNO_CONNECTION extends State {
		public void timerCheck() {
			if( isOlderThan(_timeStampStateChange,TIMEOUT_UNTIL_DEAD) ) {
				setState(new StateDEAD() );
			}
		}

		public boolean isUnreachable() {
			return true;
		}
		public String toString() {
			return "NO_CONNECTION";
		}
	}

	/**
	 * state for airwave which is dead and should be removed from whitepages
	 */
	private class StateDEAD extends State {
		public void timerCheck() {}

		public boolean isUnreachable() {
			return true;
		}
		
		public boolean isDead() {
			return true;
		}

		public String toString() {
			return "DEAD";
		}
	}

	/**
	 * The unique identifier of the UDPAirwave object
	 */
	private int _uid;
	
	/**
	 * The socket address of the UDPAirwave
	 */
	private InetSocketAddress _socketAddress;

	/**
	 * The state of the object
	 */
	private State _state;
	
	/**
	 * timestamp of last change of the objects state
	 */
	private long _timeStampStateChange;
	
	/**
	 * timestamp of last reception of packet from the airwave
	 */
	private long _timeStampPacketReceived;
	
	/**
	 * timestamp of last reception of confirmation from the airwave
	 */
	private long _timeStampConfirmationReceived;
	
	/**
	 * timestamp when the last white pages packet was sent
	 */
	private long _timeStampWhitePagesSent;
	
	/**
	 * the UDPAirwave object which is the parent
	 */
	private UDPAirwave _parent;
	
	/**
	 * Constructor 
	 * @param uid the unique id of the airwave object
	 * @param socketAddress the address of the socket used by the airwave
	 */
	
	public UDPAirwaveEntry(int uid, InetSocketAddress socketAddress) {
		_uid = uid;
		_socketAddress = socketAddress;
		_parent = null;
		setState( new StateINIT() );
	}
	
	/**
	 * Changes the state of the object to the new state. Performs exit and
	 * entry actions if the state has really changed
	 * @param state
	 */
	private void setState(State state) {
		if( state.equals(_state) ) {
			return;
		}
		if( _state != null ) {
			_logger.debug("State change for AirwaveEntry "+_socketAddress+" from "+_state+" to "+state);
//			AppLogger.debug2("State change for AirwaveEntry "+_socketAddress+" from "+_state+" to "+state);
			_state.exitAction();
		}
		_state = state;
		_timeStampStateChange = now();
		_state.entryAction();
		if( _parent != null ) {
			_parent.notifyRemoteAirwaveChanged();
		}
	}

	/**
	 * @return the socketAddress
	 */
	public InetSocketAddress getSocketAddress() {
		return _socketAddress;
	}
	
	/**
	 * @return the uid
	 */
	public int getUid() {
		return _uid;
	}

	/**
	 * Gets the current timestamp
	 * @return the current timestamp as delivered
	 * by {@link java.lang.System#currentTimeMillis()}
	 */
	private static long now() {
		return System.currentTimeMillis();
	}
	
	/**
	 * Test is the given timestamp is older than the given threshold
	 * @param the timestamp to be tested
	 * @param the threshold
	 */
	private static boolean isOlderThan(long timestamp, long threshold) {
		return( (timestamp + threshold) < now() );
	}

	/**
	 * 
	 * @see net.sourceforge.dscsim.radiotransport.udp.UDPAirwaveEntry.State#confirmReceived()
	 */
	public void confirmReceived() {
		_state.confirmReceived();
	}

	/**
	 * 
	 * @see net.sourceforge.dscsim.radiotransport.udp.UDPAirwaveEntry.State#known()
	 */
	public void known() {
		_state.known();
	}

	/**
	 * 
	 * @see net.sourceforge.dscsim.radiotransport.udp.UDPAirwaveEntry.State#packetReceived()
	 */
	public void packetReceived() {
		_state.packetReceived();
	}

	/**
	 * 
	 * @see net.sourceforge.dscsim.radiotransport.udp.UDPAirwaveEntry.State#timerCheck()
	 */
	public void timerCheck() {
		_state.timerCheck();
	}

	/**
	 * @param whitePages
	 * @see net.sourceforge.dscsim.radiotransport.udp.UDPAirwaveEntry.State#addToWhitePages(java.util.Set)
	 */
	public void addToWhitePages(Set whitePages) {
		_state.addToWhitePages(whitePages);
	}

	/**
	 * @return
	 * @see net.sourceforge.dscsim.radiotransport.udp.UDPAirwaveEntry.State#isConfirmed()
	 */
	public boolean isConfirmed() {
		return _state.isConfirmed();
	}

	/**
	 * @return
	 * @see net.sourceforge.dscsim.radiotransport.udp.UDPAirwaveEntry.State#isSendingPackets()
	 */
	public boolean isSendingPackets() {
		return _state.isSendingPackets();
	}

	/**
	 * @return
	 * @see net.sourceforge.dscsim.radiotransport.udp.UDPAirwaveEntry.State#isUnreachable()
	 */
	public boolean isUnreachable() {
		return _state.isUnreachable();
	}

	/**
	 * 
	 * @see net.sourceforge.dscsim.radiotransport.udp.UDPAirwaveEntry.State#whitePagesReceived()
	 */
	public void whitePagesReceived() {
		_state.whitePagesReceived();
	}

	/**
	 * @return
	 * @see net.sourceforge.dscsim.radiotransport.udp.UDPAirwaveEntry.State#isDead()
	 */
	public boolean isDead() {
		return _state.isDead();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if( ! (obj instanceof UDPAirwaveEntry ) ) {
			return false;
		}
		UDPAirwaveEntry other = (UDPAirwaveEntry)obj;
		return( _socketAddress.equals(other._socketAddress) );
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return _socketAddress.hashCode();
	}

	/**
	 * Updates the uid. If the uid is not yet set (=0) and the argument
	 * is not 0 then the uid will be updated to this value.
	 * @param the new uid to set
	 */
	public void updateUid(int uid) {
		if( (_uid == 0) && (uid != 0) ) {
			_uid = uid;
		}
	}
	
	/**
	 * Sets the parent
	 * @param the parent
	 */
	public void setParent(UDPAirwave parent){
		_parent = parent;
	}
	
	/**
	 * Request to (asynchronously) send the current whitepages set
	 * to the remote airwave described by this object
	 */
	private void sendWhitepages() {
		_timeStampWhitePagesSent = now();
		_parent.registerCommand(
				new Runnable() {
					public void run() {
						_logger.debug("Sending whitePages to "+UDPAirwaveEntry.this);
//						AppLogger.debug2("Sending whitePages to "+UDPAirwaveEntry.this);
						_parent.sendWhitePages(UDPAirwaveEntry.this);
					}
				}
		);
	}
	
	/**
	 * Request to (asynchronously) send the current whitepages set
	 * to all remote airwaves
	 */
	private void sendWhitepagesToAll() {
		_timeStampWhitePagesSent = now();
		_parent.registerCommand(
				new Runnable() {
					public void run() {
						_logger.debug("Sending whitePages to "+UDPAirwaveEntry.this);
//						AppLogger.debug2("Sending whitePages to "+UDPAirwaveEntry.this);
						_parent.sendWhitePagesToAll();
					}
				}
		);
	}

	/**
	 * Request to (asynchronously) send a confirm
	 * to the remote airwave described by this object
	 */
	private void sendConfirm() {
		_parent.registerCommand(
				new Runnable() {
					public void run() {
						_logger.debug("Sending confirm to "+UDPAirwaveEntry.this);
//						AppLogger.debug2("Sending confirm to "+UDPAirwaveEntry.this);
						_parent.sendConfirm(UDPAirwaveEntry.this);
					}
				}
		);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "UDPAirwaveEntry "+_socketAddress+" (UID:"+_uid+", State: "+_state+")";
	}
	
}
