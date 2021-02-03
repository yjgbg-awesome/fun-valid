package com.github.yjgbg.validation;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 可视为Error生成器 Validator接口，以及Validator的构建，Validator与Getter的wrapper运算,Validator之间的加法
 *
 * @param <A>
 */
@FunctionalInterface
public interface Validator<A> extends Function<A, Error> {
  ThreadLocal<Boolean> failFast = ThreadLocal.withInitial(() -> false);

  static <A> Validator<A> none(boolean failFast) {
    Validator.failFast.set(failFast);
    return obj -> Error.none();
  }

  static <A> Validator<A> none() {
    Validator.failFast.set(false);
    return obj -> Error.none();
  }

  static <A> Validator<A> of(Predicate<A> predicate, String message) {
    return obj -> predicate.test(obj) ? Error.none() : Error.message(message);
  }

  static <A> Validator<A> of(Predicate<A> predicate, Function<A, String> message) {
    return obj -> predicate.test(obj) ? Error.none() : Error.message(message.apply(obj));
  }

  static <A, B> Validator<A> wrapper(Getter<A, B> prop, Validator<B> validator) {
    return obj -> Error.wrapper(prop.propertyName(), validator.apply(prop.apply(obj)));
  }

  static <A> Validator<Collection<A>> coll(Validator<A> that) {
    var atomicInt = new AtomicInteger(0);
    return obj ->
        obj.stream()
            .map(that)
            .map(error -> Error.wrapper(Objects.toString(atomicInt.getAndIncrement()), error))
            .reduce(Error.none(), Error::plus);
  }

  static <A> Validator<A> plus(Validator<A> arg1, Validator<A> arg2) {
    return obj -> {
      var error1 = arg1.apply(obj);
      if (failFast.get() && error1 == Error.none()) return error1;
      return Error.plus(error1, arg2.apply(obj));
    };
  }
}
