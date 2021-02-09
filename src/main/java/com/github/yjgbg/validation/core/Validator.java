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
public interface Validator<@Nullable A> extends Function<A, Errors> {
    static <A> Validator<A> none() {
        return obj -> Errors.none();
    }

    static <A> Validator<A> of(
            Predicate<@Nullable A> predicate, Function<@Nullable A, String> message) {
        return obj -> predicate.test(obj) ? Errors.none() : Errors.message(obj, message.apply(obj));
    }

    static <A, B> Validator<A> wrapper(
            Getter<@Nullable A, @Nullable B> prop, Validator<B> validator) {
        return obj ->
                Errors.wrapper(prop.propertyName(), validator.apply(obj != null ? prop.apply(obj) : null));
    }

    static <A> Validator<Iterable<A>> iter(Validator<A> validator) {
        final var atomicInt = new AtomicInteger(0);
        return (obj) ->
                StreamSupport.stream(obj.spliterator(), false)
                        .map(validator)
                        .map(errors -> Errors.wrapper(Objects.toString(atomicInt.getAndIncrement()), errors))
                        .filter(Errors::hasError)
                        .findAny()
                        .orElse(Errors.none());
    }

    static <A> Validator<A> plus(Validator<A> arg1, Validator<A> arg2) {
        return new Validator<>() {
            @Override
            public Errors apply(A a) {
                return apply(a, false);
            }

            @Override
            public Errors apply(A obj, boolean failFast) {
                final var error1 = arg1.apply(obj, failFast);
                if (failFast && error1 != Errors.none()) return error1;
                return Errors.plus(error1, arg2.apply(obj, failFast));
            }
        };
    }

    default Errors apply(A obj, boolean failFast) {
        return apply(obj);
    }
}
