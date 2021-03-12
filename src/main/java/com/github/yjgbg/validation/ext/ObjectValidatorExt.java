package com.github.yjgbg.validation.ext;

import com.github.yjgbg.validation.core.Getter;
import com.github.yjgbg.validation.core.Validator;
import com.github.yjgbg.validation.core.ValidatorStdExt;
import lombok.experimental.ExtensionMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

@ExtensionMethod({ValidatorStdExt.class})
public class ObjectValidatorExt {
	public static <A, B> Validator<A>
	equal(Validator<A> that, Getter<A, B> prop, B value, Function<B, String> message) {
		return that.and(prop, message, x -> Objects.equals(x, value));
	}

	public static <A, B> Validator<A>
	notEquals(Validator<A> that, Getter<A, B> prop, B value, String message) {
		return that.and(prop, message.fmt(), x -> !Objects.equals(x, value));
	}

	@SafeVarargs
	public static <A, B> Validator<A>
	in(Validator<A> that, Getter<A, B> prop, Function<B, String> message, B... values) {
		return that.and(prop, message, x -> Arrays.asList(values).contains(x));
	}
	@SafeVarargs
	public static <A, B> Validator<A>
	notIn(Validator<A> that, Getter<A, B> prop, Function<B, String> message, B... value) {
		return that.and(prop, message, x -> !Arrays.asList(value).contains(x));
	}

	public static <A, B> Validator<A>
	in(Validator<A> that, Getter<A, B> prop, Function<B, String> message, Collection<B> values) {
		return that.and(prop, message, values::contains);
	}
	public static <A, B> Validator<A>
	notIn(Validator<A> that, Getter<A, B> prop, Function<B, String> message, Collection<B> values) {
		return that.and(prop, message, x -> !values.contains(x));
	}

	public static <A> Validator<A> nonNull(Validator<A> that, String message) {
		return that.and(message.fmt(), Objects::nonNull);
	}

	public static <A, B> Validator<A>
	nonNull(Validator<A> that, Getter<A, B> prop, String message) {
		return that.and(prop, message.fmt(), Objects::nonNull);
	}

	public static <A, B> Validator<A>
	isNull(Validator<A> that, Getter<A, B> prop, String message) {
		return that.and(prop, message.fmt(), Objects::isNull);
	}
}
