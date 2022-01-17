package com.japharr.socialmedia.auth.config;

import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;

public abstract class PgConfig {
  public static PgConnectOptions pgConnectOpts(JsonObject config) {
    JsonObject db = config.getJsonObject("db", new JsonObject());

    return new PgConnectOptions()
      .setPort(db.getInteger("port"))
      .setHost(db.getString("host"))
      .setDatabase(db.getString("database"))
      .setUser(db.getString("user"))
      .setPassword(db.getString("password"));
  }

  public static PoolOptions poolOptions(JsonObject config) {
    JsonObject db = config.getJsonObject("db", new JsonObject());
    JsonObject pool = config.getJsonObject("pool", new JsonObject());
    return new PoolOptions()
      .setMaxSize(db.getInteger("max-size"));
  }
}
