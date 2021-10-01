package com.thefirstlineofcode.sand.demo.server.console;

import org.pf4j.Extension;

import com.thefirstlineofcode.granite.framework.core.adf.data.IDataObjectFactory;
import com.thefirstlineofcode.granite.framework.core.adf.data.IDataObjectFactoryAware;
import com.thefirstlineofcode.granite.framework.core.annotations.BeanDependency;
import com.thefirstlineofcode.granite.framework.core.auth.Account;
import com.thefirstlineofcode.granite.framework.core.auth.IAccountManager;
import com.thefirstlineofcode.granite.framework.core.console.AbstractCommandsProcessor;
import com.thefirstlineofcode.granite.framework.core.console.IConsoleSystem;

@Extension
public class SandDemoCommandsProcessor extends AbstractCommandsProcessor implements IDataObjectFactoryAware {
	private static final String USER_NAME_KANG = "kang";
	private static final String USER_NAME_DONGGER = "dongger";
	private static final String COMMAND_GROUP_SAND_DEMO = "sand-demo";
	private static final String COMMANDS_GROUP_INTRODUCTION = "Some utils for sand demo.";
	
	@BeanDependency
	private IAccountManager accountManager;
	private IDataObjectFactory dataObjectFactory;
	
	@Override
	public void printHelp(IConsoleSystem consoleSystem) {
		consoleSystem.printTitleLine(String.format("%s Available commands:", getIntroduction()));
		consoleSystem.printContentLine("sand-demo help - Display the help information for sand demo utils.");
		consoleSystem.printContentLine("sand-demo init-users - Install predefined users into system.");
	}
	
	void processInitUsers(IConsoleSystem consoleSystem) {
		if (accountManager.exists(USER_NAME_DONGGER) &&
				accountManager.exists(USER_NAME_KANG)) {
			consoleSystem.printMessageLine("The predefined users for sand demo has already existed in system. Ignore to execute the command.");
			return;
		}
		
		accountManager.add(createAccount(USER_NAME_DONGGER));
		accountManager.add(createAccount(USER_NAME_KANG));
		
		consoleSystem.printMessageLine("The predefined users for sand demo has installed.");
	}
	
	private Account createAccount(String userName) {
		Account account = dataObjectFactory.create(Account.class);
		account.setName(userName);
		account.setPassword(userName);
		
		return account;
	}
	
	@Override
	public String getGroup() {
		return COMMAND_GROUP_SAND_DEMO;
	}
	
	@Override
	public String[] getCommands() {
		return new String[] {
			"help", "init-users"
		};
	}

	@Override
	public String getIntroduction() {
		return COMMANDS_GROUP_INTRODUCTION;
	}

	@Override
	public void setDataObjectFactory(IDataObjectFactory dataObjectFactory) {
		this.dataObjectFactory = dataObjectFactory;
	}
}
