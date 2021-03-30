package com.github.yjgbg.validation.ext;

import com.github.yjgbg.validation.core.Errors;
import com.github.yjgbg.validation.core.Getter;
import com.github.yjgbg.validation.core.Validator;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 核心扩展类
 */
public class LbkExtValidatorsCore {
	/**
	 * 用message和predicate创建一个新的简单校验器，并与that相加，然后返回
	 */
	public static <A> Validator<A>
	and(Validator<A> that, String message, Predicate<@Nullable A> constraint) {
		return Validator.plus(that, Validator.simple(constraint, x -> message.replaceAll("%s", Objects.toString(x))));
	}

	public static <A, B> Validator<A> and(Validator<A> that, Getter<A, B> prop, Validator<B> another) {
		return Validator.plus(that, Validator.transform(prop, another));
	}

	public static <A, B> Validator<A> and(Validator<A> that, Getter<A, B> prop, String message,
	                                      Predicate<@Nullable B> constraint) {
		return Validator.plus(that, Validator.transform(prop,
				Validator.simple(constraint, x -> message.replaceAll("%s", Objects.toString(x)))));
	}

	public static <A, B> Validator<A> and(Validator<A> that,Predicate<A> condition,Getter<A, B> prop, String message,
																				Predicate<@Nullable B> constraint) {
		return Validator.plus(that, Validator.transform(prop,
				Validator.simple(constraint, x -> message.replaceAll("%s", Objects.toString(x)))));
	}

	public static <A, B> Validator<A>
	andIter(Validator<A> that, Getter<A, Iterable<B>> prop, Validator<? super B> validator) {
		return Validator.plus(that, Validator.transform(prop, Validator.iter(validator)));
	}

	public static <A, B> Validator<A>
	andIter(Validator<A> that, Getter<A, Iterable<B>> prop, String message, Predicate<@Nullable B> constraint) {
		return Validator.plus(that, Validator.transform(prop, Validator.iter(Validator.simple(constraint,
				x -> message.replaceAll("%s", Objects.toString(x))))));
	}

	public static <A> Validator<Iterable<A>> iter(Validator<? super A> that) {
		return Validator.iter(that);
	}

	public static Errors mapMessage(Errors that, Function<String,String> mapper) {
		final var messageErrors = that.getMessages()
				.map(oldMessage -> Errors.simple(that.getRejectValue(),mapper.apply(oldMessage)))
				.foldLeft(Errors.none(),Errors::plus);
		return that.getFieldErrors()
				.mapValues(errors -> mapMessage(errors,mapper))
				.map(entry -> Errors.transform(entry._1(),entry._2()))
				.foldLeft(messageErrors,Errors::plus);
	}

	private static final String SELF = "_self";
	public static HashMap<String, HashSet<String>> toMessageMap(Errors errors) {
		final var map1 = errors.getMessages().isEmpty()
				? HashMap.<String,HashSet<String>>empty()
				: HashMap.of(SELF, HashSet.ofAll(errors.getMessages()));
		if (errors.getFieldErrors().isEmpty()) return map1;
		final var map2 = errors.getFieldErrors()
				.flatMap((k,v) -> toMessageMap(v).mapKeys(x -> Objects.equals(x, SELF) ? k:k+"."+x));
		return map1.merge(map2,HashSet::union);
	}
}
