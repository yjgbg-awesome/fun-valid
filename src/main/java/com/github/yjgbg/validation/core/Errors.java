package com.github.yjgbg.validation.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Error的构建，以及相关运算，包括Error的加法，以及Error与string的运算
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Errors {
    private static final Errors NONE = new Errors(null, Set.of(), Map.of());
    Object rejectValue;
    Set<String> messages;
    Map<String, Errors> errors;

    public static Errors none() {
        return NONE;
    }

    @Contract(pure = true)
    public static Errors message(@Nullable Object rejectValue, @NotNull final String message) {
        return new Errors(rejectValue, Set.of(message), Map.of());
    }

    /**
     * 定义了与字符串的wrapper运算
     *
     * @param key
     * @param errors
     * @return
     */
    @Contract(pure = true)
    public static Errors wrapper(@NotNull final String key, @Nullable final Errors errors) {
        if (errors == null) return none();
        if (errors == none()) return none();
        return new Errors(null, Collections.emptySet(), Map.of(key, errors));
    }

    /**
     * 定义了两个Error的加法
     *
     * @param errors1
     * @param errors2
     * @return
     */
    @NotNull
    @Contract(pure = true)
    public static Errors plus(@Nullable final Errors errors1, @Nullable final Errors errors2) {
        if (errors1 == null) return plus(none(), errors2);
        if (errors2 == null) return plus(errors1, none());
        if (errors1 == none()) return errors2;
        if (errors2 == none()) return errors1;
        final var reject0 = errors1.getRejectValue();
        final var reject1 = errors2.getRejectValue();
        if (reject0 != reject1)
            throw new IllegalArgumentException(String.format("不同数据对象的校验结果不可相加(%s,%s)", reject0, reject1));
        final var messages = new HashSet<>(errors1.getMessages());
        errors2.getMessages().stream().filter(x -> !messages.contains(x)).forEach(messages::add);
        final var errors = new HashMap<>(errors1.getErrors());
        errors2.getErrors().forEach((k, v) -> errors.put(k, plus(errors.get(k), v)));
        return new Errors(reject0, messages, errors);
    }

    public boolean hasError() {
        return !Objects.equals(this, none());
    }

    @Override
    public String toString() {
        if (rejectValue == null && messages.isEmpty()) return errors.toString();
        if (errors.isEmpty())
            return String.format("{'rejectValue':%s,'messages':%s}", rejectValue, messages);
        return String.format(
                "{'rejectValue':%s,'messages':%s,'errors':%s}", rejectValue, messages, errors);
    }
}
