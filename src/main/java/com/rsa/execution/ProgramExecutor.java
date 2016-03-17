package com.rsa.execution;

import com.rsa.execution.config.ConfigurationManager;
import com.rsa.execution.dto.Execution;
import com.rsa.execution.dto.Program;

public interface ProgramExecutor {

	public abstract Execution execute(Program program);

	public abstract void setConfigurationManager(
			ConfigurationManager configurationManager);

	public abstract void setTempDir(String tempDir);

}