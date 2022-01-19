package com.japharr.socialmedia.auth.database;

import com.japharr.socialmedia.auth.config.PgConfig;
import com.japharr.socialmedia.auth.database.service.UserService;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.impl.PgPoolOptions;
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

  @Override
  public Completable rxStart() {
    PgConnectOptions connectOptions = PgConfig.pgConnectOpts(config());
    PoolOptions pgPool = PgConfig.poolOptions(config());

    sqlClient = PgPool.client(vertx, connectOptions, pgPool);

    String databaseEbAddress = config().getString("CONFIG_DB_EB_QUEUE");

    UserService.create(sqlClient, result -> {

    });

    return Completable.complete();
  }
}
