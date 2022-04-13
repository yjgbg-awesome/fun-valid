package com.github.yjgbg.fun.valid;

import com.github.yjgbg.fun.valid.core.Validator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

public class Sample {

	public static void main(String[] args) {
		final var entity1 = new Person("null", 0L, Gender.FEMALE, "1234567891011", "asdqq.com", null, Map.of());
		final var validator1 = Validator.<Person>none()
				.notNull("person不能为空")
				.gt(Person::getId, "id应该大于0", false, 0L)
				.length(Person::getName, "name应该是2-5个字", false, 2, 5)
				.eq(Person::getGender, "应该是男性", Gender.MALE)
				.regexp(Person::getPhone, "电话号码不合法", true, "^\\d{11}$")
				.regexp(Person::getEmail, "email不合法", false,"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
				.email(Person::getEmail, "email还是不合法", false);
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
