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
 * Validator接口，静态:Validator的构建，Validator与Getter的wrapper运算,Validator之间的加法
 *
 * @param <A>
 */
@FunctionalInterface
public interface Validator<@Nullable A> extends BiFunction<@NotNull Boolean,A,Errors> {
  static <A> Validator<A> none() {
    return (failFast,obj) -> Errors.none();
  }

  static <A> Validator<A> of(
      Predicate<@Nullable A> predicate, Function<@Nullable A, String> message) {
    return (failFast,obj) -> predicate.test(obj) ? Errors.none() : Errors.message(obj, message.apply(obj));
  }

  static <A, B> Validator<A> wrapper(
      Getter<@Nullable A, @Nullable B> prop, Validator<B> validator) {
    return (failFast,obj) ->
            Errors.wrapper(
                prop.propertyName(),
                validator.apply(failFast, obj != null ? prop.apply(obj) : null));
  }

  /**
   * 将对元素的校验器，转换为对其集合的转换器
   *
   * @param validator
   * @param <A>
   * @return
   */
  static <A> Validator<Iterable<A>> iter(Validator<A> validator) {
    return (failFast,obj) -> {
          final var atomicInt = new AtomicInteger(0);
          final var stream =
              StreamSupport.stream(obj.spliterator(), false)
                  .map(x -> validator.apply(failFast, x))
                  .filter(Errors::hasError)
                  .map(
                      errors ->
                          Errors.wrapper(Objects.toString(atomicInt.getAndIncrement()), errors));
          return failFast
              ? stream.findFirst().orElse(Errors.none())
              : stream.reduce(Errors.none(), Errors::plus);
        };
  }

  static <A> Validator<A> plus(Validator<A> arg1, Validator<A> arg2) {
    return (failFast,obj) -> {
          final var error1 = arg1.apply(failFast, obj);
          if (failFast && error1 != Errors.none()) return error1;
          return Errors.plus(error1, arg2.apply(failFast, obj));
        };
  }
}
