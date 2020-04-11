package com.firstlinecode.sand.server.lite.concentrator;

public interface ConcentrationMapper {
	void insert(Concentration concentration);
	int selectCountByConcentratorAndNode(String concentrator, String node);
}
