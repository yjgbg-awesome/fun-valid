package com.github.yjgbg.validation.ext;

import com.github.yjgbg.validation.core.Errors;
import com.github.yjgbg.validation.core.Getter;
import com.github.yjgbg.validation.core.Validator;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 核心扩展类
 */
public class LbkExtValidatorsCore {
	/**
	 * 用message和predicate创建一个新的简单校验器，并与that相加，然后返回
	 */
	public static <A> Validator<A>
	and(Validator<A> that, String message, Predicate<@Nullable A> predicate) {
		return Validator.plus(that, Validator.simple(predicate, x -> message.replaceAll("%s", Objects.toString(x))));
	}

	public static <A, B> Validator<A> and(Validator<A> that, Getter<A, B> prop, Validator<B> another) {
		return Validator.plus(that, Validator.transform(prop, another));
	}

	public static <A, B> Validator<A> and(Validator<A> that, Getter<A, B> prop, String message,
	                                      Predicate<@Nullable B> predicate) {
		return Validator.plus(that, Validator.transform(prop,
				Validator.simple(predicate, x -> message.replaceAll("%s", Objects.toString(x)))));
	}

	public static <A, B> Validator<A>
	andIter(Validator<A> that, Getter<A, Iterable<B>> prop, Validator<? super B> validator) {
		return Validator.plus(that, Validator.transform(prop, Validator.iter(validator)));
	}

	public static <A, B> Validator<A>
	andIter(Validator<A> that, Getter<A, Iterable<B>> prop, String message, Predicate<@Nullable B> predicate) {
		return Validator.plus(that, Validator.transform(prop, Validator.iter(Validator.simple(predicate,
				x -> message.replaceAll("%s", Objects.toString(x))))));
	}

	public static <A> Validator<Iterable<A>> iter(Validator<? super A> that) {
		return Validator.iter(that);
	}

	public static Errors mapMessage(Errors that, Function<String,String> mapper) {
		final var messageErrors = that.getMessages().stream()
				.map(oldMessage -> Errors.simple(that.getRejectValue(),mapper.apply(oldMessage)))
				.reduce(Errors.none(),Errors::plus);
		final var fieldErrors = that.getFieldErrors().entrySet().stream()
				.map(entry -> Errors.transform(entry.getKey(),mapMessage(entry.getValue(),mapper)))
				.reduce(Errors.none(),Errors::plus);
		return Errors.plus(messageErrors,fieldErrors);
	}

	public static Map<String,Set<String>> toMessageMap(Errors errors) {
		var res = toMessageMap0(errors);
		return res.entrySet().stream()
				.collect(Collectors.toMap(x -> x.getKey().isBlank() ? "__self__":x.getKey(), Map.Entry::getValue));
	}

	private static Map<String, Set<String>> toMessageMap0(Errors errors) {
		final var map1 = errors.getMessages().isEmpty()
				? Map.<String,Set<String>>of()
				: Map.of("", errors.getMessages());
		if (errors.getFieldErrors().isEmpty()) return map1;
		final var map2 = errors.getFieldErrors().entrySet().stream()
				.flatMap(entry -> toMessageMap0(entry.getValue()).entrySet().stream()
						.map(x -> new SimpleImmutableEntry<>(
								x.getKey().isBlank() ? entry.getKey() : entry.getKey() + "." + x.getKey(),
								x.getValue())
						))
				.filter(entry -> !entry.getValue().isEmpty())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		final var res = new HashMap<>(map1);
		res.putAll(map2);
		return Map.copyOf(res);
	}
}
