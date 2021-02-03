package com.github.yjgbg.validation;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ExtensionMethod(ValidatorExt.class)
public class Sample {
  public static void main(String[] args) {
    final var entity1 = new Entity1(null, 2L, new ArrayList<>());

    var validator = Validator.<Entity1>none()
        .and(x -> true,"")
        .and(Entity1::getField2,x -> x > 3L,"field2大于3")
        .and(Entity1::getField1, Objects::nonNull,"field1不得为空");
    var errors = validator.valid(entity1);
    System.out.println(errors);
  }
}

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
class Entity1 {
  String field1;
  Long field2;
  List<Entity1> entity1List;
}
