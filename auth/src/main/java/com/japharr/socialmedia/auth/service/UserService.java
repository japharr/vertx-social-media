package com.japharr.socialmedia.auth.service;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.pgclient.PgPool;
import io.vertx.rxjava3.sqlclient.SqlClient;

@VertxGen
@ProxyGen
public interface UserService {
  @GenIgnore
  static UserService create(SqlClient sqlClient) {
    return new UserServiceImpl(sqlClient);
  }

  @GenIgnore
  static com.japharr.socialmedia.auth.rxjava3.service.UserService createProxy(Vertx vertx, String address) {
    return new com.japharr.socialmedia.auth.rxjava3.service.UserService(new UserServiceVertxEBProxy(vertx, address));
  }

  @Fluent
  UserService register(JsonObject data, Handler<AsyncResult<Void>> resultHandler);
}
