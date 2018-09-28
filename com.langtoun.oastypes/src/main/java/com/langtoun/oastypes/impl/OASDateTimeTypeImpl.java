package com.langtoun.oastypes.impl;

import com.langtoun.oastypes.OASDateTimeType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

public class OASDateTimeTypeImpl extends OASTypeImpl implements OASDateTimeType {

  private OASDateTimeTypeImpl(final Schema schema, final Reference reference) {
    super(schema, reference);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();

    sb.append("{");
    sb.append(super.toString());

    sb.append("}");

    return sb.toString();
  }

  /**
   * Create a new builder for an {@link OASDateTimeTypeImpl} object.
   *
   * @param schema  The {@link Schema} that will be used to build the {@link OASDateTimeTypeImpl}.
   * @param reference  A {@link Reference} associated with the schema which may be {@code null}.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final Schema schema, final Reference reference) {
    return new Builder(schema, reference);
  }

  public static class Builder {
    private final OASDateTimeTypeImpl oasDateTimeType;

    private Builder(final Schema schema, final Reference reference) {
      oasDateTimeType = new OASDateTimeTypeImpl(schema, reference);
    }

    public OASDateTimeType build() {
      return oasDateTimeType;
    }
  }

}
