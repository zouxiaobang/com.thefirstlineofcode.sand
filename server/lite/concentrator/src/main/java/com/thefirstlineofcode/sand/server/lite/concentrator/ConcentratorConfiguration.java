package com.thefirstlineofcode.sand.server.lite.concentrator;

import org.pf4j.Extension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.thefirstlineofcode.granite.framework.adf.spring.ISpringConfiguration;

@Extension
@Configuration
@ComponentScan
public class ConcentratorConfiguration implements ISpringConfiguration {}
