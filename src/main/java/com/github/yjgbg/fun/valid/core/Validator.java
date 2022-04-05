package com.github.yjgbg.fun.valid.core;

import com.github.yjgbg.fun.valid.support.CoreSupport;
import com.github.yjgbg.fun.valid.support.StandardSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Validator接口,以及Validator之间的运算
 *
 * @param <A> 校验的目标元素类型
 */
@FunctionalInterface
public interface Validator<A> extends CoreSupport<A>, StandardSupport<A> {
  @Override
  default Validator<A> self() {
    return this;
  }

  Result apply(@NotNull Boolean failFast, @Nullable A a);

  /**
   * 空校验器
   * 构造一个校验结果恒为空的校验器
   *
   * @param <A> 校验的目标元素类型
   * @return 校验结果恒为空的校验器
   */
  static <A> Validator<A> none() {
    return (failFast, obj) -> Result.none();
  }

  /**
   * 简单校验器
   * 根据constraint描述的规则，和message描述的错误信息构造一个简单校验器
   *
   * @param <A>        校验的目标元素类型
   * @param message    错误信息
   * @param constraint 约束条件表达式
   * @return 简单校验器
   */
  static <A> Validator<A> simple(Function<A, String> message, Function<@Nullable A, @NotNull Boolean> constraint) {
    return (failFast, obj) -> constraint.apply(obj) ? Result.none() : Result.simple(obj, message.apply(obj));
  }

  static <A> Validator<A> simple(String messageTemplate, Function<@Nullable A, @NotNull Boolean> constraint) {
    return (failFast, obj) -> constraint.apply(obj) ? Result.none() :
        Result.simple(obj, Arrays.stream((messageTemplate+" ").split("\\{}"))
                .reduce((a,b) ->a.endsWith("\\") ? a.substring(0,a.length()-1)+"{}"+b:a+ obj+b).map(it -> it.substring(0,it.length()-1)).orElseThrow());
  }

  /**
   * 将目标元素校验器构造器映射为校验器
   *
   * @param validatorFunction 校验器构造器
   * @param <A>               校验的目标元素类型
   * @return 校验器
   */
  static <A> Validator<A> func(Function<A, Validator<? super A>> validatorFunction) {
    return (failFast, obj) -> validatorFunction.apply(obj).apply(failFast, obj);
  }

  /**
   * A类复杂校验器
   * 根据两个目标元素的校验器，构造一个目标元素的校验器
   *
   * @return 由两个校验器组合而成的校验器
   */
  default Validator<A> plus(Validator<? super A> anotherValidator) {
    return (failFast, obj) -> {
      final var error0 = this.apply(failFast, obj);
      if (failFast && error0 != Result.none()) return error0;
      return Result.plus(error0, anotherValidator.apply(failFast, obj));
    };
  }

  /**
   * B类复杂校验器
   * 根据field类型的校验器和prop，构造一个目标类型校验器,规则为校验目标元素经过getter运算之后的结果符合validator校验
   *
   * @param prop 一个A 到 B的转换器
   * @return 目标类型校验器
   */
  default <B> Validator<B> transform(StaticMethodReferenceGetter<B, @Nullable A> prop) {
    return (failFast, obj) -> Result.transform(prop.propertyName(), this.apply(failFast, obj != null ? prop.apply(obj) : null));
  }
}
