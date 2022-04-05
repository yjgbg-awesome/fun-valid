package com.github.yjgbg.fun.valid;

import com.github.yjgbg.fun.valid.core.Validator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
public class Sample {
	public static void main(String[] args) {
		v0();
//		v1();
	}

	private static void v0() {
		// 创建两个实体
		final var entity0 = new Person("0", -1L, Gender.MALE,
				"12345678910", "abc@def.com", null, Map.of());
		final var entity1 = new Person("1", 1L, Gender.MALE,
				"12345678910", "abc@def.com", Collections.singletonList(entity0), Map.of());
		//创建两个校验器
		// validator0校验id,name,gender,phone,email这几个字段
		// message 可以使用{}取值被校验的字段
		// constraint 校验规则
		final var validator0 = Validator.<Person>none()
				.plus("person不能为空", Objects::nonNull)
				.and(Person::getId, "id应该大于0,而不是{}", id -> id != null && id > 0L)
				.and(Person::getName, "name应该是2-5个字", name -> name != null && name.length() >= 2 && name.length() < 5)
				.and(Person::getGender, "应该是男性", gender -> Objects.equals(gender, Gender.MALE))
				.and(Person::getPhone, "电话号码应该为11位数字", x -> x == null || Pattern.matches("^\\d{11}$", x))
				.and(Person::getEmail, "email格式非法", x ->
						x != null && Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", x));
		final var validator1 = validator0.andItems(Person::getChildren, validator0);
		// 第一个参数为是否开启快速校验（在第一个校验错误出现的时候，直接跳过后续校验）
		final var errors1 = validator1.apply(false, entity1);
		System.out.println(errors1.toMessageMap());
	}

	public static void v1() {
		final var entity1 = new Person("null", 0L, Gender.FEMALE, "1234567891011", "asdqq.com", null, Map.of());
		final var validator1 = Validator.<Person>none()
				.notNull("person不能为空")
				.gt(Person::getId, "id应该大于0", false, 0L)
				.length(Person::getName, "name应该是2-5个字", false, 2, 5)
				.eq(Person::getGender, "应该是男性", Gender.MALE)
				.regexp(Person::getPhone, "电话号码不合法", true, "^\\d{11}$")
				.regexp(Person::getEmail, "email不合法", false,
						"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
		final var errors1 = validator1.apply(false, entity1);
		System.out.println(errors1.toMessageMap());
	}
}

@Data
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class Person {
	String name;
	Long id;
	Gender gender;
	String phone;
	String email;
	List<Person> children;
	Map<String, Object> props;
}

enum Gender {
	MALE, FEMALE
}
