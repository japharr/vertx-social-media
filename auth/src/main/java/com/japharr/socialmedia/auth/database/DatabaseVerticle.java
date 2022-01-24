package com.japharr.socialmedia.auth.database;

import com.japharr.socialmedia.auth.config.PgConfig;
import com.japharr.socialmedia.auth.database.service.UserDatabaseService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.impl.DiscoveryImpl;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.sqlclient.PoolOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseVerticle.class);

  private static final String EB_ADDRESSES = "eb.addresses";
  private static final String EB_DB_USER_ADDRESS = "db.user";

  private static final String POST_DATABASE_SERVICE_NAME = "POST_DATABASE_SERVICE_NAME";

  private ServiceDiscovery discovery;
  private Record postDatabaseServiceRecord;

  private PgPool pgPool;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOGGER.info("deploying DatabaseVerticle, {}", config());
    discovery = new DiscoveryImpl(vertx, new ServiceDiscoveryOptions());

    PgConnectOptions connectOptions = PgConfig.pgConnectOpts(config());
    PoolOptions poolOptions = PgConfig.poolOptions(config());

    pgPool = PgPool.pool(vertx, connectOptions, poolOptions);

    String databaseEbAddress = config().getJsonObject(EB_ADDRESSES).getString(EB_DB_USER_ADDRESS);

    UserDatabaseService.create(pgPool, result -> {
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
            postDatabaseServiceRecord = EventBusService.createRecord(
                POST_DATABASE_SERVICE_NAME, // The service name
                databaseEbAddress, // the service address,
                UserDatabaseService.class // the service interface
            );

            discovery.publish(postDatabaseServiceRecord, pub -> {
              if(pub.succeeded()) {
                LOGGER.info("published userDbService OK");
                startPromise.complete();
              } else {
                LOGGER.error("published userDbService OK", pub.cause());
                startPromise.fail(pub.cause());
              }
            });
          });
      } else {
        LOGGER.error("Failed to initiate the connection to database", result.cause());
        //startPromise.fail(result.cause());
        Future.future(p -> startPromise.fail(result.cause()));
      }
    });
  }

  @Override
  public void stop() {
    pgPool.close();
  }
}
