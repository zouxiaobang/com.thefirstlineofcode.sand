package com.thefirstlineofcode.sand.emulators.commons;

import com.thefirstlineofcode.sand.client.things.simple.light.ILight;

public interface ILightEmulator extends ILight, IThingEmulator {
	void changeSwitchState(ILight.SwitchState switchState);
}
