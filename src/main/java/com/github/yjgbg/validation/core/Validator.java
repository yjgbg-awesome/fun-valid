package com.github.yjgbg.validation.core;

import io.vavr.Function2;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Validator接口,以及Validator之间的运算
 *
 * @param <A>
 */
@FunctionalInterface
public interface Validator<@Nullable A> extends Function2<@NotNull Boolean, @Nullable A, Errors> {
	/**
	 * 空校验器
	 * 构造一个校验结果恒为空的校验器
	 */
	static <A> Validator<A> none() {
		return (failFast, obj) -> Errors.none();
	}

	/**
	 * 简单校验器
	 * 根据constraint描述的规则，和message描述的错误信息构造一个简单校验器
	 */
	static <A> Validator<A> simple(Function<@Nullable A, @NotNull Boolean> constraint, Function<A, String> message) {
		return (failFast, obj) -> constraint.apply(obj) ? Errors.none() : Errors.simple(obj, message.apply(obj));
	}

	/**
	 * A类复杂校验器
	 * 根据两个目标元素的校验器，构造一个目标元素的校验器
	 */
	static <A> Validator<A> plus(Validator<? super A> validator0, Validator<? super A> validator1) {
		return (failFast, obj) -> {
			final var error0 = validator0.apply(failFast, obj);
			if (failFast && error0 != Errors.none()) return error0;
			return Errors.plus(error0, validator1.apply(failFast, obj));
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
			if (obj == null) return Errors.none();
			final var atomicInt = new AtomicInteger(0);
			final var stream = Stream.ofAll(obj)
					.map(validator.apply(failFast))
					.map(errors -> Errors.transform(Objects.toString(atomicInt.getAndIncrement()), errors));
			return failFast
					? stream.filter(Errors::hasError).getOrElse(Errors.none())
					: stream.fold(Errors.none(), Errors::plus);
		};
	}
}
