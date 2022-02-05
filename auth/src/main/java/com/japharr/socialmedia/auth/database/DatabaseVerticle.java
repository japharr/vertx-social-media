package com.japharr.socialmedia.auth.database;

import com.japharr.socialmedia.auth.database.service.UserDatabaseService;
import com.japharr.socialmedia.common.BaseDatabaseVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseVerticle extends BaseDatabaseVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseVerticle.class);

  private static final String EB_ADDRESSES = "eb.addresses";
  private static final String EB_DB_USER_ADDRESS = "db.user";

  private static final String POST_DATABASE_SERVICE_NAME = "POST_DATABASE_SERVICE_NAME";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOGGER.info("deploying DatabaseVerticle, {}", config());
    super.start(startPromise);

    String databaseEbAddress = config().getJsonObject(EB_ADDRESSES).getString(EB_DB_USER_ADDRESS);

    UserDatabaseService.create(pgPool, bindAndPublish(databaseEbAddress, POST_DATABASE_SERVICE_NAME, startPromise, UserDatabaseService.class));
  }

  private Handler<AsyncResult<UserDatabaseService>> onCreateUserDatabaseService(String databaseEbAddress, Promise<Void> startPromise) {
    return result -> {
      if(result.succeeded()) {
        LOGGER.info("succeeded");
        new ServiceBinder(vertx)
            .setAddress(databaseEbAddress)
            .register(UserDatabaseService.class, result.result())
            .exceptionHandler(throwable -> {
              LOGGER.error("Failed to establish PostgreSQL database service", throwable);
              startPromise.fail(throwable);
            })
            .completionHandler(res -> {
              LOGGER.info("PostgreSQL database service is successfully established in \"" + databaseEbAddress + "\"");
              publishEventBusService(POST_DATABASE_SERVICE_NAME, databaseEbAddress, UserDatabaseService.class, r -> {
                if(r.succeeded()) {
                  LOGGER.info("POST_DATABASE_SERVICE_NAME published");
                  startPromise.complete();
                } else {
                  LOGGER.error("POST_DATABASE_SERVICE_NAME failed to published");
                }
              });
            });
      } else {
        LOGGER.error("Failed to initiate the connection to database", result.cause());
        startPromise.fail(result.cause());
      }
    };
  }
}
