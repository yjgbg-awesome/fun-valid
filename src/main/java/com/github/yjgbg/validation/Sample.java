package com.github.yjgbg.validation;

import com.github.yjgbg.validation.core.LbkExtStdValidator;
import com.github.yjgbg.validation.core.Validator;
import com.github.yjgbg.validation.ext.LbkExtCharSequenceValidator;
import com.github.yjgbg.validation.ext.LbkExtComparableValidator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@ExtensionMethod({LbkExtStdValidator.class, LbkExtCharSequenceValidator.class, LbkExtComparableValidator.class})
public class Sample {
	public static void main(String[] args) {
		final var entity0 = new Entity1("null", 0L, true, Collections.emptyList());
		final var entity1 = new Entity1(null, 0L, false, Collections.emptyList());
		final var entity2 = new Entity1("null", 0L, false, Arrays.asList(entity0, entity1, entity1));
		final var baseValidator = Validator.<Entity1>none()
				.and("obj不能为空".fmt(), Objects::nonNull)
//				.nonNull("obj不能为空")
				.and(Entity1::getBooleanField,"field3应该为false,但其值为%s".fmt(),b -> Objects.equals(b,false))
//				.equal(Entity1::getBooleanField, false, "field3应该为false,但其值为%s".fmt())
				.and(Entity1::getStringField,"field1不得为null".fmt(),Objects::nonNull)
//				.nonNull(Entity1::getStringField, "field1不得为null")
				.and(Entity1::getStringField,"已存在field1为%s的记录".fmt(),f1 -> true)
//				.regexp(Entity1::getStringField,true,"someregex","value is wrong".fmt())
				.and(Entity1::getStringField,"value is wrong".fmt(),x -> x == null || Pattern.matches("someregex",x))
//				.inRange(Entity1::getLongField, 1L, 3L, "field2应该介于1-3之间，但其值却为%s".fmt());
				.and(Entity1::getLongField,  "field2应该介于1-3之间，但其值却为%s".fmt(),x -> x != null && x == 2);
		final var validator = baseValidator.andIter(Entity1::getListField, baseValidator);
		final var errors1 = validator.failFastApply(entity2);
		System.out.println(errors1.toMessageMap());
	}
}

@Data
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class Entity1 {
	String stringField;
	Long longField;
	Boolean booleanField;
	List<Entity1> listField;
}
