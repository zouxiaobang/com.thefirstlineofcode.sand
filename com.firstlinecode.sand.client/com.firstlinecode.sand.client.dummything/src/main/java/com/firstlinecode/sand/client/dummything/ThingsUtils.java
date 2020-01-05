package com.firstlinecode.sand.client.dummything;

import java.util.UUID;

public class ThingsUtils {
	public static String generateRandomDeviceId() {
		return generateRandomDeviceId(12);
	}
	
	public static String generateRandomDeviceId(int length) {
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
