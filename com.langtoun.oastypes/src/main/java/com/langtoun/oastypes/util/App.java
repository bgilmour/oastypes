package com.langtoun.oastypes.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.langtoun.oastypes.OASType;
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
  private final static String REMOTE_BASE = "https://raw.githubusercontent.com/OAI/OpenAPI-Specification/master/examples/v3.0/";
  private static final String LOCAL_BASE = "file:/Users/bruceg/Documents/git/oastypes/com.langtoun.oastypes/apis/";
  private final static String EXT = ".yaml";

  public static void main(final String[] args) {
    // "api-with-examples", "callback-example", "link-example", "petstore", "petstore-expanded", "uspto"
    final String[] remoteSources = new String[] {};
    // "simple-types", "arrays-simple-items", "arrays-object-items", "arrays-ref-items", "objects-simple-props", "objects-simple-array-props", "goober", "goober2"
    final String[] localSources = new String[] { "arrays-ref-items" };

    URL[] remoteUrls = Arrays.stream(remoteSources).map(s -> {
      try {
        return new URL(REMOTE_BASE + s + EXT);
      } catch (MalformedURLException e1) {
        e1.printStackTrace();
      }
      return null;
    }).filter(u -> u != null).toArray(URL[]::new);

    URL[] localUrls = Arrays.stream(localSources).map(s -> {
      try {
        return new URL(LOCAL_BASE + s + EXT);
      } catch (MalformedURLException e1) {
        e1.printStackTrace();
      }
      return null;
    }).filter(u -> u != null).toArray(URL[]::new);

    URL[] allUrls = Stream.of(remoteUrls, localUrls).flatMap(Stream::of).toArray(URL[]::new);

    final OpenApi3Parser parser = new OpenApi3Parser();

    for (final URL url : allUrls) {
      try {
        System.out.println("---- Parsing: " + url);
        final OpenApi3 model = parser.parse(url, true);
        if (model.isValid()) {
          // print out a description of the model
          describeModel(model);
        } else {
          final ValidationResults results = model.getValidationResults();
          for (final ValidationItem item : results.getItems()) {
            System.out.println(item);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static void describeModel(final OpenApi3 model) {
    // top level
    System.out.printf("  model: %s\n", model);
    System.out.printf("  title: %s\n", model.getInfo().getTitle());
    // paths
    for (final Path path : model.getPaths().values()) {
      System.out.printf("  path %s:\n", Overlay.of(path).getPathInParent());
      for (final Operation op : path.getOperations().values()) {
        System.out.printf("    %s: [%s] %s\n", Overlay.of(op).getPathInParent().toUpperCase(),
            op.getOperationId(), op.getSummary());
        for (final Parameter param : op.getParameters()) {
          System.out.printf("      %s[%s]:, %s - %s\n", param.getName(), param.getIn(), getParameterType(param),
              param.getDescription());
        }
      }
    }
    // components
    describeComponentSchemas(model.getSchemas());
  }

  private static void describeComponentSchemas(Map<String, Schema> schemas) {
    for (final Entry<String, Schema> entry : schemas.entrySet()) {
      final String key = entry.getKey();
      final Schema schema = entry.getValue();

      System.out.printf("\n--> components/schemas/%s[%s]: %s\n", key, schema.getType(), schema);
      System.out.printf("    %s reference\n", (Overlay.of(schemas).isReference(key) ? "is a" : "is not a"));

      final OASType oasType = OASTypeFactory.createOASType(schema, Overlay.of(schemas).getReference(key));
      if (oasType != null) {
        System.out.printf("    %s: %s\n", oasType.getClass().getSimpleName(), oasType);
      } else {
        System.out.println("    oasType: nil");
      }
    }
  }

  private static String getParameterType(final Parameter param) {
    final Schema schema = param.getSchema();
    return schema != null ? schema.getType() : "unknown";
  }

}
