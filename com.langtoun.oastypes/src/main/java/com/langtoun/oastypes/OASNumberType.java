package com.langtoun.oastypes;

import java.util.List;

public interface OASNumberType extends OASType {

  Number minimum();

  Number maximum();

  Boolean exclusiveMinimum();

  Boolean exclusiveMaximum();

  List<Number> enums();

  boolean isExclusiveMinimum();

  boolean isExclusiveMaximum();

}
