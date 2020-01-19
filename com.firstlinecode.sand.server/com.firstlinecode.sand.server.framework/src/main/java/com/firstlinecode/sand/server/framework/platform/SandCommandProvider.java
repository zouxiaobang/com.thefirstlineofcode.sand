package com.firstlinecode.sand.server.framework.platform;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.sand.server.framework.auth.IDeviceManager;

public class SandCommandProvider implements CommandProvider {
	private static final String MSG_HELP = "sand - monitoring and managing sand application.\r\n";
	
	private static final String MSG_DETAIL_HELP =
			"\tsand authorize <device_id> - Authorize a device to register.\r\n" +
			"\tsand devices [start_index] - Display registered devices. Twenty items each page.\r\n" +
			"\tsand help - Display help information.\r\n";
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	@Override
	public String getHelp() {
		return MSG_HELP;
	}
	
	public Object _help(CommandInterpreter interpreter) {
		String commandName = interpreter.nextArgument();
		if ("sand".equals(commandName))
			return getDetailHelp();
		
		return false;
	}
	
	private String getDetailHelp() {
		return MSG_DETAIL_HELP;
	}
	
	public void _sand(CommandInterpreter interpreter) {
		String nextArg = interpreter.nextArgument();
		
		if (nextArg == null || "help".equals(nextArg)) {
			printDetailHelp(interpreter);
		} else if ("authorize".equals(nextArg)) {
			String deviceId = interpreter.nextArgument();
			if (deviceId == null) {
				interpreter.print(String.format("You must provide a device ID.\n"));	
				return;
			}
			
			authorize(deviceId);
		} else if ("devices".equals(nextArg)) {
			String sStartIndex = interpreter.nextArgument();
			
			int startIndex = 0;
			if (sStartIndex != null) {
				try {					
					startIndex = Integer.parseInt(sStartIndex);
				} catch (NumberFormatException e) {
					interpreter.print(String.format("Invalid start index. Start index must be a number.\n"));
					return;
				}
			}
			
			displayDevices(startIndex);
		} else {
			printDetailHelp(interpreter);
		}	
	}
	
	private void displayDevices(int startIndex) {
		// TODO Auto-generated method stub
		
	}

	private void authorize(String deviceId) {
		// TODO Auto-generated method stub
		
	}

	private void printDetailHelp(CommandInterpreter interpreter) {
		interpreter.print(getDetailHelp());
	}

}
