package com.firstlinecode.sand.protocols.core;

import java.util.Calendar;

public class HourBasedTimeId {
	public enum Direction {
		G_2_T,
		T_2_G
	}
	
	private byte[] bytes;
	
	public HourBasedTimeId(byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException("Null time ID bytes.");
		}
		
		if (bytes.length != 3)
			throw new IllegalArgumentException("Hour based time ID bytes must be a three bytes array.");
		
		this.bytes = bytes;
	}
	
	public Direction getDirection() {
		if ((bytes[0] & 0xff) >> 7 == 0) {
			return Direction.G_2_T;
		}
		
		return Direction.T_2_G;
	}
	
	public int getMinutes() {
		return bytes[0] & 0x3F;
	}
	
	public int getSeconds() {
		return (bytes[1] & 0xFC) >> 2;
	}
	
	public int getMilliseconds() {
		int byte2RightMost2Bits = bytes[1] & 0x3;
		
		return (byte2RightMost2Bits << 8) | (bytes[2] & 0xFF);
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public static HourBasedTimeId createInstance(Direction direction) {
		Calendar calendar = Calendar.getInstance();		
		return createInstance(direction, calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
	}
	
	public static HourBasedTimeId createInstance(Direction direction, int minutes, int seconds, int milliseconds) {
		byte[] bytes = new byte[3];
		
		bytes[0] = (byte)((direction == Direction.T_2_G) ? ((minutes + 64) & 0xFF) : (minutes & 0xFF));
		
		int bytes2RightMost2Bits = (milliseconds >> 8) & 0xFF;
		bytes[1] = (byte)((seconds << 2) | bytes2RightMost2Bits);
		
		bytes[2] = (byte)(milliseconds & 0xFF);
		
		return new HourBasedTimeId(bytes);
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
		return String.format("HourBasedTimeId[%s, %s:%s.%s]", getDirection(), getMinutes(), getSeconds(), getMilliseconds());
	}
}
