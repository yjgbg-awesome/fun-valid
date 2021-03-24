package com.github.yjgbg.validation;

import com.github.yjgbg.validation.core.LbkExtStdValidator;
import com.github.yjgbg.validation.core.Validator;
import com.github.yjgbg.validation.ext.LbkExtObjectValidator;
import com.github.yjgbg.validation.ext.LbkExtStringValidator;
import com.github.yjgbg.validation.ext.LbkExtComparableValidator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;

import java.util.Objects;
import java.util.regex.Pattern;

@ExtensionMethod({LbkExtStdValidator.class, LbkExtObjectValidator.class,
		LbkExtStringValidator.class, LbkExtComparableValidator.class})
public class Sample {
	public static void main(String[] args) {
		final var entity0 = new Entity1("null", 0L, true,null);
		final var entity1 = new Entity1("null", 0L, false, entity0);
		final var validator0 = Validator.<Entity1>none()
				.and("obj不能为空", Objects::nonNull)
				.and(Entity1::getBooleanField,"field3应该为false,但其值为%s",b -> Objects.equals(b,false))
				.and(Entity1::getStringField,"field1不得为null",Objects::nonNull)
				.and(Entity1::getStringField,"value is wrong",x -> x == null || Pattern.matches("someregex",x))
				.and(Entity1::getLongField,  "field2应该介于1-3之间，但其值却为%s",x -> x != null && x == 2);
//		final var validator0 =Validator.<Entity1>none()
//				.nonNull("obj不能为空")
//				.equal(Entity1::getBooleanField,"field3应该为false,但其值为%s",false)
//				.nonNull(Entity1::getStringField,"field1不得为null")
//				.regexp(Entity1::getStringField,"value is wrong",true,"someregex")
//				.between(Entity1::getLongField,"field2应该介于1-3之间，但其值却为%s",1L,3L);
		final var validator1 = validator0.and(Entity1::getEntity1Field,validator0);
		final var errors2 = validator1.apply(true,entity1);
		System.out.println(errors2.toMessageMap());
	}
}

@Data
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class Entity1 {
	String stringField;
	Long longField;
	Boolean booleanField;
	Entity1 entity1Field;
}
