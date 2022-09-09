package com.thefirstlineofcode.sand.demo.things.client.sc.rbp3b;

public class WebcamConfig {
	public WebcamConfig(boolean notStartNativeService, String nativeServicePath) {
		this.notStartNativeService = notStartNativeService;
		this.nativeServicePath = nativeServicePath;
	}
	
	public boolean notStartNativeService;
	public String nativeServicePath;
}
