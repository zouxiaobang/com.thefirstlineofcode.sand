package com.firstlinecode.sand.emulators.things.emulators;

import com.firstlinecode.sand.emulators.things.ILight.SwitchState;

public interface ISwitchStateListener {
	void switchStateChanged(SwitchState oldState, SwitchState newState);
}
