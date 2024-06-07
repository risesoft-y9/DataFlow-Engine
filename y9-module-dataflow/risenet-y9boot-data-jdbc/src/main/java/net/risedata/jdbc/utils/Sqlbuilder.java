package net.risedata.jdbc.utils;

import java.util.Collection;

import net.risedata.jdbc.operation.Where;

public class Sqlbuilder {
	private StringBuilder builder;
	private static final int LENGTH = 24;
	private boolean hasWhere = false;
	private boolean hasOrder = false;

	public Sqlbuilder() {
		this(LENGTH);
	}

	public Sqlbuilder(StringBuilder builder) {
		this.builder = builder;
	}

	public StringBuilder getBuilder() {
		return builder;
	}

	public Sqlbuilder(int length) {
		this.builder = new StringBuilder(length);
	}

	public Sqlbuilder(String str) {
		this.builder = new StringBuilder(str);
	}

	public Sqlbuilder where(String condition) {
		if (hasWhere) {
			this.builder.append(" and " + condition);
		} else {
			this.builder.append(" where " + condition);
			hasWhere = true;
		}
		return this;
	}

	public Sqlbuilder order(String field) {
		if (hasOrder) {
			this.builder.append(" , " + field);
		} else {
			this.builder.append(" order by  " + field);
			hasOrder = true;
		}
		return this;
	}
	
	public Sqlbuilder in(@SuppressWarnings("rawtypes") Object[] values, boolean bracket) {
		if (bracket) {
			this.builder.append("(");
		}
		boolean flag = false;
		for (Object object : values) {
			if (flag) {
				this.builder.append(",?");
			} else {
				flag = true;
				this.builder.append("?");
			}
			
		}
		if (bracket) {
			this.builder.append(")");
		}

		return this;
	}
	
	public Sqlbuilder in(@SuppressWarnings("rawtypes") Collection values, boolean bracket) {
		if (bracket) {
			this.builder.append("(");
		}
		boolean flag = false;
		for (Object object : values) {
			if (flag) {
				this.builder.append(",?");
			} else {
				flag = true;
				this.builder.append("?");
			}
			this.builder.append(object);
		}
		if (bracket) {
			this.builder.append(")");
		}

		return this;
	}
	public String getWhereCondition() {
		return this.builder.length() > 0 ? this.builder.substring(7) : "";
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
