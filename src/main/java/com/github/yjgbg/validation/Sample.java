package com.github.yjgbg.validation;

import com.github.yjgbg.validation.core.Validator;
import com.github.yjgbg.validation.ext.LbkExtValidatorsCore;
import com.github.yjgbg.validation.ext.LbkExtValidatorsStd;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.regex.Pattern;

@ExtensionMethod({LbkExtValidatorsCore.class, LbkExtValidatorsStd.class})
public class Sample {
	public static void main(String[] args) {
		v0();
		v1();
	}
	private static void v0() {
		final var entity0 = new Person("l", 0L, Gender.MALE,"12345678910","abc@def.com");
		final var validator0 = Validator.<Person>none()
				.and("person不能为空", Objects::nonNull)
				.and(Person::getId,"id应该大于0,而不是%s", id -> id!=null && id > 0L)
				.and(Person::getName,"name应该是2-5个字",name -> name!=null && name.length() >= 2 && name.length() <5)
				.and(Person::getGender,"应该是男性",gender -> Objects.equals(gender,Gender.MALE))
				.and(Person::getPhone,"电话号码应该为11位数字", x -> x == null || Pattern.matches("^\\d{11}$",x))
				.and(Person::getEmail,  "email格式非法", x ->
						x != null && Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$",x));
		final var errors0 = validator0.apply(false,entity0);
		System.out.println(errors0.toMessageMap());
	}

	public static void v1(){
		final var entity1 = new Person("null", 0L, Gender.FEMALE,"1234567891011","asdqq.com");
		final var validator1 =Validator.<Person>none()
				.nonNull("person不能为空")
				.gt(Person::getId,"id应该大于0",0L)
				.length(Person::getName,"name应该是2-5个字",2,5)
				.equal(Person::getGender,"应该是男性",Gender.MALE)
				.regexp(Person::getPhone,"电话号码不合法", true, "^\\d{11}$")
				.regexp(Person::getEmail,"email不合法", false,
						"^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
		final var errors1 = validator1.apply(false,entity1);
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
}

enum Gender{
	MALE,FEMALE
}
