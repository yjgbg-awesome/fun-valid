package com.github.yjgbg.fun.valid.ext;

import com.github.yjgbg.fun.valid.core.Errors;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

import static io.vavr.API.Map;

public class LbkExtValidatorHypers {
	/**
	 * 将Errors映射为
	 * <a href="https://confluence.hypers.com/pages/viewpage.action?pageId=13008984">表单字段验证错误</a>
	 *
	 * @param code2message 将Errors中的message作为code，但于此同时，需要提供一个从code到message的映射器，一般用来处理i18n
	 */
	public static _422Error to422Error(Errors errors, Function<String, String> code2message, String message) {
		final var fields = LbkExtValidatorsCore.toMessageMap(errors)
				.mapValues(set ->
						set.map(msgInError ->
								Map("code", msgInError, "message", code2message.apply(msgInError)))
				);
		return new _422Error(message, fields);
	}

	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class _422Error {
		private final String message;
		private final Map<String, Set<Map<String, String>>> fields;
	}
}
