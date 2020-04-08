package com.firstlinecode.sand.server.lite.concentrator;

import com.firstlinecode.sand.server.concentrator.NodeConfirmation;

public interface NodeConfirmationMapper {
	void insert(NodeConfirmation confirmation);
	void updateCanceled(String deviceId, boolean canceled);
	NodeConfirmation[] selectByConcentratorAndNode(String concentrator, String node);
}
