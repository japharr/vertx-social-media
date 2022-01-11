package com.japharr.socialmedia.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppVerticle extends AbstractVerticle {
  private static final Logger logger = LoggerFactory.getLogger(AppVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    JsonObject conf = config();

    int port = conf.getJsonObject("http").getInteger("port", 8888);

    vertx.createHttpServer()
      .requestHandler(r -> r.response().end("Hello from Vertx"))
      .listen(port, rx -> {
        if(rx.succeeded()) {
          logger.info("server listen on port: " + rx.result().actualPort());
          startPromise.complete();
        } else {
          logger.error("failed to start server", rx.cause());
          startPromise.fail(rx.cause());
        }
      });
  }
}
