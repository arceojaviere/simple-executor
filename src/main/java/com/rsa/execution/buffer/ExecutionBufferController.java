package com.rsa.execution.buffer;

import java.util.List;

import com.rsa.execution.buffer.dto.BufferAdditionResult;
import com.rsa.execution.buffer.dto.BufferedDestination;
import com.rsa.execution.dto.Program;

public interface ExecutionBufferController {
	public abstract BufferAdditionResult addExecution(String requestId, Program program);
	public abstract List<BufferedDestination> generateDestinationList(Program program);

}
