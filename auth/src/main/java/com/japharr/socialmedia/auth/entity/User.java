package com.japharr.socialmedia.auth.entity;


import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Validator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject(generateConverter = true)
@JsonPropertyOrder({"username", "password", "email", "name", "emailVerify"})
public class User {
  @JsonProperty("username")
  private String username;
  @JsonProperty("password")
  private String password;
  @JsonProperty("email")
  private String email;
  @JsonProperty("name")
  private String name;
  @JsonProperty("emailVerify")
  private boolean emailVerify;

  // private static final String USERNAME_REGEX_PATTERN = "^(?:[A-Z\\d][A-Z\\d_-]{8,20}|[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4})$";
  // private static final String USERNAME_REGEX_PATTERN = "/^(?=[a-z0-9.]{3,20}$)[a-z0-9]+\\.?[a-z0-9]+$|^.*@\\w+\\.[\\w.]+$/i";
  private static final String USERNAME_REGEX_PATTERN = "^(?=[a-zA-Z0-9._]{8,20}$)(?!.*[_.]{2})[^_.].*[^_.]$";

  public static final Validator<User> validator = ValidatorBuilder.<User>of()
      .constraint(User::getUsername, "username", c ->
          c.notNull().notBlank().pattern(USERNAME_REGEX_PATTERN))
      .constraint(User::getPassword, "password", c -> c.notNull().notBlank().greaterThanOrEqual(2).lessThanOrEqual(14))
      .constraint(User::getEmail, "email", c -> c.notNull().notBlank().email())
      .build();

  public User () {}

  public User (User other) {
    this.username = other.username;
    this.password = other.password;
    this.email = other.email;
    this.name = other.name;
    this.emailVerify = other.emailVerify;
  }

  public User(String json) {
    this(new JsonObject(json));
  }

  public User(JsonObject jsonObject) {
    UserConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    UserConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public User(String username, String password, String email, String name, boolean emailVerify) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.name = name;
    this.emailVerify = emailVerify;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean getEmailVerify() {
    return emailVerify;
  }

  public void setEmailVerify(boolean emailVerify) {
    this.emailVerify = emailVerify;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(username, user.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  @Override
  public String toString() {
    return "{\n" +
        "\"username\": \"" + username + "\",\n" +
        "\"email\": \"" + email + "\",\n" +
        "\"name\": \"" + name +"\",\n" +
        "\"emailVerify\": \"" + emailVerify + "\",\n" +
        "}";
  }
}
