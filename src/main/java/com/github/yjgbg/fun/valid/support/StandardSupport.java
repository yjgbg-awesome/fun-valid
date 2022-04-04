package com.github.yjgbg.fun.valid.support;

import com.github.yjgbg.fun.valid.core.Getter;
import com.github.yjgbg.fun.valid.core.Validator;
import org.intellij.lang.annotations.RegExp;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

public interface StandardSupport<A> extends ValidatorSupport<A> {
  default Validator<A> notNull(String messageTemplate) {
    return self().plus(Validator.simple(messageTemplate, Objects::nonNull));
  }

  default <B> Validator<A> eq(Getter<A, B> prop, String messageTemplate, B b) {
    return self().and(prop, Validator.simple(messageTemplate, x -> x == b));
  }

  default <B> Validator<A> in(Getter<A, B> prop, String messageTemplate, Collection<B> values) {
    return self().and(prop, Validator.simple(messageTemplate, values::contains));
  }

  default <B> Validator<A> notIn(Getter<A, B> prop, String messageTemplate, Collection<B> values) {
    return self().and(prop, Validator.simple(messageTemplate, x -> !values.contains(x)));
  }

  default <B> Validator<A> notNull(Getter<A, B> prop, String messageTemplate) {
    return self().and(prop, Validator.simple(messageTemplate, Objects::nonNull));
  }

  default <B extends Comparable<B>> Validator<A> lt(Getter<A, B> prop, String message, boolean allowNull, B max) {
    return self().and(prop, message, x -> x == null ? allowNull : x.compareTo(max) < 0);
  }

  default <B extends Comparable<B>> Validator<A> le(Getter<A, B> prop, String message, boolean allowNull, B max) {
    return self().and(prop, message, x -> x == null ? allowNull : x.compareTo(max) <= 0);
  }

  default <B extends Comparable<B>> Validator<A> gt(Getter<A, B> prop, String message, boolean allowNull, B min) {
    return self().and(prop, message, x -> x == null ? allowNull : x.compareTo(min) > 0);
  }

  default <B extends Comparable<B>> Validator<A> ge(Getter<A, B> prop, String message, boolean allowNull, B min) {
    return self().and(prop, message, x -> x == null ? allowNull : x.compareTo(min) >= 0);
  }

  default <B extends Comparable<B>> Validator<A> between(Getter<A, B> prop, String message, boolean allowNull, B min, B max) {
    return self().and(prop, message, x -> x == null ? allowNull :
        x.compareTo(min) >= 0 && x.compareTo(max) <= 0);
  }

  default <B extends String> Validator<A> regexp(Getter<A, B> prop, String message, boolean allowNull, @RegExp String regexp) {
    return self().and(prop, message, x -> x == null ? allowNull : Pattern.matches(regexp, x));
  }

  default <B extends String> Validator<A> length(Getter<A, B> prop, String message, boolean allowNull, int min, int max) {
    return self().and(prop, message, x -> x == null ? allowNull : x.length() >= min && x.length() <= max);
  }

  default <B extends Collection<?>> Validator<A> size(Getter<A, B> prop, String message, boolean allowNull, int min, int max) {
    return self().and(prop, message, x -> x == null ? allowNull : (x.size() >= min && x.size() <= max));
  }
}
