package com.japharr.socialmedia.auth;

import com.japharr.socialmedia.auth.api.WebVerticle;
import com.japharr.socialmedia.auth.database.DatabaseVerticle;
import com.japharr.socialmedia.auth.migration.MigrationVerticle;
import io.reactivex.rxjava3.core.Single;
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

  @Override
  public void start(Promise<Void> startFuture) throws Exception {
    initConfig()
      .doOnError(startFuture::fail)
      .map(this::deployVerticle)
      .subscribe(
        id -> startFuture.complete(),
        startFuture::fail
      );
  }

  private Single<String> deployVerticle(JsonObject config) {
    var deploymentOptions = new DeploymentOptions().setConfig(config);

    Single<String> migrationDeployment = vertx.deployVerticle(new MigrationVerticle(), deploymentOptions);
    Single<String> databaseDeployment = vertx.deployVerticle(new DatabaseVerticle(), deploymentOptions);
    Single<String> webDeployment = vertx.deployVerticle(new WebVerticle(), deploymentOptions);

    return migrationDeployment
      .flatMap(id -> databaseDeployment)
      .flatMap(id -> webDeployment);
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

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
