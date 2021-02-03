package com.github.yjgbg.validation.core;

import lombok.SneakyThrows;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.HashMap;
import java.util.function.Function;

/**
 * 表示一个javaBean中的get方法
 * 用途：希望对路径有类型安全的表达，而不是拼接字符串
 * <p>
 * 例如: Role role = new User().getRole();
 * Getter&lt;User,Role&gt; getter = User::getRole;
 * assert "role" == getter.propertyName();
 *
 * @param <A> javaBean的类型
 * @param <B> get方法返回的类型
 */
@FunctionalInterface
public interface Getter<A, B> extends Function<A, B>, Serializable {
    HashMap<Class<?>, String> GETTER_NAME_CACHE = new HashMap<>();
	@SneakyThrows
	private String propertyName0() {
		final var method = this.getClass().getDeclaredMethod("writeReplace");
		method.setAccessible(Boolean.TRUE);
		final var serializedLambda = (SerializedLambda) method.invoke(this);
		final var getter = serializedLambda.getImplMethodName();
		return Introspector.decapitalize(getter.replace("get", ""));
	}

	/**
	 * 返回该get方法对应的属性的名称
	 * @return 返回该get方法对应的属性的名称
	 */
	default String propertyName() {
		final var clazz = getClass();
		final var res0 = GETTER_NAME_CACHE.get(clazz);
		if (res0 != null) return res0;
		final var res = propertyName0();
		GETTER_NAME_CACHE.put(clazz, res);
		return res;
	}
}