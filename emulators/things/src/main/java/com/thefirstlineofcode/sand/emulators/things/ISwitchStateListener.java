package com.thefirstlineofcode.sand.emulators.things;

import com.thefirstlineofcode.sand.emulators.things.ILight.SwitchState;

public interface ISwitchStateListener {
	void switchStateChanged(SwitchState oldState, SwitchState newState);
}
