package net.risesoft.util;

import java.util.stream.Stream;

public enum JobLogStatusEnum {

	EXECUTING("0", "执行中"), SUCCESS("1", "成功"), FAILURE("2", "失败");

	private final String code;
	private final String description;

	JobLogStatusEnum(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public static JobLogStatusEnum fromCode(String code) {
		return Stream.of(values()).filter(status -> status.getCode().equals(code)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid status code: " + code));
	}

}
