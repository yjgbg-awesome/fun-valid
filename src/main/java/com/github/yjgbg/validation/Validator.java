package com.github.yjgbg.validation;

import java.util.function.Predicate;

/**
 * Validator接口，以及Validator的构建以及，Validator与Getter的乘法,Validator之间的加法
 * @param <A>
 */
@FunctionalInterface
public interface Validator<A> {
	Error valid(A obj);

	static <A> Validator<A> none() {
		return obj -> Error.none();
	}

	static <A> Validator<A> of(Predicate<A> test, String code) {
		return obj -> test.test(obj) ? Error.none() : Error.code(code);
	}

	static <A,B> Validator<A> wrapper(Getter<A,B> prop,Validator<B> validator) {
		return obj -> Error.wrapper(prop.propertyName(), validator.valid(prop.apply(obj)));
	}

	static <A> Validator<A> plus(Validator<A> arg1, Validator<A> arg2) {
		return obj -> Error.plus(arg1.valid(obj),arg2.valid(obj));
	}
}
