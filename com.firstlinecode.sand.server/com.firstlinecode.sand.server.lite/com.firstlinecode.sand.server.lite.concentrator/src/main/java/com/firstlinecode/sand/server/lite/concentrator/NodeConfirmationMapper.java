package com.firstlinecode.sand.server.lite.concentrator;

import com.firstlinecode.sand.server.concentrator.NodeConfirmationRequest;

public interface NodeConfirmationMapper {
	void insert(NodeConfirmationRequest request);
	void updateCanceled(String deviceId, boolean canceled);
	NodeConfirmationRequest[] selectByDeviceId(String deviceId);
}
