package com.japharr.socialmedia.common;

import com.japharr.socialmedia.common.config.PgConfig;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDatabaseVerticle extends MicroServiceVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseDatabaseVerticle.class);

  protected PgPool pgPool;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    super.start(startPromise);
    PgConnectOptions connectOptions = PgConfig.pgConnectOpts(config());
    PoolOptions poolOptions = PgConfig.poolOptions(config());

    pgPool = PgPool.pool(vertx, connectOptions, poolOptions);
  }

  protected <T> Handler<AsyncResult<T>> bindAndPublish(String databaseEbAddress, String dbServiceName, Promise<Void> startPromise, Class<T> clazz) {
    return result -> {
      if(result.succeeded()) {
        new ServiceBinder(vertx)
            .setAddress(databaseEbAddress)
            .register(clazz, result.result())
            .exceptionHandler(throwable -> {
              LOGGER.error("Failed to establish PostgreSQL database service", throwable);
              startPromise.fail(throwable);
            })
            .completionHandler(res -> {
              LOGGER.info("PostgreSQL database service is successfully established in \"" + databaseEbAddress + "\"");
              publishEventBusService(dbServiceName, databaseEbAddress, clazz, r -> {
                if(r.succeeded()) {
                  LOGGER.info("POST_DATABASE_SERVICE_NAME published");
                  startPromise.complete();
                } else {
                  LOGGER.error("POST_DATABASE_SERVICE_NAME failed to published");
                  startPromise.fail(r.cause());
                }
              });
            });
      } else {
        LOGGER.error("Failed to initiate the connection to database", result.cause());
        startPromise.fail(result.cause());
      }
    };
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    super.stop(stopPromise);
    if(pgPool != null) {
      pgPool.close(rx -> {
        if(rx.failed()) stopPromise.fail(rx.cause());
        else stopPromise.complete();
      });
    } else {
      stopPromise.complete();
    }
  }
}
