package com.japharr.socialmedia.auth.api;

public final class Endpoints {
  public static final String GET_USERS = "/users";
  public static final String REGISTER_NEW_USER = "/register";
  public static final String AUTHENTICATE_USER = "/authenticate";
  public static final String GENERATE_TOKEN = "/token";
  public static final String GET_USER_BY_USERNAME = "/books/:username";
  public static final String DELETE_USER_BY_USERNAME = "/books/:username";
  public static final String UPDATE_BOOK_BY_USERNAME = "/books/:username";

  private Endpoints() {}
}
