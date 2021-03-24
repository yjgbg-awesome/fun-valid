package com.github.yjgbg.validation.ext;

import com.github.yjgbg.validation.core.Getter;
import com.github.yjgbg.validation.core.LbkExtStdValidator;
import com.github.yjgbg.validation.core.Validator;
import lombok.experimental.ExtensionMethod;

import java.util.function.Function;
import java.util.regex.Pattern;

@ExtensionMethod({LbkExtStdValidator.class})
public class LbkExtCharSequenceValidator {
	public static <A,B extends CharSequence> Validator<A>
	regexp(Validator<A> that, Getter<A, B> prop,boolean allowNull, String regexp, Function<B,String> message) {
		return that.and(prop,message,x -> x==null ? allowNull	 :Pattern.matches(regexp,x));
	}

	public static <A,B extends CharSequence> Validator<A>
	lengthBetween(Validator<A> that,Getter<A,B> prop, int lower,int upper, Function<B,String> message) {
		return that.and(prop,message,x -> x!=null && x.length() >= lower && x.length() <= upper);
	}
}
