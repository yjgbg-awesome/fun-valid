package com.github.yjgbg.validation.core;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

/**
 * Validator接口，静态:Validator的构建，Validator与Getter的wrapper运算,Validator之间的加法
 *
 * @param <A>
 */
public interface Validator<@Nullable A> extends Function<A, Errors> {
  Errors apply(A obj);

  Errors apply(A obj, boolean failFast);

  static <A> SimpleValidator<A> none() {
    return obj -> Errors.none();
  }

  static <A> SimpleValidator<A> of(
      Predicate<@Nullable A> predicate, Function<@Nullable A, String> message) {
    return obj -> predicate.test(obj) ? Errors.none() : Errors.message(obj, message.apply(obj));
  }

  static <A, B> SimpleValidator<A> wrapper(
      Getter<@Nullable A, @Nullable B> prop, Validator<B> validator) {
    return obj ->
        Errors.wrapper(prop.propertyName(), validator.apply(obj != null ? prop.apply(obj) : null));
  }

  /**
   * 将对元素的校验器，转换为对其集合的转换器
   *
   * @param validator
   * @param <A>
   * @return
   */
  static <A> ComposedValidator<Iterable<A>> iter(Validator<A> validator) {
    final var atomicInt = new AtomicInteger(0);
    return (obj, failFast) ->
        failFast
            ? StreamSupport.stream(obj.spliterator(), false)
                .map(validator)
                .filter(Errors::hasError)
                .map(
                    errors -> Errors.wrapper(Objects.toString(atomicInt.getAndIncrement()), errors))
                .findFirst()
                .orElse(Errors.none())
            : StreamSupport.stream(obj.spliterator(), false)
                .map(validator)
                .map(
                    errors -> Errors.wrapper(Objects.toString(atomicInt.getAndIncrement()), errors))
                .reduce(Errors.none(), Errors::plus);
  }

  static <A> ComposedValidator<A> plus(Validator<A> arg1, Validator<A> arg2) {
    return (obj, failFast) -> {
      final var error1 = arg1.apply(obj, failFast);
      if (failFast && error1 != Errors.none()) return error1;
      return Errors.plus(error1, arg2.apply(obj, failFast));
    };
  }

  @FunctionalInterface
  interface SimpleValidator<A> extends Validator<A> {
    @Override
    default Errors apply(A obj, boolean failFast) {
      return apply(obj);
    }
  }

  @FunctionalInterface
  interface ComposedValidator<A> extends Validator<A> {
    @Override
    default Errors apply(A obj) {
      return apply(obj, false);
    }
  }
}
