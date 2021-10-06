package com.thefirstlineofcode.sand.protocols.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.thefirstlineofcode.sand.protocols.core.HourBasedTimeId.Direction;

public class HourBasedTimeIdTest {
	@Test
	public void all() {
		int minutes = 23;
		int seconds = 52;
		int milliseconds = 997;
		
		HourBasedTimeId timeId = HourBasedTimeId.createInstance(Direction.G_2_T, minutes, seconds, milliseconds);
		assertEquals(HourBasedTimeId.Direction.G_2_T, timeId.getDirection());
		assertEquals(minutes, timeId.getMinutes());
		assertEquals(seconds, timeId.getSeconds());
		assertEquals(milliseconds, timeId.getMilliseconds());
	}
}
