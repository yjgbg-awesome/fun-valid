package com.github.yjgbg.validation.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/** Error的构建，以及相关运算，包括Error的加法，以及Error与string的运算 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Error {
  Object rejectValue;
  Set<String> messages;
  Map<String, Error> errors;

  private static final Error NONE = new Error(null,Set.of(), Map.of());

  public static Error none() {
    return NONE;
  }

  @Contract(pure = true)
  public static Error message(@Nullable Object rejectValue, @NotNull final String message) {
      return new Error(rejectValue, Set.of(message), Map.of());
  }

  /**
   * 定义了与字符串的wrapper运算
   *
   * @param key
   * @param error
   * @return
   */
  @Contract(pure = true)
  public static Error wrapper(@NotNull final String key, @Nullable final Error error) {
    if (error == null) return none();
    if (error == none()) return none();
    return new Error(null,Collections.emptySet(), Map.of(key, error));
  }

  /**
   * 定义了两个Error的加法
   *
   * @param error1
   * @param error2
   * @return
   */
  @NotNull
  @Contract(pure = true)
  public static Error plus(@Nullable final Error error1, @Nullable final Error error2) {
      if (error1 == null) return plus(none(), error2);
      if (error2 == null) return plus(error1, none());
      if (error1 == none()) return error2;
      if (error2 == none()) return error1;
      final var reject0 = error1.getRejectValue();
      final var reject1 = error2.getRejectValue();
      if (reject0 != reject1) throw new IllegalArgumentException(
              String.format("不同数据对象的校验结果不可合并(%s,%s)", reject0, reject1));
      final var messages = new HashSet<>(error1.getMessages());
      error2.getMessages().stream().filter(x -> !messages.contains(x)).forEach(messages::add);
      final var errors = new HashMap<>(error1.getErrors());
      error2.getErrors().forEach((k, v) -> errors.put(k, plus(errors.get(k), v)));
      return new Error(reject0, messages, errors);
  }

    @Override
    public String toString() {
        if (rejectValue == null && messages.isEmpty()) return errors.toString();
        if (errors.isEmpty()) return String.format("{'rejectValue':%s,'messages':%s}", rejectValue, messages);
        return String.format("{'rejectValue':%s,'messages':%s,'errors':%s}", rejectValue, messages, errors);
    }
}
