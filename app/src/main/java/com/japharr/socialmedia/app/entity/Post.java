package com.japharr.socialmedia.app.entity;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Validator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
@JsonPropertyOrder({"text", "userid"})
public class Post {
  @JsonProperty("text")
  private String text;
  @JsonProperty("userid")
  private String userid;

  public static final Validator<Post> validator = ValidatorBuilder.<Post>of()
      .constraint(Post::getText, "text", c ->
          c.notNull().notBlank().lessThanOrEqual(200))
      .build();

  public Post () {}

  public Post (Post other) {
    this.text = other.text;
    this.userid = other.userid;
  }

  public Post(String json) {
    this(new JsonObject(json));
  }

  public Post(JsonObject jsonObject) {
    PostConverter.fromJson(jsonObject, this);
  }

  public JsonObject toJson() {
    JsonObject jsonObject = new JsonObject();
    PostConverter.toJson(this, jsonObject);
    return jsonObject;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getUserid() {
    return userid;
  }

  public void setUserid(String userid) {
    this.userid = userid;
  }
}
