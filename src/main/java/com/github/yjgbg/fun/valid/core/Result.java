package com.github.yjgbg.fun.valid.core;

import lombok.AccessLevel;
import lombok.Getter;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Result extends RuntimeException{

	private static final Result NONE = new Result(null, Set.of(), Map.of());
	Object rejectValue;
	Set<String> messages;
	Map<String, Result> fieldResults;

	private Result(Object rejectValue,Set<String> messages,Map<String,Result> fieldResults) {
		super(null,null,false,false);
		this.rejectValue = rejectValue;
		this.messages = messages;
		this.fieldResults = fieldResults;
	}

	/**
	 * @return 空错误，表示没有错
	 */
	public static Result none() {
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
	public static Result simple(@Nullable final Object rejectValue, @NotNull final String message) {
		if (rejectValue == null && message.isBlank()) return none();
		return new Result(rejectValue, Set.of(message), Map.of());
	}

	/**
	 * A类复杂错误
	 * 由两个错误相加而成，对应字段相加
	 * @param result0 错误0
	 * @param result1 错误1
	 * @return 由两个错误相加而成，对应字段相加
	 */
	@NotNull
	@Contract(pure = true)
	public static Result plus(@Nullable final Result result0, @Nullable final Result result1) {
		if (result0 == null) return plus(none(), result1);
		if (result1 == null) return plus(result0, none());
		if (result0 == none()) return result1;
		if (result1 == none()) return result0;
		final var reject0 = result0.getRejectValue();
		final var reject1 = result1.getRejectValue();
		if (reject0 != null && reject1 != null && reject0 != reject1)
			throw new IllegalArgumentException(String.format("不同数据对象的校验结果不可相加(%s,%s)", reject0, reject1));
		// 并集
		final var messages = new HashSet<String>();
		messages.addAll(result0.messages);
		messages.addAll(result1.messages);
		final var fieldResults = new HashMap<>(result0.fieldResults);
		result1.fieldResults.forEach((k, v) -> fieldResults.merge(k,v, Result::plus));
		return new Result(reject0 != null ? reject0 : reject1, messages, fieldResults);
	}

	/**
	 * B类复杂错误
	 * 根据一个fieldError以及field名，构造一个实体Error
	 * @param key field名
	 * @param result result
	 * @return Results
	 */
	@Contract(pure = true)
	public static Result transform(@NotNull final String key, @Nullable final Result result) {
		if (result == null) return none();
		if (result == none()) return none();
		return new Result(null, Set.of(), Map.of(key, result));
	}

	/**
	 * 是否包含错误（！是否为空错误）
	 * @return 是否包含错误
	 */
	public boolean hasError() {
		return this != none();
	}

	public void throwIfHasError() {
		if (hasError()) throw this;
	}

	public Result mapMessage(Function<String,String> mapper) {
		final var messageResults = this.getMessages().stream()
				.map(oldMessage -> Result.simple(getRejectValue(), mapper.apply(oldMessage)))
				.reduce(Result.none(), Result::plus);
		return this.getFieldResults().entrySet().stream()
				.map(entry -> Result.transform(entry.getKey(), entry.getValue().mapMessage(mapper)))
				.reduce(messageResults, Result::plus);
	}

	private static final String SELF = "__self__";
	private static final String SEPARATOR = ".";

	public Map<String, Set<String>> toMessageMap() {
		final Map<String, Set<String>> messages = new HashMap<>(getMessages().isEmpty()
				? Map.of() : Map.of(SELF, getMessages()));
		final var fieldResults = getFieldResults().entrySet().stream()
				.flatMap(entry -> entry.getValue().toMessageMap().entrySet().stream()
						.map(e -> new Object(){
							final String k = Objects.equals(e.getKey(),SELF) ? entry.getKey() : entry.getKey() + SEPARATOR + e.getKey();
							final Set<String> v = e.getValue();
						})
				)
				.collect(Collectors.toMap(annoy -> annoy.k, annoy -> annoy.v));
		fieldResults.forEach((k,v) -> messages.merge(k,v,(v0,v1) -> {
			final var tmp = new HashSet<>(v0);
			tmp.addAll(v1);
			return tmp;
		}));
		return messages;
	}

}
