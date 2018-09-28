package com.langtoun.oastypes;

import java.util.List;

public interface OASStringType extends OASType {

  Integer minLength();

  Integer maxLength();

  String pattern();

  List<String> enums();

}
