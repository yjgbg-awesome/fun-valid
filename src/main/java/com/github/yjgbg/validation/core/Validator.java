package com.github.yjgbg.validation.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

/**
 * Validator接口,以及Validator之间的运算
 *
 * @param <A>
 */
@FunctionalInterface
public interface Validator<@Nullable A> extends BiFunction<@NotNull Boolean,@Nullable A,Errors> {
  static <A> Validator<A> none() {
    return (failFast,obj) -> Errors.none();
  }

  static <A> Validator<A> of(Predicate<@Nullable A> predicate, Function<A,String> message) {
    return (failFast, obj) -> predicate.test(obj) ? Errors.none() : Errors.of(obj, message.apply(obj));
  }

  static <A, B> Validator<A> wrapper(Getter<A, @Nullable B> prop, Validator<? super B> validator) {
    return (failFast, obj) ->
        Errors.wrapper(prop.propertyName(), validator.apply(failFast, obj != null ? prop.apply(obj) : null));
  }

  /**
   * 将对元素的校验器，转换为对其集合的校验器
   */
  static <A> Validator<Iterable<A>> iter(Validator<? super A> validator) {
    return (failFast, obj) -> {
    	if (obj==null) return Errors.none();
      final var atomicInt = new AtomicInteger(0);
      final var stream =
          StreamSupport.stream(obj.spliterator(), false)
              .map(x -> validator.apply(failFast, x))
              .map(errors -> Errors.wrapper(Objects.toString(atomicInt.getAndIncrement()), errors));
      return failFast
          ? stream.filter(Errors::hasError).findFirst().orElse(Errors.none())
          : stream.reduce(Errors.none(), Errors::plus);
    };
  }

  /**
   * 校验器相加
   */
  static <A> Validator<A> plus(Validator<? super A> validator0, Validator<? super A> validator1) {
    return (failFast, obj) -> {
      final var error1 = validator0.apply(failFast, obj);
      if (failFast && error1 != Errors.none()) return error1;
      return Errors.plus(error1, validator1.apply(failFast, obj));
    };
  }
}
