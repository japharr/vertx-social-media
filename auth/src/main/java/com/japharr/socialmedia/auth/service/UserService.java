package com.japharr.socialmedia.auth.service;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface UserService {
  static UserService create() {
    return new UserServiceImpl();
  }

  static com.japharr.socialmedia.auth.rxjava3.service.UserService createProxy(Vertx vertx, String address) {
    return new  com.japharr.socialmedia.auth.rxjava3.service.UserService(new UserServiceVertxEBProxy(vertx, address));
  }

  void register(JsonObject data);
}
