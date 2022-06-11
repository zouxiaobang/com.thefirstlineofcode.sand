package com.thefirstlineofcode.sand.server.lite.concentrator;

import com.thefirstlineofcode.sand.server.concentrator.Node;

public interface ConcentrationMapper {
	void insert(D_Concentration concentration);
	int selectCountByConcentratorAndLanId(String concentratorDeviceName, String lanId);
	Node selectNodeByConcentratorAndLanId(String concentratorDeviceName, String lanId);
	int selectCountByConcentratorAndNode(String concentratorDeviceName, String nodeDeviceId);
	Node selectNodeByConcentratorAndNode(String concentratorDeviceName, String nodeDeviceId);
	int selectCountByNode(String nodeDeviceId);
	D_Concentration selectConcentrationByNode(String nodeDeviceId);
}
