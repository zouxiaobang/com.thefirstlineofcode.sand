package com.firstlinecode.sand.server.platform;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.firstlinecode.basalt.protocol.core.Protocol;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.granite.framework.core.auth.IAccountManager;
import com.firstlinecode.granite.framework.core.config.IApplicationConfiguration;
import com.firstlinecode.granite.framework.core.config.IApplicationConfigurationAware;
import com.firstlinecode.granite.framework.core.config.IConfiguration;
import com.firstlinecode.granite.framework.core.config.IConfigurationAware;
import com.firstlinecode.granite.framework.core.event.IEventProducer;
import com.firstlinecode.granite.framework.core.event.IEventProducerAware;
import com.firstlinecode.sand.protocols.actuator.Execute;
import com.firstlinecode.sand.protocols.emulators.light.Flash;
import com.firstlinecode.sand.server.actuator.ExecutionEvent;
import com.firstlinecode.sand.server.concentrator.Confirmed;
import com.firstlinecode.sand.server.concentrator.ConfirmedEvent;
import com.firstlinecode.sand.server.concentrator.IConcentrator;
import com.firstlinecode.sand.server.concentrator.IConcentratorFactory;
import com.firstlinecode.sand.server.concentrator.Node;
import com.firstlinecode.sand.server.device.Device;
import com.firstlinecode.sand.server.device.IDeviceManager;

public class SandCommandProvider implements CommandProvider, IEventProducerAware,
			IApplicationConfigurationAware, IConfigurationAware {
	private static final String COMMAND_SAND = "sand";
	private static final String PARAM_AUTHORIZE = "authorize";
	private static final String PARAM_DEVICES = "devices";
	private static final String PARAM_CONFIRM = "confirm";
	private static final String PARAM_EXECUTE = "execute";
	private static final String PARAM_HELP = "help";

	private static final String MSG_HELP = "sand - Monitoring and managing sand application.\r\n";
	
	private static final String MSG_DETAIL_HELP =
			"\tsand authorize <device_id> [authorizier] - Authorize a device to register.\r\n" +
			"\tsand devices [start_index] - Display registered devices. Twenty items each page.\r\n" +
			"\tsand confirm <concentrator_device_id> <node_device_id> - Confirm to add a node to concentrator.\r\n" +
			"\tsand execute <device_location> <action_name> [params...] - Execute an action on the specified device.\r\n" +
			"\tsand help - Display help information.\r\n";
	
	private static final String ACTION_NAME_FLASH = "flash";
	
	private static final String AUTHORIZE_DEVICE_VALIDITY_TIME = "authorize.device.validity.time";
	private static final int DEFAULT_AUTHORIZE_DEVICE_VALIDITY_TIME = 1000 * 60 * 30;
	
	@Dependency("account.manager")
	private IAccountManager accountManager;
	
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	@Dependency("concentrator.factory")
	private IConcentratorFactory concentratorFactory;
	
	private String domainName;
	
	private IEventProducer eventProducer;
	
	private Map<String, Protocol> actionNameToProtocols;
	
	private int deviceAuthorizationValidityTime;
	
	public SandCommandProvider() {
		actionNameToProtocols = createActionNameToProtocols();
	}
	
	private Map<String, Protocol> createActionNameToProtocols() {
		Map<String, Protocol> actionNameToProtocols = new HashMap<>();
		actionNameToProtocols.put(ACTION_NAME_FLASH, Flash.PROTOCOL);
		
		return actionNameToProtocols;
	}

	@Override
	public String getHelp() {
		return MSG_HELP;
	}
	
	public Object _help(CommandInterpreter interpreter) {
		String commandName = interpreter.nextArgument();
		if (COMMAND_SAND.equals(commandName))
			return getDetailHelp();
		
		return false;
	}
	
	private String getDetailHelp() {
		return MSG_DETAIL_HELP;
	}
	
	public void _sand(CommandInterpreter interpreter) {
		String nextArg = interpreter.nextArgument();
		
		if (nextArg == null || PARAM_HELP.equals(nextArg)) {
			printDetailHelp(interpreter);
		} else if (PARAM_AUTHORIZE.equals(nextArg)) {
			String deviceId = interpreter.nextArgument();
			if (deviceId == null) {
				interpreter.print("Error: You must provide a device ID.\n");	
				return;
			}
			String authorizer = interpreter.nextArgument();
			
			authorize(interpreter, deviceId, authorizer);
		} else if (PARAM_DEVICES.equals(nextArg)) {
			String sStartIndex = interpreter.nextArgument();
			
			int startIndex = 0;
			if (sStartIndex != null) {
				try {					
					startIndex = Integer.parseInt(sStartIndex);
				} catch (NumberFormatException e) {
					interpreter.print("Invalid start index. Start index must be a number.\n");
					return;
				}
			}
			
			displayDevices(startIndex);
		} else if (PARAM_CONFIRM.equals(nextArg)) {
			String concentratorDeviceId = interpreter.nextArgument();
			String nodeDeviceId = null;
			if (concentratorDeviceId != null) {
				nodeDeviceId = interpreter.nextArgument();				
			}
			
			if (nodeDeviceId == null) {
				interpreter.print("Error: You must provide a concentrator device ID and a node device ID.\n");	
				return;
			}
			
			Device device = deviceManager.getByDeviceId(concentratorDeviceId);
			if (device == null) {
				interpreter.print(String.format("Error: Concentrator which's device ID is '%s' not existed.\n", concentratorDeviceId));	
				return;
			}
			
			if (!concentratorFactory.isConcentrator(device)) {				
				interpreter.print(String.format("Error: Device which's device ID is '%s' isn't a concentrator.", concentratorDeviceId));	
				return;
			}
			
			Confirmed confirmed = concentratorFactory.getConcentrator(device).confirm(nodeDeviceId, domainName);
			eventProducer.fire(new ConfirmedEvent(confirmed.getRequestId(), confirmed.getNodeCreated()));
			
			interpreter.print(String.format("Device '%s' has already been confirmed to be a node of concentrator '%s'.\n", device.getDeviceId(), nodeDeviceId));
		} else if (PARAM_EXECUTE.equals(nextArg)) {
			String deviceLocation = interpreter.nextArgument();
			
			if (deviceLocation == null) {
				interpreter.print("Error: You must provide device location of the device which should execute the action.\n");	
				return;
			}
			
			String actionName = interpreter.nextArgument();
			if (actionName == null) {
				interpreter.print("Error: You must provide the action name and parameters to be executed on the device.\n");	
				return;
			}
			
			String sParams = interpreter.nextArgument();
			Map<String, String> params = null;
			if (sParams != null) {
				params = new HashMap<>();
				
				StringTokenizer tokenizer = new StringTokenizer(sParams, ",");
				while (tokenizer.hasMoreTokens()) {
					String param = tokenizer.nextToken();
					int equalMarkIndex = param.indexOf('=');
					
					if (equalMarkIndex == -1) {
						interpreter.print(String.format("Error: Illegal parameter: %s.\n", param));
						return;
					}
					
					if (equalMarkIndex == param.length() - 1) {
						interpreter.print(String.format("Error: Illegal parameter: %s.\n", param));
						return;
					}
					
					String paramName = param.substring(0, equalMarkIndex).trim();
					String paramValue = param.substring(equalMarkIndex + 1, param.length()).trim();
					params.put(paramName, paramValue);
				}
			}
			
			execute(interpreter, deviceLocation, actionName, params);
		} else {
			printDetailHelp(interpreter);
		}	
	}
	
	private void execute(CommandInterpreter interpreter, String deviceLocation, String actionName, Map<String, String> params) {
		String deviceId = null;
		String lanId = null;
		
		int slashIndex = deviceLocation.indexOf('/');
		
		if (slashIndex == deviceLocation.length() - 1)
			interpreter.print("Error: Invalid device location.\n");
			
		if (slashIndex == -1) {
			deviceId = deviceLocation;
		} else {
			deviceId = deviceLocation.substring(0, slashIndex);
			lanId = deviceLocation.substring(slashIndex + 1, deviceLocation.length());
		}
		
		deviceId = deviceId.trim();
		lanId = lanId.trim();
		
		if (!deviceManager.deviceIdExists(deviceId)) {
			interpreter.print("Error: Device which's device ID is '%s' not existed.\n");	
			return;
		}
		
		Device device = deviceManager.getByDeviceId(deviceId);
		Protocol protocol = actionNameToProtocols.get(actionName);
		if (protocol == null) {			
			interpreter.print("Error: Unsupported action name '%s'.\n");	
		}
		
		if (!concentratorFactory.isConcentrator(device) || lanId == null ||
				IConcentrator.LAN_ID_CONCENTRATOR.equals(lanId)) {
			executeOnDevice(interpreter, device, protocol, params);
		} else {
			executeOnNode(interpreter, device, lanId, protocol, params);			
		}
	}
	
	private boolean isActionSupported(CommandInterpreter interpreter, Device device, Protocol protocol) {
		String model = deviceManager.getModel(device.getDeviceId());
		if (!deviceManager.isActionSupported(model, protocol)) {
			interpreter.print(String.format("Error: Action which's protocol is '%s' isn't supported by device which's device ID is '%s'.\n",
					protocol, device.getDeviceId()));
			
			return false;
		}
		
		return true;
	}
	
	private void executeOnDevice(CommandInterpreter interpreter, Device device, Protocol protocol, Map<String, String> params) {
		if (!isActionSupported(interpreter, device, protocol))
			return;
		
		Object actionObject = createActionObject(interpreter, device.getModel(), protocol, params);
		eventProducer.fire(new ExecutionEvent(device, null, new Execute(actionObject)));
	}
	
	private void executeOnNode(CommandInterpreter interpreter, Device concentratorDevice, String lanId, Protocol protocol, Map<String, String> params) {
		Device nodeDevice = getNodeDevice(interpreter, concentratorDevice, lanId);
		if (nodeDevice == null)
			return;
		
		if (!isActionSupported(interpreter, nodeDevice, protocol))
			return;
		
		Object actionObject = createActionObject(interpreter, nodeDevice.getModel(), protocol, params);
		eventProducer.fire(new ExecutionEvent(concentratorDevice, lanId, new Execute(actionObject)));
	}
	
	private Device getNodeDevice(CommandInterpreter interpreter, Device concentratorDevice, String lanId) {
		IConcentrator concentrator = concentratorFactory.getConcentrator(concentratorDevice);
		Node node = concentrator.getNode(lanId);
		
		if (node != null) {
			return deviceManager.getByDeviceId(node.getDeviceId());
		}
		
		interpreter.print(String.format("Error: Node not existed. Concentrator's device ID is '%s'. Lan ID is '%s'.\n",
				concentratorDevice.getDeviceId(), lanId));
		
		return null;
	}

	private Object createActionObject(CommandInterpreter interpreter, String model, Protocol protocol, Map<String, String> params) {		
		Class<?> actionType = deviceManager.getActionType(model, protocol);
		try {
			Object action = actionType.newInstance();
			if (params != null && !params.isEmpty()) {				
				populateProperties(action, params);
			}
			
			return action;
		} catch (InstantiationException | IllegalAccessException e) {
			interpreter.print(String.format("Error: Can't initialize action object. Action type is %s.\n", actionType));
		} catch (IllegalArgumentException e) {
			interpreter.print(String.format("Error: Can't populate action's properties. Detail info is: %s.\n", e.getMessage()));
		}
		
		return null;
	}

	private void populateProperties(Object action, Map<String, String> params) {
		for (String paramName : params.keySet()) {
			try {
				PropertyDescriptor pd = new PropertyDescriptor(paramName, action.getClass());
				if (pd.getWriteMethod() != null) {
					Object value = getParam(pd.getPropertyType(), params.get(paramName));
					if (value == null)
						continue;
					
					pd.getWriteMethod().invoke(action, value);
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	private Object getParam(Class<?> propertyType, String paramValue) {
		if (!isPrimitiveType(propertyType))
			throw new IllegalArgumentException(String.format("Unsupported property type: %s", propertyType.getName()));
		
		return convertStringToPrimitiveType(propertyType, paramValue);
	}
	
	private Object convertStringToPrimitiveType(Class<?> type, String value) {
		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return Boolean.valueOf(value);
		} else if (type.equals(int.class) || type.equals(Integer.class)) {
			return Integer.valueOf(value);
		} else if (type.equals(long.class) || type.equals(Long.class)) {
			return Long.valueOf(value);
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			return Float.valueOf(value);
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return Double.valueOf(value);
		} else if (type.equals(BigInteger.class)) {
			return new BigInteger(value);
		} else if (type.equals(BigDecimal.class)) {
			return new BigDecimal(value);
		} else {
			return value;
		}
	}
	
	private boolean isPrimitiveType(Class<?> fieldType) {
		return fieldType.equals(String.class) ||
				fieldType.equals(boolean.class) ||
				fieldType.equals(Boolean.class) ||
				fieldType.equals(int.class) ||
				fieldType.equals(Integer.class) ||
				fieldType.equals(long.class) ||
				fieldType.equals(Long.class) ||
				fieldType.equals(float.class) ||
				fieldType.equals(Float.class) ||
				fieldType.equals(double.class) ||
				fieldType.equals(Double.class) ||
				fieldType.equals(BigInteger.class) ||
				fieldType.equals(BigDecimal.class) ||
				fieldType.isEnum();
	}

	private void displayDevices(int startIndex) {
		// TODO Auto-generated method stub
		
	}

	private void authorize(CommandInterpreter interpreter, String deviceId, String authorizer) {
		if (!deviceManager.isValid(deviceId)) {
			interpreter.print(String.format("Error: Invalid device ID '%s'.\n", deviceId));
			return;
		}
		
		if (deviceManager.deviceIdExists(deviceId)) {
			interpreter.print(String.format("Error: Device which's ID is '%s' has already registered.\n", deviceId));
			return;
		}
		
		if (authorizer != null && !accountManager.exists(authorizer)) {
			interpreter.print(String.format("Error: '%s' isn't a valid user.\n", authorizer));
			return;
		}
		
		deviceManager.authorize(deviceId, authorizer, deviceAuthorizationValidityTime);
		if (authorizer != null) {
			interpreter.print(String.format("Device which's ID is '%s' has authorized by '%s' in server console.\n", deviceId, authorizer));
		} else {
			interpreter.print(String.format("Device which's ID is '%s' has authorized by unknown user in server console.\n", deviceId));
		}
	}

	private void printDetailHelp(CommandInterpreter interpreter) {
		interpreter.print(getDetailHelp());
	}

	@Override
	public void setEventProducer(IEventProducer eventProducer) {
		this.eventProducer = eventProducer;
	}

	@Override
	public void setApplicationConfiguration(IApplicationConfiguration appConfiguration) {
		domainName = appConfiguration.getDomainName();
	}

	@Override
	public void setConfiguration(IConfiguration configuration) {
		deviceAuthorizationValidityTime = configuration.getInteger(AUTHORIZE_DEVICE_VALIDITY_TIME, DEFAULT_AUTHORIZE_DEVICE_VALIDITY_TIME);
	}

}
