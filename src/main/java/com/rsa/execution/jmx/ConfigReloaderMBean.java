package com.rsa.execution.jmx;

import com.rsa.execution.config.ConfigurationLoadingException;

public interface ConfigReloaderMBean {
	public abstract void reloadConfiguration() throws ConfigurationLoadingException;
}
