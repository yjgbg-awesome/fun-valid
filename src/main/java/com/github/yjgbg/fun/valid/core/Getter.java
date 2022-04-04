package com.github.yjgbg.fun.valid.core;

import lombok.SneakyThrows;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 表示一个javaBean中的get方法 用途：希望对路径有类型安全的表达，而不是拼接字符串
 *
 * <p>例如: Role role = new User().getRole(); Getter&lt;User,Role&gt; getter = User::getRole; assert
 * "role" == getter.propertyName();
 *
 * @param <A> javaBean的类型
 * @param <B> get方法返回的类型
 */
@FunctionalInterface
public interface Getter<A, B> extends Function<A, B>, Serializable {
	Map<Class<?>, String> GETTER_NAME_CACHE = new ConcurrentHashMap<>();

	@SneakyThrows
	private String propertyName0() {
		final var method = this.getClass().getDeclaredMethod("writeReplace");
		method.setAccessible(true);
		final var serializedLambda = (SerializedLambda) method.invoke(this);
		final var getter = serializedLambda.getImplMethodName();
		// 如果函数名是get开头，则取掉开头的get，并将剩余部分首字母小写，否则返回函数名
		return getter.startsWith("get") ? Introspector.decapitalize(getter.substring(3)) : getter;
	}

	/**
	 * 返回该get方法对应的属性的名称
	 *
	 * @return 返回该get方法对应的属性的名称
	 */
	default String propertyName() {
		return GETTER_NAME_CACHE.computeIfAbsent(getClass(), __ -> propertyName0());
	}

	static <A,B> Getter<A,B> of(String propName,Function<A,B> func) {
		return new Getter<>() {
			@Override
			public B apply(A a) {
				return func.apply(a);
			}

			@Override
			public String propertyName() {
				return propName;
			}
		};
	}
}
