package com.japharr.socialmedia.auth.database.service;

import com.japharr.socialmedia.auth.database.service.impl.UserDatabaseServiceImpl;
import com.japharr.socialmedia.auth.entity.User;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;

@VertxGen
@ProxyGen
public interface UserDatabaseService {
  @GenIgnore
  static UserDatabaseService create(PgPool pgPool, Handler<AsyncResult<UserDatabaseService>> resultHandler) {
    return new UserDatabaseServiceImpl(pgPool, resultHandler);
  }

  @GenIgnore
  static com.japharr.socialmedia.auth.database.rxjava3.service.UserDatabaseService createProxy(Vertx vertx, String address) {
    return new com.japharr.socialmedia.auth.database.rxjava3.service.UserDatabaseService(new UserDatabaseServiceVertxEBProxy(vertx, address));
  }

  @Fluent
  UserDatabaseService register(User user, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  UserDatabaseService findAll(Handler<AsyncResult<JsonArray>> resultHandler);

  @Fluent
  UserDatabaseService authenticate(String usernameOrEmail, String password, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  UserDatabaseService findByUsernameOrEmail(String usernameOrEmail, Handler<AsyncResult<JsonObject>> resultHandler);

  @Fluent
  UserDatabaseService countByUsernameOrEmail(String usernameOrEmail, Handler<AsyncResult<Long>> resultHandler);
}
