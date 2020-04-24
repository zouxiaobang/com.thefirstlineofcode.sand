package com.firstlinecode.sand.server.lite.concentrator;

import com.firstlinecode.sand.server.concentrator.Node;

public interface ConcentrationMapper {
	void insert(Concentration concentration);
	int selectCountByConcentratorAndNode(String concentrator, String node);
	Node selectByLanId(String concentrator, String node);
}
