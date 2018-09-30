package com.langtoun.oastypes;

/**
 * Public interface for OpenAPI specification date-time types. Objects of this
 * type are derived from YAML like this:
 * <pre><code>
 *   type: string
 *   format: date-time
 * </code></pre>
 * This interface requires that a simple string type conforms to
 * the {@code date-time} format defined in RFC3339.
 *
 * @see <a href="https://xml2rfc.tools.ietf.org/public/rfc/html/rfc3339.html#anchor14">RFC3339</a>
 */
public interface OASDateTimeType extends OASType {

}
