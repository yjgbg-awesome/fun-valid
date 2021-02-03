package com.github.yjgbg.validation.core;

import com.github.yjgbg.validation.core.Getter;
import com.github.yjgbg.validation.core.Validator;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.yjgbg.validation.core.Validator.*;

public class BaseValidatorExt {
  public static <A> Validator<A> and(Validator<A> that, Validator<A> another) {
    return plus(that, another);
  }

  public static <A> Validator<A> and(
      Validator<A> that, Predicate<A> predicate, Function<A, String> message) {
    return plus(that, of(predicate, message));
  }

  public static <A, B> Validator<A> and(
          Validator<A> that, Getter<A, B> prop, Validator<B> another) {
    return plus(that, wrapper(prop, another));
  }

  public static <A, B> Validator<A> and(
      Validator<A> that, Getter<A, B> prop, Predicate<B> predicate, Function<B, String> message) {
    return plus(that, wrapper(prop, of(predicate, message)));
  }

  public static <A, B> Validator<A> andColl(
      Validator<A> that, Getter<A, Iterable<B>> prop, Validator<B> validator) {
    return plus(that, wrapper(prop, coll(validator)));
  }

  public static <A, B> Validator<A> andColl(
      Validator<A> that,
      Getter<A, Iterable<B>> prop,
      Predicate<B> predicate,
      Function<B, String> message) {
    return plus(that, wrapper(prop, coll(of(predicate, message))));
  }
}
