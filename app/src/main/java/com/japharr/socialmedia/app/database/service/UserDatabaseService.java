package com.japharr.socialmedia.app.database.service;

import com.japharr.socialmedia.app.database.service.impl.UserDatabaseServiceImpl;
import com.japharr.socialmedia.app.entity.User;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgPool;

@VertxGen
@ProxyGen
public interface UserDatabaseService {
  @GenIgnore
  static UserDatabaseService create(PgPool pgPool, Handler<AsyncResult<UserDatabaseService>> resultHandler) {
    return new UserDatabaseServiceImpl(pgPool, resultHandler);
  }

  @GenIgnore
  static com.japharr.socialmedia.app.database.rxjava3.service.UserDatabaseService createProxy(Vertx vertx, String address) {
    return new com.japharr.socialmedia.app.database.rxjava3.service.UserDatabaseService(new UserDatabaseServiceVertxEBProxy(vertx, address));
  }

  @Fluent
  UserDatabaseService create(User user, Handler<AsyncResult<Void>> resultHandler);
}
