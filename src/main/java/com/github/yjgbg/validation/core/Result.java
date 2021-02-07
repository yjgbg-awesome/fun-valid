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
public class Result {
    private static final Result NONE = new Result(null, Set.of(), Map.of());
    Object rejectValue;
    Set<String> messages;
    Map<String, Result> errors;

    public static Result none() {
        return NONE;
    }

    @Contract(pure = true)
    public static Result message(@Nullable Object rejectValue, @NotNull final String message) {
        return new Result(rejectValue, Set.of(message), Map.of());
    }

    /**
     * 定义了与字符串的wrapper运算
     *
     * @param key
     * @param result
     * @return
     */
    @Contract(pure = true)
    public static Result wrapper(@NotNull final String key, @Nullable final Result result) {
        if (result == null) return none();
        if (result == none()) return none();
        return new Result(null, Collections.emptySet(), Map.of(key, result));
    }

    /**
     * 定义了两个Error的加法
     *
     * @param result1
     * @param result2
     * @return
     */
    @NotNull
    @Contract(pure = true)
    public static Result plus(@Nullable final Result result1, @Nullable final Result result2) {
        if (result1 == null) return plus(none(), result2);
        if (result2 == null) return plus(result1, none());
        if (result1 == none()) return result2;
        if (result2 == none()) return result1;
        final var reject0 = result1.getRejectValue();
        final var reject1 = result2.getRejectValue();
        if (reject0 != reject1)
            throw new IllegalArgumentException(String.format("不同数据对象的校验结果不可合并(%s,%s)", reject0, reject1));
        final var messages = new HashSet<>(result1.getMessages());
        result2.getMessages().stream().filter(x -> !messages.contains(x)).forEach(messages::add);
        final var errors = new HashMap<>(result1.getErrors());
        result2.getErrors().forEach((k, v) -> errors.put(k, plus(errors.get(k), v)));
        return new Result(reject0, messages, errors);
    }

    public boolean hasError() {
        return !Objects.equals(this, none());
    }

    public String getMessage() {
        return String.join(";", messages);
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
