package com.github.yjgbg.validation;

import lombok.val;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

@FunctionalInterface
public interface Validator<A> {
	void valid(String key,A obj, BindingResult bindingResult);

	default BeanPropertyBindingResult valid(String key,A obj) {
		val res = new BeanPropertyBindingResult(obj,key);
		valid(key, obj,res );
		return res;
	}

	default BeanPropertyBindingResult valid(A obj) {
		return valid("",obj);
	}

	static <A> Validator<A> empty() {
		return (key, obj, bindingResult) -> {};
	}
}
