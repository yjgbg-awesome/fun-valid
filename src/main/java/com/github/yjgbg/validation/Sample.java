package com.github.yjgbg.validation;

import com.github.yjgbg.validation.core.Validator;
import com.github.yjgbg.validation.core.ValidatorStdExt;
import com.github.yjgbg.validation.ext.SomeUsefulValidatorExt;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ExtensionMethod({ValidatorStdExt.class, SomeUsefulValidatorExt.class})
public class Sample {
	public static void main(String[] args) {
		final var entity0 = new Entity1("null", 0L, false, Collections.emptyList());
		final var entity1 = new Entity1(null, 0L, false, Collections.emptyList());
		final var entity2 = new Entity1("null", 0L, false, Arrays.asList(entity0, entity1, entity1));
		final var baseValidator = Validator.<Entity1>none()
				.nonNull("对象为空:%s")
				.and("对象应当为空".fmt(), Objects::isNull)
				.equal(Entity1::getField3, false, "field3应该为false,但其值为%s".fmt())
				.nonNull(Entity1::getField1, "field1不得为null")
				.littleThan(Entity1::getField2, 1L, "field2应该小于1,但是真实值为%s".fmt())
				.notEquals(Entity1::getField2, 2L, "field2应该不等于2")
				.inRangeInclusive(Entity1::getField2, 1L, 3L, "field2应该介于1-3之间，但其值却为%s".fmt());
		final var validator = baseValidator.andIter(Entity1::getEntity1List, baseValidator);
		final var errors1 = validator.noFailFastApply(entity2);
		System.out.println(errors1.output());
	}
}

@Data
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class Entity1 {
	String field1;
	Long field2;
	Boolean field3;
	List<Entity1> entity1List;
}
