package com.japharr.socialmedia.app.database.service.impl;

import com.japharr.socialmedia.app.database.service.PostDatabaseService;
import com.japharr.socialmedia.app.entity.Post;
import com.japharr.socialmedia.common.exception.BadRequestException;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.pgclient.PgPool;
import io.vertx.rxjava3.sqlclient.Row;
import io.vertx.rxjava3.sqlclient.RowSet;
import io.vertx.rxjava3.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PostDatabaseServiceImpl implements PostDatabaseService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PostDatabaseServiceImpl.class);

  private static final String SQL_COUNT_POSTS = "SELECT COUNT(*) FROM posts";
  private static final String SQL_INSERT_POST =
      "INSERT INTO posts (post_id, text, userid, created_date, last_modified_date) " +
          "VALUES ($1, $2, current_timestamp, current_timestamp)";
  private static final String SQL_SELECT_ALL = "SELECT * FROM posts";

  private final PgPool pgPool;

  public PostDatabaseServiceImpl(io.vertx.pgclient.PgPool pgPool, Handler<AsyncResult<PostDatabaseService>> resultHandler) {
    this.pgPool = new PgPool(pgPool);
    this.pgPool.rxGetConnection()
        .flatMap(pgConnection -> pgConnection
            .query(SQL_COUNT_POSTS)
            .rxExecute()
            .doAfterTerminate(pgConnection::close))
        .subscribe(
            result -> resultHandler.handle(Future.succeededFuture(this)),
            throwable -> {
              LOGGER.error("Unable to connect to database", throwable);
              resultHandler.handle(Future.failedFuture(throwable));
            }
        );
  }

  @Override
  public PostDatabaseService create(Post post, Handler<AsyncResult<Void>> resultHandler) {
    pgPool.preparedQuery(SQL_INSERT_POST)
        .rxExecute(Tuple.of(UUID.randomUUID().toString(), post.getText(), post.getUserid()))
        .subscribe(
            result -> resultHandler.handle(Future.succeededFuture()),
            error -> {
              LOGGER.error("unable to create user", error);
              resultHandler.handle(Future.failedFuture(new BadRequestException(error)));
            }
        );
    return this;
  }

  @Override
  public PostDatabaseService findAll(Handler<AsyncResult<JsonArray>> resultHandler) {
    pgPool.preparedQuery(SQL_SELECT_ALL)
        .rxExecute()
        .subscribe(
          result -> resultHandler.handle(Future.succeededFuture(mapToJsonArray(result))),
          throwable -> {
            LOGGER.error("unable to fetch all", throwable);
            resultHandler.handle(Future.failedFuture(throwable));
          }
        );
    return this;
  }

  private JsonObject mapToJsonObject(Row row) {
    return new JsonObject()
        .put("userid", row.getValue("userid"))
        .put("username", row.getValue("username"))
        .put("email", row.getValue("email"))
        .put("name", row.getValue("name"))
        .put("emailVerify", row.getValue("email_verify"));
  }

  private JsonArray mapToJsonArray(RowSet<Row> rows) {
    JsonArray data = new JsonArray();
    rows.forEach(row -> data.add(mapToJsonObject(row)));
    return data;
  }
}
