package com.japharr.socialmedia.auth.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

@DataObject(generateConverter = true)
@JsonPropertyOrder({"username", "password", "email", "firstName", "lastName"})
public class User {
  @JsonProperty("username")
  private String username;
  @JsonProperty("password")
  private String password;
  @JsonProperty("email")
  private String email;
  @JsonProperty("firstName")
  private String firstName;
  @JsonProperty("lastName")
  private String lastName;

  public User () {}

  public User (User other) {
    this.username = other.username;
    this.password = other.password;
    this.email = other.email;
    this.firstName = other.firstName;
    this.lastName = other.lastName;
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

  public User(String username, String password, String email, String firstName, String lastName) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
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

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
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
        "\"password\": \"" + password + "\",\n" +
        "\"email\": \"" + email + "\",\n" +
        "\"firstName\": \"" + firstName +"\",\n" +
        "\"lastName\": \"" + lastName + "\",\n" +
        "}";
  }
}
