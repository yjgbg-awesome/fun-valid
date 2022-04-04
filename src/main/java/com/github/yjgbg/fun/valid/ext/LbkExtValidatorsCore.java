package com.github.yjgbg.fun.valid.ext;

import com.github.yjgbg.fun.valid.core.Getter;
import com.github.yjgbg.fun.valid.core.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * 核心扩展类(修改下注释，看下文档效果)
 */
public class LbkExtValidatorsCore {
	public static <A> Validator<A>
	and(Validator<A> that, Function<A, Validator<? super A>> another) {
		return Validator.plus(that, Validator.from(another));
	}

	public static <A> Validator<A>
	and(Validator<A> that, @NotNull String message, Function<@Nullable A, @NotNull Boolean> constraint) {
		return Validator.plus(that, Validator.simple(constraint, x -> message.replaceAll("%s", Objects.toString(x))));
	}

	public static <A, B> Validator<A> and(Validator<A> that, Getter<A, B> prop, Validator<B> another) {
		return Validator.plus(that, Validator.transform(prop, another));
	}

	public static <A, B> Validator<A> and(Validator<A> that, Getter<A, B> prop, @NotNull String message,
																				Function<@Nullable B, @NotNull Boolean> constraint) {
		return Validator.plus(that, Validator.transform(prop,
				Validator.simple(constraint, x -> message.replaceAll("%s", Objects.toString(x)))));
	}

	public static <A> Validator<Iterable<A>> andIter(Validator<Iterable<A>> that, Validator<? super A> validator) {
		return Validator.plus(that, Validator.iter(validator));
	}

	public static <A, B> Validator<A>
	andIter(Validator<A> that, Getter<A, Iterable<B>> prop, Validator<? super B> validator) {
		return Validator.plus(that, Validator.transform(prop, Validator.iter(validator)));
	}

	public static <A, B> Validator<A> andIter(Validator<A> that, Getter<A, Iterable<B>> prop, @NotNull String message,
																						Function<@Nullable B, @NotNull Boolean> constraint) {
		return Validator.plus(that, Validator.transform(prop, Validator.iter(Validator.simple(constraint,
				x -> message.replaceAll("%s", Objects.toString(x))))));
	}

	public static <A> Validator<Iterable<A>> iter(Validator<? super A> that) {
		return Validator.iter(that);
	}
}
