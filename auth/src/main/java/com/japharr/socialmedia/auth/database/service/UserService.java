package com.japharr.socialmedia.auth.database.service;

import com.japharr.socialmedia.auth.database.service.impl.UserServiceImpl;
import com.japharr.socialmedia.auth.entity.User;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.rxjava3.sqlclient.SqlClient;

@VertxGen
@ProxyGen
public interface UserService {
  @GenIgnore
  static UserService create(SqlClient sqlClient, Handler<AsyncResult<UserService>> resultHandler) {
    return new UserServiceImpl(sqlClient);
  }

  @GenIgnore
  static com.japharr.socialmedia.auth.database.rxjava3.service.UserService createProxy(Vertx vertx, String address) {
    return new com.japharr.socialmedia.auth.database.rxjava3.service.UserService(new UserServiceVertxEBProxy(vertx, address));
  }

  @Fluent
  UserService register(User user, Handler<AsyncResult<Void>> resultHandler);
}
