package com.thefirstlineofcode.sand.protocols.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.thefirstlineofcode.sand.protocols.core.ITraceId.Type;

public class HourTimeBasedIdTest {
	@Test
	public void all() {
		int minutes = 23;
		int seconds = 52;
		int milliseconds = 997;
		
		HourTimeBasedId requestId = HourTimeBasedId.createInstance(Type.REQUEST, minutes, seconds, milliseconds);
		assertEquals(HourTimeBasedId.Type.REQUEST, requestId.getType());
		assertEquals(minutes, requestId.getMinutes());
		assertEquals(seconds, requestId.getSeconds());
		assertEquals(milliseconds, requestId.getMilliseconds());
		
		ITraceId responseId = requestId.createResponseId();
		assertTrue(requestId.isResponse(responseId.getBytes()));
		assertEquals(Type.RESPONSE, responseId.getType());
		
		ITraceId errorId = requestId.createErrorId();
		assertTrue(requestId.isError(errorId.getBytes()));
		assertEquals(Type.ERROR, errorId.getType());
	}
}
