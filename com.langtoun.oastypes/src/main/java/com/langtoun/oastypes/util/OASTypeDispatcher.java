package com.langtoun.oastypes.util;

import com.langtoun.oastypes.OASType;

public interface OASTypeDispatcher<T, C> {

  T visit(OASType oasType, C context);

}
