package com.firstlinecode.sand.server.lite.devices;

import org.pf4j.Extension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.firstlinecode.granite.framework.adf.spring.ISpringConfiguration;

@Extension
@Configuration
@ComponentScan
public class DevicesConfiguration implements ISpringConfiguration {}
