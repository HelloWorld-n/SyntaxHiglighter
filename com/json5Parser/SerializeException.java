package com.json5Parser;

/**
 * Exception on serialization error
 */
public class SerializeException extends RuntimeException {

  public SerializeException(String s) {
    super(s);
  }

  public SerializeException(String s, Throwable cause) {
    super(s, cause);
  }

}