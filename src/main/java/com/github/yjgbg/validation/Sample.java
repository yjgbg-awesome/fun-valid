package com.github.yjgbg.validation;

import com.github.yjgbg.validation.core.BaseValidatorExt;
import com.github.yjgbg.validation.core.Error;
import com.github.yjgbg.validation.core.Validator;
import com.github.yjgbg.validation.ext.JSR380Ext;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@ExtensionMethod({BaseValidatorExt.class, JSR380Ext.class})
public class Sample {
  public static void main(String[] args) {
      final var entity1 = new Entity1(null, 2L, true, Collections.emptyList());
      final var validator =
              Validator.<Entity1>failFast(false)
                      .assertTrue(Entity1::getField3)
                      .min(Entity1::getField2, 3L, "field2应该大于3")
                      .max(Entity1::getField2, 1L, "field2应该小于1")
                      .and(Entity1::getField1, Objects::nonNull, __ -> "field1不得为null");
      final var errors = validator.apply(entity1);
    final var requestBody = Error.wrapper("requestBody", errors);
    System.out.println(errors);
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
