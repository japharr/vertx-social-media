package com.japharr.socialmedia;

import com.japharr.socialmedia.verticle.AppVerticle;
import com.japharr.socialmedia.verticle.AuthVerticle;
import com.japharr.socialmedia.verticle.DatabaseVerticle;
import com.japharr.socialmedia.verticle.WebVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
  private static final Logger logger = LoggerFactory.getLogger(App.class);
  private static Vertx vertx;

  public static void main(String[] args) {
    vertx = Vertx.vertx();

    initConfig(vertx)
      .compose(App::deployVerticle)
      .onComplete(deployed -> {
        if(deployed.succeeded()) {
          logger.info("Verticle successfully deployed");
        } else {
          logger.error("Verticle failed to deploy", deployed.cause());
        }
      });
  }

  private static Future<JsonObject> initConfig(Vertx vertx) {
    var storeOptions = new ConfigStoreOptions()
      .setFormat("yaml").setType("file")
      .setConfig(new JsonObject().put("path", "conf.yaml"));

    var retrievalOptions = new ConfigRetrieverOptions()
      .addStore(storeOptions);

    var configRetriever = ConfigRetriever.create(vertx, retrievalOptions);

    return configRetriever.getConfig();
  }

  private static Future<Void> deployVerticle(JsonObject config) {
    DeploymentOptions opts = new DeploymentOptions().setConfig(config);

    Future<String> dbVerticle = vertx.deployVerticle(new DatabaseVerticle(), opts);
    Future<String> appVerticle = vertx.deployVerticle(new AppVerticle(), opts);
    //Future<String> authVerticle = vertx.deployVerticle(new AuthVerticle(), opts);
    Future<String> webVerticle = vertx.deployVerticle(new WebVerticle(), opts);

    return CompositeFuture.all(dbVerticle, appVerticle, webVerticle).mapEmpty();
  }
}

// todo
// load config
// create postg client
