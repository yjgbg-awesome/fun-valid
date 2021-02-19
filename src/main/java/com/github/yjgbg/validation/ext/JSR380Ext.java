package com.github.yjgbg.validation.ext;

import com.github.yjgbg.validation.core.Getter;
import com.github.yjgbg.validation.core.Validator;
import com.github.yjgbg.validation.core.ValidatorStdExt;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(ValidatorStdExt.class)
public class JSR380Ext {
  public static <A> Validator<A> assertTrue(Validator<A> that, Getter<A, Boolean> prop) {
    return that.and(prop, JSR380Validators.assertTrue());
  }

  public static <A> Validator<A> assertTrue(
      Validator<A> that, Getter<A, Boolean> prop, String message) {
    return that.and(prop, JSR380Validators.assertTrue(message));
  }

  public static <A> Validator<A> assertFalse(Validator<A> that, Getter<A, Boolean> prop) {
    return that.and(prop, JSR380Validators.assertFalse());
  }

  public static <A> Validator<A> assertFalse(
      Validator<A> that, Getter<A, Boolean> prop, String message) {
    return that.and(prop, JSR380Validators.assertFalse(__ -> message));
  }

  public static <A, B extends Comparable<B>> Validator<A> max(
      Validator<A> that, Getter<A, B> prop, B upperBound) {
    return that.and(prop, JSR380Validators.max(upperBound));
  }

  public static <A, B extends Comparable<B>> Validator<A> max(
      Validator<A> that, Getter<A, B> prop, B upperBound, String message) {
    return that.and(prop, JSR380Validators.max(upperBound, __ -> message));
  }

  public static <A, B extends Comparable<B>> Validator<A> min(
      Validator<A> that, Getter<A, B> prop, B lowerBound) {
    return that.and(prop, JSR380Validators.min(lowerBound));
  }

  public static <A, B extends Comparable<B>> Validator<A> min(
      Validator<A> that, Getter<A, B> prop, B lowerBound, String message) {
    return that.and(prop, JSR380Validators.min(lowerBound, __ -> message));
  }

  public static <A, B extends Comparable<B>> Validator<A> range(
      Validator<A> that, Getter<A, B> prop, B lowerBound, B upperBound, String message) {
    return that.and(prop, JSR380Validators.range(lowerBound, upperBound, __ -> message));
  }

  public static <A, B extends Comparable<B>> Validator<A> range(
      Validator<A> that, Getter<A, B> prop, B lowerBound, B upperBound) {
    return that.and(prop, JSR380Validators.range(lowerBound, upperBound));
  }
}
