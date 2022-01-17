package com.japharr.socialmedia.auth;

import com.japharr.socialmedia.auth.verticle.DbMigrationVerticle;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.CompositeFuture;
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

    return vertx.deployVerticle(new DbMigrationVerticle(), opts);
  }
}
