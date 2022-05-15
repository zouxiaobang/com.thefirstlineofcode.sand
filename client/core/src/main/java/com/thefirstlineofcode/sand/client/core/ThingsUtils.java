package com.thefirstlineofcode.sand.client.core;

import java.util.UUID;

public class ThingsUtils {
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
	
	public static String getGlobalErrorCode(String model, String errorCode) {
		return String.format("%s-E%s", model, errorCode);
	}
	
	public static String getExecutionErrorDescription(String model, String errorCode) {
		return String.format("Execution error. Global error code: %s.", getGlobalErrorCode(model, errorCode));
	}
}

