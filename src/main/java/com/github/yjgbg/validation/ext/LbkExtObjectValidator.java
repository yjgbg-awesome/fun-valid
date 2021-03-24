package com.github.yjgbg.validation.ext;

import com.github.yjgbg.validation.core.Getter;
import com.github.yjgbg.validation.core.LbkExtStdValidator;
import com.github.yjgbg.validation.core.Validator;
import lombok.experimental.ExtensionMethod;

import java.util.Arrays;
import java.util.Objects;

@ExtensionMethod({LbkExtStdValidator.class})
public class LbkExtObjectValidator {
	public static <A, B> Validator<A> equal(Validator<A> that, Getter<A, B> prop, String message, B value) {
		return that.and(prop, message, x -> Objects.equals(x, value));
	}

	@SafeVarargs
	public static <A, B> Validator<A> in(Validator<A> that, Getter<A, B> prop, String message, B... values) {
		return that.and(prop, message, x -> Arrays.asList(values).contains(x));
	}
	@SafeVarargs
	public static <A, B> Validator<A> notIn(Validator<A> that, Getter<A, B> prop, String message, B... value) {
		return that.and(prop, message, x -> !Arrays.asList(value).contains(x));
	}

	public static <A> Validator<A> nonNull(Validator<A> that, String message) {
		return that.and(message, Objects::nonNull);
	}

	public static <A, B> Validator<A> nonNull(Validator<A> that, Getter<A, B> prop, String message) {
		return that.and(prop, message, Objects::nonNull);
	}

	public static <A, B> Validator<A> isNull(Validator<A> that, Getter<A, B> prop, String message) {
		return that.and(prop, message, Objects::isNull);
	}
}
