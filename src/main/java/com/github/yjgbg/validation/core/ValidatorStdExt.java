package com.github.yjgbg.validation.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ValidatorStdExt {
	public static <A> Errors failFastApply(Validator<A> that, A obj) {
		return that.apply(true, obj);
	}

	public static <A> Errors noFailFastApply(Validator<A> that, A obj) {
		return that.apply(false, obj);
	}

	public static <A> Validator<A> and(Validator<A> that, Validator<? super A> another) {
		return Validator.plus(that, another);
	}

	public static <A> Validator<A>
	and(Validator<A> that, Function<@Nullable A, String> message, Predicate<@Nullable A> predicate) {
		return Validator.plus(that, Validator.of(predicate, message));
	}


	public static <A, B> Validator<A> and(Validator<A> that, Getter<A, B> prop, Validator<B> another) {
		return Validator.plus(that, Validator.wrapper(prop, another));
	}

	public static <A, B> Validator<A>
	and(Validator<A> that, Getter<A, B> prop, Function<@Nullable B, String> message,
	    Predicate<@Nullable B> predicate) {
		return Validator.plus(that, Validator.wrapper(prop, Validator.of(predicate, message)));
	}

	public static <A, B> Validator<A>
	andIter(Validator<A> that, Getter<A, Iterable<B>> prop, Validator<? super B> validator) {
		return Validator.plus(that, Validator.wrapper(prop, Validator.iter(validator)));
	}

	public static <A, B> Validator<A>
	andIter(Validator<A> that, Getter<A, Iterable<B>> prop,
	        Function<@Nullable B, String> message, Predicate<@Nullable B> predicate) {
		return Validator.plus(that, Validator.wrapper(prop, Validator.iter(Validator.of(predicate, message))));
	}

	public static <A> Function<@Nullable A, String> fmt(@NotNull String message) {
		return x -> String.format(message, x, x, x, x, x, x, x, x);
	}

	public static <A> Validator<Iterable<A>> iter(Validator<? super A> that) {
		return Validator.iter(that);
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
