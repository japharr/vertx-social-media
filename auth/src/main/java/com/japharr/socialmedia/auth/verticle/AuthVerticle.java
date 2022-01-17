package com.japharr.socialmedia.auth.verticle;

import com.japharr.socialmedia.auth.config.PgConfig;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.auth.sqlclient.SqlAuthentication;
import io.vertx.rxjava3.pgclient.PgPool;
import io.vertx.rxjava3.sqlclient.SqlClient;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthVerticle extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(AuthVerticle.class);

  private SqlClient sqlClient;
  private SqlAuthentication sqlAuthentication;

  @Override
  public Completable rxStart() {
    PgConnectOptions connectOptions = PgConfig.pgConnectOpts(config());
    PoolOptions pgPool = PgConfig.poolOptions(config());

    sqlClient = PgPool.client(vertx, connectOptions, pgPool);
    sqlAuthentication = SqlAuthentication.create(sqlClient, new SqlAuthenticationOptions());

    return Completable.complete();
  }
}
