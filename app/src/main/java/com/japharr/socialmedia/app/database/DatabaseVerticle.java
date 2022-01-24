package com.japharr.socialmedia.app.database;

import com.japharr.socialmedia.app.database.service.PostDatabaseService;
import com.japharr.socialmedia.common.BaseDatabaseVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.impl.DiscoveryImpl;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseVerticle extends BaseDatabaseVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseDatabaseVerticle.class);
  private static final String EB_ADDRESSES = "eb.addresses";
  private static final String EB_DB_POST_ADDRESS = "db.post";
  private static final String POST_DATABASE_SERVICE_NAME = "post_db_service";

  private ServiceDiscovery discovery;
  private Record postDatabaseServiceRecord;

  @Override
  protected void createProxyServices(Promise<Void> startPromise) {
    discovery = new DiscoveryImpl(vertx, new ServiceDiscoveryOptions());

    String databaseEbAddress = config().getJsonObject(EB_ADDRESSES).getString(EB_DB_POST_ADDRESS);

    PostDatabaseService.create(pgPool, result -> {
      if(result.succeeded()) {
        LOGGER.info("succeeded");
        new ServiceBinder(vertx)
            .setAddress(databaseEbAddress)
            .register(PostDatabaseService.class, result.result())
            .exceptionHandler(throwable -> {
              LOGGER.error("Failed to establish PostgreSQL database service", throwable);
              startPromise.fail(throwable);
            })
            .completionHandler(res -> {
              LOGGER.info("PostgreSQL database service is successfully established in \"" + databaseEbAddress + "\"");

              postDatabaseServiceRecord = EventBusService.createRecord(
                  POST_DATABASE_SERVICE_NAME, // The service name
                  databaseEbAddress, // the service address,
                  PostDatabaseService.class // the service interface
              );

              discovery.publish(postDatabaseServiceRecord);

              startPromise.complete();
            });
      } else {
        LOGGER.error("Failed to initiate the connection to database", result.cause());
        //startPromise.fail(result.cause());
        Future.future(p -> startPromise.fail(result.cause()));
      }
    });
  }
}
