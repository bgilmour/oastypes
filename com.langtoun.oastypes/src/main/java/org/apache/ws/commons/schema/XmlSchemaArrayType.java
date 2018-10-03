package org.apache.ws.commons.schema;

import java.util.List;


/**
 * A transient class to represent array types. When embedded in an element we propagate the
 * minimum, maximum and item type to the element.
 */
public class XmlSchemaArrayType extends XmlSchemaType {

  private long minOccurs = 1;

  private long maxOccurs = 1;

  private XmlSchemaType itemType;

  public long getMinOccurs() {
    return minOccurs;
  }

  public void setMinOccurs(final long minOccurs) {
    this.minOccurs = minOccurs;
  }

  public long getMaxOccurs() {
    return maxOccurs;
  }

  public void setMaxOccurs(final long maxOccurs) {
    this.maxOccurs = maxOccurs;
  }

  public XmlSchemaType getItemType() {
    return itemType;
  }

  public void setItemType(final XmlSchemaType itemType) {
    this.itemType = itemType;
  }

  public XmlSchemaArrayType(final XmlSchema schema, final boolean topLevel) {
    super(schema, topLevel);
  }

  /**
   * Update an XML schema element.
   *
   * @param element
   *          The {@link XmlSchemaElement} to be updated.
   */
  public void updateElement(final XmlSchemaElement element) {
    assert element.getSchemaType() == this;
    assert itemType != null;
    // assert !(itemType instanceof XmlSchemaArrayType); // Can't cope with T[][] yet.

    if (element.isTopLevel()) {
      // We can't move the multiplicities to the enclosing element, so we need to create an additional
      // element in the tree to attach these to. We use the name 'array' for this element.
      final XmlSchemaComplexType complexType = new XmlSchemaComplexType(element.getParent(), false);

      final XmlSchemaSequence xmlSchemaSequence = new XmlSchemaSequence();
      final List<XmlSchemaSequenceMember> items = xmlSchemaSequence.getItems();
      complexType.setParticle(xmlSchemaSequence);

      final XmlSchemaElement childElement = new XmlSchemaElement(element.getParent(), false);
      childElement.setName("array"); //$NON-NLS-1$
      childElement.setType(this);
      items.add(childElement);

      element.setAnnotation(getAnnotation());
      element.setSchemaType(complexType);
      element.setSchemaTypeName(complexType.getQName());

      updateElement(childElement);
    } else {
      element.setMinOccurs(minOccurs);
      element.setMaxOccurs(maxOccurs);
      if (itemType.getQName() != null) {
        element.setSchemaTypeName(itemType.getQName());
      } else {
        element.setSchemaType(itemType);
      }
    }
  }

}
