package com.github.yjgbg.validation.ext;

import com.github.yjgbg.validation.core.Getter;
import com.github.yjgbg.validation.core.LbkExtStdValidator;
import com.github.yjgbg.validation.core.Validator;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(LbkExtStdValidator.class)
public class LbkExtComparableValidator {
	public static <A, B extends Comparable<B>> Validator<A>
	lt(Validator<A> that, Getter<A, B> prop, B upperBound, String message) {
		return that.and(prop, message, x -> x != null && x.compareTo(upperBound) < 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	le(Validator<A> that, Getter<A, B> prop, B upperBound,String message) {
		return that.and(prop, message, x -> x != null && x.compareTo(upperBound) <= 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	gt(Validator<A> that, Getter<A, B> prop, B lowerBound, String message) {
		return that.and(prop, message, x -> x != null && x.compareTo(lowerBound) > 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	ge(Validator<A> that, Getter<A, B> prop, B lowerBound, String message) {
		return that.and(prop, message, x -> x != null && x.compareTo(lowerBound) >= 0);
	}

	public static <A, B extends Comparable<B>> Validator<A>
	between(Validator<A> that, Getter<A, B> prop, String message, B lowerBound, B upperBound) {
		return that.and(prop, message, x -> x != null
				&& x.compareTo(lowerBound) >= 0 && x.compareTo(upperBound) <= 0);
	}
}
