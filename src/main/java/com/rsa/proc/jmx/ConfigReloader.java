package com.rsa.proc.jmx;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import com.rsa.proc.config.ConfigurationLoadingException;
import com.rsa.proc.config.ConfigurationManager;

public class ConfigReloader extends StandardMBean implements ConfigReloaderMBean {

	public ConfigReloader() throws NotCompliantMBeanException{
		super(ConfigReloaderMBean.class);
	}
	private ConfigurationManager manager;
	
	@Override
	public void reloadConfiguration() throws ConfigurationLoadingException{
		this.manager.reloadConfiguration();

	}

	public ConfigurationManager getManager() {
		return manager;
	}

	public void setManager(ConfigurationManager manager) {
		this.manager = manager;
	}

}
