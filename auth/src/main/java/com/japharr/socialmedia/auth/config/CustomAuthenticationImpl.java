package com.japharr.socialmedia.auth.config;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.HashingStrategy;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.Credentials;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.sqlclient.SqlAuthentication;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.ext.auth.sqlclient.impl.SqlAuthenticationImpl;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import java.util.Objects;

public class CustomAuthenticationImpl extends SqlAuthenticationImpl {
  private final SqlClient client;
  private final SqlAuthenticationOptions options;
  private final HashingStrategy strategy = HashingStrategy.load();

  public CustomAuthenticationImpl(SqlClient client, SqlAuthenticationOptions options) {
    super(client, options);
    this.client = Objects.requireNonNull(client);
    this.options = Objects.requireNonNull(options);
  }

   static SqlAuthentication create(SqlClient client, SqlAuthenticationOptions options) {
    return new SqlAuthenticationImpl(client, options);
  }

  @Override
  public void authenticate(Credentials credentials, Handler<AsyncResult<User>> resultHandler) {
    try {
      UsernamePasswordCredentials authInfo = (UsernamePasswordCredentials) credentials;
      authInfo.checkValid(null);

      client.preparedQuery(options.getAuthenticationQuery()).execute(Tuple.of(authInfo.getUsername()), preparedQuery -> {
        if (preparedQuery.succeeded()) {
          final RowSet<Row> rows = preparedQuery.result();
          switch (rows.size()) {
            case 0: {
              // Unknown user/password
              resultHandler.handle(Future.failedFuture("Invalid username/password"));
              break;
            }
            case 1: {
              Row row = rows.iterator().next();
              String hashedStoredPwd = row.getString(0);
              String email = row.getString(1);
              boolean emailVerify = row.getBoolean(2);
              if (authInfo.getUsername().equalsIgnoreCase(email) && !emailVerify) {
                resultHandler.handle(Future.failedFuture("Kindly verified your email address"));
              } else if (strategy.verify(hashedStoredPwd, authInfo.getPassword())) {
                resultHandler.handle(Future.succeededFuture(User.fromName(authInfo.getUsername())));
              } else {
                resultHandler.handle(Future.failedFuture("Invalid username/password"));
              }
              break;
            }
            default: {
              // More than one row returned!
              resultHandler.handle(Future.failedFuture("Failure in authentication"));
              break;
            }
          }
        } else {
          resultHandler.handle(Future.failedFuture(preparedQuery.cause()));
        }
      });
    } catch (RuntimeException e) {
      resultHandler.handle(Future.failedFuture(e));
    }
  }
}
