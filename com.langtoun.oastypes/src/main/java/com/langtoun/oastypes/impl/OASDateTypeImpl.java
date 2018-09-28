package com.langtoun.oastypes.impl;

import com.langtoun.oastypes.OASDateType;
import com.reprezen.jsonoverlay.Reference;
import com.reprezen.kaizen.oasparser.model3.Schema;

public class OASDateTypeImpl extends OASTypeImpl implements OASDateType {

  private OASDateTypeImpl(final Schema schema, final Reference reference) {
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
   * Create a new builder for an {@link OASDateTypeImpl} object.
   *
   * @param schema  The {@link Schema} that will be used to build the {@link OASDateTypeImpl}.
   * @param reference  A {@link Reference} associated with the schema which may be {@code null}.
   * @return The {@link Builder} object.
   */
  public static Builder builder(final Schema schema, final Reference reference) {
    return new Builder(schema, reference);
  }

  public static class Builder {
    private final OASDateTypeImpl oasDateType;

    private Builder(final Schema schema, final Reference reference) {
      oasDateType = new OASDateTypeImpl(schema, reference);
    }

    public OASDateType build() {
      return oasDateType;
    }
  }

}
