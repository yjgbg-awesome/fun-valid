package com.github.yjgbg.validation;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public class ValidatorExt {
  public static <A> Validator<A> and(Validator<A> that, Validator<A> another) {
    return Validator.plus(that, another);
  }

  public static <A> Validator<A> and(Validator<A> that, Predicate<A> predicate, String message) {
    return Validator.plus(that, Validator.of(predicate, message));
  }

  public static <A> Validator<A> and(
      Validator<A> that, Predicate<A> predicate, Function<A, String> message) {
    return Validator.plus(that, Validator.of(predicate, message));
  }

  public static <A, B> Validator<A> and(
      Validator<A> that, Getter<A, B> prop, Validator<B> another) {
    return Validator.plus(that, Validator.wrapper(prop, another));
  }

  public static <A, B> Validator<A> and(
      Validator<A> that, Getter<A, B> prop, Predicate<B> predicate, String message) {
    return Validator.plus(that, Validator.wrapper(prop, Validator.of(predicate, message)));
  }

  public static <A, B> Validator<A> and(
      Validator<A> that, Getter<A, B> prop, Predicate<B> predicate, Function<B, String> message) {
    return Validator.plus(that, Validator.wrapper(prop, Validator.of(predicate, message)));
  }
    public static <A, B> Validator<A> andColl(
            Validator<A> that, Getter<A, Collection<B>> prop, Predicate<B> predicate, Function<B, String> message) {
        return Validator.plus(that, Validator.wrapper(prop, Validator.coll(Validator.of(predicate, message))));
    }
    public static <A, B> Validator<A> andColl(
            Validator<A> that, Getter<A, Collection<B>> prop, Validator<B> validator) {
        return Validator.plus(that, Validator.wrapper(prop, Validator.coll(validator)));
    }
}
