package net.sourceforge.dscsim.radio.core.impl;
import org.apache.log4j.Logger;

import net.sourceforge.dscsim.util.ByteConverter;

public class SoundDataPacket {
	
	private static Logger logger = Logger.getLogger(SoundDataPacket.class);

	private static final int PACKET_TYPE = 10;

	private byte[] dataBuff;
	
	private int sequenceNumber;

	private int byteCount;
	
	public SoundDataPacket(int sequenceNumber, int byteCount){
		this.sequenceNumber = sequenceNumber;
		this.byteCount = byteCount;
	}
	
//	public void setDataByte(int index, byte data) {
//		if( dataBuff == null ){
//			dataBuff = new byte[byteCount];
//		}
//		dataBuff[index] = data;
//	}

	public void setDataBytes(byte[] data) {
		dataBuff = data;
	}

	public byte getDataByte( int index ) {
		return dataBuff[index];
	}

	public byte[] getDataBytes() {
		return dataBuff;
	}
	
	public byte[] getBytes() {
		byte[] result = new byte[byteCount+8];
		ByteConverter.intToByteArray(PACKET_TYPE, result, 0 );
		System.arraycopy(this.dataBuff,0,result,4,byteCount/*this.dataBuff.length*/);
		ByteConverter.intToByteArray(sequenceNumber, result, byteCount+4 );
		return result;
	}
	
	public static SoundDataPacket getFromBytes(byte[] inBytes, int pos, int length) {
		int byteCount = length-8;
		int type = ByteConverter.byteArrayToInt(inBytes, pos);
		if( type != PACKET_TYPE ){
			//not the appropriate type
			logger.debug("Received packet is of wrong type. Expected "+PACKET_TYPE+" but was "+type);
			new RuntimeException().printStackTrace();
			return null;
		}
		int sequenceNumber = ByteConverter.byteArrayToInt(inBytes, pos+byteCount+4);
		SoundDataPacket result = new SoundDataPacket( sequenceNumber, byteCount );
		byte[] tempBytes = new byte[byteCount]; 
		System.arraycopy(inBytes,pos+4,tempBytes,0,byteCount);
		result.setDataBytes(tempBytes);
		return result;
	}

	/**
	 * @return Returns the sequenceNumber.
	 */
	public int getSequenceNumber() {
		return sequenceNumber;
	}
}
