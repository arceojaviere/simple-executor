package com.rsa.execution.buffer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rsa.execution.ProgramExecutor;
import com.rsa.execution.buffer.dto.BufferedDestination;
import com.rsa.execution.dto.Execution;
import com.rsa.execution.dto.Program;

public class ExecutionBuffer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionBuffer.class);
	
	private ExecutionBufferController controller;
	private ProgramExecutor executor;
	
	public List<BufferedDestination> execute(Program program){
		List<BufferedDestination> destinations;
		Execution execution;
		
		LOGGER.debug("Commencing execution for program: " + program);
		
		execution = this.executor.execute(program);
		
		destinations = this.controller.generateDestinationList(program);
		
		LOGGER.debug("Assigning response: " + execution + " to " + destinations);
		
		if(destinations != null){
			for (BufferedDestination d : destinations){
				d.setExecutionResult(execution);
			}
		}
		
		return destinations;
	}
	
	public ExecutionBufferController getController() {
		return controller;
	}

	public void setController(ExecutionBufferController controller) {
		this.controller = controller;
	}

	public ProgramExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(ProgramExecutor executor) {
		this.executor = executor;
	}
	
}
