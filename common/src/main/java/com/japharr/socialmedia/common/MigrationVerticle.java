package com.japharr.socialmedia.common;

import com.japharr.socialmedia.common.config.PgConfig;
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
          Future.future(p -> startFuture.complete());
        } else {
          LOGGER.error("error");
          Future.future(p -> startFuture.fail(rx.cause()));
        }
      });
  }

  private Future<Void> doDbMigrations() {
    LOGGER.info("doDbMigrations");
    JsonObject db = config().getJsonObject(PgConfig.DB, new JsonObject());

    String user = db.getString(PgConfig.USER);
    String password = db.getString(PgConfig.PASSWORD);

    String url = PgConfig.resolveUrl(db);

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
