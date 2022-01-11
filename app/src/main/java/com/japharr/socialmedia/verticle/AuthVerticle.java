package com.japharr.socialmedia.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.sqlclient.SqlAuthentication;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.ext.auth.sqlclient.SqlUserUtil;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import org.postgresql.PGProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.util.Properties;

public class AuthVerticle extends AbstractVerticle {
  private SqlClient sqlClient;
  private AuthenticationProvider authenticationProvider;
  private SqlUserUtil userUtil;

  private static final Logger logger = LoggerFactory.getLogger(AuthVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    // Create the client pool
    sqlClient = PgPool.client(vertx, connectOptions(), poolOptions);
    authenticationProvider = SqlAuthentication.create(sqlClient, new SqlAuthenticationOptions());

    userUtil = SqlUserUtil.create(sqlClient);

    Router router = Router.router(vertx);
    BodyHandler bodyHandler = BodyHandler.create();
    router.post().handler(bodyHandler);
    router.put().handler(bodyHandler);

    router.post("/api/register")
      .handler(this::register);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8082, r -> {
        if(r.succeeded()) {
          logger.info("AuthVerticle deployed");
        } else {
          logger.info("failed to deployed");
        }
      });
  }

  private void register(RoutingContext ctx) {
    JsonObject body = jsonBody(ctx);
    String username = body.getString("username");
    String password = body.getString("password");

    userUtil
      .createUser("john", "pass123", rx -> {
        if(rx.succeeded()) {
          ctx.response().setStatusCode(200).end("user created!");
        } else {
          logger.error("error: {}", rx.cause().getMessage());
          ctx.response().setStatusCode(500).end(rx.cause().getMessage());
        }
      });
  }

  private JsonObject jsonBody(RoutingContext ctx) {
    if (ctx.getBody().length() == 0) {
      return new JsonObject();
    } else {
      return ctx.getBodyAsJson();
    }
  }



  private PgConnectOptions connectOptions() {
    JsonObject dbConfig = config().getJsonObject("db", new JsonObject());

    String dbUrl = dbConfig.getString("url", "jdbc:postgresql://127.0.0.1:5432/social_media");
    Properties props = org.postgresql.Driver.parseURL(dbUrl, null);

    String host = props.getProperty(PGProperty.PG_HOST.getName());
    int port = Integer.parseInt(props.getProperty(PGProperty.PG_PORT.getName()));
    String dbName = props.getProperty(PGProperty.PG_DBNAME.getName());

    logger.info("host: {}", host);
    logger.info("port: {}", port);
    logger.info("dbName: {}", dbName);
    logger.info("user: {}", dbConfig.getString("user"));
    logger.info("password: {}", dbConfig.getString("password"));

    return new PgConnectOptions()
      .setPort(port)
      .setHost(host)
      .setDatabase(dbName)
      .setUser(dbConfig.getString("user"))
      .setPassword(dbConfig.getString("password"));
  }
}
