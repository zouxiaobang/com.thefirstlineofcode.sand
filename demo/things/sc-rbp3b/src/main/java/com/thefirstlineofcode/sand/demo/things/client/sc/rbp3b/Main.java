package com.thefirstlineofcode.sand.demo.things.client.sc.rbp3b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.thefirstlineofcode.chalk.core.stream.StandardStreamConfig;
import com.thefirstlineofcode.chalk.logger.LogConfigurator;
import com.thefirstlineofcode.chalk.logger.LogConfigurator.LogLevel;
import com.thefirstlineofcode.sand.protocols.core.DeviceIdentity;

public class Main {
	private Camera camera;
	private ConsoleThread consoleThread;
	
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
			} else if ("log-level".equals(name)) {
				logLevel = value;
			} else {
				throw new IllegalArgumentException(String.format("Unknown option: %s.", name));				
			}
		}
		
		if (logLevel == null)
			logLevel = "info";
		
		new LogConfigurator().configure(Camera.THING_MODEL, getLogLevel(logLevel));
		
		if (host != null) {
			if (port == null) {
				port = 6222;
			}
			
			StandardStreamConfig streamConfig = new StandardStreamConfig(host, port);
			
			if (tlsPreferred != null)
				streamConfig.setTlsPreferred(tlsPreferred);
			
			streamConfig.setResource(DeviceIdentity.DEFAULT_RESOURCE_NAME);
			camera = new Camera(streamConfig);
		} else {
			camera = new Camera();
		}
		
		camera.start();
		
		System.out.println("Starting console...");
		startConsoleThread();
	}
	
	private void startConsoleThread() {
		consoleThread = new ConsoleThread();
		new Thread(consoleThread, "Thing Console Thread").start();
	}
	
	private class ConsoleThread implements Runnable {
		private volatile boolean stop = false;
		
		@Override
		public void run() {
			printConsoleHelp();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				try {
					String command = readCommand(in);
					
					if (stop)
						break;
					
					if ("help".equals(command)) {
						printConsoleHelp();
					} else if ("exit".equals(command)) {
						exitSystem();
					} else {
						System.out.println(String.format("Unknown command: '%s'", command));
						printConsoleHelp();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private String readCommand(BufferedReader in) throws IOException {
			while (!in.ready()) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (stop) {
					return null;
				}
			}
			
			return in.readLine();
		}
	}

	private void exitSystem() {
		try {
			 camera.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (consoleThread != null) {
			consoleThread.stop = true;
		}
	}

	private void printConsoleHelp() {
		System.out.println("Commands:");
		System.out.println("help        Display help information.");
		System.out.println("exit        Exit system.");
		System.out.print("$");
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
		System.out.println("--help                     Display help information.");
		System.out.println("--host=HOST                Specify host name of server.");
		System.out.println("--port=PORT                Specify server port.");
		System.out.println("--tls-preferred            Specify whether TLS is preferred when connecting to server.");
		System.out.println("--log-level=LOG_LEVEL      Specify log level. Option values are info, debug or trace.");
	}
}
