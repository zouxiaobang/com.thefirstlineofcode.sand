package com.firstlinecode.sand.emulators.things.emulators;

import com.firstlinecode.sand.emulators.things.ILight.LightState;

public interface ILightStateListener {
	void lightStateChanged(LightState oldState, LightState newState);
}
