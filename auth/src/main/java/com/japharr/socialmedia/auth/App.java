package com.japharr.socialmedia.auth;

import com.japharr.socialmedia.auth.api.WebVerticle;
import com.japharr.socialmedia.auth.database.DatabaseVerticle;
import com.japharr.socialmedia.auth.migration.MigrationVerticle;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.rxjava3.config.ConfigRetriever;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
  private static final Logger logger = LoggerFactory.getLogger(App.class);
  private static Vertx vertx;

  public static void main(String[] args) {
    vertx = Vertx.vertx();

    initConfig()
      .map(App::deployVerticle)
      .subscribe(
        rx -> logger.info("Verticle successfully deployed"),
        error -> logger.error("Verticle failed to deploy", error)
      );
  }

  private static Single<JsonObject> initConfig() {
    var storeOptions = new ConfigStoreOptions()
      .setFormat("yaml").setType("file")
      .setConfig(new JsonObject().put("path", "conf.yaml"));

    var retrievalOptions = new ConfigRetrieverOptions()
      .addStore(storeOptions);

    var configRetriever = ConfigRetriever.create(vertx, retrievalOptions);

    return configRetriever.getConfig();
  }

  private static Single<String> deployVerticle(JsonObject config) {
    DeploymentOptions opts = new DeploymentOptions().setConfig(config);

    Single<String> migrationDeployment = vertx.rxDeployVerticle(new MigrationVerticle(), opts);
    Single<String> databaseDeployment = vertx.rxDeployVerticle(new DatabaseVerticle(), opts);
    Single<String> webDeployment = vertx.deployVerticle(new WebVerticle(), opts);

    //Single.zip(migrationDeployment, databaseDeployment, webDeployment)
    return migrationDeployment
      .flatMap(id -> databaseDeployment)
      .flatMap(id -> webDeployment);

    // return vertx.deployVerticle(new MigrationVerticle(), opts);
  }
}
