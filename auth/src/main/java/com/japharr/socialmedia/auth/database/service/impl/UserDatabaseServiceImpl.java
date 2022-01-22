package com.japharr.socialmedia.auth.database.service.impl;

import com.japharr.socialmedia.auth.database.service.UserDatabaseService;
import com.japharr.socialmedia.auth.entity.User;
import com.japharr.socialmedia.common.exception.BadRequestException;
import com.japharr.socialmedia.common.exception.ResourceAlreadyExistException;
import com.japharr.socialmedia.common.exception.ResourceNotFoundException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.VertxContextPRNG;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.rxjava3.ext.auth.sqlclient.SqlAuthentication;
import io.vertx.rxjava3.pgclient.PgPool;
import io.vertx.rxjava3.sqlclient.Row;
import io.vertx.rxjava3.sqlclient.RowSet;
import io.vertx.rxjava3.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDatabaseServiceImpl implements UserDatabaseService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserDatabaseServiceImpl.class);

  private static final String SQL_COUNT_USERS = "SELECT COUNT(*) FROM users";
  private static final String INSERT_USER =
      "INSERT INTO users (username, password, email, first_name, last_name, created_date, last_modified_date) " +
      "VALUES ($1, $2, $3, $4, $5, current_timestamp, current_timestamp)";
  private static final String SQL_SELECT_ALL = "SELECT * FROM users";
  private final static String SQL_AUTHENTICATE_QUERY = "SELECT password FROM users WHERE username = $1 OR email = $1";
  private final static String SQL_SELECT_BY_USERNAME_EMAIL = "SELECT username, email, first_name, last_name FROM users WHERE username = $1 OR email = $1 LIMIT 1";
  private final static String SQL_COUNT_BY_USERNAME_EMAIL = "SELECT COUNT(*) FROM users WHERE username = $1 OR email = $1 LIMIT 1";

  private final PgPool pgPool;
  private final SqlAuthentication sqlAuth;

  public UserDatabaseServiceImpl(io.vertx.pgclient.PgPool pgPool, Handler<AsyncResult<UserDatabaseService>> resultHandler) {
    LOGGER.info("UserServiceImpl");
    this.pgPool = new PgPool(pgPool);
    this.sqlAuth = SqlAuthentication.create(this.pgPool, new SqlAuthenticationOptions(
        new JsonObject().put("authenticationQuery", SQL_AUTHENTICATE_QUERY))) ;

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
  public UserDatabaseService register(User user, Handler<AsyncResult<Void>> resultHandler) {
    String hash = sqlAuth.hash(
        "pbkdf2",
        VertxContextPRNG.current().nextString(32),
        user.getPassword()
    );

    pgPool.preparedQuery(INSERT_USER)
        .rxExecute(Tuple.of(user.getUsername(), hash, user.getEmail(), user.getFirstName(), user.getLastName()))
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
  public UserDatabaseService findAll(Handler<AsyncResult<JsonArray>> resultHandler) {
    pgPool.preparedQuery(SQL_SELECT_ALL)
        .rxExecute()
        .subscribe(
            result -> {
              var jsonArray = mapToJsonArray(result);
              resultHandler.handle(Future.succeededFuture(jsonArray));
            },
            throwable -> {
              LOGGER.error("Failed to get the filtered books by the following conditions", throwable);
              resultHandler.handle(Future.failedFuture(throwable));
            }
        );

    return this;
  }

  @Override
  public UserDatabaseService authenticate(String usernameOrEmail, String password, Handler<AsyncResult<Void>> resultHandler) {
    var json = new JsonObject()
        .put("username", usernameOrEmail)
        .put("password", password);

    sqlAuth.rxAuthenticate(json)
        .subscribe(
            result -> resultHandler.handle(Future.succeededFuture()),
            throwable -> {
              LOGGER.error("Failed to get login", throwable);
              resultHandler.handle(Future.failedFuture(throwable));
            }
        );
    return this;
  }

  @Override
  public UserDatabaseService findByUsernameOrEmail(String usernameOrEmail, Handler<AsyncResult<JsonObject>> resultHandler) {
    pgPool.preparedQuery(SQL_SELECT_BY_USERNAME_EMAIL)
        .rxExecute(Tuple.of(usernameOrEmail))
        .subscribe(
          result -> {
            LOGGER.info("User found");
            fetchOne(result, resultHandler);
          },
          throwable -> {
            LOGGER.error("Failed to fetch user", throwable);
            resultHandler.handle(Future.failedFuture(throwable));
          }
        );
    return this;
  }

  @Override
  public UserDatabaseService countByUsernameOrEmail(String usernameOrEmail, Handler<AsyncResult<Long>> resultHandler) {
    pgPool.preparedQuery(SQL_COUNT_BY_USERNAME_EMAIL)
        .rxExecute(Tuple.of(usernameOrEmail))
        .subscribe(
          result -> {
            LOGGER.info("User found");
            var count = result.iterator().next().getLong(0);
            resultHandler.handle(Future.succeededFuture(count));
          },
          throwable -> {
            LOGGER.error("Failed to fetch user", throwable);
            resultHandler.handle(Future.failedFuture(throwable));
          }
        );
    return this;
  }

  private void fetchOne(RowSet<Row> rows, Handler<AsyncResult<JsonObject>> resultHandler) {
    if(rows.size() == 0) {
      resultHandler.handle(Future.failedFuture(new ResourceNotFoundException("User with name or email not found")));
      return;
    }

    Row row = rows.iterator().next();
    JsonObject user = mapToJsonObject(row);

    resultHandler.handle(Future.succeededFuture(user));
  }

  private JsonObject mapToJsonObject(Row row) {
    return new JsonObject()
        .put("username", row.getValue("username"))
        .put("email", row.getValue("email"))
        .put("firstName", row.getValue("first_name"))
        .put("lastName", row.getValue("last_name"));
  }

  private JsonArray mapToJsonArray(RowSet<Row> rows) {
    JsonArray data = new JsonArray();
    rows.forEach(row -> data.add(mapToJsonObject(row)));
    return data;
  }
}
