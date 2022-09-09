package com.thefirstlineofcode.sand.demo.things.client.sc.rbp3b;

import com.thefirstlineofcode.chalk.core.stream.StandardStreamConfig;
import com.thefirstlineofcode.chalk.logger.LogConfigurator;
import com.thefirstlineofcode.chalk.logger.LogConfigurator.LogLevel;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public class Main {
	private Camera camera;
	
	public static void main(String[] args) {
		new Main().run(args);
	}

	private void run(String[] args) {
		if (args.length == 1 && args[0].equals("--help")) {
			printUsage();
			
			return;
		}
		
		String host = null;
		Integer port = null;
		Boolean tlsPreferred = null;
		String logLevel = null;
		boolean notStartNativeService = false;
		String nativeServicePath = null;
		
		for (int i = 0; i < args.length; i++) {
			if (!args[i].startsWith("--")) {
				throw new IllegalArgumentException("Illegal option format.");
			}
			
			int equalSignIndex = args[i].indexOf('=');
			if (equalSignIndex == 2 ||
					equalSignIndex == (args[i].length() - 1)) {
				throw new IllegalArgumentException("Illegal option format.");
			}
			
			String name, value;
			if (equalSignIndex == -1) {
				name = args[i].substring(2,  args[i].length());
				value = "TRUE";
			} else {
				name = args[i].substring(2, equalSignIndex).trim();
				value = args[i].substring(equalSignIndex + 1, args[i].length()).trim();
			}
			
			if ("help".equals(name)) {
				throw new IllegalArgumentException("Illegal option format.");
			} else if ("host".equals(name)) {
				host = value;
			} else if ("port".equals(name)) {
				port = Integer.parseInt(value);
			} else if ("tls-preferred".equals(name)) {
				tlsPreferred = Boolean.parseBoolean(value);
			} else if ("not-start-native-service".equals(name)) {
				notStartNativeService = Boolean.parseBoolean(value);
			} else if ("native-service-path".equals(name)) {
				nativeServicePath = value;
			} else if ("log-level".equals(name)) {
				logLevel = value;
			} else {
				throw new IllegalArgumentException(String.format("Unknown option: %s.", name));				
			}
		}
		
		if (logLevel == null)
			logLevel = "info";
		
		new LogConfigurator().configure(Camera.THING_MODEL, getLogLevel(logLevel));
		
		WebcamConfig webcamConfig = new WebcamConfig(notStartNativeService, nativeServicePath);
		if (host != null) {
			if (port == null) {
				port = 6222;
			}
			
			StandardStreamConfig streamConfig = new StandardStreamConfig(host, port);
			if (tlsPreferred != null)
				streamConfig.setTlsPreferred(tlsPreferred);
			streamConfig.setResource(DeviceIdentity.DEFAULT_RESOURCE_NAME);
			
			camera = new Camera(webcamConfig, streamConfig);
		} else {
			camera = new Camera(webcamConfig);
		}
		
		camera.start();
	}

	private LogLevel getLogLevel(String sLogLevel) {
		if ("info".equals(sLogLevel))
			return LogLevel.INFO;
		else if ("debug".equals(sLogLevel))
			return LogLevel.DEBUG;
		else if ("trace".equals(sLogLevel)) {
			return LogLevel.TRACE;
		} else {
			throw new IllegalArgumentException(String.format("Illegal log level: %s.", sLogLevel));
		}
	}

	private void printUsage() {
		System.out.println("Usage: java sand-demo-things-sc-rbp3b--${VERSION}.jar [OPTIONS]");
		System.out.println("OPTIONS:");
		System.out.println("--help                      Display help information.");
		System.out.println("--host=HOST                 Specify host name of server.");
		System.out.println("--port=PORT                 Specify server port.");
		System.out.println("--tls-preferred             Specify whether TLS is preferred when connecting to server.");
		System.out.println("--log-level=LOG_LEVEL       Specify log level. Option values are info, debug or trace.");
		System.out.println("--not-start-native-service  Don't start native service process.");
		System.out.println("--native-service-path       Specify log level. Option values are info, debug or trace.");
	}
}
