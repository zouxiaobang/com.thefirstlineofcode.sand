package com.firstlinecode.sand.protocols.emulators.light;

import com.firstlinecode.sand.protocols.core.IAction;

public class Flash implements IAction {
	private int repeat;
	
	public Flash() {
		repeat = 1;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	
}
