package com.github.yjgbg.validation.ext;

import com.github.yjgbg.validation.core.Getter;
import com.github.yjgbg.validation.core.LbkExtStdValidator;
import com.github.yjgbg.validation.core.Validator;
import lombok.experimental.ExtensionMethod;

import java.util.regex.Pattern;

@ExtensionMethod({LbkExtStdValidator.class})
public class LbkExtStringValidator {
	public static <A, B extends String> Validator<A>
	regexp(Validator<A> that, Getter<A, B> prop, String message, boolean allowNull, String regexp) {
		return that.and(prop, message, x -> x == null ? allowNull : Pattern.matches(regexp, x));
	}

	public static <A, B extends String> Validator<A>
	lengthBetween(Validator<A> that, Getter<A, B> prop, String message, int lower, int upper) {
		return that.and(prop, message, x -> x != null && x.length() >= lower && x.length() <= upper);
	}
}
