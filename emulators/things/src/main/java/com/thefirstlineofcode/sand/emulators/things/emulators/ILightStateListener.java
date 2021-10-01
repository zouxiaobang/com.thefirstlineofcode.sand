package com.thefirstlineofcode.sand.emulators.things.emulators;

import com.thefirstlineofcode.sand.emulators.things.ILight.LightState;

public interface ILightStateListener {
	void lightStateChanged(LightState oldState, LightState newState);
}
