package com.japharr.socialmedia.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseVerticle extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    doDatabaseMigrations()
      .onComplete(res -> {
        if(res.succeeded()) {
          logger.info("Database migrated successfully");
          startPromise.complete();
        } else {
          logger.error("failed to migrate database", res.cause());
          startPromise.fail(res.cause());
        }
      });
  }

  private Future<Void> doDatabaseMigrations() {
    JsonObject dbConfig = config().getJsonObject("db", new JsonObject());
    String url = dbConfig.getString("url", "jdbc:postgresql://127.0.0.1:5432/social_media");
    String adminUser = dbConfig.getString("user", "postgres");
    String adminPass = dbConfig.getString("password", "introduction");

    Flyway flyway = Flyway.configure()
      .dataSource(url, adminUser, adminPass)
      .load();

    try {
      flyway.migrate();
      return Future.succeededFuture();
    } catch (FlywayException fe) {
      return Future.failedFuture(fe);
    }
  }
}
