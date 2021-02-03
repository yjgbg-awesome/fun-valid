package com.github.yjgbg.validation;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod(ValidatorExt.class)
public class JSR380Ext {
    public static <A> Validator<A> assertTrue(Validator<A> that,Getter<A,Boolean> prop) {
        return that.and(prop,JSR380Validators.assertTrue());
    }

    public static <A> Validator<A> assertTrue(Validator<A> that,Getter<A,Boolean> prop,String message) {
        return that.and(prop,JSR380Validators.assertTrue(message));
    }

    public static <A> Validator<A> assertFalse(Validator<A> that,Getter<A,Boolean> prop) {
        return that.and(prop,JSR380Validators.assertFalse());
    }

    public static <A> Validator<A> assertFalse(Validator<A> that,Getter<A,Boolean> prop,String message) {
        return that.and(prop,JSR380Validators.assertFalse(message));
    }
}
