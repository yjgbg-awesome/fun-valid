package com.github.yjgbg.validation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JSR380Validators {
  public Validator<Boolean> assertTrue() {
    return Validator.of(x -> x, "javax.validation.constraints.AssertTrue.message");
  }

  public Validator<Boolean> assertTrue(String message) {
    return Validator.of(x -> x, message);
  }

  public Validator<Boolean> assertFalse() {
    return Validator.of(x -> !x, "javax.validation.constraints.AssertFalse.message");
  }

  public Validator<Boolean> assertFalse(String message) {
    return Validator.of(x -> !x, message);
  }

  public <A> Validator<Comparable<A>> max(A upperBound) {
    return Validator.of(
        x -> x.compareTo(upperBound) <= 0, "javax.validation.constraints.Max.message");
  }

  public <A> Validator<Comparable<A>> max(A upperBound, String message) {
    return Validator.of(x -> x.compareTo(upperBound) <= 0, message);
  }

  public <A> Validator<Comparable<A>> min(A lowerBound) {
    return Validator.of(
        x -> x.compareTo(lowerBound) >= 0, "javax.validation.constraints.Min.message");
  }

  public <A> Validator<Comparable<A>> min(A lowerBound, String message) {
    return Validator.of(x -> x.compareTo(lowerBound) >= 0, message);
  }

  public <A> Validator<Comparable<A>> between(A lowerBound, A upperBound, String message) {
    return Validator.plus(max(upperBound, message), min(lowerBound, message));
  }
}
