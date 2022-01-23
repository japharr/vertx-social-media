package com.japharr.socialmedia.app.entity;


import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Validator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
@JsonPropertyOrder({"userid", "password", "email", "name", "emailVerify"})
public class User{
  @JsonProperty("userid")
  private String userid;
  @JsonProperty("name")
  private String name;
  @JsonProperty("imageUrl")
  private String imageUrl;

  public static final Validator<User> validator = ValidatorBuilder.<User>of()
      .constraint(User::getName, "name", c ->
          c.notNull().notBlank())
      .build();

  public User() {}

  public User (User other) {
    this.userid = other.userid;
    this.name = other.name;
    this.imageUrl = other.imageUrl;
  }

  public User (String json) {
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

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  @Override
  public String toString() {
    return "{\n" +
        "\"userid\": \"" + userid + "\",\n" +
        "\"name\": \"" + name + "\",\n" +
        "\"imageUrl\": \"" + imageUrl +"\",\n" +
        "}";
  }
}
