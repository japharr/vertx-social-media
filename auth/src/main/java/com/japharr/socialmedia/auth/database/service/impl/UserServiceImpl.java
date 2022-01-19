package com.japharr.socialmedia.auth.database.service.impl;

import com.japharr.socialmedia.auth.database.service.UserService;
import com.japharr.socialmedia.auth.entity.User;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.VertxContextPRNG;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.rxjava3.ext.auth.sqlclient.SqlAuthentication;
import io.vertx.rxjava3.sqlclient.SqlClient;
import io.vertx.rxjava3.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceImpl implements UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  private static final String INSERT_USER =
      "INSERT INTO users (username, password, email, first_name, last_name, created_date, last_modified_date) " +
      "VALUES ($1, $2, $3, $4, $5, current_timestamp, current_timestamp)";

  private SqlClient sqlClient;
  private SqlAuthentication sqlAuth;

  public UserServiceImpl(SqlClient sqlClient, Handler<AsyncResult<UserService>> resultHandler) {
    this.sqlClient = sqlClient;
    this.sqlAuth = SqlAuthentication.create(sqlClient, new SqlAuthenticationOptions()) ;
  }

  @Override
  public UserService register(User user, Handler<AsyncResult<Void>> resultHandler) {
    String hash = sqlAuth.hash(
        "pbkdf2", // hashing algorithm (OWASP recommended)
        VertxContextPRNG.current().nextString(32), // secure random salt
        user.getPassword() // password
    );

    sqlClient.preparedQuery(INSERT_USER)
        .rxExecute(Tuple.of(user.getUsername(), hash, user.getEmail(), user.getFirstName(), user.getLastName()))
        .subscribe(
            result -> {resultHandler.handle(Future.succeededFuture());},
            error -> {
              LOGGER.error("unable to create user", error);
              resultHandler.handle(Future.failedFuture(error));
            }
        );
    return this;
  }
}
