package com.github.yjgbg.validation;

import com.github.yjgbg.validation.core.ExtStdValidator;
import com.github.yjgbg.validation.core.Validator;
import com.github.yjgbg.validation.ext.ExtCharSequenceValidator;
import com.github.yjgbg.validation.ext.ExtComparableValidator;
import com.github.yjgbg.validation.ext.ExtObjectValidator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtensionMethod({ExtStdValidator.class, ExtObjectValidator.class, ExtComparableValidator.class,
		ExtCharSequenceValidator.class})
public class Sample {
	public static void main(String[] args) {
		final var entity0 = new Entity1("null", 0L, true, Collections.emptyList());
		final var entity1 = new Entity1(null, 0L, false, Collections.emptyList());
		final var entity2 = new Entity1("null", 0L, false, Arrays.asList(entity0, entity1, entity1));
		final var baseValidator = Validator.<Entity1>none()
				.nonNull("对象为空:%s")
				.equal(Entity1::getField3, false, "field3应该为false,但其值为%s".fmt())
				.nonNull(Entity1::getField1, "field1不得为null")
//				.and(Entity1::getField1,"已存在field1为%s的记录".fmt(),f1 -> !dao().spec().in(Entity1::getField1,f1).exist())
				.regexp(Entity1::getField1,true,"1","value is wrong".fmt())
				.inRange(Entity1::getField2, 1L, 3L, "field2应该介于1-3之间，但其值却为%s".fmt());
		final var validator = baseValidator.andIter(Entity1::getField4, baseValidator);
		final var errors1 = validator.failFastApply(entity2);
		System.out.println(errors1.toMessageMap());
	}
}

@Data
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class Entity1 {
	String field1;
	Long field2;
	Boolean field3;
	List<Entity1> field4;
}
