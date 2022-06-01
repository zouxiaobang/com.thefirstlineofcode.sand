package com.thefirstlineofcode.sand.server.lite.concentrator;

import com.thefirstlineofcode.sand.server.concentrator.Node;

public interface ConcentrationMapper {
	void insert(Concentration concentration);
	int selectCountByConcentratorAndNode(String concentratorDeviceName, String nodeDeviceId);
	int selectCountByConcentratorAndLanId(String concentratorDeviceName, String lanId);
	Node selectNodeByConcentratorAndLanId(String concentratorDeviceName, String lanId);
	Node selectNodeByDeviceId(String nodeDeviceId);
}
