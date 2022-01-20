package com.japharr.socialmedia.auth.database.service.impl;

import com.japharr.socialmedia.auth.database.service.UserService;
import com.japharr.socialmedia.auth.entity.User;
import com.japharr.socialmedia.common.exception.BadRequestException;
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

public class UserServiceImpl implements UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  private static final String SQL_COUNT_USERS = "SELECT COUNT(*) FROM users";
  private static final String INSERT_USER =
      "INSERT INTO users (username, password, email, first_name, last_name, created_date, last_modified_date) " +
      "VALUES ($1, $2, $3, $4, $5, current_timestamp, current_timestamp)";
  private static final String SQL_SELECT_ALL = "SELECT * FROM users";
  private final static String SQL_AUTHENTICATE_QUERY = "SELECT password FROM users WHERE username = $1 OR email = $1";

  private PgPool pgPool;
  private SqlAuthentication sqlAuth;

  public UserServiceImpl(io.vertx.pgclient.PgPool pgPool, Handler<AsyncResult<UserService>> resultHandler) {
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
  public UserService register(User user, Handler<AsyncResult<Void>> resultHandler) {
    String hash = sqlAuth.hash(
        "pbkdf2", // hashing algorithm (OWASP recommended)
        VertxContextPRNG.current().nextString(32), // secure random salt
        user.getPassword() // password
    );

    pgPool.preparedQuery(INSERT_USER)
        .rxExecute(Tuple.of(user.getUsername(), hash, user.getEmail(), user.getFirstName(), user.getLastName()))
        .subscribe(
            result -> {resultHandler.handle(Future.succeededFuture());},
            error -> {
              LOGGER.error("unable to create user", error);
              //resultHandler.handle(Future.future(p -> p.fail(new BadRequestException(error))));
              resultHandler.handle(Future.failedFuture(new BadRequestException(error)));
            }
        );
    return this;
  }

  @Override
  public UserService findAll(Handler<AsyncResult<JsonArray>> resultHandler) {
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
  public UserService authenticate(User user, Handler<AsyncResult<Void>> resultHandler) {
    var json = new JsonObject()
        .put("username", user.getUsername())
        .put("password", user.getPassword());

    sqlAuth.rxAuthenticate(json)
        .subscribe(
            result -> {
              resultHandler.handle(Future.succeededFuture());
            },
            throwable -> {
              LOGGER.error("Failed to get login", throwable);
              resultHandler.handle(Future.failedFuture(throwable));
            }
        );
    return this;
  }

  private JsonObject mapToJsonObject(Row row) {
    return new JsonObject()
        .put("username", row.getValue("username"))
        .put("email", row.getValue("email"))
        .put("firstName", row.getValue("first_name"));
  }

  private JsonArray mapToJsonArray(RowSet<Row> rows) {
    JsonArray data = new JsonArray();
    rows.forEach(row -> data.add(mapToJsonObject(row)));
    return data;
  }
}
