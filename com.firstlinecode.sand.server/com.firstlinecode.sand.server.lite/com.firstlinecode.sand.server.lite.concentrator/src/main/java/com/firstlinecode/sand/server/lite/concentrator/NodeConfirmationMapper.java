package com.firstlinecode.sand.server.lite.concentrator;

import java.util.Date;

import com.firstlinecode.sand.server.concentrator.NodeConfirmation;

public interface NodeConfirmationMapper {
	void insert(NodeConfirmation confirmation);
	void updateCanceled(String deviceId, boolean canceled);
	NodeConfirmation[] selectByConcentratorAndNode(String concentrator, String node);
	void updateConfirmed(String id, String confirmer, Date confirmedTime);
}
