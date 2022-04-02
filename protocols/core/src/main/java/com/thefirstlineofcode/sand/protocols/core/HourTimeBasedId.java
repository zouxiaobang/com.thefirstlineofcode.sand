package com.thefirstlineofcode.sand.protocols.core;

import java.util.Calendar;

public class HourTimeBasedId implements ITraceId {
	private byte[] bytes;
	
	private HourTimeBasedId(byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException("Null bytes.");
		}
		
		if (bytes.length != 3)
			throw new IllegalArgumentException("Hour time based ID bytes must be an three bytes array.");
		
		this.bytes = bytes;
	}
	
	@Override
	public Type getType() {
		int iType = (bytes[0] & 0xff) >> 6;
		
		for (Type type : Type.values()) {
			if (type.ordinal() == iType)
				return type;
		}
		
		throw new RuntimeException("Illegal type for traceable id. Type ordinal value is " + iType + ".");
	}
	
	public int getMinutes() {
		return bytes[0] & 0x3F;
	}
	
	public int getSeconds() {
		return (bytes[1] & 0xFF) >> 2;
	}
	
	public int getMilliseconds() {
		int rightest2BitsOfByte2 = bytes[1] & 0x3;
		
		return (rightest2BitsOfByte2 << 8) | (bytes[2] & 0xFF);
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public static HourTimeBasedId createInstance(Type type) {
		Calendar calendar = Calendar.getInstance();		
		return createInstance(type, calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
	}
	
	public static HourTimeBasedId createInstance(Type type, int minutes, int seconds, int milliseconds) {
		byte[] bytes = new byte[3];
		
		int leftest2BitsOfByte0 = type.ordinal() & 0xff;
		bytes[0] = (byte)(leftest2BitsOfByte0 << 6 | minutes & 0xFF); 
		
		int rightest2BitsOfBytes2 = (milliseconds >> 8) & 0xFF;
		bytes[1] = (byte)((seconds << 2) | rightest2BitsOfBytes2);
		
		bytes[2] = (byte)(milliseconds & 0xFF);
		
		return new HourTimeBasedId(bytes);
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		
		hash += 31 * hash + bytes[0];
		hash += 31 * hash + bytes[1];
		hash += 31 * hash + bytes[2];
		
		return hash;
	}
	
	@Override
	public String toString() {
		return String.format("HourBasedTimeId[%s, %s:%s.%s]", getType(), getMinutes(), getSeconds(), getMilliseconds());
	}
	
	private boolean isAnswer(ITraceId responseId, Type type) {
		if (!(responseId instanceof HourTimeBasedId))
			return false;
		
		if (responseId.getType() != type)
			return false;
		
		HourTimeBasedId another = (HourTimeBasedId)responseId;
		
		return (another.getMinutes() == this.getMinutes()) &&
				(another.getSeconds() == this.getSeconds()) &&
				(another.getMilliseconds() == this.getMilliseconds());
	}

	@Override
	public boolean isResponse(byte[] requestId) {
		return isAnswer(new HourTimeBasedId(requestId), Type.RESPONSE);
	}
	
	@Override
	public boolean isError(byte[] errorId) {
		return isAnswer(new HourTimeBasedId(errorId), Type.ERROR);
	}

	@Override
	public ITraceId createResponseId() {
		return createAnswerId(Type.RESPONSE);
	}

	private ITraceId createAnswerId(Type type) {
		int newByte0 = bytes[0];
		newByte0 = newByte0 << 2;
		newByte0 = newByte0 >> 2;
		
		int leftest2BitsOfNewByte0 = (type.ordinal() << 6) & 0xFF;
		newByte0 = newByte0 | leftest2BitsOfNewByte0;
		
		return new HourTimeBasedId(new byte[] {(byte)newByte0, bytes[1], bytes[2]});
	}

	@Override
	public ITraceId createErrorId() {
		return createAnswerId(Type.ERROR);
	}

	public static HourTimeBasedId createInstance(byte[] bytes) {
		return new HourTimeBasedId(bytes);
	}
}
