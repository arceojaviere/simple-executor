package com.rsa.proc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.UUID;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rsa.proc.config.ConfigurationManager;
import com.rsa.proc.config.ConfigurationNotFoundException;
import com.rsa.proc.config.ProgramConfiguration;
import com.rsa.proc.dto.Execution;
import com.rsa.proc.dto.Program;
import com.rsa.proc.streams.AccumulatingLogOutputStream;

public class ProCExecutorCommonsExec {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProCExecutorCommonsExec.class);
	
	private static final String[] STRING_ARRAY_ZERO = new String[0]; 
			
	private ConfigurationManager configurationManager;
	private String tempDir;
	private int bufferSize;
	
	public ProCExecutorCommonsExec(){
		this.bufferSize = 10240;
	}

	public Execution execute(Program program) {
		ProgramConfiguration programConfiguration;
		Execution result = new Execution();
		CommandLine commandLine;
		LinkedList<String> arguments;
		DefaultExecutor executor = new DefaultExecutor();
		PumpStreamHandler streamHandler;
		AccumulatingLogOutputStream stderrAccumulator = new AccumulatingLogOutputStream();
		AccumulatingLogOutputStream stdoutAccumulator = new AccumulatingLogOutputStream();
		Integer returnCode = null;
		StringBuffer tempFileBuffer;
		File tempFile;
		
	
		ProCExecutorCommonsExec.LOGGER.debug("Executing program: {}", program);
		
		result.setProgram(program);
		result.setExecutionSuccessful(true);
		result.setStartDate(Calendar.getInstance().getTime());
		
		tempFileBuffer = new StringBuffer(this.tempDir);
		tempFileBuffer.append("/");
		tempFileBuffer.append(program.getName());
		tempFileBuffer.append(UUID.randomUUID().toString());
		
		tempFile = new File(tempFileBuffer.toString());
		
		ProCExecutorCommonsExec.LOGGER.debug("Using temp file: {}", tempFile.getAbsoluteFile());
		
		try {
			
			programConfiguration = this.configurationManager.getProgramConfiguration(program.getName());
			
			arguments = new LinkedList<String>();
			arguments.addAll(program.getArguments());
			
			this.appendFixedArguments(arguments, programConfiguration, tempFile);
			
			commandLine = new CommandLine(programConfiguration.getBinaryFile());
			commandLine.addArguments(arguments.toArray(ProCExecutorCommonsExec.STRING_ARRAY_ZERO));
			
			streamHandler = new PumpStreamHandler(stdoutAccumulator, stderrAccumulator);
			executor.setStreamHandler(streamHandler);
		    
			ProCExecutorCommonsExec.LOGGER.debug("Commencing execution. Program: {}; Config: {}; Real arguments: {}", program, programConfiguration, arguments);
			
			
			returnCode = executor.execute(commandLine);
			
			result.setExecutionSuccessful(programConfiguration.getSuccessfulReturnValues().isEmpty() ||
					programConfiguration.getSuccessfulReturnValues().contains(returnCode));
			
			ProCExecutorCommonsExec.LOGGER.debug("Execution finished. Return code: {}. Recovering file... ", returnCode);
			
			result.setResult(this.readProgramResultFile(tempFile));
			
			ProCExecutorCommonsExec.LOGGER.debug("Recovery complete. Deleting file.");
			
			if(!tempFile.delete()){
				ProCExecutorCommonsExec.LOGGER.error("Could not delete file " + tempFile.getAbsolutePath());
			}
			
		}catch (IOException e) {
			ProCExecutorCommonsExec.LOGGER.error("Exception executing program {}", program, e);
			result.setExecutionSuccessful(false);
			result.setErrorDescription(e.getMessage());
		}catch (ConfigurationNotFoundException e) {
			ProCExecutorCommonsExec.LOGGER.error("Exception executing program {}", program, e);
			result.setExecutionSuccessful(false);
			result.setErrorDescription(e.getMessage());
		}finally{
			result.setEndDate(Calendar.getInstance().getTime());
			result.setReturnCode(returnCode);
			result.setStderr(stderrAccumulator.getStreamContent());
			result.setStdout(stdoutAccumulator.getStreamContent());
			
		}
		
		ProCExecutorCommonsExec.LOGGER.debug("Returning result: {}", result);
		
		return result;
	}

	private void appendFixedArguments(LinkedList<String> arguments,
			ProgramConfiguration programConfiguration, File tempFile) {
		
		arguments.add(tempFile.getAbsolutePath());
		
		if(programConfiguration.getCacheDir() != null){
			
			arguments.add(programConfiguration.getCacheDir());
		}
		
		if(programConfiguration.getUser() != null){
			arguments.add(programConfiguration.getUser());
		}
		
		
	}

	private byte[] readProgramResultFile(File tempFile) throws IOException{
		byte[] result;
		ByteArrayOutputStream bos = new ByteArrayOutputStream(this.bufferSize);
		FileInputStream fis = new FileInputStream(tempFile);
		int buffer;
		
		while((buffer = fis.read()) >= 0){
			bos.write(buffer);
		}
		
		fis.close();
		bos.flush();
		result = bos.toByteArray();
		
		bos.close();
		
		return result;
	}

	public ConfigurationManager getConfigurationManager() {
		return configurationManager;
	}

	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public String getTempDir() {
		return tempDir;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}
	

}
