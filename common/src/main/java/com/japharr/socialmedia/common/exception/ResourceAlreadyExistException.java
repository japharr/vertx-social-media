package com.japharr.socialmedia.common.exception;

public class ResourceAlreadyExistException extends RuntimeException {
  public ResourceAlreadyExistException(Throwable throwable) {
    super(throwable);
  }

  public ResourceAlreadyExistException(String message) {
    super(message);
  }

  public ResourceAlreadyExistException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
