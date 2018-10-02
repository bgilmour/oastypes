package com.langtoun.oastypes.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * A utility class that provides help with trace level logging. This includes support
 * for increasing and decreasing indentation levels to make it easier to follow deeply
 * nested processing.
 */
public final class TraceLogger {

  private static final String INDENT = "   ";
  private static final int INDENT_LEN = 3;

  private final Logger logger;

  private String indentation = "";
  private final boolean enableTrace;

  private TraceLogger(final String callerLabel, final boolean enableTrace) {
    logger = Logger.getLogger(callerLabel);
    this.enableTrace = enableTrace;
    if (this.enableTrace) {
      logger.setLevel(Level.TRACE);
    }
  }

  /**
   * Configure the trace logger. This is where any customer appenders could be added.
   *
   * @param callerLabel
   *          Label identifying the component that called the module with trace level
   *          logging enabled.
   * @param enableTrace
   *          A boolean that determines whether or not to enable trace level logging.
   * @return A new {@link Logger} instance.
   */
  public static TraceLogger newLogger(final String callerLabel, final boolean enableTrace) {
    return new TraceLogger(callerLabel, enableTrace);
  }

  /**
   * Increase the indentation level.
   */
  public void increaseIndent() {
    indentation = INDENT + indentation;
  }

  /**
   * Decrease the indentation level.
   */
  public void decreaseIndent() {
    indentation = indentation.substring(INDENT_LEN);
  }

  /**
   * Log a message using the TRACE level. The message is prefixed with a string to indent it
   * according to the current indentation level.
   *
   * @param message
   *          The message to be logged.
   */
  public void log(final String message) {
    if (enableTrace) {
      logger.trace(indentation + message);
    }
  }

}
