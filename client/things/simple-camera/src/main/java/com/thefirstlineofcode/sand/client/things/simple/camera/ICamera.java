package com.thefirstlineofcode.sand.client.things.simple.camera;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;

public interface ICamera {
	void takePhoto() throws ExecutionException;
}
