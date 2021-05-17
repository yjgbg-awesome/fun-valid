package com.github.yjgbg.fun.valid.ext;

import com.github.yjgbg.fun.valid.core.Errors;
import com.github.yjgbg.fun.valid.core.Getter;
import com.github.yjgbg.fun.valid.core.Validator;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

import static io.vavr.API.Map;

/**
 * 核心扩展类
 */
public class LbkExtValidatorsCore {
	/**
	 * 用message和predicate创建一个新的简单校验器，并与that相加，然后返回
	 */

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

	public static Errors mapMessage(Errors that, Function<@NotNull String, @NotNull String> mapper) {
		final var messageErrors = that.getMessages()
				.map(oldMessage -> Errors.simple(that.getRejectValue(), mapper.apply(oldMessage)))
				.fold(Errors.none(), Errors::plus);
		return that.getFieldErrors()
				.mapValues(errors -> mapMessage(errors, mapper))
				.map(entry -> Errors.transform(entry._1(), entry._2()))
				.fold(messageErrors, Errors::plus);
	}

	private static final String SELF = "_self";
	private static final String SEPARATOR = ".";

	public static Map<String, Set<String>> toMessageMap(Errors errors) {
		final Map<String, Set<String>> messages = errors.getMessages().isEmpty()
				? Map() : Map(SELF, errors.getMessages());
		final var fieldErrors = errors.getFieldErrors()
				.flatMap((field, error) -> toMessageMap(error)
						.mapKeys(subField -> Objects.equals(subField, SELF) ? field : field + SEPARATOR + subField)
				);
		return messages.merge(fieldErrors, Set::union);
	}
}
