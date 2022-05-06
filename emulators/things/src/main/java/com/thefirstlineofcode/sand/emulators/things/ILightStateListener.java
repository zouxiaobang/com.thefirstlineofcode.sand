package com.thefirstlineofcode.sand.emulators.things;

import com.thefirstlineofcode.sand.emulators.things.ILight.LightState;

public interface ILightStateListener {
	void lightStateChanged(LightState oldState, LightState newState);
}
