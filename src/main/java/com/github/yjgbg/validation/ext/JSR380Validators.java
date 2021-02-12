package com.github.yjgbg.validation.ext;

import com.github.yjgbg.validation.core.Validator;

import java.util.function.Function;

public final class JSR380Validators {
  private JSR380Validators() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static Validator<Boolean> assertTrue() {
    return Validator.of(
        x -> x != null && x, __ -> "javax.validation.constraints.AssertTrue.message");
  }

  public static Validator<Boolean> assertTrue(String message) {
    return Validator.of(x -> x != null && x, __ -> message);
  }

  public static Validator<Boolean> assertFalse() {
    return Validator.of(
        x -> x != null && !x, __ -> "javax.validation.constraints.AssertFalse.message");
  }

  public static Validator<Boolean> assertFalse(Function<Boolean, String> message) {
    return Validator.of(x -> x != null && !x, message);
  }

  public static <A extends Comparable<A>> Validator<A> max(A upperBound) {
    return Validator.of(
        x -> x != null && x.compareTo(upperBound) <= 0,
        __ -> "javax.validation.constraints.Max.message");
  }

  public static <A extends Comparable<A>> Validator<A> max(
      A upperBound, Function<A, String> message) {
    return Validator.of(x -> x != null && x.compareTo(upperBound) <= 0, message);
  }

  public static <A extends Comparable<A>> Validator<A> min(A lowerBound) {
    return Validator.of(
        x -> x != null && x.compareTo(lowerBound) >= 0,
        __ -> "javax.validation.constraints.Min.message");
  }

  public static <A extends Comparable<A>> Validator<A> min(
      A lowerBound, Function<A, String> message) {
    return Validator.of(x -> x != null && x.compareTo(lowerBound) >= 0, message);
  }

  public static <A extends Comparable<A>> Validator<A> range(
      A lowerBound, A upperBound, Function<A, String> message) {
    return Validator.plus(max(upperBound, message), min(lowerBound, message));
  }

  public static <A extends Comparable<A>> Validator<A> range(A lowerBound, A upperBound) {
    return Validator.plus(max(upperBound), min(lowerBound));
  }
}
