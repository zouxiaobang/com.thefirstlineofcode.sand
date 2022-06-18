package com.thefirstlineofcode.sand.client.things.simple.camera;
import com.thefirstlineofcode.sand.client.core.IThing;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;

public interface ICamera extends IThing {
	void takePhoto() throws ExecutionException;
}
