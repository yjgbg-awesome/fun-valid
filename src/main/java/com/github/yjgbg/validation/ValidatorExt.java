package com.github.yjgbg.validation;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@UtilityClass
public class ValidatorExt {
  public static <A, B> Validator<A> and(
      Validator<A> that, Getter<A, B> getter, Validator<B> validator) {
    return (key, obj, bindingResult) -> {
      that.valid(key, obj, bindingResult);
      val prop = getter.propertyName();
      val newKey = StringUtils.hasText(prop) ? key + "." + prop : key;
      validator.valid(newKey, getter.apply(obj), bindingResult);
    };
  }

  public static <A> Validator<A> and(Validator<A> that, Validator<A> another) {
    return and(that, Getter.self(), another);
  }

  public <A, B> Validator<A> and(
      Validator<A> that, Getter<A, B> getter, Predicate<B> predicate, String message) {
    return and(that, getter, (key,obj, bindingResult) -> {
      if (!predicate.test(obj)) bindingResult.reject(key,message);
    });
  }

  public <A> Validator<A> and(
      Validator<A> that,  Predicate<A> predicate, String message) {
    return and(that, Getter.self(), (key,obj, bindingResult) -> {
      if (!predicate.test(obj)) bindingResult.reject(key,message);
    });
  }

  public static <A, B> Validator<A> andIterable(
      Validator<A> that, Getter<A, Iterable<B>> getter, Validator<B> validator) {
    return and(that, getter, (key, obj, bindingResult) -> {
      if (obj == null) return;
      val i = new AtomicInteger(0);
      obj.forEach(e -> validator.valid(key + "[" + i + "]", e, bindingResult));
    });
  }

  public <A, B> Validator<A> andIterable(
      Validator<A> that, Getter<A, Iterable<B>> getter, Predicate<B> predicate, String message) {
    return andIterable(that, getter, (key,obj, bindingResult) -> {
      if (!predicate.test(obj)) bindingResult.reject(key,message);
    });
  }
}
