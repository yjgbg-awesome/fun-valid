package com.github.yjgbg.fun.valid.support;

import com.github.yjgbg.fun.valid.core.StaticMethodReferenceGetter;
import com.github.yjgbg.fun.valid.core.Validator;
import org.intellij.lang.annotations.Language;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

public interface StandardSupport<A> extends ValidatorSupport<A> {
  default Validator<A> notNull(String messageTemplate) {
    return self().plus(Validator.simple(messageTemplate, Objects::nonNull));
  }
  default Validator<A> isNull(String messageTemplate) {
    return self().plus(Validator.simple(messageTemplate, Objects::isNull));
  }

  default <B> Validator<A> eq(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, B b) {
    return self().and(prop, Validator.simple(messageTemplate, x -> x == b));
  }

  default <B> Validator<A> in(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, Collection<B> values) {
    return self().and(prop, Validator.simple(messageTemplate, values::contains));
  }

  default <B> Validator<A> notIn(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, Collection<B> values) {
    return self().and(prop, Validator.simple(messageTemplate, x -> !values.contains(x)));
  }

  default <B> Validator<A> notNull(StaticMethodReferenceGetter<A, B> prop, String messageTemplate) {
    return self().and(prop, Validator.simple(messageTemplate, Objects::nonNull));
  }
  default <B> Validator<A> isNull(StaticMethodReferenceGetter<A, B> prop, String messageTemplate) {
    return self().and(prop, Validator.simple(messageTemplate, Objects::isNull));
  }

  default <B extends Comparable<B>> Validator<A> lt(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, boolean allowNull, B max) {
    return self().and(prop, messageTemplate, x -> x == null ? allowNull : x.compareTo(max) < 0);
  }

  default <B extends Comparable<B>> Validator<A> le(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, boolean allowNull, B max) {
    return self().and(prop, messageTemplate, x -> x == null ? allowNull : x.compareTo(max) <= 0);
  }

  default <B extends Comparable<B>> Validator<A> gt(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, boolean allowNull, B min) {
    return self().and(prop, messageTemplate, x -> x == null ? allowNull : x.compareTo(min) > 0);
  }

  default <B extends Comparable<B>> Validator<A> ge(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, boolean allowNull, B min) {
    return self().and(prop, messageTemplate, x -> x == null ? allowNull : x.compareTo(min) >= 0);
  }

  default <B extends Comparable<B>> Validator<A> between(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, boolean allowNull, B min, B max) {
    return self().and(prop, messageTemplate, x -> x == null ? allowNull :
        x.compareTo(min) >= 0 && x.compareTo(max) <= 0);
  }

  default <B extends String> Validator<A> regexp(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, boolean allowNull, @Language("regexp") String regexp) {
    return self().and(prop, messageTemplate, x -> x == null ? allowNull : Pattern.matches(regexp, x));
  }

  default <B extends String> Validator<A> length(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, boolean allowNull, int min, int max) {
    return self().and(prop, messageTemplate, x -> x == null ? allowNull : x.length() >= min && x.length() <= max);
  }

  default <B extends Collection<?>> Validator<A> size(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, boolean allowNull, int min, int max) {
    return self().and(prop, messageTemplate, x -> x == null ? allowNull : (x.size() >= min && x.size() <= max));
  }

  default <B extends String> Validator<A> email(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, boolean allowNull) {
    return self().regexp(prop, messageTemplate, allowNull, "\\w+([- +.]\\w)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
  }

  default <B extends BigDecimal> Validator<A> digits(StaticMethodReferenceGetter<A, B> prop, String messageTemplate, int integer, int fraction) {
    return self().and(prop,messageTemplate,it -> Math.abs(it.setScale(0, RoundingMode.DOWN).intValue()) < Math.pow(10,integer -1)
        &&  it.subtract(it.setScale(0, RoundingMode.DOWN)).multiply(BigDecimal.valueOf(Math.pow(10,fraction))).remainder(BigDecimal.ONE).equals(BigDecimal.ZERO));
  }
}
