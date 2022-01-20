package com.japharr.socialmedia.auth;

import com.japharr.socialmedia.auth.api.WebVerticle;
import com.japharr.socialmedia.auth.database.DatabaseVerticle;
import com.japharr.socialmedia.auth.migration.MigrationVerticle;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.config.ConfigRetriever;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
  private Disposable disposable;

  @Override
  public void start(Promise<Void> startFuture) throws Exception {
    disposable = initConfig()
      .flatMap(this::deployVerticle)
      .subscribe(
        id -> startFuture.complete(),
        error -> {
          LOGGER.error("an error is handled", error);
          startFuture.fail(error);
        }
      );
  }

  private Single<String> deployVerticle(JsonObject config) {
    var deploymentOptions = new DeploymentOptions().setConfig(config);

    return vertx.deployVerticle(new MigrationVerticle(), deploymentOptions)
      .flatMap(id -> vertx.deployVerticle(new DatabaseVerticle(), deploymentOptions))
      .flatMap(id -> vertx.deployVerticle(new WebVerticle(), deploymentOptions));
  }

  private Single<JsonObject> initConfig() {
    LOGGER.info("initConfig");
    var storeOptions = new ConfigStoreOptions()
      .setFormat("yaml").setType("file")
      .setConfig(new JsonObject().put("path", "conf.yaml"));

    var retrievalOptions = new ConfigRetrieverOptions()
      .addStore(storeOptions);

    var configRetriever = ConfigRetriever.create(vertx, retrievalOptions);

    return configRetriever.getConfig();
  }

  @Override
  public void stop(Promise<Void> stopFuture) throws Exception {
    if(disposable != null)
      disposable.dispose();
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
