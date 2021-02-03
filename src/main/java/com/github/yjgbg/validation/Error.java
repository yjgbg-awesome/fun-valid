package com.github.yjgbg.validation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Error的构建，以及相关运算，包括Error的加法，以及Error与string的运算
 */
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Error {
  List<String> codes;
  Map<String, Error> errors;

  private static final Error NONE = new Error(List.of(), Map.of());
  public static Error none() {
    return NONE;
  }
  public static Error code(@NotNull String code) {
    return new Error(List.of(code),new HashMap<>());
  }

  /**
   * 定义了与字符串的wrapper运算
   * @param key
   * @param error
   * @return
   */
  public static Error wrapper(@NotNull String key, @NotNull Error error) {
    if (error==none()) return none();
    return new Error(List.of(), Map.of(key, error));
  }

  /**
   * 定义了两个Error的加法
   * @param error1
   * @param error2
   * @return
   */
  @NotNull
  public static Error plus(@Nullable Error error1, @Nullable Error error2) {
    if (error1==null||error1==none()) {
      if (error2==null||error2==none()) return none();
      return error2;
    }
    if (error2==null||error2==none()) return error1;
    final var codes1 = error1.codes;
    final var codes2 = error2.codes;
    final var codes = new ArrayList<String>();
    codes.addAll(codes1);
    codes.addAll(codes2);
    final var errors1 = error1.errors;
    final var errors2 = error2.errors;
    final var errors = new HashMap<>(errors1);
    errors2.forEach((k, v) -> errors.put(k, plus(errors.get(k), v)));
    return new Error(codes, errors);
  }
}
