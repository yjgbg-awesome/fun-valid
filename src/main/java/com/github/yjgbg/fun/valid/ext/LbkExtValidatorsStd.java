package com.github.yjgbg.fun.valid.ext;

import com.github.yjgbg.fun.valid.core.Errors;
import com.github.yjgbg.fun.valid.core.Getter;
import com.github.yjgbg.fun.valid.core.Validator;
import io.vavr.collection.Set;
import lombok.experimental.ExtensionMethod;
import org.intellij.lang.annotations.RegExp;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 常用的Validator扩展函数
 */
@ExtensionMethod(LbkExtValidatorsCore.class)
public class LbkExtValidatorsStd {
	public static <A, B> Validator<A>
	andF(Validator<A> that, Getter<A, B> prop, Function<B, Validator<? super B>> validatorFunction) {
		return that.and(prop, Validator.from(validatorFunction));
	}

	public static <A, B> Validator<A>
	andIterF(Validator<A> that, Getter<A, Iterable<B>> prop, Function<B, Validator<? super B>> validatorFunction) {
		return that.andIter(prop, Validator.from(validatorFunction));
	}

	public static <A, B> Validator<A>
	equal(Validator<A> that, Getter<A, B> prop, String message, B value) {
		return that.and(prop, message, x -> Objects.equals(x, value));
	}

	public static <A, B> Validator<A>
	in(Validator<A> that, Getter<A, B> prop, String message, Collection<? super B> values) {
		return that.and(prop, message, values::contains);
	}

	@SafeVarargs
	public static <A, B> Validator<A>
	in(Validator<A> that, Getter<A, B> prop, String message, B... values) {
		return that.and(prop, message, x -> Arrays.asList(values).contains(x));
	}


	public static <A, B> Validator<A>
	notIn(Validator<A> that, Getter<A, B> prop, String message, Collection<? super B> values) {
		return that.and(prop, message, x -> !values.contains(x));
	}

	@SafeVarargs
	public static <A, B> Validator<A>
	notIn(Validator<A> that, Getter<A, B> prop, String message, B... value) {
		return that.and(prop, message, x -> !Arrays.asList(value).contains(x));
	}

	public static <A> Validator<A>
	nonNull(Validator<A> that, String message) {
		return that.and(message, Objects::nonNull);
	}

	public static <A, B> Validator<A>
	nonNull(Validator<A> that, Getter<A, B> prop, String message) {
		return that.and(prop, message, Objects::nonNull);
	}

	public static <A, B> Validator<A>
	isNull(Validator<A> that, Getter<A, B> prop, String message) {
		return that.and(prop, message, Objects::isNull);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	lt(Validator<A> that, Getter<A, B> prop, String message, boolean allowNull, B max) {
		return that.and(prop, message, x -> x == null ? allowNull : x.compareTo(max) < 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	le(Validator<A> that, Getter<A, B> prop, String message, boolean allowNull, B max) {
		return that.and(prop, message, x -> x == null ? allowNull : x.compareTo(max) <= 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	gt(Validator<A> that, Getter<A, B> prop, String message, boolean allowNull, B min) {
		return that.and(prop, message, x -> x == null ? allowNull : x.compareTo(min) > 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	ge(Validator<A> that, Getter<A, B> prop, String message, boolean allowNull, B min) {
		return that.and(prop, message, x -> x == null ? allowNull : x.compareTo(min) >= 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	between(Validator<A> that, Getter<A, B> prop, String message, boolean allowNull, B min, B max) {
		return that.and(prop, message, x -> x == null ? allowNull :
				x.compareTo(min) >= 0 && x.compareTo(max) <= 0);
	}

	public static <A, B extends String> Validator<A>
	regexp(Validator<A> that, Getter<A, B> prop, String message, boolean allowNull, @RegExp String regexp) {
		return that.and(prop, message, x -> x == null ? allowNull : Pattern.matches(regexp, x));
	}

	public static <A, B extends String> Validator<A>
	length(Validator<A> that, Getter<A, B> prop, String message, boolean allowNull, int min, int max) {
		return that.and(prop, message, x -> x == null ? allowNull : x.length() >= min && x.length() <= max);
	}

	public static <A, B extends Collection<?>> Validator<A>
	size(Validator<A> that, Getter<A, B> prop, String message, boolean allowNull, int min, int max) {
		return that.and(prop, message, x -> x == null ? allowNull : (x.size() >= min && x.size() <= max));
	}

	public static Map<String, java.util.Set<String>> toJavaMap(Errors errors) {
		return errors.toMessageMap().mapValues(Set::toJavaSet).toJavaMap();
	}
}
