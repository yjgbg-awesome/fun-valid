package com.github.yjgbg.validation.core;

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
@FunctionalInterface
public interface Validator<A> extends Function<A, Error> {
  ThreadLocal<Boolean> failFast = ThreadLocal.withInitial(() -> false);

  static <A> Validator<A> failFast(boolean failFast) {
    Validator.failFast.set(failFast);
    return obj -> Error.none();
  }

  static <A> Validator<A> of(Predicate<A> predicate, Function<A, String> message) {
    return obj -> predicate.test(obj) ? Error.none() : Error.message(obj, message.apply(obj));
  }

  static <A, B> Validator<A> wrapper(Getter<A, B> prop, Validator<B> validator) {
    return obj -> Error.wrapper(prop.propertyName(), validator.apply(prop.apply(obj)));
  }

  static <A> Validator<Iterable<A>> coll(Validator<A> validator) {
    final var atomicInt = new AtomicInteger(0);
    return obj ->
        StreamSupport.stream(obj.spliterator(), false)
            .map(validator)
            .map(error -> Error.wrapper(Objects.toString(atomicInt.getAndIncrement()), error))
            .reduce(Error.none(), Error::plus);
  }

  static <A> Validator<A> plus(Validator<A> arg1, Validator<A> arg2) {
    return obj -> {
      final var error1 = arg1.apply(obj);
      if (failFast.get() && error1 != Error.none()) return error1;
      return Error.plus(error1, arg2.apply(obj));
    };
  }
}
