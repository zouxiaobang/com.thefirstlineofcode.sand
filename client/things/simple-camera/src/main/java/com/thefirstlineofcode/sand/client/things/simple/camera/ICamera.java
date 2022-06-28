package com.thefirstlineofcode.sand.client.things.simple.camera;
import java.io.File;

import com.thefirstlineofcode.sand.client.core.IThing;
import com.thefirstlineofcode.sand.protocols.actuator.ExecutionException;
import com.thefirstlineofcode.sand.protocols.things.simple.camera.TakePhoto;

public interface ICamera extends IThing {
	public static final String ERROR_CODE_PHOTO_WAS_NOT_TAKEN = "01";
	public static final String FAILED_TO_UPLOAD_PHOTO = "02";
	
	File takePhoto(TakePhoto takePhoto) throws ExecutionException;
}
