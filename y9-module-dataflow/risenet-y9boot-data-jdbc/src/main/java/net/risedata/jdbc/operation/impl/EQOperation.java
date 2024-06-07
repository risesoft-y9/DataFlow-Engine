package net.risedata.jdbc.operation.impl;

import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.operation.SqlDefaultOperation;

public class EQOperation extends DefaultOperation implements SqlDefaultOperation{

	public EQOperation() {
		super("=");
	}

	@Override
	public boolean hasOperation(String sqlType) {
		return true;
	}

	@Override
	public Operation getOperation(String sqlType) {
		return this;
	}

	
}
