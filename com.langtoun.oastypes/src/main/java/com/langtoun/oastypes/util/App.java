package com.langtoun.oastypes.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.apache.ws.commons.schema.XmlSchemaType;

import com.langtoun.oastypes.visitor.OASTypeToXmlSchemaVisitor;
import com.reprezen.jsonoverlay.Overlay;
import com.reprezen.kaizen.oasparser.OpenApi3Parser;
import com.reprezen.kaizen.oasparser.model3.OpenApi3;
import com.reprezen.kaizen.oasparser.model3.Operation;
import com.reprezen.kaizen.oasparser.model3.Parameter;
import com.reprezen.kaizen.oasparser.model3.Path;
import com.reprezen.kaizen.oasparser.model3.Schema;
import com.reprezen.kaizen.oasparser.val.ValidationResults;
import com.reprezen.kaizen.oasparser.val.ValidationResults.ValidationItem;

/**
 * Hello world!
 *
 */
public class App
{
  private static Logger LOGGER = Logger.getLogger("oastypes.App");

  private final static String REMOTE_BASE = "https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/";
  private static final String LOCAL_BASE = "file:/Users/bruceg/Documents/git/oastypes/com.langtoun.oastypes/apis/";
  private final static String EXT = ".yaml";

  final static String DUMMY_NAMESPACE_URI = "http://langtoun.com/";

  public static void main(final String[] args) {
    // "api-with-examples", "callback-example", "link-example", "petstore", "petstore-expanded", "uspto"
    final String[] remoteSources = new String[] {};
    // "simple-types", "arrays-simple-items", "arrays-object-items", "arrays-ref-items", "objects-simple-props", "objects-simple-array-props", "goober", "goober2"
    final String[] localSources = new String[] { "arrays-ref-items" };

    URL[] remoteURLs = Arrays.stream(remoteSources).map(s -> {
      try {
        return new URI(REMOTE_BASE + s + EXT).toURL();
      } catch (URISyntaxException | MalformedURLException e1) {
        e1.printStackTrace();
      }
      return null;
    }).filter(u -> u != null).toArray(URL[]::new);

    URL[] localURLs = Arrays.stream(localSources).map(s -> {
      try {
        return new URI(LOCAL_BASE + s + EXT).toURL();
      } catch (URISyntaxException | MalformedURLException e1) {
        e1.printStackTrace();
      }
      return null;
    }).filter(u -> u != null).toArray(URL[]::new);

    URL[] allUrls = Stream.of(remoteURLs, localURLs).flatMap(Stream::of).toArray(URL[]::new);

    final OpenApi3Parser parser = new OpenApi3Parser();

    for (final URL url : allUrls) {
      try {
        LOGGER.info("---- Parsing: " + url);
        final OpenApi3 model = parser.parse(url, true);
        final ValidationResults results = model.getValidationResults();
        if (results != null && !results.getItems().isEmpty()) {
          for (final ValidationItem item : results.getItems()) {
            LOGGER.error(item);
          }
        } else {
          // print out a description of the model
          describeModel(model);
          // describe the component schemas
          describeComponentSchemas(model);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static void describeModel(final OpenApi3 model) {
    // top level
    LOGGER.info(String.format("model: %s", model));
    LOGGER.info(String.format("title: %s", model.getInfo().getTitle()));
    // paths
    for (final Path path : model.getPaths().values()) {
      LOGGER.info(String.format("path %s:", Overlay.of(path).getPathInParent()));
      // operations
      for (final Operation op : path.getOperations().values()) {
        LOGGER.info(String.format("  %s: [%s] %s", Overlay.of(op).getPathInParent().toUpperCase(), op.getOperationId(), op.getSummary()));
        // parameters
        for (final Parameter param : op.getParameters()) {
          LOGGER.info(String.format("    %s[%s]:, %s - %s", param.getName(), param.getIn(), getParameterType(param), param.getDescription()));
        }
      }
    }
  }

  private static void describeComponentSchemas(OpenApi3 model) {
    // use the visitor to build the OASType objects and the XmlSchemaType objects
    final OASTypeToXmlSchemaVisitor typeCompiler = OASTypeToXmlSchemaVisitor.newVisitor(model, DUMMY_NAMESPACE_URI, App.class);
    final XmlSchemaWriter writer = new XmlSchemaWriter(DUMMY_NAMESPACE_URI);
    for (final Entry<String, Schema> entry : model.getSchemas().entrySet()) {
      final XmlSchemaType schemaType = typeCompiler.findXmlSchemaTypeBySchema(entry.getValue());
      LOGGER.info(String.format("---- %s ----", entry.getKey()));
      if (schemaType != null) {
        LOGGER.info("\n" + writer.writeSchema(schemaType));
      }
    }
  }

  private static String getParameterType(final Parameter param) {
    final Schema schema = param.getSchema();
    return schema != null ? schema.getType() : "unknown";
  }

}
