package com.github.yjgbg.fun.valid.support;

import com.github.yjgbg.fun.valid.core.Getter;
import com.github.yjgbg.fun.valid.core.Validator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public interface CoreSupport<A> extends ValidatorSupport<A> {

  default Validator<A> plus(String messageTemplate, Function<A,Boolean> constraint) {
    return self().plus(Validator.simple(messageTemplate,constraint));
  }
  default  <B> Validator<A> and(Getter<A,B> prop,Validator<B> validator) {
    return self().plus(validator.transform(prop));
  }

  default <B> Validator<A> and(Getter<A,B> prop,String messageTemplate,Function<B,Boolean> constraint) {
    return self().and(prop,Validator.simple(messageTemplate,constraint));
  }
  /**
   * C类复杂校验器
   * 根据元素的校验器，构造一个元素集合的校验器
   * @return A元素集合的校验器
   */
  default <CC extends Iterable<A>> Validator<CC> iterable() {
    return Validator.func(iterable -> {
      if (iterable == null)return Validator.none();
      final var atomicInt = new AtomicInteger(0);
      return StreamSupport.stream(iterable.spliterator(),false)
          .map(a -> self().transform(Getter.<Iterable<A>,A>of(String.valueOf(atomicInt.getAndIncrement()), __ -> a)))
          .reduce(Validator.none(),Validator::plus);
    });
  }
  default <B,CC extends Iterable<B>> Validator<A> andIterable(Getter<A,CC> prop,Validator<B> validator) {
    return self().and(prop,validator.iterable());
  }

  default <B,CC extends Iterable<B>> Validator<A> andIterable(Getter<A,CC> prop,String messageTemplate,Function<B,Boolean> constraint) {
    return self().and(prop,Validator.simple(messageTemplate,constraint).iterable());
  }
}
