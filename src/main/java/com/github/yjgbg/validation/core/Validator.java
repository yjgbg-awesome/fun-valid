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
@FunctionalInterface
public interface Validator<@Nullable A> extends Function<A, Result> {
    static <A> Validator<A> none() {
        return obj -> Result.none();
    }

    static <A> Validator<A> of(
            Predicate<@Nullable A> predicate, Function<@Nullable A, String> message) {
        return obj -> predicate.test(obj) ? Result.none() : Result.message(obj, message.apply(obj));
    }

    static <A, B> Validator<A> wrapper(
            Getter<@Nullable A, @Nullable B> prop, Validator<B> validator) {
        return obj ->
                Result.wrapper(prop.propertyName(), validator.apply(obj != null ? prop.apply(obj) : null));
    }

    static <A> Validator<Iterable<A>> coll(Validator<A> validator) {
        final var atomicInt = new AtomicInteger(0);
        return obj ->
                obj == null
                        ? Result.none()
                        : StreamSupport.stream(obj.spliterator(), false)
                        .map(validator)
                        .map(result -> Result.wrapper(Objects.toString(atomicInt.getAndIncrement()), result))
                        .reduce(Result.none(), Result::plus);
  }

  static <A> Validator<A> plus(Validator<A> arg1, Validator<A> arg2) {
      return new Validator<>() {
          @Override
          public Result apply(A obj) {
              return apply(obj, false);
          }

          @Override
          public Result apply(A obj, boolean failFast) {
              final var error1 = arg1.apply(obj, failFast);
              if (failFast && error1 != Result.none()) return error1;
              return Result.plus(error1, arg2.apply(obj, failFast));
          }
      };
  }

    @Override
    Result apply(A obj);

    default Result apply(A obj, boolean failFast) {
        return apply(obj);
    }
}
