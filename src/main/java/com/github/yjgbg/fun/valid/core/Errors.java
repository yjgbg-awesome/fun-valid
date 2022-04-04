package com.github.yjgbg.fun.valid.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
	Map<String, Errors> fieldErrors;

	/**
	 * @return 空错误，表示没有错
	 */
	public static Errors none() {
		return NONE;
	}

	/**
	 * 简单错误
	 * 由rejectValue和message组成的简单错误
	 * @param rejectValue 不符合constraint的值
	 * @param message 错误信息
	 * @return 由rejectValue和message组成的简单错误
	 */
	@Contract(pure = true)
	public static Errors simple(@Nullable final Object rejectValue, @NotNull final String message) {
		if (rejectValue == null && message.isBlank()) return none();
		return new Errors(rejectValue, Set.of(message), Map.of());
	}

	/**
	 * A类复杂错误
	 * 由两个错误相加而成，对应字段相加
	 * @param errors0 错误0
	 * @param errors1 错误1
	 * @return 由两个错误相加而成，对应字段相加
	 */
	@NotNull
	@Contract(pure = true)
	public static Errors plus(@Nullable final Errors errors0, @Nullable final Errors errors1) {
		if (errors0 == null) return plus(none(), errors1);
		if (errors1 == null) return plus(errors0, none());
		if (errors0 == none()) return errors1;
		if (errors1 == none()) return errors0;
		final var reject0 = errors0.getRejectValue();
		final var reject1 = errors1.getRejectValue();
		if (reject0 != null && reject1 != null && reject0 != reject1)
			throw new IllegalArgumentException(String.format("不同数据对象的校验结果不可相加(%s,%s)", reject0, reject1));
		// 并集
		final var messages = new HashSet<String>();
		messages.addAll(errors0.messages);
		messages.addAll(errors1.messages);
		final var fieldErrors = new HashMap<>(errors0.fieldErrors);
		errors1.fieldErrors.forEach((k,v) -> fieldErrors.merge(k,v,Errors::plus));
		return new Errors(reject0 != null ? reject0 : reject1, messages, fieldErrors);
	}

	/**
	 * B类复杂错误
	 * 根据一个fieldError以及field名，构造一个实体Error
	 * @param key field名
	 * @param errors error
	 * @return errors
	 */
	@Contract(pure = true)
	public static Errors transform(@NotNull final String key, @Nullable final Errors errors) {
		if (errors == null) return none();
		if (errors == none()) return none();
		return new Errors(null, Set.of(), Map.of(key, errors));
	}

	/**
	 * 是否包含错误（！是否为空错误）
	 * @return 是否包含错误
	 */
	public boolean hasError() {
		return this != none();
	}

	public Errors mapMessage(Function<String,String> mapper) {
		final var messageErrors = this.getMessages().stream()
				.map(oldMessage -> Errors.simple(getRejectValue(), mapper.apply(oldMessage)))
				.reduce(Errors.none(), Errors::plus);
		return this.getFieldErrors().entrySet().stream()
				.map(entry -> Errors.transform(entry.getKey(), entry.getValue().mapMessage(mapper)))
				.reduce(messageErrors, Errors::plus);
	}

	private static final String SELF = "__self__";
	private static final String SEPARATOR = ".";

	public Map<String, Set<String>> toMessageMap() {
		final Map<String, Set<String>> messages = new HashMap<>(getMessages().isEmpty()
				? Map.of() : Map.of(SELF, getMessages()));
		final var fieldErrors = getFieldErrors().entrySet().stream()
				.flatMap(entry -> entry.getValue().toMessageMap().entrySet().stream()
						.map(e -> new Object(){
							final String k = Objects.equals(e.getKey(),SELF) ? entry.getKey() : entry.getKey() + SEPARATOR + e.getKey();
							final Set<String> v = e.getValue();
						})
				)
				.collect(Collectors.toMap(annoy -> annoy.k, annoy -> annoy.v));
		fieldErrors.forEach((k,v) -> messages.merge(k,v,(v0,v1) -> {
			final var tmp = new HashSet<>(v0);
			tmp.addAll(v1);
			return tmp;
		}));
		return messages;
	}
}
