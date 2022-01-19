package com.japharr.socialmedia.auth.migration;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.AbstractVerticle;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrationVerticle extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(MigrationVerticle.class);

  @Override
  public Completable rxStart() {
    doDbMigrations()
      .doOnError(err -> logger.error("Woops", err))
      .subscribe();

    return Completable.complete();
  }

  private Maybe<Void> doDbMigrations() {
    logger.info("doDbMigrations");
    JsonObject dbConfig = config().getJsonObject("db", new JsonObject());

    String url = dbConfig.getString("url", "jdbc:h2:mem:test_mem");
    String adminUser = dbConfig.getString("user", "sa");
    String adminPass = dbConfig.getString("password", "");

    Flyway flyway = Flyway.configure()
      .dataSource(url, adminUser, adminPass)
      .load();

    try {
      flyway.migrate();
      return Maybe.empty();
    } catch (Exception ex) {
      return Maybe.error(ex);
    }
  }
}
