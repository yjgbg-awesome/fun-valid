package com.github.yjgbg.validation.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.yjgbg.validation.core.Validator.*;

public class BaseValidatorExt {
    public static <A> Validator<A> and(Validator<A> that, Validator<A> another) {
        return plus(that, another);
    }

    public static <A> Validator<A> and(
            Validator<A> that, Predicate<@Nullable A> predicate, Function<@Nullable A, String> message) {
        return plus(that, of(predicate, message));
    }

    public static <A, B> Validator<A> and(
            Validator<A> that, Getter<A, B> prop, Validator<B> another) {
        return plus(that, wrapper(prop, another));
    }

    public static <A, B> Validator<A> and(
            Validator<A> that,
            Getter<A, B> prop,
            Predicate<@Nullable B> predicate,
            Function<@Nullable B, String> message) {
        return plus(that, wrapper(prop, of(predicate, message)));
    }

    public static <A, B> Validator<A> andIter(
            Validator<A> that, Getter<A, Iterable<B>> prop, Validator<B> validator) {
        return plus(that, wrapper(prop, iter(validator)));
    }

    public static <A, B> Validator<A> andIter(
            Validator<A> that,
            Getter<A, Iterable<B>> prop,
            Predicate<@Nullable B> predicate,
            Function<@Nullable B, String> message) {
        return plus(that, wrapper(prop, iter(of(predicate, message))));
    }

    public static <A> Function<@Nullable A, String> msg(@NotNull String message) {
        return x -> message.replaceAll("%s", Objects.toString(x));
    }

    public static <A> Validator<Iterable<A>> iter(Validator<A> that) {
        return Validator.iter(that);
    }
}
