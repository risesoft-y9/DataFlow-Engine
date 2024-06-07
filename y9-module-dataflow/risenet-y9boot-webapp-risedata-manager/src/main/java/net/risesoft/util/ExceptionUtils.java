package net.risesoft.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExceptionUtils {
	public static Throwable extractConcurrentException(Throwable throwable) {
		if (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
			if (throwable.getCause() != null) {
				return throwable.getCause();
			}
		}
		return throwable;
	}

	public static <T> List<T> flatMapAndCollect(List<Map<String, Object>> dataList,
			Function<Map<String, Object>, Collection<T>> mapper) {
		return dataList.stream().map(mapper).flatMap(Collection::stream).collect(Collectors.toList());
	}
}
