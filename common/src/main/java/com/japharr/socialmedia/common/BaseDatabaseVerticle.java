package com.japharr.socialmedia.common;

import com.japharr.socialmedia.common.config.PgConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public abstract class BaseDatabaseVerticle extends AbstractVerticle {
  protected PgPool pgPool;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    PgConnectOptions connectOptions = PgConfig.pgConnectOpts(config());
    PoolOptions poolOptions = PgConfig.poolOptions(config());

    pgPool = PgPool.pool(vertx, connectOptions, poolOptions);

    createProxyServices(startPromise);
  }

  protected abstract void createProxyServices(Promise<Void> startPromise);

  @Override
  public void stop() {
    pgPool.close();
  }
}
