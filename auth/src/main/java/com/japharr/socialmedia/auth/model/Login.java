package com.japharr.socialmedia.auth.model;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.Validator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Login (@JsonProperty("username") String username, @JsonProperty("password") String password) {
  public static final Validator<Login> validator = ValidatorBuilder.<Login>of()
      .constraint(Login::username, "username", c -> c.notNull().notBlank())
      .constraint(Login::password, "password", c -> c.notNull().notBlank())
      .build();
}
