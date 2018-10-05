package com.langtoun.oastypes.visitor;

import static com.langtoun.oastypes.util.OpenApiTypeUtil.isComplexType;
import static com.langtoun.oastypes.util.OpenApiTypeUtil.isIntegerType;
import static com.langtoun.oastypes.util.OpenApiTypeUtil.isRequired;
import static com.langtoun.oastypes.util.OpenApiTypeUtil.schemaNumericTypeName;
import static com.langtoun.oastypes.util.OverlayUtil.getReference;
import static com.langtoun.oastypes.util.ReferenceUtil.namespaceAlias;
import static com.langtoun.oastypes.util.ReferenceUtil.typeName;
import static com.langtoun.oastypes.util.StringExtensions.singleQuote;
import static com.langtoun.oastypes.visitor.SchemaVisitorContext.newContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaArrayType;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaFacet;
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaMaxExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMinExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSequenceMember;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.constants.Constants;
import org.eclipse.xtext.xbase.lib.Pair;

import com.langtoun.oastypes.OASArrayType;
import com.langtoun.oastypes.OASBooleanType;
import com.langtoun.oastypes.OASDateTimeType;
import com.langtoun.oastypes.OASDateType;
import com.langtoun.oastypes.OASIntegerType;
import com.langtoun.oastypes.OASNullType;
import com.langtoun.oastypes.OASNumberType;
import com.langtoun.oastypes.OASNumericType;
import com.langtoun.oastypes.OASObjectType;
import com.langtoun.oastypes.OASStringType;
import com.langtoun.oastypes.OASType;
import com.langtoun.oastypes.util.OASTypeEquality;
import com.langtoun.oastypes.util.OASTypeFactory;
import com.langtoun.oastypes.util.ReferenceUtil;
import com.langtoun.oastypes.util.TraceLogger;
import com.reprezen.kaizen.oasparser.model3.OpenApi3;
import com.reprezen.kaizen.oasparser.model3.Schema;

public final class OASTypeToXmlSchemaVisitor implements OASTypeDispatcher<XmlSchemaType, SchemaVisitorContext>, OASTypeVisitor<XmlSchemaType, SchemaVisitorContext> {

  private static final Logger LOGGER = Logger.getLogger(OASTypeToXmlSchemaVisitor.class);

  public static final long UNBOUNDED = Long.MAX_VALUE;

  final OpenApi3 model;
  private final String targetNamespace;

  private final XmlSchemaCollection collection;
  private final Map<String, XmlSchema> schemaMap;
  private final Map<String, Map<String, String>> namespaceAliasMap;

  private final Map<Schema, OASType> schemaToOasTypeMap;
  private final Map<Schema, XmlSchemaType> schemaToXmlSchemaMap;
  private final Map<QName, Pair<OASType, XmlSchemaType>> visitedTypeMap;

  // for use with TRACE level debug
  private final TraceLogger tracer;

  /**
   * Static factory method that creates a new {@link OASTypeToXmlSchemaVisitor}. The
   * visitor processes the specified {@link OpenApi3} model in the target namespace.
   *
   * @param model
   *          The {@link OpenApi3} model to be processed.
   * @param targetNamespace
   *          The target namespace for the specified model.
   * @param caller
   *          The type of the calling class (used only for trace logging).
   * @return A new visitor.
   */
  public static OASTypeToXmlSchemaVisitor newVisitor(final OpenApi3 model, final String targetNamespace, final Class<?> caller) {
    return new OASTypeToXmlSchemaVisitor(model, targetNamespace, caller);
  }

  private OASTypeToXmlSchemaVisitor(final OpenApi3 model, final String targetNamespace, final Class<?> caller) {
    this.model = model;
    this.targetNamespace = targetNamespace;
    this.collection = new XmlSchemaCollection();
    this.schemaMap = new HashMap<>();
    this.namespaceAliasMap = new HashMap<>();
    this.schemaToOasTypeMap = new LinkedHashMap<>();
    this.schemaToXmlSchemaMap = new LinkedHashMap<>();
    this.visitedTypeMap = new LinkedHashMap<>();

    /*
     * To enable TRACE level debugging in the TypeToSchemaVisitor class, replace:
     *
     *   this.tracer = TraceLogger.newLogger(caller.getSimpleName(), false);
     *
     * with
     *
     *   this.tracer = TraceLogger.newLogger(caller.getSimpleName(), true);
     */
    this.tracer = TraceLogger.newLogger(caller.getSimpleName(), true);

    tracer.log("---- starting type compilation ----"); //$NON-NLS-1$

    initialiseTypeMap(model, targetNamespace, new HashSet<String>());
  }

  /*
   * ---- schema methods ----
   */

  /**
   * Method to create a new {@link XmlSchema} object to be used in creating XML schema
   * types and elements.
   *
   * @param targetNamespaceURI
   *          The target namespace URI.
   * @return An {@link XmlSchema} object.
   */
  private XmlSchema newSchema(final String targetNamespaceURI) {
    XmlSchema xmlSchema = schemaMap.getOrDefault(targetNamespaceURI, null);
    if (xmlSchema == null) {
      xmlSchema = new XmlSchema(targetNamespaceURI, "openapi-xsd", collection); //$NON-NLS-1$
      xmlSchema.setTargetNamespace(targetNamespaceURI);
      xmlSchema.setElementFormDefault(XmlSchemaForm.QUALIFIED);
    }
    return xmlSchema;
  }

  /**
   * Retrieve the {@link XmlSchema} object associated with the specified target
   * namespace URI.
   *
   * @param targetNamespaceURI
   *          The target namespace URI.
   * @return An {@link XmlSchema} object, or {@code null} is there is no mapping.
   */
  private XmlSchema getSchema(final String targetNamespaceURI) {
    XmlSchema xmlSchema = schemaMap.getOrDefault(targetNamespaceURI, null);
    if (xmlSchema == null) {
      xmlSchema = getSchema();
    }
    return xmlSchema;
  }

  /**
   * Retrieve the {@link XmlSchema} object associated with the visitor's default
   * target namespace.
   *
   * @return An {@link XmlSchema} object, or {@code null} is there is no mapping.
   */
  private XmlSchema getSchema() {
    return schemaMap.getOrDefault(targetNamespace, null);
  }

  /*
   * ---- namespace methods ----
   */

  /**
   * Return the target namespace associated with the model being processed.
   *
   * @return The target namespace.
   */
  public String getTargetNamespace() {
    return targetNamespace;
  }

  /**
   * Calculate the target namespace URI for an OpenAPI type definition.
   *
   * @param oasType
   *          The {@link OASType} for which to calculate the target namespace.
   * @param context
   *          The visitor context.
   * @return The target namespace URI.
   */
  private String calculateTargetNamespaceURI(final OASType oasType, final SchemaVisitorContext context) {
    if (oasType == null) {
      return "?unknown?";
    } else {
      final String namespaceAlias = namespaceAlias(oasType.reference());
      if (namespaceAlias == null) {
        tracer.log("-- calculateTargetNamespaceURI: createNamespace(context) ==> " + context.getTargetNamespace());
        return context.getTargetNamespace();
      } else {
        String namespaceURI = getNamespaceAliasEntry(context.getTargetNamespace(), namespaceAlias);
        if (namespaceURI == null) {
          namespaceURI = ReferenceUtil.namespaceURI(oasType.reference());
          addNamespaceAliasMapping(context.getTargetNamespace(), namespaceAlias, namespaceURI);
        }
        tracer.log(
          "-- calculateTargetNamespaceURI: lookupUsingAlias(tns=" + context.getTargetNamespace()
          + ", alias=" + namespaceAlias + ") ==> " + namespaceURI
        );
        return namespaceURI;
      }
    }
  }

  /**
   * Add a new map entry for an alias and the namespace of the library that it references. Mappings
   * are scoped according to the namespace of the model (default RAML / library) that is being
   * initialised.
   *
   * @param targetNamespace
   *          The namespace of the default OpenAPI or library whose types are being processed.
   * @param namespaceAlias
   *          The alias from the uses list that is used to reference a library.
   * @param namespaceURI
   *          The namespace URI derived from the referenced library.
   */
  private void addNamespaceAliasMapping(final String targetNamespace, final String namespaceAlias, final String namespaceURI) {
    tracer.log(
      "-- addNamespaceAliasMapping: tns=" + targetNamespace
      + " ==> (alias=" + namespaceAlias + " ==> ns=" + namespaceURI + ")"
    );
    Map<String, String> aliasMap = namespaceAliasMap.getOrDefault(targetNamespace, null);
    if (aliasMap == null) {
      aliasMap = new HashMap<>();
      namespaceAliasMap.put(targetNamespace, aliasMap);
    }
    aliasMap.put(namespaceAlias, namespaceURI);
  }

  /**
   * Lookup the namespace URI for an alias in the scope of a specified namespace.
   *
   * @param thisNamespace
   *          The scoping namespace.
   * @param namespaceAlias
   *          The alias whose corresponding namespace is required.
   * @return The namespace URI corresponding to the specified alias.
   */
  private String getNamespaceAliasEntry(final String thisNamespace, final String namespaceAlias) {
    Map<String, String> aliasMap = namespaceAliasMap.getOrDefault(thisNamespace, null);
    if (aliasMap != null) {
      return aliasMap.get(namespaceAlias);
    }
    return null;
  }

  /*
   * ---- visited type map methods ----
   */

  /**
   * Recursively initialise the type map. Libraries may be specified multiple times with
   * different aliases, and all aliases are mapped. The recursive processing of libraries
   * considers each one once in any scope, and skips it if it encounters it again during
   * the recursive initialisation process.
   *
   * @param model
   *          The model built from parsing the OpenAPI.
   * @param targetNamespaceURI
   *          The target namespace URI for the model being processed.
   * @param visitedModelSet
   *          The set of types that have been visited (to guard against infinite recursion).
   */
  private void initialiseTypeMap(final OpenApi3 model, final String targetNamespaceURI, Set<String> visitedModelSet) {
    if (!visitedModelSet.contains(targetNamespaceURI)) {
      tracer.log("<> initialiseTypeMap(tns=" + targetNamespaceURI + ")");

      schemaMap.put(targetNamespaceURI, newSchema(targetNamespaceURI));
      visitedModelSet.add(targetNamespaceURI);

      tracer.increaseIndent();

      final List<OASType> oasTypes = new ArrayList<>();

      final Map<String, Schema> schemas = model.getSchemas();
      schemas.entrySet().forEach(entry -> {
        final String schemaName = entry.getKey();
        final Schema schema = entry.getValue();
        final OASType oasType = OASTypeFactory.createOASType(schemaName, schema, getReference(model.getSchemas(), schemaName));
        addVisitedType(createQName(targetNamespaceURI, schemaName), oasType);
        oasTypes.add(oasType);
      });

      oasTypes.forEach(oasType -> {
        toTopLevelType(oasType, newContext(false, targetNamespaceURI));
      });
    } else {
      tracer.increaseIndent();
      tracer.log("-- already initialised: " + targetNamespaceURI);
      tracer.decreaseIndent();
    }
  }

  /**
   * Create a qualified name using a namespace URI and a type name.
   *
   * @param namespaceURI
   *          The URI of the namespace that contains the type.
   * @param localPart
   *          The name of the type.
   * @return The {@link QName} object.
   */
  private QName createQName(final String namespaceURI, final String localPart) {
    return new QName(namespaceURI, localPart != null ? localPart : "");
  }

  /**
   * Find the XML schema type associated with a {@link Schema} object.
   *
   * @param schema
   *          The {@link Schema} to use in the search.
   * @return The {@link XmlSchemaType} corresponding to schema, otherwise {@code null}.
   */
  public XmlSchemaType findXmlSchemaTypeBySchema(final Schema schema) {
    return schemaToXmlSchemaMap.getOrDefault(schema, null);
  }

  /**
   * Find an existing XML schema type using its qualified name. Entries consist of a pair containing
   * a {@link OASType} and an {@link XmlSchemaType} built from the type definition.
   *
   * @param qName
   *          The qualified name of the type being looked up.
   * @return Pair.of({@link OASType}, {@link XmlSchemaType}) corresponding to {@code qName},
   *         or {@code Pair.of(null, null)}.
   */
  private Pair<OASType, XmlSchemaType> lookupTypeByName(final QName qName) {
    Pair<OASType, XmlSchemaType> typeMapEntry = visitedTypeMap.getOrDefault(qName, null);
    if (typeMapEntry != null) {
      if (typeMapEntry.getValue() != null) {
        tracer.log(
          "-- lookupTypeByName: qn=" + qName + " ==> xs=" + typeMapEntry.getValue().getName()
          + " [qn=" + typeMapEntry.getValue().getQName() + "]"
        );
      } else {
        tracer.log("-- lookupTypeByName: qn=" + qName + " ==> " + "undefined schema type");
      }
      return typeMapEntry;
    }
    tracer.log("-- lookupTypeByName: qn=" + qName + " ==> no entry found -- visitor required");
    return Pair.of(null, null);
  }

  /**
   * Add an {@link OASType} to the type map as a guard against infinite recursion. All
   * top level types are added to the type map before the visiting process is invoked
   * to create the schema objects.
   *
   * @param qName
   *          The qualified name of the type entry being added.
   * @param oasType
   *          The {@link OASType} to be added to the type map.
   */
  private void addVisitedType(final QName qName, final OASType oasType) {
    tracer.log("-- addVisitedType: qn=" + qName + " ==> td=" + oasType.name() + ":" + oasType.type());
    tracer.log("--                 oas=" + oasType);
    if (!visitedTypeMap.containsKey(qName)) {
      schemaToOasTypeMap.put(oasType.schema(), oasType);
      schemaToXmlSchemaMap.put(oasType.schema(), null);
      visitedTypeMap.put(qName, Pair.of(oasType,  null));
    }
  }

  /**
   * Update the type map with the {@link XmlSchemaType} corresponding to a {@link OASType}.
   *
   * @param qName
   *          The qualified name of the type entry being updated.
   * @param schemaType
   *          The {@link XmlSchemaType} that will update an existing type map entry.
   * @return The updated type map entry.
   */
  private Pair<OASType, XmlSchemaType> updateVisitedType(final QName qName, final XmlSchemaType schemaType) {
    Pair<OASType, XmlSchemaType> typeMapEntry = visitedTypeMap.getOrDefault(qName, null);
    if (typeMapEntry != null) {
      tracer.log(
        "-- updateVisitedType: qn=" + qName + " ==> xs=" + schemaType.getName()
        + " [qn=" + schemaType.getQName() + "] -- " + schemaType
      );
      visitedTypeMap.put(qName, Pair.of(typeMapEntry.getKey(), schemaType));
      schemaToXmlSchemaMap.put(typeMapEntry.getKey().schema(), schemaType);
      return typeMapEntry;
    }
    return Pair.of(null, null);
  }

  /**
   * Transform an {@link OASType} into an XML schema type.
   *
   * @param oasType
   *          The {@link OASType} that is to be expressed as a top level XML schema type.
   * @param context
   *          The visitor context.
   * @return The {@link XmlSchemaType} created from the RAML type declaration.
   */
  public XmlSchemaType toTopLevelType(final OASType oasType, final SchemaVisitorContext context) {
    tracer.log(
      "<> toTopLevelType(td=" + oasType.name() + ":" + oasType.type()
      + ", tns=" + getTargetNamespace() + ")"
    );
    tracer.increaseIndent();
    tracer.log("-- schema=" + oasType.schema());
    tracer.log("-- oasType=" + oasType);
    context.pushTopLevel(true);
    final XmlSchemaType schemaType = visit(oasType, context);
    context.popTopLevel();
    tracer.decreaseIndent();
    return schemaType;
  }

  /**
   * Build an XML schema element from an OpenAPI type declaration.
   *
   * @param name
   *          The element name.
   * @param oasType
   *          The {@link OASType} that will provide the body of the element.
   * @param context
   *          The visitor context.
   * @param expand
   *          If {@code true} expand the element content.
   * @return The {@link XmlSchemaElement} built from the type declaration.
   */
  public XmlSchemaElement buildElement(final String name, final OASType oasType, final SchemaVisitorContext context, final boolean expand) {
    tracer.log(
      "<> buildElement(name=" + name
      + ", td=" + oasType.name() + ":" + oasType.type()
      + ", tns=" + context.getTargetNamespace()
      + ", top=" + context.isTopLevel() + ")"
    );
    tracer.increaseIndent();

    final String namespaceURI = calculateTargetNamespaceURI(oasType, context);

    final XmlSchemaElement schemaElement = new XmlSchemaElement(getSchema(namespaceURI), context.isTopLevel());

    schemaElement.setName(name);
//    schemaElement.setAnnotation(createRamlNameAnnotation(schemaElement.getAnnotation(), name, legal(altFieldPrefix(name))));
//    schemaElement.setAnnotation(createAuditMappingAnnotation(schemaElement.getAnnotation(), oasType));
//    schemaElement.setAnnotation(createClobTypeAnnotation(schemaElement.getAnnotation(), oasType));
//    schemaElement.setAnnotation(createGenericAnnotationType(schemaElement.getAnnotation(), oasType));

    String typeName = oasType.type();
    Pair<OASType, XmlSchemaType> typeMapEntry = lookupTypeByName(createQName(namespaceURI, typeName));
    XmlSchemaType referencedType = typeMapEntry.getValue();

    final boolean isComplex = isComplexType(oasType);
    boolean checkEquality = false;
    if (isComplex && !context.isVisitedType(oasType)) {
      context.addVisitedType(oasType);
      checkEquality = true;
    } else if (!isComplex) {
      checkEquality = true;
    }
    if (checkEquality) {
      if (!OASTypeEquality.equals(oasType, typeMapEntry.getKey())) {
        tracer.log("-- el: type equality check failed - visitor required");
        referencedType = null;
      } else {
        tracer.log("-- el: type equality check passed");
      }
    }

    if (referencedType == null || expand) {
      context.pushTopLevel(false);
//      tracer.increaseIndent();
      final XmlSchemaType schemaType = visit(oasType, context);
//      tracer.decreaseIndent();
      context.popTopLevel();
      if (schemaType != null) {
        schemaElement.setSchemaType(schemaType);
        if (schemaType instanceof XmlSchemaArrayType) {
          ((XmlSchemaArrayType) schemaType).updateElement(schemaElement);
        } else if (schemaType.getQName() != null && !expand) {
          schemaElement.setSchemaTypeName(schemaType.getQName());
        }
        if (schemaType.getQName() == null) {
          tracer.log("-- el:" + name + ": no QName for visited type: " + schemaType.getName());
        } else {
          tracer.log("-- el:" + name + ": visited type: qn=" + schemaType.getQName());
        }
      } else {
        LOGGER.error(String.format("Unable to create an XML schema type for %s", singleQuote(oasType.name())));
      }
    } else {
      schemaElement.setSchemaType(referencedType);
      schemaElement.setSchemaTypeName(referencedType.getQName());
      if (referencedType.getQName() == null) {
        tracer.log("-- el:" + name + ": no QName for referenced type: " + referencedType.getName());
      } else {
        tracer.log("-- el:" + name + ": referenced type: qn=" + referencedType.getQName());
      }
    }

    long minOccurs = 1;
    if (oasType instanceof OASArrayType && ((OASArrayType) oasType).minItems() != null) {
        minOccurs = ((OASArrayType) oasType).minItems();
    } else if (!isRequired(oasType)) {
      minOccurs = 0;
    }
    schemaElement.setMinOccurs(minOccurs);

    if (oasType.defaultValue() != null) {
      schemaElement.setDefaultValue(oasType.defaultValue().toString());
    }

    tracer.decreaseIndent();
    return schemaElement;
  }

  /*
   * ---- type visitor dispatcher ----
   */

  @Override
  public XmlSchemaType visit(final OASType oasType, SchemaVisitorContext context) {
    if (oasType instanceof OASObjectType) {
      return visitObject((OASObjectType) oasType, context);
    } else if (oasType instanceof OASArrayType) {
      return visitArray((OASArrayType) oasType, context);
    } else if (oasType instanceof OASStringType) {
      return visitString((OASStringType) oasType, context);
    } else if (oasType instanceof OASNumberType) {
      return visitNumber((OASNumberType) oasType, context);
    } else if (oasType instanceof OASIntegerType) {
      return visitInteger((OASIntegerType) oasType, context);
    } else if (oasType instanceof OASBooleanType) {
      return visitBoolean((OASBooleanType) oasType, context);
    } else if (oasType instanceof OASDateType) {
      return visitDate((OASDateType) oasType, context);
    } else if (oasType instanceof OASDateTimeType) {
      return visitDateTime((OASDateTimeType) oasType, context);
    } else if (oasType instanceof OASNullType) {
      return visitNull((OASNullType) oasType, context);
    }
    return null;
  }

  /*
   * ---- complex type support methods ----
   */

  /**
   * Calculate the type name for a complex type definition.
   *
   * @param oasType
   *          The type definition.
   * @return A {@code String} containing the calculated name.
   */
  private String complexOASTypeName(final OASType oasType) {
    String typeName = oasType.name();
    if (typeName == null || typeName.contains("/") || "object".equals(typeName)) {
      typeName = oasType.type();
    }
    return typeName;
  }

  /**
   * Using an enclosing namespace, a type name, and a type declaration, attempt to lookup an existing
   * type map entry that may or may not contain a constructed XML schema type object. If a type entry
   * is found then the specified type declaration is compared with the type declaration in the type map
   * entry to determine whether the type is a reference to a top level type or is a subclass that
   * extends a top level type. For type references, the XML schema type is returned from the type map
   * entry. For a subclass, the method creates a new {@link XmlSchemaComplexType} object. If there is no
   * type map entry then the type is not found and {@code null} is returned.
   *
   * @param namespaceURI
   *          The enclosing namespace for the complex type.
   * @param typeName
   *          The type name.
   * @param oasType
   *          The type declaration.
   * @param context
   *          The visitor context.
   * @return Pair.of({@link XmlSchemaComplexType}, {@link Boolean}), or Pair.of(null, false) if type is
   *         anonymous or type declarations are not equal.
   */
  private Pair<XmlSchemaComplexType, Boolean> prepareComplexType(final String namespaceURI, final String typeName, final OASType oasType, final SchemaVisitorContext context) {
    if (typeName != null) {
      final QName qName = createQName(namespaceURI, context.isTopLevel() ? typeName : oasType.type());
      Pair<OASType, XmlSchemaType> typeMapEntry = lookupTypeByName(qName);
      final XmlSchemaType schemaType = typeMapEntry.getValue();
      if (schemaType != null && OASTypeEquality.equals(oasType, typeMapEntry.getKey())) {
        tracer.log("-- prepare: existing complex type, type equality check passed: " + schemaType);
        if (schemaType instanceof XmlSchemaComplexType) {
          return Pair.of((XmlSchemaComplexType) schemaType, true);
        } else {
          LOGGER.error(String.format("Type %s is not appropriate for %s", schemaType.getClass().getName(), singleQuote(qName.toString())));
        }
      } else if (schemaType == null && OASTypeEquality.equals(oasType, typeMapEntry.getKey())) {
        final XmlSchemaComplexType complexType = new XmlSchemaComplexType(getSchema(namespaceURI), true);
        tracer.log("-- prepare: new complex type, type equality check passed: " + complexType);
        return Pair.of(complexType, false);
      }
      tracer.log("-- prepare: type equality check failed - visitor required");
    }
    tracer.log("-- prepare: undefined complex type");
    return Pair.of(null, false);
  }

  /**
   * Process any RAML name or generic annotations that have been added to a top level complex type.
   *
   * @param typeName
   *          the type name
   * @param oasType
   *          the type declaration
   * @param context
   *          the visitor context
   * @return an {@link XmlSchemaAnnotation} object containing the processed RAML annotations, or
   *         {@code null} if none are specified for the type declaration
   */
  private XmlSchemaAnnotation prepareComplexTypeAnnotation(final String typeName, final OASType oasType, final SchemaVisitorContext context) {
    XmlSchemaAnnotation xmlSchemaAnnotation = null;
    if (context.isTopLevel() && !"object".equals(oasType.name())) {
//      xmlSchemaAnnotation = createRamlNameAnnotation(xmlSchemaAnnotation, typeName, legal(altFieldPrefix(typeName)));
//      xmlSchemaAnnotation = createAuditMappingAnnotation(xmlSchemaAnnotation, oasType);
//      xmlSchemaAnnotation = createGenericAnnotationType(xmlSchemaAnnotation, oasType);
    }
    return xmlSchemaAnnotation;
  }

  /*
   * ---- complex type visitor methods ----
   */

  @Override
  public XmlSchemaType visitObject(final OASObjectType oasObjectType, final SchemaVisitorContext context) {
    tracer.log(
        "<> visitObject(td=" + oasObjectType.name() + ":" + oasObjectType.type() + ")"
      );
      tracer.increaseIndent();

      final String namespaceURI = calculateTargetNamespaceURI(oasObjectType, context);
      final String typeName = complexOASTypeName(oasObjectType);

      final Pair<XmlSchemaComplexType, Boolean> complexTypeContext = prepareComplexType(namespaceURI, typeName, oasObjectType, context);

      XmlSchemaComplexType complexType = complexTypeContext.getKey();
      final boolean isTypeReference = complexTypeContext.getValue();

      final XmlSchemaAnnotation xmlSchemaAnnotation = prepareComplexTypeAnnotation(typeName, oasObjectType, context);

      if (complexType != null && isTypeReference && xmlSchemaAnnotation == null) {
        tracer.decreaseIndent();
        return complexType;
      }

      if (complexType == null) {
        complexType = new XmlSchemaComplexType(getSchema(context.getTargetNamespace()), context.isTopLevel());
        tracer.log("-- new complex type created: " + complexType);
      }

      if (context.isTopLevel() && complexType.isTopLevel()) {
        complexType.setName(typeName);
      } else if (complexType.isTopLevel()) {
        complexType.setName(oasObjectType.type());
      }

      if (complexType.isTopLevel()) {
        complexType.setAnnotation(xmlSchemaAnnotation);
      }

      if (context.isTopLevel() && complexType.isTopLevel()) {
        updateVisitedType(createQName(context.getTargetNamespace(), typeName), complexType);
      } else if (complexType.isTopLevel()) {
        updateVisitedType(createQName(namespaceURI, oasObjectType.type()), complexType);
      }

      final XmlSchemaSequence xmlSchemaSequence = new XmlSchemaSequence();
      final List<XmlSchemaSequenceMember> items = xmlSchemaSequence.getItems();

      for (Entry<String, OASType> propertyEntry : oasObjectType.properties().entrySet()) {
//        XMLFacetInfo facetInfo = property.xml();
//        String name = facetInfo != null ? defaultTo(facetInfo.name(), property.name()) : property.name();
        final String propertyName = propertyEntry.getKey();
        final OASType propertyType = propertyEntry.getValue();
        context.pushTopLevel(false);
        final XmlSchemaElement schemaElement = buildElement(propertyName, propertyType, context, false);
        context.popTopLevel();
        if (!oasObjectType.isRequired(propertyName)) {
          schemaElement.setMinOccurs(0);
        }
        items.add(schemaElement);
      }

//      if (asBoolean(oasObjectType.additionalProperties(), true)) {
//        final XmlSchemaAny schemaAny = new XmlSchemaAny();
//        schemaAny.setMinOccurs(0);
//        schemaAny.setMaxOccurs(UNBOUNDED);
//        schemaAny.setProcessContent(XmlSchemaContentProcessing.SKIP);
//        items.add(schemaAny);
//      }

      complexType.setParticle(xmlSchemaSequence);

      tracer.decreaseIndent();
      return complexType;
  }

  /*
   * ---- array type support methods ----
   */

  /**
   * Calculate the type name for a array type definition.
   *
   * @param oasType
   *          the type definition
   * @return a {@code String} containing the calculated name
   */
  private String arrayOASTypeName(final OASType oasType) {
    String typeName = oasType.name();
    if (typeName == null || typeName.contains("/") || "object".equals(typeName)) {
      typeName = oasType.type();
    }
    return typeName;
  }

  /**
   * Using an enclosing namespace, a type name, and a type declaration, attempt to lookup an existing
   * type map entry that may or may not contain a constructed XML schema type object. If a type entry
   * is found then the specified type declaration is compared with the type declaration in the type map
   * entry to determine whether the type is a reference to a top level type or is a subclass that
   * extends a top level type. For type references, the XML schema type is returned from the type map
   * entry. For a subclass, the method creates a new {@link XmlSchemaArrayType} object. If there is no
   * type map entry then the type is anonymous and {@code null} is returned.
   *
   * @param namespaceURI
   *          the enclosing namespace for the array type
   * @param typeName
   *          the type name
   * @param oasType
   *          the type definition
   * @param context
   *          the visitor context
   * @return Pair.of({@link XmlSchemaArrayType}, {@link Boolean}), or Pair.of(null, false) if type is
   *         not found or type declarations are not equal
   */
  private Pair<XmlSchemaArrayType, Boolean> prepareArrayType(final String namespaceURI, final String typeName, final OASType oasType, final SchemaVisitorContext context) {
    if (typeName != null) {
      final QName qName = createQName(namespaceURI, context.isTopLevel() ? typeName : oasType.type());
      Pair<OASType, XmlSchemaType> typeMapEntry = lookupTypeByName(qName);
      final XmlSchemaType schemaType = typeMapEntry.getValue();
      if (schemaType != null && OASTypeEquality.equals(oasType, typeMapEntry.getKey())) {
        tracer.log("-- prepare: existing array type, type equality check passed: " + schemaType); //$NON-NLS-1$
        if (schemaType instanceof XmlSchemaArrayType) {
          return Pair.of((XmlSchemaArrayType) schemaType, true);
        } else {
          LOGGER.error(String.format("Type %s is not appropriate for %s", schemaType.getClass().getName(), singleQuote(qName.toString())));
        }
      } else if (schemaType == null && OASTypeEquality.equals(oasType, typeMapEntry.getKey())) {
        final XmlSchemaArrayType complexType = new XmlSchemaArrayType(getSchema(namespaceURI), true);
        tracer.log("-- prepare: new array type, type equality check passed: " + complexType);
        return Pair.of(complexType, false);
      }
      tracer.log("-- prepare: type equality check failed - visitor required");
    }
    tracer.log("-- prepare: undefined array type");
    return Pair.of(null, false);
  }

  /**
   * Process any OpenAPI name or generic annotations that have been added to a top level array type.
   *
   * @param oasType
   *          the type declaration
   * @param context
   *          the visitor context
   * @return an {@link XmlSchemaAnnotation} object containing the processed OpenAPI annotations, or
   *         {@code null} if none are specified for the type declaration
   */
  private XmlSchemaAnnotation prepareArrayTypeAnnotation(final OASType oasType, final SchemaVisitorContext context) {
    XmlSchemaAnnotation xmlSchemaAnnotation = null;
    if (context.isTopLevel() && !"object".equals(oasType.name())) { //$NON-NLS-1$
//      xmlSchemaAnnotation = createAuditMappingAnnotation(xmlSchemaAnnotation, oasType);
//      xmlSchemaAnnotation = createGenericAnnotationType(xmlSchemaAnnotation, oasType);
    }
    return xmlSchemaAnnotation;
  }

  /*
   * ---- array type visitor methods ----
   */

  /**
   * {@inheritDoc}
   */
  @Override
  public XmlSchemaType visitArray(final OASArrayType oasArrayType, final SchemaVisitorContext context) {
    final OASType itemType = oasArrayType.items();
    tracer.log(
      "<> visitArray(td=" + oasArrayType.name() + ":" + oasArrayType.type()
      + " [items=" + itemType.name() + ":" + itemType.type() + "])"
    );
    tracer.increaseIndent();

    final String namespaceURI = calculateTargetNamespaceURI(oasArrayType, context);
    final String typeName = arrayOASTypeName(oasArrayType);

    final Pair<XmlSchemaArrayType, Boolean> arrayTypeContext = prepareArrayType(namespaceURI, typeName, oasArrayType, context);

    XmlSchemaArrayType arrayType = arrayTypeContext.getKey();
    final boolean isTypeReference = arrayTypeContext.getValue();

    final XmlSchemaAnnotation xmlSchemaAnnotation = prepareArrayTypeAnnotation(oasArrayType, context);

    if (arrayType != null && isTypeReference && xmlSchemaAnnotation == null) {
      tracer.decreaseIndent();
      return arrayType;
    }

    if (arrayType == null) {
      arrayType = new XmlSchemaArrayType(getSchema(context.getTargetNamespace()), context.isTopLevel());
      tracer.log("-- new array type created: " + arrayType); //$NON-NLS-1$
    }

    if (context.isTopLevel() && arrayType.isTopLevel()) {
      arrayType.setName(typeName);
    } else if (arrayType.isTopLevel()) {
      arrayType.setName(oasArrayType.type());
    }

    if (arrayType.isTopLevel()) {
//      arrayType.setAnnotation(createGenericAnnotationType(arrayType.getAnnotation(), oasArrayType));
    }

    if (context.isTopLevel() && arrayType.isTopLevel()) {
      updateVisitedType(createQName(context.getTargetNamespace(), typeName), arrayType);
    } else if (arrayType.isTopLevel()) {
      updateVisitedType(createQName(namespaceURI, oasArrayType.type()), arrayType);
    }

    if (oasArrayType.minItems() != null) {
      arrayType.setMinOccurs(oasArrayType.minItems());
    } else {
      arrayType.setMinOccurs(0);
    }

    if (oasArrayType.maxItems() != null) {
      arrayType.setMaxOccurs(oasArrayType.maxItems());
    } else {
      arrayType.setMaxOccurs(UNBOUNDED);
    }

    context.pushTopLevel(!"array".equals(oasArrayType.type()) && !"object".equals(itemType.name()) && itemType.type() != null);
    arrayType.setItemType(visit(itemType, context));
    context.popTopLevel();

    tracer.decreaseIndent();
    return arrayType;
  }

  /*
   * ---- simple type support methods ----
   */

  /**
   * Calculate the type name for a simple type definition.
   *
   * @param oasType
   *          The type definition.
   * @param context
   *          The visitor context.
   * @return A {@code String} containing the calculated name.
   */
  private String simpleOASTypeName(final OASType oasType, final SchemaVisitorContext context) {
    if (oasType.reference() == null) {
      return (context.isTopLevel() || oasType.type() == null) ? oasType.name() : oasType.type();
    }
    return typeName(oasType.reference());
  }

  /**
   * Using an enclosing namespace, a type name, and a type definition, attempt to lookup an existing
   * type map entry that may or may not contain a constructed XML schema type object.
   *
   * @param namespaceURI
   *          The enclosing namespace for the type.
   * @param typeName
   *          The type name.
   * @param oasType
   *          The type declaration.
   * @param context
   *          The visitor context.
   * @return Pair.of({@link XmlSchemaType}, {@link Boolean}), or Pair.of(null, false) if type is
   *         anonymous or type definitions are not equal
   */
  private XmlSchemaType prepareSimpleType(final String namespaceURI, final String typeName, final OASType oasType, final SchemaVisitorContext context) {
    if (typeName != null) {
      Pair<OASType, XmlSchemaType> typeMapEntry = lookupTypeByName(createQName(namespaceURI, context.isTopLevel() ? oasType.type() : typeName));
      final XmlSchemaType schemaType = typeMapEntry.getValue();
      if (schemaType != null && OASTypeEquality.equals(oasType, typeMapEntry.getKey())) {
        return schemaType;
      }
    }
    return null;
  }

  /**
   * Process any OpenAPI name, CLOB type, or generic annotations that have been added to a top level type.
   *
   * @param typeName
   *          The type name.
   * @param oasType
   *          The type definition.
   * @param context
   *          The visitor context.
   * @return An {@link XmlSchemaAnnotation} object containing the processed OpenAPI annotations, or
   *         {@code null} if none are specified for the type definition.
   */
  private XmlSchemaAnnotation prepareSimpleTypeAnnotation(final String typeName, final OASType oasType, final SchemaVisitorContext context) {
    XmlSchemaAnnotation xmlSchemaAnnotation = null;
    if (context.isTopLevel()) {
//      xmlSchemaAnnotation = createOpenApiNameAnnotation(xmlSchemaAnnotation, typeName, legal(altFieldPrefix(typeName)));
//      xmlSchemaAnnotation = createAuditMappingAnnotation(xmlSchemaAnnotation, oasType);
//      xmlSchemaAnnotation = createClobTypeAnnotation(xmlSchemaAnnotation, oasType);
//      xmlSchemaAnnotation = createGenericAnnotationType(xmlSchemaAnnotation, oasType);
    }
    return xmlSchemaAnnotation;
  }

  /**
   * Prepare the type restriction that is to be added as simple type content.
   *
   * @param schemaType
   *          The simple type object.
   * @param qName
   *          The qualified name of the restriction base.
   * @param context
   *          The visitor context.
   * @return An {@link XmlSchemaSimpleTypeRestriction} object.
   */
  private XmlSchemaSimpleTypeRestriction prepareSimpleTypeRestriction(final XmlSchemaType schemaType, final QName qName, final SchemaVisitorContext context) {
    final XmlSchemaSimpleTypeRestriction content = new XmlSchemaSimpleTypeRestriction();
    if (context.isTopLevel() && schemaType != null) {
      content.setBaseType((XmlSchemaSimpleType) schemaType);
    } else {
      content.setBaseTypeName(qName);
    }
    return content;
  }

  private CompiledSimpleTypeInfo commonSimpleTypePreamble(final OASType oasType, final SchemaVisitorContext context, final String message) {
    tracer.log(message);
    tracer.increaseIndent();

    final String namespaceURI = calculateTargetNamespaceURI(oasType, context);
    final String typeName = simpleOASTypeName(oasType, context);

    return
      new CompiledSimpleTypeInfo.Builder(namespaceURI, typeName)
            .setSchemaType(prepareSimpleType(namespaceURI, typeName, oasType, context))
            .setAnnotation(prepareSimpleTypeAnnotation(typeName, oasType, context))
            .build();
  }

  /*
   * ---- simple type visitor methods ----
   */

  @Override
  public XmlSchemaType visitString(final OASStringType oasStringType, final SchemaVisitorContext context) {
    CompiledSimpleTypeInfo typeInfo = commonSimpleTypePreamble(
      oasStringType, context,
      "<> visitString(td=" + oasStringType.name() + ":" + oasStringType.type() + ")"
    );

    if (!context.isTopLevel() && typeInfo.getSchemaType() != null && typeInfo.getAnnotation() == null) {
      tracer.decreaseIndent();
      return typeInfo.getSchemaType();
    }

    final XmlSchemaSimpleTypeRestriction content = prepareSimpleTypeRestriction(typeInfo.getSchemaType(), Constants.XSD_STRING, context);

    final Integer minLength = oasStringType.minLength(); /* resolveStringMinLengthFacet(stringType); */
    if (minLength != null) {
      final XmlSchemaFacet minLengthFacet = new XmlSchemaMinLengthFacet();
      minLengthFacet.setValue(minLength);
      content.getFacets().add(minLengthFacet);
    }

    final Integer maxLength = oasStringType.maxLength(); /* resolveStringMaxLengthFacet(stringType); */
    if (maxLength != null) {
      final XmlSchemaFacet maxLengthFacet = new XmlSchemaMaxLengthFacet();
      maxLengthFacet.setValue(maxLength);
      content.getFacets().add(maxLengthFacet);
    }

    final String pattern = oasStringType.pattern(); /* resolveStringPatternFacet(stringType); */
    if (pattern != null) {
      final XmlSchemaFacet patternFacet = new XmlSchemaPatternFacet();
      patternFacet.setValue(pattern);
      content.getFacets().add(patternFacet);
    }

    final List<String> enums = oasStringType.enums(); /* resolveStringEnumFacet(oasStringType); */
    if (!enums.isEmpty()) {
      for (String anEnum : enums) {
        final XmlSchemaFacet enumFacet = new XmlSchemaEnumerationFacet();
        enumFacet.setValue(anEnum);
        content.getFacets().add(enumFacet);
      }
    }

    if (!context.isTopLevel() && typeInfo.getSchemaType() == null && typeInfo.getAnnotation() == null && content.getFacets().isEmpty()) {
      tracer.decreaseIndent();
      return collection.getTypeByQName(Constants.XSD_STRING);
    } else {
      final XmlSchemaSimpleType simpleType = new XmlSchemaSimpleType(getSchema(typeInfo.getNamespaceURI()), false);
      simpleType.setContent(content);
      if (context.isTopLevel()) {
        simpleType.setName(typeInfo.getTypeName());
        simpleType.setAnnotation(typeInfo.getAnnotation());
        updateVisitedType(createQName(typeInfo.getNamespaceURI(), typeInfo.getTypeName()), simpleType);
      }
      tracer.decreaseIndent();
      return simpleType;
    }
  }

  /**
   * Create a number schema type.
   *
   * @param oasNumericType
   *          An numeric type from the OpenAPI definition.
   * @param baseTypeName
   *          The base type name that will be used to construct a name for this type.
   * @param context
   *          The visitor context.
   * @return An {@link XmlSchemaType} built from the number schema type.
   */
  private XmlSchemaType createNumericSchemaType(final OASNumericType oasNumericType, final QName baseTypeName, final SchemaVisitorContext context) {
    CompiledSimpleTypeInfo typeInfo = commonSimpleTypePreamble(
      oasNumericType, context,
      "<> createNumericSchemaType(td=" + oasNumericType.name() + ":" + oasNumericType.type()
      + " base=" + baseTypeName + ")"
    );

    if (!context.isTopLevel() && typeInfo.getSchemaType() != null && typeInfo.getAnnotation() == null) {
      tracer.decreaseIndent();
      return typeInfo.getSchemaType();
    }

    final QName resolvedTypeQName = schemaNumericTypeName(oasNumericType.format(), baseTypeName);

    final XmlSchemaSimpleTypeRestriction content = prepareSimpleTypeRestriction(typeInfo.getSchemaType(), resolvedTypeQName, context);

    final Number minimum = oasNumericType.minimum(); /* resolveNumberMinimumFacet(oasNumericType); */
    if (minimum != null) {
      final XmlSchemaFacet minimumFacet = oasNumericType.isExclusiveMinimum() ? new XmlSchemaMinExclusiveFacet()
                                                                              : new XmlSchemaMinInclusiveFacet();
      if (isIntegerType(oasNumericType.format())) {
        minimumFacet.setValue(((OASIntegerType) oasNumericType).isLong() ? minimum.longValue() : minimum.intValue());
      } else {
        minimumFacet.setValue(((OASNumberType) oasNumericType).isDouble() ? minimum.doubleValue() : minimum.floatValue());
      }
      content.getFacets().add(minimumFacet);
    }

    final Number maximum = oasNumericType.maximum(); /* resolveNumberMaximumFacet(oasNumericType); */
    if (maximum != null) {
      final XmlSchemaFacet maximumFacet = oasNumericType.isExclusiveMaximum() ? new XmlSchemaMaxExclusiveFacet()
                                                                              : new XmlSchemaMaxInclusiveFacet();
      if (isIntegerType(oasNumericType.format())) {
        maximumFacet.setValue(((OASIntegerType) oasNumericType).isLong() ? Long.valueOf(maximum.longValue()) : Integer.valueOf(maximum.intValue()));
      } else {
        maximumFacet.setValue(((OASNumberType) oasNumericType).isDouble() ? Double.valueOf(maximum.doubleValue()) : Float.valueOf(maximum.floatValue()));
      }
      content.getFacets().add(maximumFacet);
    }

    final List<Number> enums = oasNumericType.enums(); /* resolveNumberEnumFacet(oasNumericType); */
    if (!enums.isEmpty()) {
      for (Number anEnum : enums) {
        final XmlSchemaFacet enumValue = new XmlSchemaEnumerationFacet();
        enumValue.setValue(anEnum);
        content.getFacets().add(enumValue);
      }
    }

    if (!context.isTopLevel() && typeInfo.getSchemaType() == null && typeInfo.getAnnotation() == null && content.getFacets().isEmpty()) {
      tracer.decreaseIndent();
      return collection.getTypeByQName(resolvedTypeQName);
    } else {
      final XmlSchemaSimpleType simpleType = new XmlSchemaSimpleType(getSchema(typeInfo.getNamespaceURI()), false);
      simpleType.setContent(content);
      if (context.isTopLevel()) {
        simpleType.setName(oasNumericType.name());
        simpleType.setAnnotation(typeInfo.getAnnotation());
        updateVisitedType(createQName(typeInfo.getNamespaceURI(), typeInfo.getTypeName()), simpleType);
      }
      tracer.decreaseIndent();
      return simpleType;
    }
  }

  @Override
  public XmlSchemaType visitNumber(final OASNumberType oasNumberType, final SchemaVisitorContext context) {
    return createNumericSchemaType(oasNumberType, oasNumberType.isDouble() ? Constants.XSD_DOUBLE : Constants.XSD_FLOAT, context);
  }

  @Override
  public XmlSchemaType visitInteger(final OASIntegerType oasIntegerType, final SchemaVisitorContext context) {
    return createNumericSchemaType(oasIntegerType, oasIntegerType.isLong() ? Constants.XSD_LONG : Constants.XSD_INTEGER, context);
  }

  @Override
  public XmlSchemaType visitBoolean(final OASBooleanType oasBooleanType, final SchemaVisitorContext context) {
    CompiledSimpleTypeInfo typeInfo = commonSimpleTypePreamble(
        oasBooleanType, context,
      "<> visitBoolean(td=" + oasBooleanType.name() + ":" + oasBooleanType.type() + ")"
    );

    if (!context.isTopLevel() && typeInfo.getSchemaType() != null && typeInfo.getAnnotation() == null) {
      tracer.decreaseIndent();
      return typeInfo.getSchemaType();
    }

    final XmlSchemaSimpleTypeRestriction content = prepareSimpleTypeRestriction(typeInfo.getSchemaType(), Constants.XSD_BOOLEAN, context);

//    final List<Boolean> enums = oasBooleanType.enums(); /* resolveBooleanEnumFacet(oasBooleanType); */
//    if (!enums.isEmpty()) {
//      for (Boolean anEnum : enums) {
//        final XmlSchemaEnumerationFacet enumValue = new XmlSchemaEnumerationFacet();
//        enumValue.setValue(anEnum);
//        content.getFacets().add(enumValue);
//      }
//    }

    if (!context.isTopLevel() && typeInfo.getSchemaType() == null && typeInfo.getAnnotation() == null && content.getFacets().isEmpty()) {
      tracer.decreaseIndent();
      return collection.getTypeByQName(Constants.XSD_BOOLEAN);
    } else {
      final XmlSchemaSimpleType simpleType = new XmlSchemaSimpleType(getSchema(typeInfo.getNamespaceURI()), false);
      simpleType.setContent(content);
      if (context.isTopLevel()) {
        simpleType.setName(oasBooleanType.name());
        simpleType.setAnnotation(typeInfo.getAnnotation());
        updateVisitedType(createQName(typeInfo.getNamespaceURI(), typeInfo.getTypeName()), simpleType);
      }
      tracer.decreaseIndent();
      return simpleType;
    }
  }

  @Override
  public XmlSchemaType visitDate(final OASDateType oasDateType, final SchemaVisitorContext context) {
    CompiledSimpleTypeInfo typeInfo = commonSimpleTypePreamble(
        oasDateType, context,
      "<> visitDate(td=" + oasDateType.name() + ":" + oasDateType.type() + ")"
    );

    if (!context.isTopLevel() && typeInfo.getSchemaType() != null && typeInfo.getAnnotation() == null) {
      tracer.decreaseIndent();
      return typeInfo.getSchemaType();
    }

    final XmlSchemaSimpleTypeRestriction content = prepareSimpleTypeRestriction(typeInfo.getSchemaType(), Constants.XSD_DATE, context);

    if (!context.isTopLevel() && typeInfo.getSchemaType() == null && typeInfo.getAnnotation() == null) {
      tracer.decreaseIndent();
      return collection.getTypeByQName(Constants.XSD_DATE);
    } else {
      final XmlSchemaSimpleType simpleType = new XmlSchemaSimpleType(getSchema(typeInfo.getNamespaceURI()), false);
      simpleType.setContent(content);
      if (context.isTopLevel()) {
        simpleType.setName(oasDateType.name());
        simpleType.setAnnotation(typeInfo.getAnnotation());
        updateVisitedType(createQName(typeInfo.getNamespaceURI(), typeInfo.getTypeName()), simpleType);
      }
      tracer.decreaseIndent();
      return simpleType;
    }
  }

  @Override
  public XmlSchemaType visitDateTime(final OASDateTimeType oasDateTimeType, final SchemaVisitorContext context) {
    CompiledSimpleTypeInfo typeInfo = commonSimpleTypePreamble(
        oasDateTimeType, context,
      "<> visitDateTime(td=" + oasDateTimeType.name() + ":" + oasDateTimeType.type() + ")"
    );

    if (!context.isTopLevel() && typeInfo.getSchemaType() != null && typeInfo.getAnnotation() == null) {
      tracer.decreaseIndent();
      return typeInfo.getSchemaType();
    }

    final XmlSchemaSimpleTypeRestriction content = prepareSimpleTypeRestriction(typeInfo.getSchemaType(), Constants.XSD_DATETIME, context);

    if (!context.isTopLevel() && typeInfo.getSchemaType() == null && typeInfo.getAnnotation() == null) {
      tracer.decreaseIndent();
      return collection.getTypeByQName(Constants.XSD_DATETIME);
    } else {
      final XmlSchemaSimpleType simpleType = new XmlSchemaSimpleType(getSchema(typeInfo.getNamespaceURI()), false);
      simpleType.setContent(content);
      if (context.isTopLevel()) {
        simpleType.setName(oasDateTimeType.name());
        simpleType.setAnnotation(typeInfo.getAnnotation());
        updateVisitedType(createQName(typeInfo.getNamespaceURI(), typeInfo.getTypeName()), simpleType);
      }
      tracer.decreaseIndent();
      return simpleType;
    }
  }

  @Override
  public XmlSchemaType visitNull(final OASNullType oasNullType, final SchemaVisitorContext context) {
    tracer.log(
      "<> visitNull(td=" + oasNullType.name() + ":" + oasNullType.type() + ")"
    );
    tracer.increaseIndent();

    final String namespaceURI = calculateTargetNamespaceURI(oasNullType, context);
    final String typeName = simpleOASTypeName(oasNullType, context);

    if (typeName != null) {
      Pair<OASType, XmlSchemaType> typeMapEntry = lookupTypeByName(createQName(namespaceURI, context.isTopLevel() ? oasNullType.type() : typeName));
      if (context.isTopLevel() || OASTypeEquality.equals(oasNullType, typeMapEntry.getKey())) {
        return typeMapEntry.getValue();
      }
    }

    tracer.decreaseIndent();
    return collection.getTypeByQName(Constants.XSD_ANY);
  }

}
