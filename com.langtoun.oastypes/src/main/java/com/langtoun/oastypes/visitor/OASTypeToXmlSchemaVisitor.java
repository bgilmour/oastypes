package com.langtoun.oastypes.visitor;

import static com.langtoun.oastypes.util.OpenApiTypeUtil.isComplexType;
import static com.langtoun.oastypes.util.OpenApiTypeUtil.isRequired;
import static com.langtoun.oastypes.util.OverlayUtil.getReference;
import static com.langtoun.oastypes.util.StringExtensions.singleQuote;
import static com.langtoun.oastypes.visitor.SchemaVisitorContext.newContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaArrayType;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.eclipse.xtext.xbase.lib.Pair;

import com.langtoun.oastypes.OASArrayType;
import com.langtoun.oastypes.OASBooleanType;
import com.langtoun.oastypes.OASDateTimeType;
import com.langtoun.oastypes.OASDateType;
import com.langtoun.oastypes.OASIntegerType;
import com.langtoun.oastypes.OASNullType;
import com.langtoun.oastypes.OASNumberType;
import com.langtoun.oastypes.OASObjectType;
import com.langtoun.oastypes.OASStringType;
import com.langtoun.oastypes.OASType;
import com.langtoun.oastypes.util.OASTypeEquality;
import com.langtoun.oastypes.util.OASTypeFactory;
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
   * @return An {@link XmlSchema} object, or {@code null} is there is no mapping
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
   * @return An {@link XmlSchema} object, or {@code null} is there is no mapping
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
   * @return the target namespace
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
   * @return the target namespace URI
   */
  private String calculateTargetNamespaceURI(final OASType oasType, final SchemaVisitorContext context) {
    if (oasType == null) {
      return "?unknown?"; //$NON-NLS-1$
    } else {
      return getTargetNamespace();
    }
  }

  /**
   * Add a new map entry for an alias and the namespace of the library that it references. Mappings
   * are scoped according to the namespace of the model (default RAML / library) that is being
   * initialised.
   *
   * @param targetNamespace
   *          The namespace of the default OpenAPI or library whose types are being processed
   * @param namespaceAlias
   *          the alias from the uses list that is used to reference a library
   * @param namespaceURI
   *          the namespace URI derived from the referenced library
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
   * top level types are added to the type map before the visiting process in invoked
   * to create the schema objects.
   *
   * @param qName
   *          The qualified name of the type entry being added.
   * @param oasType
   *          The {@link OASType} to be added to the type map.
   */
  private void addVisitedType(final QName qName, final OASType oasType) {
    tracer.log(
      "-- addVisitedType: qn=" + qName + " ==> td=" + oasType.name() + ":" + oasType.type()
    );
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
   *          The qualified name of the type entry being updated
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
   * @return the {@link XmlSchemaType} created from the RAML type declaration.
   */
  public XmlSchemaType toTopLevelType(final OASType oasType, final SchemaVisitorContext context) {
    tracer.log(
      "<> toTopLevelType(td=" + oasType.name() + ":" + oasType.type()
      + ", tns=" + getTargetNamespace() + ")"
    );
    tracer.increaseIndent();
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
   *          the element name
   * @param oasType
   *          the {@link OASType} that will provide the body of the element
   * @param context
   *          the visitor context
   * @param expand
   *          if {@code true} expand the element content
   * @return an {@link XmlSchemaElement} built from the type declaration
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
    final boolean isArrayNotation = typeName.endsWith("[]");
    Pair<OASType, XmlSchemaType> typeMapEntry = lookupTypeByName(
      createQName(
        namespaceURI,
        isArrayNotation ? typeName.substring(0, typeName.length() - 2)
                        : typeName
      )
    );
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
      final OASType typeToCheck = isArrayNotation ? ((OASArrayType) oasType).items() : oasType;
      if (!OASTypeEquality.equals(typeToCheck, typeMapEntry.getKey())) {
        tracer.log("-- el: type equality check failed - visitor required");
        referencedType = null;
      } else {
        tracer.log("-- el: type equality check passed");
      }
    }

    if (referencedType == null || expand) {
      context.pushTopLevel(false);
      tracer.increaseIndent();
      final XmlSchemaType schemaType = visit(oasType, context);
      tracer.decreaseIndent();
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
      if (isArrayNotation) {
        schemaElement.setMaxOccurs(UNBOUNDED);
      }
      schemaElement.setSchemaType(referencedType);
      schemaElement.setSchemaTypeName(referencedType.getQName());
      if (referencedType.getQName() == null) {
        tracer.log("-- el:" + name + ": no QName for referenced type: " + referencedType.getName());
      } else {
        tracer.log("-- el:" + name + ": referenced type: qn=" + referencedType.getQName());
      }
    }

    if (!isRequired(oasType) || isArrayNotation) {
      schemaElement.setMinOccurs(0);
    }

    schemaElement.setDefaultValue(oasType.defaultValue().toString());

    tracer.decreaseIndent();
    return schemaElement;
  }

  /*
   * ---- complex type support methods ----
   */

  @Override
  public XmlSchemaType visitObject(final OASObjectType objectType, final SchemaVisitorContext context) {
    tracer.log(
      "<> visitObject(td=" + objectType.name() + ":" + objectType.type() + ")"
    );
    tracer.increaseIndent();

    tracer.decreaseIndent();
    return null;
  }

  /*
   * ---- array type support methods ----
   */

  @Override
  public XmlSchemaType visitArray(final OASArrayType arrayType, final SchemaVisitorContext context) {
    final OASType itemType = arrayType.items();
    tracer.log(
      "<> visitArray(td=" + arrayType.name() + ":" + arrayType.type()
      + " [items=" + itemType.name() + ":" + itemType.type() + "])"
    );
    tracer.increaseIndent();

    tracer.decreaseIndent();
    return null;
  }

  /*
   * ---- simple type support methods ----
   */

  @Override
  public XmlSchemaType visitString(final OASStringType stringType, final SchemaVisitorContext context) {
    tracer.log(
      "<> visitString(td=" + stringType.name() + ":" + stringType.type() + ")"
    );
    tracer.increaseIndent();

    tracer.decreaseIndent();
    return null;
  }

  @Override
  public XmlSchemaType visitNumber(final OASNumberType numberType, final SchemaVisitorContext context) {
    tracer.log(
      "<> visitNumber(td=" + numberType.name() + ":" + numberType.type() + ")"
    );
    tracer.increaseIndent();

    tracer.decreaseIndent();
    return null;
  }

  @Override
  public XmlSchemaType visitInteger(final OASIntegerType integerType, final SchemaVisitorContext context) {
    tracer.log(
      "<> visitInteger(td=" + integerType.name() + ":" + integerType.type() + ")"
    );
    tracer.increaseIndent();

    tracer.decreaseIndent();
    return null;
  }

  @Override
  public XmlSchemaType visitBoolean(final OASBooleanType booleanType, final SchemaVisitorContext context) {
    tracer.log(
      "<> visitBoolean(td=" + booleanType.name() + ":" + booleanType.type() + ")"
    );
    tracer.increaseIndent();

    tracer.decreaseIndent();
    return null;
  }

  @Override
  public XmlSchemaType visitDate(final OASDateType dateType, final SchemaVisitorContext context) {
    tracer.log(
      "<> visitDate(td=" + dateType.name() + ":" + dateType.type() + ")"
    );
    tracer.increaseIndent();

    tracer.decreaseIndent();
    return null;
  }

  @Override
  public XmlSchemaType visitDateTime(final OASDateTimeType dateTimeType, final SchemaVisitorContext context) {
    tracer.log(
      "<> visitDateTime(td=" + dateTimeType.name() + ":" + dateTimeType.type() + ")"
    );
    tracer.increaseIndent();

    tracer.decreaseIndent();
    return null;
  }

  @Override
  public XmlSchemaType visitNull(final OASNullType nullType, final SchemaVisitorContext context) {
    // TODO Auto-generated method stub
    return null;
  }

}
