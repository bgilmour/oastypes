package com.langtoun.oastypes.util

import java.util.Collection
import java.util.Deque
import java.util.HashMap
import javax.xml.namespace.QName
import org.apache.log4j.Logger
import org.apache.ws.commons.schema.XmlSchemaAll
import org.apache.ws.commons.schema.XmlSchemaAnnotated
import org.apache.ws.commons.schema.XmlSchemaAnnotation
import org.apache.ws.commons.schema.XmlSchemaAny
import org.apache.ws.commons.schema.XmlSchemaAppInfo
import org.apache.ws.commons.schema.XmlSchemaArrayType
import org.apache.ws.commons.schema.XmlSchemaChoice
import org.apache.ws.commons.schema.XmlSchemaChoiceMember
import org.apache.ws.commons.schema.XmlSchemaComplexType
import org.apache.ws.commons.schema.XmlSchemaElement
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet
import org.apache.ws.commons.schema.XmlSchemaFacet
import org.apache.ws.commons.schema.XmlSchemaFractionDigitsFacet
import org.apache.ws.commons.schema.XmlSchemaGroup
import org.apache.ws.commons.schema.XmlSchemaGroupRef
import org.apache.ws.commons.schema.XmlSchemaLengthFacet
import org.apache.ws.commons.schema.XmlSchemaMaxExclusiveFacet
import org.apache.ws.commons.schema.XmlSchemaMaxInclusiveFacet
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet
import org.apache.ws.commons.schema.XmlSchemaMinExclusiveFacet
import org.apache.ws.commons.schema.XmlSchemaMinInclusiveFacet
import org.apache.ws.commons.schema.XmlSchemaMinLengthFacet
import org.apache.ws.commons.schema.XmlSchemaParticle
import org.apache.ws.commons.schema.XmlSchemaPatternFacet
import org.apache.ws.commons.schema.XmlSchemaSequence
import org.apache.ws.commons.schema.XmlSchemaSequenceMember
import org.apache.ws.commons.schema.XmlSchemaSimpleType
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeList
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeUnion
import org.apache.ws.commons.schema.XmlSchemaTotalDigitsFacet
import org.apache.ws.commons.schema.XmlSchemaWhiteSpaceFacet

import static extension com.langtoun.oastypes.util.Namespaces.*
import static extension com.langtoun.oastypes.util.StringExtensions.*

class XmlSchemaWriter {

  static val LOGGER = Logger.getLogger(XmlSchemaWriter.name)

  val HashMap<String, String> namespaceAliasMap = newHashMap

  val Deque<XmlSchemaAnnotated> path = newLinkedList

  var String defaultNamespace = ""

  new(String defaultNamespace) {
    this.defaultNamespace = defaultNamespace
    this.namespaceAliasMap.put(WSDL_NAMESPACE, "wsdl")
    this.namespaceAliasMap.put(XML_SCHEMA_NAMESPACE, "xsd")
    this.namespaceAliasMap.put(HTTP_AWS_SCHEMA_NAMESPACE, "http")
  }

  def void addNamespaceAlias(String namespaceURI, String namespaceAlias) {
    namespaceAliasMap.put(namespaceURI, namespaceAlias)
  }

  protected def namespaceToAlias(String namespaceURI) {
    val namespaceAlias = namespaceAliasMap.getOrDefault(namespaceURI, null)
    if (namespaceAlias === null) {
      LOGGER.error("Unknown namespace " + namespaceURI.singleQuote + ".")
      return "?"
    }
    return namespaceAlias
  }

  def namespaceToPrefix(String namespaceURI) {
    if (defaultNamespace == namespaceURI)
      ""
    else
      namespaceToAlias(namespaceURI) + ":"
  }

  def xsd() {
    namespaceToPrefix(XML_SCHEMA_NAMESPACE)
  }

  def wsdl() {
    namespaceToPrefix(WSDL_NAMESPACE)
  }

  def http() {
    namespaceToPrefix(HTTP_AWS_SCHEMA_NAMESPACE)
  }

  def topLevel() {
    path.isEmpty
  }

  def getPath() {
    path
  }

  /*
   * XmlSchemaObject dispatcher
   */

  def dispatch CharSequence writeSchema(XmlSchemaGroup group) '''
    <!-- XmlSchemaGroup: «group.class» -->
  '''

  def dispatch CharSequence writeSchema(XmlSchemaGroupRef groupRef) '''
    <!-- XmlSchemaGroupRef: «groupRef.class» -->
  '''

  def dispatch CharSequence writeSchema(XmlSchemaElement element) '''
    «val elementName = element.name.altFieldPrefix.legal»
    «IF element.schemaTypeName === null»
      <«xsd»element name="«elementName»"«element.displayMinBounds»«element.displayMaxBounds»«element.displayDefaultValue»>
        «IF element.annotation !== null»
          «{ path.push(element); null }»
          «writeSchema(element.annotation)»
          «{ path.pop(); null }»
        «ENDIF»
        «{ path.push(element); null }»
        «writeSchema(element.schemaType)»
        «{ path.pop(); null }»
      </«xsd»element>
    «ELSE»
      «IF element.annotation !== null»
        <«xsd»element name="«elementName»" type="«element.schemaTypeName.toTypeReference»"«element.displayMinBounds»«element.displayMaxBounds»«element.displayDefaultValue»>
          «{ path.push(element); null }»
          «writeSchema(element.annotation)»
          «{ path.pop(); null }»
        </«xsd»element>
      «ELSE»
        <«xsd»element name="«elementName»" type="«element.schemaTypeName.toTypeReference»"«element.displayMinBounds»«element.displayMaxBounds»«element.displayDefaultValue»/>
      «ENDIF»
    «ENDIF»
  '''

  def dispatch CharSequence writeSchema(XmlSchemaSequence sequence) '''
    <«xsd»sequence>
      «{ path.push(sequence); null }»
      «FOR item : sequence.items as Collection<XmlSchemaSequenceMember>»
        «writeSchema(item as XmlSchemaAnnotated)»
      «ENDFOR»
      «{ path.pop(); null }»
    </«xsd»sequence>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaChoice choice) '''
    <«xsd»choice>
      «{ path.push(choice); null }»
      «FOR item : choice.items as Collection<XmlSchemaChoiceMember>»
        «writeSchema(item as XmlSchemaAnnotated)»
      «ENDFOR»
      «{ path.pop(); null }»
    </«xsd»choice>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaAll all) '''
    <!-- XmlSchemaAll: «all.class» -->
  '''

  def dispatch CharSequence writeSchema(XmlSchemaAny any) '''
«««    «IF !Option.skipAnyElements.set»
«««      <«xsd»any maxOccurs="unbounded" minOccurs="0" processContents="skip"/>
«««    «ELSE»
      <!-- Skip <«xsd»any> elements for now -->
«««    «ENDIF»
  '''

  def dispatch CharSequence writeSchema(XmlSchemaArrayType array) '''
    <«xsd»complexType «IF topLevel»name="«array.name.legal»"«ENDIF»>
      «IF array.annotation !== null»
        «{ path.push(array); null }»
        «writeSchema(array.annotation)»
        «{ path.pop(); null }»
      «ENDIF»
      <«xsd»sequence>
        «IF array.itemType.QName !== null»
          <«xsd»element name="array" type="«array.itemType.QName.toTypeReference»" maxOccurs="unbounded"/>
        «ELSE»
          <«xsd»element name="array" maxOccurs="unbounded">
            «{ path.push(array); null }»
            «writeSchema(array.itemType)»
            «{ path.pop(); null }»
          </«xsd»element>
        «ENDIF»
      </«xsd»sequence>
    </«xsd»complexType>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaAnnotation annotation) '''
    «val appInfoList = annotation.items.filter[item | item instanceof XmlSchemaAppInfo]»
    «IF !appInfoList.empty»
      <«xsd»annotation>
        «FOR appInfo : appInfoList»
          <«xsd»appinfo«IF appInfo.sourceURI !== null» source="«appInfo.sourceURI»"«ENDIF»>
            «(appInfo as XmlSchemaAppInfo).source»
          </«xsd»appinfo>
        «ENDFOR»
      </«xsd»annotation>
    «ENDIF»
  '''

  def dispatch CharSequence writeSchema(XmlSchemaComplexType complexType) '''
    <«xsd»complexType«IF topLevel» name="«complexType.name.legal»"«ENDIF»>
      «IF complexType.annotation !== null»
        «{ path.push(complexType); null }»
        «writeSchema(complexType.annotation)»
        «{ path.pop(); null }»
      «ENDIF»
      «{ path.push(complexType); null }»
      «writeSchema(complexType.particle)»
      «{ path.pop(); null }»
    </«xsd»complexType>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaSimpleType simpleType) '''
    <«xsd»simpleType«IF topLevel» name="«simpleType.name.legal»"«ENDIF»>
      «IF simpleType.annotation !== null»
        «{ path.push(simpleType); null }»
        «writeSchema(simpleType.annotation)»
        «{ path.pop(); null }»
      «ENDIF»
      «{ path.push(simpleType); null }»
      «{ if (simpleType.content !== null) writeSchema(simpleType.content) }»
      «{ path.pop(); null }»
    </«xsd»simpleType>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaSimpleTypeList list) '''
    «IF list.itemType !== null»
      <«xsd»list itemtype="«list.itemType.QName.toTypeReference»"/>
    «ELSE»
      <«xsd»list>
        «{ path.push(list); null }»
        «writeSchema(list.itemType)»
        «{ path.pop(); null }»
      </«xsd»>
    «ENDIF»
  '''

  def dispatch CharSequence writeSchema(XmlSchemaSimpleTypeUnion union) '''
    <!-- XmlSchemaSimpleTypeUnion: «union.class» -->
  '''

  def dispatch CharSequence writeSchema(XmlSchemaSimpleTypeRestriction restriction) '''
    «IF restriction.facets !== null && !restriction.facets.empty»
      «IF restriction.baseType !== null»
        <«xsd»restriction base="«restriction.baseType.toTypeReference»">
      «ELSE»
        <«xsd»restriction base="«restriction.baseTypeName.toTypeReference»">
      «ENDIF»
        «{ path.push(restriction); null }»
        «FOR facet : restriction.facets as Collection<XmlSchemaFacet>»
          «writeSchema(facet)»
        «ENDFOR»
        «{ path.pop(); null }»
      </«xsd»restriction>
    «ELSE»
      «IF restriction.baseType !== null»
        <«xsd»restriction base="«restriction.baseType.toTypeReference»"/>
      «ELSE»
        <«xsd»restriction base="«restriction.baseTypeName.toTypeReference»"/>
      «ENDIF»
    «ENDIF»
  '''

  def dispatch CharSequence writeSchema(XmlSchemaEnumerationFacet facet) '''
    <«xsd»enumeration value="«facet.value»"/>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaFractionDigitsFacet facet) '''
    <«xsd»fractionDigits value="«facet.value»"/>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaLengthFacet facet) '''
    <«xsd»length value="«facet.value»"/>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaMaxExclusiveFacet facet) '''
    <«xsd»maxExclusive value="«facet.value»"/>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaMaxInclusiveFacet facet) '''
    <«xsd»maxInclusive value="«facet.value»"/>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaMaxLengthFacet facet) '''
    <«xsd»maxLength value="«facet.value»"/>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaMinExclusiveFacet facet) '''
    <«xsd»minExclusive value="«facet.value»"/>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaMinInclusiveFacet facet) '''
    <«xsd»minInclusive value="«facet.value»"/>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaMinLengthFacet facet) '''
    <«xsd»minLength value="«facet.value»"/>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaPatternFacet facet) '''
    <«xsd»pattern value="«facet.value»"/>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaTotalDigitsFacet facet) '''
    <«xsd»totalDigits value="«facet.value»"/>
  '''

  def dispatch CharSequence writeSchema(XmlSchemaWhiteSpaceFacet facet) '''
    <«xsd»whiteSpace value="«facet.value»"/>
  '''

  private def String toTypeReference(QName qname) {
    qname.namespaceURI.namespaceToPrefix + qname.localPart.legal
  }

  private def String toTypeReference(XmlSchemaSimpleType simpleType) {
    simpleType.QName.toTypeReference
  }

  private def displayMinBounds(XmlSchemaParticle particle) {
    if (particle.minOccurs == 1)
      ''''''
    else
      ''' minOccurs="«particle.minOccurs»"'''
  }

  private def displayMaxBounds(XmlSchemaParticle particle) {
    if (particle.maxOccurs == 1)
      ''''''
    else if (particle.maxOccurs == Long.MAX_VALUE)
      ''' maxOccurs="unbounded"'''
    else
      ''' maxOccurs="«particle.maxOccurs»"'''
  }

  private def displayDefaultValue(XmlSchemaElement element) {
    if (element.defaultValue === null)
      ''''''
    else
      ''' default="«element.defaultValue»"'''
  }

}
