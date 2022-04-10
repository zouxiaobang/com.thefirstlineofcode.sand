package com.thefirstlineofcode.sand.server.lite.concentrator;

import com.thefirstlineofcode.sand.server.concentrator.Node;

public interface ConcentrationMapper {
	void insert(Concentration concentration);
	int selectCountByConcentratorAndNode(String concentrator, String node);
	int selectCountByConcentratorAndLanId(String concentrator, String lanId);
	Node selectNodeByLanId(String concentrator, String lanId);
	Node selectNodeByDeviceId(String deviceId);
}
