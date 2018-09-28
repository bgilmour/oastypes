package com.langtoun.oastypes.impl;

import com.langtoun.oastypes.OASBooleanType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

public class OASBooleanTypeImpl extends OASTypeImpl implements OASBooleanType {

  private OASBooleanTypeImpl(final Schema schema, final Reference reference) {
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
   * Create a new builder for an {@link OASBooleanTypeImpl} object.
   *
   * @param schema  The {@link Schema} that will be used to build the {@link OASBooleanTypeImpl}.
   * @param reference  A {@link Reference} associated with the schema which may be {@code null}.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final Schema schema, final Reference reference) {
    return new Builder(schema, reference);
  }

  public static class Builder {
    private final OASBooleanTypeImpl oasBooleanType;

    private Builder(final Schema schema, final Reference reference) {
      oasBooleanType = new OASBooleanTypeImpl(schema, reference);
    }

    public OASBooleanType build() {
      return oasBooleanType;
    }
  }

}
