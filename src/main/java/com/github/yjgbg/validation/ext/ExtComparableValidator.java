package com.github.yjgbg.validation.ext;

import com.github.yjgbg.validation.core.ExtStdValidator;
import com.github.yjgbg.validation.core.Getter;
import com.github.yjgbg.validation.core.Validator;
import lombok.experimental.ExtensionMethod;

import java.util.function.Function;

@ExtensionMethod(ExtStdValidator.class)
public class ExtComparableValidator {
	public static <A, B extends Comparable<B>> Validator<A>
	littleThan(Validator<A> that, Getter<A, B> prop, B upperBound, Function<B, String> message) {
		return that.and(prop, message, x -> x != null && x.compareTo(upperBound) < 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	notGreatThan(Validator<A> that, Getter<A, B> prop, B upperBound, Function<B, String> message) {
		return that.and(prop, message, x -> x != null && x.compareTo(upperBound) <= 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	greatThan(Validator<A> that, Getter<A, B> prop, B lowerBound, Function<B, String> message) {
		return that.and(prop, message, x -> x != null && x.compareTo(lowerBound) > 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	notLittleThan(Validator<A> that, Getter<A, B> prop, B lowerBound, Function<B, String> message) {
		return that.and(prop, message, x -> x != null && x.compareTo(lowerBound) >= 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	inRange(Validator<A> that, Getter<A, B> prop, B lowerBound, B upperBound, Function<B, String> message) {
		return that.and(prop, message, x -> x != null && x.compareTo(lowerBound) >= 0 && x.compareTo(upperBound) <= 0);
	}
}
