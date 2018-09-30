package com.langtoun.oastypes;

/**
 * Public interface for OpenAPI specification date types. Objects of this
 * type are derived from YAML like this:
 * <pre><code>
 *   type: string
 *   format: date
 * </code></pre>
 * This interface requires a simple string type conforms to
 * the {@code full-date} format defined in RFC3339.
 *
 * @see <a href="https://xml2rfc.tools.ietf.org/public/rfc/html/rfc3339.html#anchor14">RFC3339</a>
 */
public interface OASDateType extends OASType {

}
