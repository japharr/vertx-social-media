package com.japharr.socialmedia.auth.migration;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrationVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(MigrationVerticle.class);

  @Override
  public void start(Promise<Void> startFuture){
    doDbMigrations()
      .onComplete(rx -> {
        if(rx.succeeded()) {
          LOGGER.info("success");
          startFuture.complete();
        } else {
          LOGGER.error("error");
          startFuture.fail(rx.cause());
        }
      });
  }

  private Future<Void> doDbMigrations() {
    LOGGER.info("doDbMigrations");
    JsonObject dbConfig = config().getJsonObject("db", new JsonObject());


    String host = dbConfig.getString("host");
    int port = dbConfig.getInteger("port");
    String database = dbConfig.getString("database");
    String user = dbConfig.getString("user");
    String password = dbConfig.getString("password", "");

    String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);

    Flyway flyway = Flyway.configure()
      .dataSource(url, user, password)
      .load();

    try {
      flyway.migrate();
      return Future.succeededFuture();
    } catch (Exception ex) {
      return Future.failedFuture(ex);
    }
  }
}
