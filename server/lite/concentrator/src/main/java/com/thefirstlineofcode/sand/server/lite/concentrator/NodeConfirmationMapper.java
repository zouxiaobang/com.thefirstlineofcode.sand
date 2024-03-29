package com.thefirstlineofcode.sand.server.lite.concentrator;

import java.util.Date;

import com.thefirstlineofcode.sand.server.concentrator.NodeConfirmation;

public interface NodeConfirmationMapper {
	void insert(NodeConfirmation confirmation);
	void updateCanceled(String deviceId, boolean canceled);
	NodeConfirmation[] selectByConcentratorAndNode(String concentratorDeviceName, String nodeDeviceId);
	void updateConfirmed(String id, String confirmer, Date confirmedTime);
}
