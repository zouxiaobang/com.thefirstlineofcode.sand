package com.thefirstlineofcode.sand.server.console;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.pf4j.Extension;

import com.thefirstlineofcode.basalt.oxm.convention.PropertyDescriptor;
import com.thefirstlineofcode.basalt.protocol.core.Protocol;
import com.thefirstlineofcode.basalt.protocol.core.stanza.Iq;
import com.thefirstlineofcode.basalt.protocol.core.stanza.error.StanzaError;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.annotations.Dependency;
import com.thefirstlineofcode.granite.framework.core.auth.IAccountManager;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfiguration;
import com.thefirstlineofcode.granite.framework.core.config.IServerConfigurationAware;
import com.thefirstlineofcode.granite.framework.core.console.AbstractCommandsProcessor;
import com.thefirstlineofcode.granite.framework.core.console.IConsoleSystem;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventFirer;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.event.IEventFirerAware;
import com.thefirstlineofcode.granite.framework.core.pipeline.stages.processing.IProcessingContext;
import com.thefirstlineofcode.sand.protocols.actuator.Execute;
import com.thefirstlineofcode.sand.protocols.devices.gateway.ChangeMode;
import com.thefirstlineofcode.sand.protocols.devices.light.Flash;
import com.thefirstlineofcode.sand.server.actuator.ExecutionEvent;
import com.thefirstlineofcode.sand.server.actuator.IExecutionCallback;
import com.thefirstlineofcode.sand.server.concentrator.Confirmed;
import com.thefirstlineofcode.sand.server.concentrator.ConfirmedEvent;
import com.thefirstlineofcode.sand.server.concentrator.IConcentrator;
import com.thefirstlineofcode.sand.server.concentrator.IConcentratorFactory;
import com.thefirstlineofcode.sand.server.concentrator.Node;
import com.thefirstlineofcode.sand.server.devices.Device;
import com.thefirstlineofcode.sand.server.devices.DeviceAuthorizationDelegator;
import com.thefirstlineofcode.sand.server.devices.IDeviceManager;

@Extension
public class SandCommandsProcessor extends AbstractCommandsProcessor implements IEventFirerAware,
			IServerConfigurationAware {
	private static final char SEPARATOR_PARAM_NAME_AND_VALUE = '=';
	private static final String SEPARATOR_PARAMS = ",";
	private static final String COMMAND_GROUP_SAND = "sand";
	private static final String COMMANDS_GROUP_INTRODUCTION = "Monitoring and managing sand application.";

	private static final String COMMAND_EXECUTE = "execute";
	private static final String ACTION_NAME_FLASH = "flash";
	private static final String ACTION_NAME_CHANGE_MODE = "change-mode";
	
	@Dependency("device.authorization.delegator")
	private DeviceAuthorizationDelegator deviceAuthorizationDelegator;
	
	@BeanDependency
	private IAccountManager accountManager;
	
	@BeanDependency
	private IDeviceManager deviceManager;
	
	@BeanDependency
	private IConcentratorFactory concentratorFactory;
	
	private String domainName;
	
	private IEventFirer eventFirer;
	private Map<String, Protocol> actionNameToProtocols;
	
	public SandCommandsProcessor() {
		actionNameToProtocols = createActionNameToProtocols();
	}
	
	private Map<String, Protocol> createActionNameToProtocols() {
		Map<String, Protocol> actionNameToProtocols = new HashMap<>();
		actionNameToProtocols.put(ACTION_NAME_FLASH, Flash.PROTOCOL);
		actionNameToProtocols.put(ACTION_NAME_CHANGE_MODE, ChangeMode.PROTOCOL);
		
		return actionNameToProtocols;
	}

	@Override
	public void printHelp(IConsoleSystem consoleSystem) {
		consoleSystem.printTitleLine(String.format("%s Available commands:", getIntroduction()));
		consoleSystem.printContentLine("sand help - Display the help information for sand application management.");
		consoleSystem.printContentLine("sand authorize <DEVICE_ID> [AUTHORIZIER] - Authorize a device to register.");
		consoleSystem.printContentLine("sand devices [START_INDEX] - Display registered devices. Twenty items each page.");
		consoleSystem.printContentLine("sand confirm <CONCENTRATOR_DEVICE_ID> <NODE_DEVICE_ID> [CONFIRMER] - Confirm to add a node to concentrator.");
		consoleSystem.printContentLine("sand execute <DEVICE_LOCATION> <ACTION_NAME> [PARAMS...] - Execute an action on the specified device.");
	}
	
	@Override
	protected boolean isArgumentsMatched(String command, String[] args) {
		if (COMMAND_EXECUTE.equals(command)) {
			return args.length == 2 || args.length == 3;
		}
		
		return true;
	}
	
	void processExecute(IConsoleSystem consoleSystem, String[] args) {
		String deviceLocation = args[0];
		int slashIndex = deviceLocation.indexOf('/');
		
		if (slashIndex == deviceLocation.length() - 1) {
			consoleSystem.printMessageLine("Error: Invalid device location '%s'.");
			return;
		}
		
		String deviceId = null;
		String lanId = null;
		if (slashIndex == -1) {
			deviceId = deviceLocation;
		} else {
			deviceId = deviceLocation.substring(0, slashIndex);
			lanId = deviceLocation.substring(slashIndex + 1, deviceLocation.length());
		}
		
		deviceId = deviceId.trim();
		if (lanId != null)
			lanId = lanId.trim();
		
		if (!deviceManager.deviceIdExists(deviceId)) {
			consoleSystem.printMessageLine(String.format("Error: Device which's device ID is '%s' not existed.", deviceId));	
			return;
		}
		
		String actionName = args[1];
		Protocol protocol = actionNameToProtocols.get(actionName);
		if (protocol == null) {
			consoleSystem.printMessageLine(String.format("Error: Unsupported action name '%s'.", actionName));	
			return;
		}
		
		Map<String, String> params = null;
		if (args.length == 3) {
			params = getActionParams(args[2]);
			
			if (params == null) {
				consoleSystem.printMessageLine(String.format("Error: Illegal action parameters '%s' for action '%s'.",
						args[2], actionName));
				return;
			}
		}
		
		if (!deviceManager.isRegistered(deviceId)) {
			consoleSystem.printMessageLine(String.format("Error: Device which's device ID is '%s' isn't a registered device.", deviceId));
			return;
		}
		
		if (concentratorFactory.isLanNode(deviceId)) {
			consoleSystem.printMessageLine(String.format(
					"Error: Device which's device ID is '%s' is a LAN node. You should access it by it's concentrator.", deviceId));
			return;			
		}
		
		Device device = deviceManager.getByDeviceId(deviceId);
		if (!concentratorFactory.isConcentrator(device) && lanId != null &&
				!IConcentrator.LAN_ID_CONCENTRATOR.equals(lanId)) {
			consoleSystem.printMessageLine(String.format("Error: Try to deliver action by device '%s', but it isn't a concentrator.", deviceId));
			return;
		}
		
		if (lanId == null || IConcentrator.LAN_ID_CONCENTRATOR.equals(lanId)) {
			executeOnDevice(consoleSystem, device, protocol, params);
		} else {
			executeOnNode(consoleSystem, device, lanId, protocol, params);			
		}
	}
	
	private Map<String, String> getActionParams(String sParams) {
		Map<String, String> params = new HashMap<>();
		
		StringTokenizer tokenizer = new StringTokenizer(sParams, SEPARATOR_PARAMS);
		while (tokenizer.hasMoreTokens()) {
			String param = tokenizer.nextToken();
			int equalMarkIndex = param.indexOf(SEPARATOR_PARAM_NAME_AND_VALUE);
			
			if (equalMarkIndex == -1) {
				return null;
			}
			
			if (equalMarkIndex == param.length() - 1) {
				return null;
			}
			
			String paramName = param.substring(0, equalMarkIndex).trim();
			String paramValue = param.substring(equalMarkIndex + 1, param.length()).trim();
			params.put(paramName, paramValue);
		}
		
		return params;
	}

	private boolean isActionSupported(IConsoleSystem consoleSystem, Device device, Protocol protocol) {
		String model = deviceManager.getModel(device.getDeviceId());
		if (!deviceManager.isActionSupported(model, protocol)) {
			consoleSystem.printMessageLine(String.format("Error: Action which's protocol is '%s' isn't supported by device which's device ID is '%s'.",
					protocol, device.getDeviceId()));
			return false;
		}
		
		return true;
	}
	
	private void executeOnDevice(IConsoleSystem consoleSystem, Device device, Protocol protocol,
			Map<String, String> params) {
		if (!isActionSupported(consoleSystem, device, protocol)) {
			return;
		}
		
		Object actionObject = createActionObject(consoleSystem, device.getModel(), protocol, params);
		eventFirer.fire(new ExecutionEvent(device, null, new Execute(actionObject),
				new ExecutionCallback(device.getDeviceId(), protocol, consoleSystem)));
	}
	
	private void executeOnNode(IConsoleSystem consoleSystem, Device concentratorDevice, String lanId, Protocol protocol, Map<String, String> params) {
		Device nodeDevice = getNodeDevice(concentratorDevice, lanId);
		if (nodeDevice == null) {
			consoleSystem.printMessageLine(String.format("Error: Node not existed. Concentrator's device ID is '%s'. Lan ID is '%s'.\n",
					concentratorDevice.getDeviceId(), lanId));
			return;
		}
		
		if (!isActionSupported(consoleSystem, nodeDevice, protocol)) {
			return;
		}
		
		Object actionObject = createActionObject(consoleSystem, nodeDevice.getModel(), protocol, params);
		eventFirer.fire(new ExecutionEvent(concentratorDevice, lanId, createExecute(actionObject) ,
				new ExecutionCallback(concentratorDevice.getDeviceId() + "/" + lanId, protocol, consoleSystem)));
	}
	
	private Execute createExecute(Object actionObject) {
		Execute execute = new Execute(actionObject, true);
		calculateLanTimeout(execute, actionObject);
		
		return execute;
	}

	private void calculateLanTimeout(Execute execute, Object actionObject) {
		if (actionObject instanceof Flash) {
			Flash flash = (Flash)actionObject;
			int lanTimeout = flash.getRepeat() + 8;
			execute.setLanTimeout(lanTimeout);
		}
	}

	private class ExecutionCallback implements IExecutionCallback {
		private String deviceLocation;
		private Protocol protocol;
		private IConsoleSystem consoleSystem;
		
		public ExecutionCallback(String deviceLocation, Protocol protocol, IConsoleSystem consoleSystem) {
			this.deviceLocation = deviceLocation;
			this.protocol = protocol;
			this.consoleSystem = consoleSystem;
		}

		@Override
		public boolean processResult(IProcessingContext context, Iq result) {
			consoleSystem.printBlankLine();
			consoleSystem.printBlankLine();
			consoleSystem.printMessageLine(String.format(
					"Action(protocol: %s) executed successfully on the devcie which's location is '%s'.",
					protocol, deviceLocation));
			consoleSystem.printBlankLine();
			consoleSystem.printPrompt();
			
			return true;
		}

		@Override
		public boolean processError(IProcessingContext context, StanzaError error) {
			consoleSystem.printBlankLine();
			consoleSystem.printBlankLine();
			consoleSystem.printMessageLine(String.format(
					"Failed to execute an action(protocol: %s) on the device which's location is '%s'. %s",
					protocol, deviceLocation, getErrorDescrption(error)));
			consoleSystem.printBlankLine();
			consoleSystem.printPrompt();
			
			return true;
		}

		private String getErrorDescrption(StanzaError error) {
			if (error.getDefinedCondition() != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("Error description: ").
					append("Defined condition = ").
					append(error.getDefinedCondition()).
					append(".");
				
				if (error.getText() != null) {
					sb.append(" Error text = ").
						append(error.getText().getText());
					
					if (sb.charAt(sb.length() - 1) != '.') {						
						sb.append(".");
					}
				}
				
				return sb.toString();
			} else if (error.getApplicationSpecificCondition() != null) {
				return String.format("Error description: Application specific condition = %s.", error.getApplicationSpecificCondition().toString());
			} else {
				return "Unknown error.";
			}
			
		}
		
	}
	
	private Device getNodeDevice(Device concentratorDevice, String lanId) {
		IConcentrator concentrator = concentratorFactory.getConcentrator(concentratorDevice);
		Node node = concentrator.getNode(lanId);
		
		if (node != null) {
			return deviceManager.getByDeviceId(node.getDeviceId());
		}
		
		return null;
	}

	private Object createActionObject(IConsoleSystem consoleSystem, String model, Protocol protocol, Map<String, String> params) {		
		Class<?> actionType = deviceManager.getActionType(model, protocol);
		try {
			Object action = actionType.newInstance();
			if (params != null && !params.isEmpty()) {				
				populateProperties(action, params);
			}
			
			return action;
		} catch (InstantiationException | IllegalAccessException e) {
			consoleSystem.printMessageLine(String.format("Error: Can't initialize action object. Action type is %s.\n", actionType));
		} catch (IllegalArgumentException e) {
			consoleSystem.printMessageLine(String.format("Error: Can't populate action's properties. Detail info is: %s.\n", e.getMessage()));
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
		} else if (type.isEnum()) {
			Object[] constants = type.getEnumConstants();
			for (Object constant : constants) {
				if (((Enum<?>)constant).toString().equalsIgnoreCase(value)) {
					return constant;
				}
			}
			
			throw new RuntimeException(String.format("Can't convert string '%s' to instance of enum type %s.", type, value));
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
	
	void processAuthorize(IConsoleSystem consoleSystem, String deviceId) {
		this.processAuthorize(consoleSystem, deviceId, domainName);
	}
	
	void processAuthorize(IConsoleSystem consoleSystem, String deviceId, String authorizer) {
		if (!deviceManager.isValid(deviceId)) {
			consoleSystem.printMessageLine(String.format("Error: Invalid device ID '%s'.", deviceId));
			return;
		}
		
		if (deviceManager.deviceIdExists(deviceId)) {
			consoleSystem.printMessageLine(String.format("Error: Device which's ID is '%s' has already registered.", deviceId));
			return;
		}
		
		if (authorizer != null && !domainName.equals(authorizer) && !accountManager.exists(authorizer)) {
			consoleSystem.printMessageLine(String.format("Error: '%s' isn't a valid authorizer.", authorizer));
			return;
		}
		
		deviceAuthorizationDelegator.authorize(deviceId, authorizer);
		if (authorizer != null) {
			consoleSystem.printMessageLine(String.format("Device which's ID is '%s' has authorized by '%s' in server console.", deviceId, authorizer));
		} else {
			consoleSystem.printMessageLine(String.format("Device which's ID is '%s' has authorized by unknown user in server console.", deviceId));
		}
	}
	
	void processConfirm(IConsoleSystem consoleSystem, String concentratorDeviceId, String nodeDeviceId) {
		this.processConfirm(consoleSystem, new String[] {concentratorDeviceId, nodeDeviceId, domainName});
	}
	
	void processConfirm(IConsoleSystem consoleSystem, String[] args) {
		String concentratorDeviceId = args[0];		
		Device device = deviceManager.getByDeviceId(concentratorDeviceId);
		if (device == null) {
			consoleSystem.printMessageLine(String.format("Error: Concentrator which's device ID is '%s' not existed.", concentratorDeviceId));	
			return;
		}
		
		if (!concentratorFactory.isConcentrator(device)) {
			consoleSystem.printMessageLine(String.format("Error: Device which's device ID is '%s' isn't a concentrator.", concentratorDeviceId));
			return;
		}
		
		String nodeDeviceId = args[1];
		if (!deviceManager.isValid(nodeDeviceId)) {
			consoleSystem.printMessageLine(String.format("Error: Invalid node device ID '%s'.", nodeDeviceId));
			return;
		}
		
		String confirmer = args[2];
		if (!confirmer.equals(domainName) && !accountManager.exists(confirmer)) {
			consoleSystem.printMessageLine(String.format("Error: '%s' isn't a valid user.", confirmer));
			return;
		}
		
		Confirmed confirmed = concentratorFactory.getConcentrator(device).confirm(nodeDeviceId, confirmer);
		eventFirer.fire(new ConfirmedEvent(confirmed.getRequestId(), confirmed.getNodeCreated()));
		
		consoleSystem.printMessageLine(String.format("Concentrator device which's ID is '%s' has been confirmed to add device which's ID is '%s' as it's node by user '%s' in server console.",
				concentratorDeviceId, nodeDeviceId, confirmer));
	}
	
	@Override
	public void setServerConfiguration(IServerConfiguration serverConfiguration) {
		domainName = serverConfiguration.getDomainName();
	}
	
	@Override
	public void setEventFirer(IEventFirer evenetFirer) {
		this.eventFirer = evenetFirer;
	}
	
	@Override
	public String getGroup() {
		return COMMAND_GROUP_SAND;
	}

	@Override
	public String[] getCommands() {
		return new String[] {
			"authorize", "devices", "confirm", COMMAND_EXECUTE, "help"
		};
	}

	@Override
	public String getIntroduction() {
		return COMMANDS_GROUP_INTRODUCTION;
	}
}
