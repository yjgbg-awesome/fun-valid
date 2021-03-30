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
	/**
	 * 空校验器
	 * 构造一个校验结果恒为空的校验器
	 */
  static <A> Validator<A> none() {
    return (failFast,obj) -> Errors.none();
  }

	/**
	 * 简单校验器
	 * 根据constraint描述的规则，和message描述的错误信息构造一个简单校验器
	 */
  static <A> Validator<A> simple(Predicate<@Nullable A> constraint, Function<A,String> message) {
    return (failFast, obj) -> constraint.test(obj) ? Errors.none() : Errors.simple(obj, message.apply(obj));
  }

	/**
	 * A类复杂校验器
	 * 根据两个目标元素的校验器，构造一个目标元素的校验器
	 */
	static <A> Validator<A> plus(Validator<? super A> validator0, Validator<? super A> validator1) {
		return (failFast, obj) -> {
			final var error1 = validator0.apply(failFast, obj);
			if (failFast && error1 != Errors.none()) return error1;
			return Errors.plus(error1, validator1.apply(failFast, obj));
		};
	}

	/**
	 * B类复杂校验器
	 * 根据field类型的校验器和prop，构造一个目标类型校验器
	 */
  static <A, B> Validator<A> transform(Getter<A, @Nullable B> prop, Validator<? super B> validator) {
    return (failFast, obj) ->
        Errors.transform(prop.propertyName(), validator.apply(failFast, obj != null ? prop.apply(obj) : null));
  }

  /**
	 * C类复杂校验器
   * 根据元素的校验器，构造一个元素集合的校验器
   */
  static <A> Validator<Iterable<A>> iter(Validator<? super A> validator) {
    return (failFast, obj) -> {
    	if (obj==null) return Errors.none();
      final var atomicInt = new AtomicInteger(0);
      final var stream =
          StreamSupport.stream(obj.spliterator(), false)
              .map(x -> validator.apply(failFast, x))
              .map(errors -> Errors.transform(Objects.toString(atomicInt.getAndIncrement()), errors));
      return failFast
          ? stream.filter(Errors::hasError).findFirst().orElse(Errors.none())
          : stream.reduce(Errors.none(), Errors::plus);
    };
  }
}
