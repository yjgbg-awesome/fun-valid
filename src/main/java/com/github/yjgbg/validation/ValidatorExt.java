package com.github.yjgbg.validation;

import java.util.function.Predicate;

public class ValidatorExt {
	public static <A> Validator<A> and(Validator<A> that,Validator<A> another) {
		return Validator.plus(that,another);
	}

	public static <A> Validator<A> and(Validator<A> that, Predicate<A> test,String code) {
		return Validator.plus(that,Validator.of(test,code));
	}

	public static  <A,B> Validator<A> and(Validator<A> that,Getter<A,B> prop, Validator<B> another) {
		return Validator.plus(that,Validator.wrapper(prop,another));
	}

	public static  <A,B> Validator<A> and(Validator<A> that,Getter<A,B> prop, Predicate<B> test,String code) {
		return Validator.plus(that,Validator.wrapper(prop,Validator.of(test,code)));
	}
}
