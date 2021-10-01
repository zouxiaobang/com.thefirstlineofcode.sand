package com.thefirstlineofcode.sand.client.things;

import java.util.UUID;

public class ThingsUtils {
	public static String getHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("0x%02x ", b));
		}
		
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	public static String generateRandomId() {
		return generateRandomId(12);
	}
	
	public static String generateRandomId(int length) {
		if (length <= 16) {
			return String.format("%016X", java.util.UUID.randomUUID().getLeastSignificantBits()).substring(16 - length, 16);
		}
		
		if (length > 32) {
			length = 32;
		}
		
		UUID uuid = UUID.randomUUID();
		String uuidHexString = String.format("%016X%016X", uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
				
		return uuidHexString.substring(32 - length, 32); 
	}
}

