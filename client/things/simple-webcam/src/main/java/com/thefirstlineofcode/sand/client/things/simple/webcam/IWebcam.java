package com.thefirstlineofcode.sand.client.things.simple.webcam;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;

public interface IWebcam {
	void takePhoto() throws ExecutionException;
}
