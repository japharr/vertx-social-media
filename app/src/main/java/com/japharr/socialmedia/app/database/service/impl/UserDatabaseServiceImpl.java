package com.japharr.socialmedia.app.database.service.impl;

import com.japharr.socialmedia.app.database.service.UserDatabaseService;
import com.japharr.socialmedia.app.entity.User;
import com.japharr.socialmedia.common.exception.BadRequestException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.rxjava3.pgclient.PgPool;
import io.vertx.rxjava3.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDatabaseServiceImpl implements UserDatabaseService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserDatabaseServiceImpl.class);

  private static final String SQL_COUNT_USERS = "SELECT COUNT(*) FROM users";
  private static final String SQL_INSERT_USER =
      "INSERT INTO users (userid, name, created_date, last_modified_date) " +
          "VALUES ($1, $2, current_timestamp, current_timestamp)";

  private final PgPool pgPool;

  public UserDatabaseServiceImpl(io.vertx.pgclient.PgPool pgPool, Handler<AsyncResult<UserDatabaseService>> resultHandler) {
    LOGGER.info("UserServiceImpl");
    this.pgPool = new PgPool(pgPool);

    this.pgPool.rxGetConnection()
        .flatMap(pgConnection -> pgConnection
            .query(SQL_COUNT_USERS)
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
  public UserDatabaseService create(User user, Handler<AsyncResult<Void>> resultHandler) {
    pgPool.preparedQuery(SQL_INSERT_USER)
        .rxExecute(Tuple.of(user.getUserid(), user.getName()))
        .subscribe(
            result -> resultHandler.handle(Future.succeededFuture()),
            error -> {
              LOGGER.error("unable to create user", error);
              resultHandler.handle(Future.failedFuture(new BadRequestException(error)));
            }
        );
    return this;
  }
}
