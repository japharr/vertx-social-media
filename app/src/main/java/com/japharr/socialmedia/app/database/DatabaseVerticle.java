package com.japharr.socialmedia.app.database;

import com.japharr.socialmedia.app.database.service.PostDatabaseService;
import com.japharr.socialmedia.app.database.service.UserDatabaseService;
import com.japharr.socialmedia.common.BaseDatabaseVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DatabaseVerticle extends BaseDatabaseVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(BaseDatabaseVerticle.class);
  private static final String EB_ADDRESSES = "eb.addresses";
  private static final String EB_DB_POST_ADDRESS = "db.post";
  private static final String EB_DB_USER_ADDRESS = "db.user";

  private static final String PROXY_SERVICES = "proxy.service";
  private static final String PS_DB_POST_NAME = "db.post";
  private static final String PS_DB_USER_NAME = "db.user";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOGGER.info("deploying DatabaseVerticle, {}", config());
    super.start(startPromise);

    String ebDbPostAddress = config().getJsonObject(EB_ADDRESSES).getString(EB_DB_POST_ADDRESS);
    String ebDbUserAddress = config().getJsonObject(EB_ADDRESSES).getString(EB_DB_USER_ADDRESS);

    String servicePostName = config().getJsonObject(PROXY_SERVICES).getString(PS_DB_POST_NAME);
    String serviceUserName = config().getJsonObject(PROXY_SERVICES).getString(PS_DB_USER_NAME);

    Promise<Void> userDbServicePromise = Promise.promise();
    Promise<Void> postDbServicePromise = Promise.promise();

    UserDatabaseService.create(pgPool, bindAndPublish(ebDbPostAddress, serviceUserName, userDbServicePromise, UserDatabaseService.class));
    PostDatabaseService.create(pgPool, bindAndPublish(ebDbUserAddress, servicePostName, postDbServicePromise, PostDatabaseService.class));

    CompositeFuture.all(userDbServicePromise.future(), postDbServicePromise.future())
        .onComplete(r -> {
          if(r.succeeded()) {
            LOGGER.info("both completed");
            startPromise.complete();
          } else {
            LOGGER.error("any failed", r.cause());
            startPromise.fail(r.cause());
          }
        });
  }
}
