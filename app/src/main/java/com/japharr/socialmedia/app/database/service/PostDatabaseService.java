package com.japharr.socialmedia.app.database.service;

import com.japharr.socialmedia.app.database.service.impl.PostDatabaseServiceImpl;
import com.japharr.socialmedia.app.entity.Post;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.pgclient.PgPool;

@VertxGen
@ProxyGen
public interface PostDatabaseService {
  @GenIgnore
  static PostDatabaseService create(PgPool pgPool, Handler<AsyncResult<PostDatabaseService>> resultHandler) {
    return new PostDatabaseServiceImpl(pgPool, resultHandler);
  }

  @Fluent
  PostDatabaseService create(Post post, Handler<AsyncResult<Void>> resultHandler);

  @Fluent
  PostDatabaseService findAll(Handler<AsyncResult<JsonArray>> resultHandler);
}
