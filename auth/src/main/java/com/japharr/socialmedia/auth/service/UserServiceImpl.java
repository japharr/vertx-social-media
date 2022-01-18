package com.japharr.socialmedia.auth.service;

import io.reactivex.rxjava3.core.Single;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.Vertx;
import io.vertx.rxjava3.sqlclient.SqlClient;

public class UserServiceImpl implements UserService {
  private SqlClient sqlClient;

  public UserServiceImpl(SqlClient sqlClient) {
    this.sqlClient = sqlClient;
  }

  @Override
  public UserService register(JsonObject data, Handler<AsyncResult<Void>> resultHandler) {
    return this;
  }
}
