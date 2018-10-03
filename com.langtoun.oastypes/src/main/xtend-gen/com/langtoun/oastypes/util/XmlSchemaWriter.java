package com.langtoun.oastypes.util;

import com.google.common.base.Objects;
import com.langtoun.oastypes.util.Namespaces;
import com.langtoun.oastypes.util.StringExtensions;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.apache.ws.commons.schema.XmlSchemaAll;
import org.apache.ws.commons.schema.XmlSchemaAnnotated;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAnnotationItem;
import org.apache.ws.commons.schema.XmlSchemaAny;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.apache.ws.commons.schema.XmlSchemaArrayType;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaChoiceMember;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaFacet;
import org.apache.ws.commons.schema.XmlSchemaFractionDigitsFacet;
import org.apache.ws.commons.schema.XmlSchemaGroup;
import org.apache.ws.commons.schema.XmlSchemaGroupRef;
import org.apache.ws.commons.schema.XmlSchemaLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMinExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSequenceMember;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeContent;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeList;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeUnion;
import org.apache.ws.commons.schema.XmlSchemaTotalDigitsFacet;
import org.apache.ws.commons.schema.XmlSchemaWhiteSpaceFacet;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class XmlSchemaWriter {
  private final static Logger LOGGER = Logger.getLogger(XmlSchemaWriter.class.getName());
  
  private final HashMap<String, String> namespaceAliasMap = CollectionLiterals.<String, String>newHashMap();
  
  private final Deque<XmlSchemaAnnotated> path = CollectionLiterals.<XmlSchemaAnnotated>newLinkedList();
  
  private String defaultNamespace = "";
  
  public XmlSchemaWriter(final String defaultNamespace) {
    this.defaultNamespace = defaultNamespace;
    this.namespaceAliasMap.put(Namespaces.WSDL_NAMESPACE, "wsdl");
    this.namespaceAliasMap.put(Namespaces.XML_SCHEMA_NAMESPACE, "xsd");
    this.namespaceAliasMap.put(Namespaces.HTTP_AWS_SCHEMA_NAMESPACE, "http");
  }
  
  public void addNamespaceAlias(final String namespaceURI, final String namespaceAlias) {
    this.namespaceAliasMap.put(namespaceURI, namespaceAlias);
  }
  
  protected String namespaceToAlias(final String namespaceURI) {
    final String namespaceAlias = this.namespaceAliasMap.getOrDefault(namespaceURI, null);
    if ((namespaceAlias == null)) {
      String _singleQuote = StringExtensions.singleQuote(namespaceURI);
      String _plus = ("Unknown namespace " + _singleQuote);
      String _plus_1 = (_plus + ".");
      XmlSchemaWriter.LOGGER.error(_plus_1);
      return "?";
    }
    return namespaceAlias;
  }
  
  public String namespaceToPrefix(final String namespaceURI) {
    String _xifexpression = null;
    boolean _equals = Objects.equal(this.defaultNamespace, namespaceURI);
    if (_equals) {
      _xifexpression = "";
    } else {
      String _namespaceToAlias = this.namespaceToAlias(namespaceURI);
      _xifexpression = (_namespaceToAlias + ":");
    }
    return _xifexpression;
  }
  
  public String xsd() {
    return this.namespaceToPrefix(Namespaces.XML_SCHEMA_NAMESPACE);
  }
  
  public String wsdl() {
    return this.namespaceToPrefix(Namespaces.WSDL_NAMESPACE);
  }
  
  public String http() {
    return this.namespaceToPrefix(Namespaces.HTTP_AWS_SCHEMA_NAMESPACE);
  }
  
  public boolean topLevel() {
    return this.path.isEmpty();
  }
  
  public Deque<XmlSchemaAnnotated> getPath() {
    return this.path;
  }
  
  /**
   * XmlSchemaObject dispatcher
   */
  protected CharSequence _writeSchema(final XmlSchemaGroup group) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<!-- XmlSchemaGroup: ");
    Class<? extends XmlSchemaGroup> _class = group.getClass();
    _builder.append(_class);
    _builder.append(" -->");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaGroupRef groupRef) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<!-- XmlSchemaGroupRef: ");
    Class<? extends XmlSchemaGroupRef> _class = groupRef.getClass();
    _builder.append(_class);
    _builder.append(" -->");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaElement element) {
    StringConcatenation _builder = new StringConcatenation();
    final String elementName = StringExtensions.legal(StringExtensions.altFieldPrefix(element.getName()));
    _builder.newLineIfNotEmpty();
    {
      QName _schemaTypeName = element.getSchemaTypeName();
      boolean _tripleEquals = (_schemaTypeName == null);
      if (_tripleEquals) {
        _builder.append("<");
        String _xsd = this.xsd();
        _builder.append(_xsd);
        _builder.append("element name=\"");
        _builder.append(elementName);
        _builder.append("\"");
        CharSequence _displayMinBounds = this.displayMinBounds(element);
        _builder.append(_displayMinBounds);
        CharSequence _displayMaxBounds = this.displayMaxBounds(element);
        _builder.append(_displayMaxBounds);
        CharSequence _displayDefaultValue = this.displayDefaultValue(element);
        _builder.append(_displayDefaultValue);
        _builder.append(">");
        _builder.newLineIfNotEmpty();
        {
          XmlSchemaAnnotation _annotation = element.getAnnotation();
          boolean _tripleNotEquals = (_annotation != null);
          if (_tripleNotEquals) {
            _builder.append("  ");
            Object _xblockexpression = null;
            {
              this.path.push(element);
              _xblockexpression = null;
            }
            _builder.append(_xblockexpression, "  ");
            _builder.newLineIfNotEmpty();
            _builder.append("  ");
            CharSequence _writeSchema = this.writeSchema(element.getAnnotation());
            _builder.append(_writeSchema, "  ");
            _builder.newLineIfNotEmpty();
            _builder.append("  ");
            Object _xblockexpression_1 = null;
            {
              this.path.pop();
              _xblockexpression_1 = null;
            }
            _builder.append(_xblockexpression_1, "  ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("  ");
        Object _xblockexpression_2 = null;
        {
          this.path.push(element);
          _xblockexpression_2 = null;
        }
        _builder.append(_xblockexpression_2, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        CharSequence _writeSchema_1 = this.writeSchema(element.getSchemaType());
        _builder.append(_writeSchema_1, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        Object _xblockexpression_3 = null;
        {
          this.path.pop();
          _xblockexpression_3 = null;
        }
        _builder.append(_xblockexpression_3, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("</");
        String _xsd_1 = this.xsd();
        _builder.append(_xsd_1);
        _builder.append("element>");
        _builder.newLineIfNotEmpty();
      } else {
        {
          XmlSchemaAnnotation _annotation_1 = element.getAnnotation();
          boolean _tripleNotEquals_1 = (_annotation_1 != null);
          if (_tripleNotEquals_1) {
            _builder.append("<");
            String _xsd_2 = this.xsd();
            _builder.append(_xsd_2);
            _builder.append("element name=\"");
            _builder.append(elementName);
            _builder.append("\" type=\"");
            String _typeReference = this.toTypeReference(element.getSchemaTypeName());
            _builder.append(_typeReference);
            _builder.append("\"");
            CharSequence _displayMinBounds_1 = this.displayMinBounds(element);
            _builder.append(_displayMinBounds_1);
            CharSequence _displayMaxBounds_1 = this.displayMaxBounds(element);
            _builder.append(_displayMaxBounds_1);
            CharSequence _displayDefaultValue_1 = this.displayDefaultValue(element);
            _builder.append(_displayDefaultValue_1);
            _builder.append(">");
            _builder.newLineIfNotEmpty();
            _builder.append("  ");
            Object _xblockexpression_4 = null;
            {
              this.path.push(element);
              _xblockexpression_4 = null;
            }
            _builder.append(_xblockexpression_4, "  ");
            _builder.newLineIfNotEmpty();
            _builder.append("  ");
            CharSequence _writeSchema_2 = this.writeSchema(element.getAnnotation());
            _builder.append(_writeSchema_2, "  ");
            _builder.newLineIfNotEmpty();
            _builder.append("  ");
            Object _xblockexpression_5 = null;
            {
              this.path.pop();
              _xblockexpression_5 = null;
            }
            _builder.append(_xblockexpression_5, "  ");
            _builder.newLineIfNotEmpty();
            _builder.append("</");
            String _xsd_3 = this.xsd();
            _builder.append(_xsd_3);
            _builder.append("element>");
            _builder.newLineIfNotEmpty();
          } else {
            _builder.append("<");
            String _xsd_4 = this.xsd();
            _builder.append(_xsd_4);
            _builder.append("element name=\"");
            _builder.append(elementName);
            _builder.append("\" type=\"");
            String _typeReference_1 = this.toTypeReference(element.getSchemaTypeName());
            _builder.append(_typeReference_1);
            _builder.append("\"");
            CharSequence _displayMinBounds_2 = this.displayMinBounds(element);
            _builder.append(_displayMinBounds_2);
            CharSequence _displayMaxBounds_2 = this.displayMaxBounds(element);
            _builder.append(_displayMaxBounds_2);
            CharSequence _displayDefaultValue_2 = this.displayDefaultValue(element);
            _builder.append(_displayDefaultValue_2);
            _builder.append("/>");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaSequence sequence) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("sequence>");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    Object _xblockexpression = null;
    {
      this.path.push(sequence);
      _xblockexpression = null;
    }
    _builder.append(_xblockexpression, "  ");
    _builder.newLineIfNotEmpty();
    {
      List<XmlSchemaSequenceMember> _items = sequence.getItems();
      for(final XmlSchemaSequenceMember item : ((Collection<XmlSchemaSequenceMember>) _items)) {
        _builder.append("  ");
        CharSequence _writeSchema = this.writeSchema(((XmlSchemaAnnotated) item));
        _builder.append(_writeSchema, "  ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    Object _xblockexpression_1 = null;
    {
      this.path.pop();
      _xblockexpression_1 = null;
    }
    _builder.append(_xblockexpression_1, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("</");
    String _xsd_1 = this.xsd();
    _builder.append(_xsd_1);
    _builder.append("sequence>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaChoice choice) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("choice>");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    Object _xblockexpression = null;
    {
      this.path.push(choice);
      _xblockexpression = null;
    }
    _builder.append(_xblockexpression, "  ");
    _builder.newLineIfNotEmpty();
    {
      List<XmlSchemaChoiceMember> _items = choice.getItems();
      for(final XmlSchemaChoiceMember item : ((Collection<XmlSchemaChoiceMember>) _items)) {
        _builder.append("  ");
        CharSequence _writeSchema = this.writeSchema(((XmlSchemaAnnotated) item));
        _builder.append(_writeSchema, "  ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    Object _xblockexpression_1 = null;
    {
      this.path.pop();
      _xblockexpression_1 = null;
    }
    _builder.append(_xblockexpression_1, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("</");
    String _xsd_1 = this.xsd();
    _builder.append(_xsd_1);
    _builder.append("choice>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaAll all) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<!-- XmlSchemaAll: ");
    Class<? extends XmlSchemaAll> _class = all.getClass();
    _builder.append(_class);
    _builder.append(" -->");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaAny any) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<!-- Skip <");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("any> elements for now -->");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaArrayType array) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("complexType ");
    {
      boolean _pLevel = this.topLevel();
      if (_pLevel) {
        _builder.append("name=\"");
        String _legal = StringExtensions.legal(array.getName());
        _builder.append(_legal);
        _builder.append("\"");
      }
    }
    _builder.append(">");
    _builder.newLineIfNotEmpty();
    {
      XmlSchemaAnnotation _annotation = array.getAnnotation();
      boolean _tripleNotEquals = (_annotation != null);
      if (_tripleNotEquals) {
        _builder.append("  ");
        Object _xblockexpression = null;
        {
          this.path.push(array);
          _xblockexpression = null;
        }
        _builder.append(_xblockexpression, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        CharSequence _writeSchema = this.writeSchema(array.getAnnotation());
        _builder.append(_writeSchema, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        Object _xblockexpression_1 = null;
        {
          this.path.pop();
          _xblockexpression_1 = null;
        }
        _builder.append(_xblockexpression_1, "  ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("<");
    String _xsd_1 = this.xsd();
    _builder.append(_xsd_1, "  ");
    _builder.append("sequence>");
    _builder.newLineIfNotEmpty();
    {
      QName _qName = array.getItemType().getQName();
      boolean _tripleNotEquals_1 = (_qName != null);
      if (_tripleNotEquals_1) {
        _builder.append("    ");
        _builder.append("<");
        String _xsd_2 = this.xsd();
        _builder.append(_xsd_2, "    ");
        _builder.append("element name=\"array\" type=\"");
        String _typeReference = this.toTypeReference(array.getItemType().getQName());
        _builder.append(_typeReference, "    ");
        _builder.append("\"");
        CharSequence _displayMinBounds = this.displayMinBounds(array);
        _builder.append(_displayMinBounds, "    ");
        CharSequence _displayMaxBounds = this.displayMaxBounds(array);
        _builder.append(_displayMaxBounds, "    ");
        _builder.append("/>");
        _builder.newLineIfNotEmpty();
      } else {
        _builder.append("    ");
        _builder.append("<");
        String _xsd_3 = this.xsd();
        _builder.append(_xsd_3, "    ");
        _builder.append("element name=\"array\"");
        CharSequence _displayMinBounds_1 = this.displayMinBounds(array);
        _builder.append(_displayMinBounds_1, "    ");
        CharSequence _displayMaxBounds_1 = this.displayMaxBounds(array);
        _builder.append(_displayMaxBounds_1, "    ");
        _builder.append("\">");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("  ");
        Object _xblockexpression_2 = null;
        {
          this.path.push(array);
          _xblockexpression_2 = null;
        }
        _builder.append(_xblockexpression_2, "      ");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("  ");
        CharSequence _writeSchema_1 = this.writeSchema(array.getItemType());
        _builder.append(_writeSchema_1, "      ");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("  ");
        Object _xblockexpression_3 = null;
        {
          this.path.pop();
          _xblockexpression_3 = null;
        }
        _builder.append(_xblockexpression_3, "      ");
        _builder.newLineIfNotEmpty();
        _builder.append("    ");
        _builder.append("</");
        String _xsd_4 = this.xsd();
        _builder.append(_xsd_4, "    ");
        _builder.append("element>");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("</");
    String _xsd_5 = this.xsd();
    _builder.append(_xsd_5, "  ");
    _builder.append("sequence>");
    _builder.newLineIfNotEmpty();
    _builder.append("</");
    String _xsd_6 = this.xsd();
    _builder.append(_xsd_6);
    _builder.append("complexType>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaAnnotation annotation) {
    StringConcatenation _builder = new StringConcatenation();
    final Function1<XmlSchemaAnnotationItem, Boolean> _function = (XmlSchemaAnnotationItem item) -> {
      return Boolean.valueOf((item instanceof XmlSchemaAppInfo));
    };
    final Iterable<XmlSchemaAnnotationItem> appInfoList = IterableExtensions.<XmlSchemaAnnotationItem>filter(annotation.getItems(), _function);
    _builder.newLineIfNotEmpty();
    {
      boolean _isEmpty = IterableExtensions.isEmpty(appInfoList);
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append("<");
        String _xsd = this.xsd();
        _builder.append(_xsd);
        _builder.append("annotation>");
        _builder.newLineIfNotEmpty();
        {
          for(final XmlSchemaAnnotationItem appInfo : appInfoList) {
            _builder.append("  ");
            _builder.append("<");
            String _xsd_1 = this.xsd();
            _builder.append(_xsd_1, "  ");
            _builder.append("appinfo");
            {
              String _sourceURI = appInfo.getSourceURI();
              boolean _tripleNotEquals = (_sourceURI != null);
              if (_tripleNotEquals) {
                _builder.append(" source=\"");
                String _sourceURI_1 = appInfo.getSourceURI();
                _builder.append(_sourceURI_1, "  ");
                _builder.append("\"");
              }
            }
            _builder.append(">");
            _builder.newLineIfNotEmpty();
            _builder.append("  ");
            _builder.append("  ");
            String _source = ((XmlSchemaAppInfo) appInfo).getSource();
            _builder.append(_source, "    ");
            _builder.newLineIfNotEmpty();
            _builder.append("  ");
            _builder.append("</");
            String _xsd_2 = this.xsd();
            _builder.append(_xsd_2, "  ");
            _builder.append("appinfo>");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("</");
        String _xsd_3 = this.xsd();
        _builder.append(_xsd_3);
        _builder.append("annotation>");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaComplexType complexType) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("complexType");
    {
      boolean _pLevel = this.topLevel();
      if (_pLevel) {
        _builder.append(" name=\"");
        String _legal = StringExtensions.legal(complexType.getName());
        _builder.append(_legal);
        _builder.append("\"");
      }
    }
    _builder.append(">");
    _builder.newLineIfNotEmpty();
    {
      XmlSchemaAnnotation _annotation = complexType.getAnnotation();
      boolean _tripleNotEquals = (_annotation != null);
      if (_tripleNotEquals) {
        _builder.append("  ");
        Object _xblockexpression = null;
        {
          this.path.push(complexType);
          _xblockexpression = null;
        }
        _builder.append(_xblockexpression, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        CharSequence _writeSchema = this.writeSchema(complexType.getAnnotation());
        _builder.append(_writeSchema, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        Object _xblockexpression_1 = null;
        {
          this.path.pop();
          _xblockexpression_1 = null;
        }
        _builder.append(_xblockexpression_1, "  ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    Object _xblockexpression_2 = null;
    {
      this.path.push(complexType);
      _xblockexpression_2 = null;
    }
    _builder.append(_xblockexpression_2, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    CharSequence _writeSchema_1 = this.writeSchema(complexType.getParticle());
    _builder.append(_writeSchema_1, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    Object _xblockexpression_3 = null;
    {
      this.path.pop();
      _xblockexpression_3 = null;
    }
    _builder.append(_xblockexpression_3, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("</");
    String _xsd_1 = this.xsd();
    _builder.append(_xsd_1);
    _builder.append("complexType>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaSimpleType simpleType) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("simpleType");
    {
      boolean _pLevel = this.topLevel();
      if (_pLevel) {
        _builder.append(" name=\"");
        String _legal = StringExtensions.legal(simpleType.getName());
        _builder.append(_legal);
        _builder.append("\"");
      }
    }
    _builder.append(">");
    _builder.newLineIfNotEmpty();
    {
      XmlSchemaAnnotation _annotation = simpleType.getAnnotation();
      boolean _tripleNotEquals = (_annotation != null);
      if (_tripleNotEquals) {
        _builder.append("  ");
        Object _xblockexpression = null;
        {
          this.path.push(simpleType);
          _xblockexpression = null;
        }
        _builder.append(_xblockexpression, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        CharSequence _writeSchema = this.writeSchema(simpleType.getAnnotation());
        _builder.append(_writeSchema, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        Object _xblockexpression_1 = null;
        {
          this.path.pop();
          _xblockexpression_1 = null;
        }
        _builder.append(_xblockexpression_1, "  ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    Object _xblockexpression_2 = null;
    {
      this.path.push(simpleType);
      _xblockexpression_2 = null;
    }
    _builder.append(_xblockexpression_2, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    CharSequence _xifexpression = null;
    XmlSchemaSimpleTypeContent _content = simpleType.getContent();
    boolean _tripleNotEquals_1 = (_content != null);
    if (_tripleNotEquals_1) {
      _xifexpression = this.writeSchema(simpleType.getContent());
    }
    _builder.append(_xifexpression, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    Object _xblockexpression_3 = null;
    {
      this.path.pop();
      _xblockexpression_3 = null;
    }
    _builder.append(_xblockexpression_3, "  ");
    _builder.newLineIfNotEmpty();
    _builder.append("</");
    String _xsd_1 = this.xsd();
    _builder.append(_xsd_1);
    _builder.append("simpleType>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaSimpleTypeList list) {
    StringConcatenation _builder = new StringConcatenation();
    {
      XmlSchemaSimpleType _itemType = list.getItemType();
      boolean _tripleNotEquals = (_itemType != null);
      if (_tripleNotEquals) {
        _builder.append("<");
        String _xsd = this.xsd();
        _builder.append(_xsd);
        _builder.append("list itemtype=\"");
        String _typeReference = this.toTypeReference(list.getItemType().getQName());
        _builder.append(_typeReference);
        _builder.append("\"/>");
        _builder.newLineIfNotEmpty();
      } else {
        _builder.append("<");
        String _xsd_1 = this.xsd();
        _builder.append(_xsd_1);
        _builder.append("list>");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        Object _xblockexpression = null;
        {
          this.path.push(list);
          _xblockexpression = null;
        }
        _builder.append(_xblockexpression, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        CharSequence _writeSchema = this.writeSchema(list.getItemType());
        _builder.append(_writeSchema, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        Object _xblockexpression_1 = null;
        {
          this.path.pop();
          _xblockexpression_1 = null;
        }
        _builder.append(_xblockexpression_1, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("</");
        String _xsd_2 = this.xsd();
        _builder.append(_xsd_2);
        _builder.append(">");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaSimpleTypeUnion union) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<!-- XmlSchemaSimpleTypeUnion: ");
    Class<? extends XmlSchemaSimpleTypeUnion> _class = union.getClass();
    _builder.append(_class);
    _builder.append(" -->");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaSimpleTypeRestriction restriction) {
    StringConcatenation _builder = new StringConcatenation();
    {
      if (((restriction.getFacets() != null) && (!restriction.getFacets().isEmpty()))) {
        {
          XmlSchemaSimpleType _baseType = restriction.getBaseType();
          boolean _tripleNotEquals = (_baseType != null);
          if (_tripleNotEquals) {
            _builder.append("<");
            String _xsd = this.xsd();
            _builder.append(_xsd);
            _builder.append("restriction base=\"");
            String _typeReference = this.toTypeReference(restriction.getBaseType());
            _builder.append(_typeReference);
            _builder.append("\">");
            _builder.newLineIfNotEmpty();
          } else {
            _builder.append("<");
            String _xsd_1 = this.xsd();
            _builder.append(_xsd_1);
            _builder.append("restriction base=\"");
            String _typeReference_1 = this.toTypeReference(restriction.getBaseTypeName());
            _builder.append(_typeReference_1);
            _builder.append("\">");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("  ");
        Object _xblockexpression = null;
        {
          this.path.push(restriction);
          _xblockexpression = null;
        }
        _builder.append(_xblockexpression, "  ");
        _builder.newLineIfNotEmpty();
        {
          List<XmlSchemaFacet> _facets = restriction.getFacets();
          for(final XmlSchemaFacet facet : ((Collection<XmlSchemaFacet>) _facets)) {
            _builder.append("  ");
            CharSequence _writeSchema = this.writeSchema(facet);
            _builder.append(_writeSchema, "  ");
            _builder.newLineIfNotEmpty();
          }
        }
        _builder.append("  ");
        Object _xblockexpression_1 = null;
        {
          this.path.pop();
          _xblockexpression_1 = null;
        }
        _builder.append(_xblockexpression_1, "  ");
        _builder.newLineIfNotEmpty();
        _builder.append("</");
        String _xsd_2 = this.xsd();
        _builder.append(_xsd_2);
        _builder.append("restriction>");
        _builder.newLineIfNotEmpty();
      } else {
        {
          XmlSchemaSimpleType _baseType_1 = restriction.getBaseType();
          boolean _tripleNotEquals_1 = (_baseType_1 != null);
          if (_tripleNotEquals_1) {
            _builder.append("<");
            String _xsd_3 = this.xsd();
            _builder.append(_xsd_3);
            _builder.append("restriction base=\"");
            String _typeReference_2 = this.toTypeReference(restriction.getBaseType());
            _builder.append(_typeReference_2);
            _builder.append("\"/>");
            _builder.newLineIfNotEmpty();
          } else {
            _builder.append("<");
            String _xsd_4 = this.xsd();
            _builder.append(_xsd_4);
            _builder.append("restriction base=\"");
            String _typeReference_3 = this.toTypeReference(restriction.getBaseTypeName());
            _builder.append(_typeReference_3);
            _builder.append("\"/>");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaEnumerationFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("enumeration value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaFractionDigitsFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("fractionDigits value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaLengthFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("length value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaMaxExclusiveFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("maxExclusive value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaMaxInclusiveFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("maxInclusive value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaMaxLengthFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("maxLength value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaMinExclusiveFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("minExclusive value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaMinInclusiveFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("minInclusive value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaMinLengthFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("minLength value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaPatternFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("pattern value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaTotalDigitsFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("totalDigits value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  protected CharSequence _writeSchema(final XmlSchemaWhiteSpaceFacet facet) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<");
    String _xsd = this.xsd();
    _builder.append(_xsd);
    _builder.append("whiteSpace value=\"");
    Object _value = facet.getValue();
    _builder.append(_value);
    _builder.append("\"/>");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  private String toTypeReference(final QName qname) {
    String _namespaceToPrefix = this.namespaceToPrefix(qname.getNamespaceURI());
    String _legal = StringExtensions.legal(qname.getLocalPart());
    return (_namespaceToPrefix + _legal);
  }
  
  private String toTypeReference(final XmlSchemaSimpleType simpleType) {
    return this.toTypeReference(simpleType.getQName());
  }
  
  private CharSequence displayMinBounds(final XmlSchemaParticle particle) {
    CharSequence _xifexpression = null;
    long _minOccurs = particle.getMinOccurs();
    boolean _equals = (_minOccurs == 1);
    if (_equals) {
      StringConcatenation _builder = new StringConcatenation();
      _xifexpression = _builder;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append(" ");
      _builder_1.append("minOccurs=\"");
      long _minOccurs_1 = particle.getMinOccurs();
      _builder_1.append(_minOccurs_1, " ");
      _builder_1.append("\"");
      _xifexpression = _builder_1;
    }
    return _xifexpression;
  }
  
  private CharSequence displayMaxBounds(final XmlSchemaParticle particle) {
    CharSequence _xifexpression = null;
    long _maxOccurs = particle.getMaxOccurs();
    boolean _equals = (_maxOccurs == 1);
    if (_equals) {
      StringConcatenation _builder = new StringConcatenation();
      _xifexpression = _builder;
    } else {
      CharSequence _xifexpression_1 = null;
      long _maxOccurs_1 = particle.getMaxOccurs();
      boolean _equals_1 = (_maxOccurs_1 == Long.MAX_VALUE);
      if (_equals_1) {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append(" ");
        _builder_1.append("maxOccurs=\"unbounded\"");
        _xifexpression_1 = _builder_1;
      } else {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append(" ");
        _builder_2.append("maxOccurs=\"");
        long _maxOccurs_2 = particle.getMaxOccurs();
        _builder_2.append(_maxOccurs_2, " ");
        _builder_2.append("\"");
        _xifexpression_1 = _builder_2;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }
  
  private CharSequence displayMinBounds(final XmlSchemaArrayType arrayType) {
    CharSequence _xifexpression = null;
    long _minOccurs = arrayType.getMinOccurs();
    boolean _equals = (_minOccurs == 1);
    if (_equals) {
      StringConcatenation _builder = new StringConcatenation();
      _xifexpression = _builder;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append(" ");
      _builder_1.append("minOccurs=\"");
      long _minOccurs_1 = arrayType.getMinOccurs();
      _builder_1.append(_minOccurs_1, " ");
      _builder_1.append("\"");
      _xifexpression = _builder_1;
    }
    return _xifexpression;
  }
  
  private CharSequence displayMaxBounds(final XmlSchemaArrayType arrayType) {
    CharSequence _xifexpression = null;
    long _maxOccurs = arrayType.getMaxOccurs();
    boolean _equals = (_maxOccurs == 1);
    if (_equals) {
      StringConcatenation _builder = new StringConcatenation();
      _xifexpression = _builder;
    } else {
      CharSequence _xifexpression_1 = null;
      long _maxOccurs_1 = arrayType.getMaxOccurs();
      boolean _equals_1 = (_maxOccurs_1 == Long.MAX_VALUE);
      if (_equals_1) {
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append(" ");
        _builder_1.append("maxOccurs=\"unbounded\"");
        _xifexpression_1 = _builder_1;
      } else {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append(" ");
        _builder_2.append("maxOccurs=\"");
        long _maxOccurs_2 = arrayType.getMaxOccurs();
        _builder_2.append(_maxOccurs_2, " ");
        _builder_2.append("\"");
        _xifexpression_1 = _builder_2;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }
  
  private CharSequence displayDefaultValue(final XmlSchemaElement element) {
    CharSequence _xifexpression = null;
    String _defaultValue = element.getDefaultValue();
    boolean _tripleEquals = (_defaultValue == null);
    if (_tripleEquals) {
      StringConcatenation _builder = new StringConcatenation();
      _xifexpression = _builder;
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      _builder_1.append(" ");
      _builder_1.append("default=\"");
      String _defaultValue_1 = element.getDefaultValue();
      _builder_1.append(_defaultValue_1, " ");
      _builder_1.append("\"");
      _xifexpression = _builder_1;
    }
    return _xifexpression;
  }
  
  public CharSequence writeSchema(final XmlSchemaObject all) {
    if (all instanceof XmlSchemaAll) {
      return _writeSchema((XmlSchemaAll)all);
    } else if (all instanceof XmlSchemaChoice) {
      return _writeSchema((XmlSchemaChoice)all);
    } else if (all instanceof XmlSchemaFractionDigitsFacet) {
      return _writeSchema((XmlSchemaFractionDigitsFacet)all);
    } else if (all instanceof XmlSchemaLengthFacet) {
      return _writeSchema((XmlSchemaLengthFacet)all);
    } else if (all instanceof XmlSchemaMaxLengthFacet) {
      return _writeSchema((XmlSchemaMaxLengthFacet)all);
    } else if (all instanceof XmlSchemaMinLengthFacet) {
      return _writeSchema((XmlSchemaMinLengthFacet)all);
    } else if (all instanceof XmlSchemaSequence) {
      return _writeSchema((XmlSchemaSequence)all);
    } else if (all instanceof XmlSchemaTotalDigitsFacet) {
      return _writeSchema((XmlSchemaTotalDigitsFacet)all);
    } else if (all instanceof XmlSchemaAny) {
      return _writeSchema((XmlSchemaAny)all);
    } else if (all instanceof XmlSchemaArrayType) {
      return _writeSchema((XmlSchemaArrayType)all);
    } else if (all instanceof XmlSchemaComplexType) {
      return _writeSchema((XmlSchemaComplexType)all);
    } else if (all instanceof XmlSchemaElement) {
      return _writeSchema((XmlSchemaElement)all);
    } else if (all instanceof XmlSchemaEnumerationFacet) {
      return _writeSchema((XmlSchemaEnumerationFacet)all);
    } else if (all instanceof XmlSchemaGroupRef) {
      return _writeSchema((XmlSchemaGroupRef)all);
    } else if (all instanceof XmlSchemaMaxExclusiveFacet) {
      return _writeSchema((XmlSchemaMaxExclusiveFacet)all);
    } else if (all instanceof XmlSchemaMaxInclusiveFacet) {
      return _writeSchema((XmlSchemaMaxInclusiveFacet)all);
    } else if (all instanceof XmlSchemaMinExclusiveFacet) {
      return _writeSchema((XmlSchemaMinExclusiveFacet)all);
    } else if (all instanceof XmlSchemaMinInclusiveFacet) {
      return _writeSchema((XmlSchemaMinInclusiveFacet)all);
    } else if (all instanceof XmlSchemaPatternFacet) {
      return _writeSchema((XmlSchemaPatternFacet)all);
    } else if (all instanceof XmlSchemaSimpleType) {
      return _writeSchema((XmlSchemaSimpleType)all);
    } else if (all instanceof XmlSchemaSimpleTypeList) {
      return _writeSchema((XmlSchemaSimpleTypeList)all);
    } else if (all instanceof XmlSchemaSimpleTypeRestriction) {
      return _writeSchema((XmlSchemaSimpleTypeRestriction)all);
    } else if (all instanceof XmlSchemaSimpleTypeUnion) {
      return _writeSchema((XmlSchemaSimpleTypeUnion)all);
    } else if (all instanceof XmlSchemaWhiteSpaceFacet) {
      return _writeSchema((XmlSchemaWhiteSpaceFacet)all);
    } else if (all instanceof XmlSchemaGroup) {
      return _writeSchema((XmlSchemaGroup)all);
    } else if (all instanceof XmlSchemaAnnotation) {
      return _writeSchema((XmlSchemaAnnotation)all);
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(all).toString());
    }
  }
}
