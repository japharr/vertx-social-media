package com.japharr.socialmedia.auth.api;

import com.japharr.socialmedia.auth.api.handler.HttpRequestValidator;
import com.japharr.socialmedia.auth.api.handler.UserApi;
import com.japharr.socialmedia.auth.database.service.UserDatabaseService;
import com.japharr.socialmedia.common.handler.FailureHandler;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.auth.jwt.JWTAuth;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import io.vertx.rxjava3.ext.web.handler.JWTAuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.japharr.socialmedia.auth.api.Endpoints.*;

public class WebVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebVerticle.class);

  private static final String HTTP_KEY = "http";
  private static final String PORT_KEY = "port";
  private static final String EB_ADDRESSES = "eb.addresses";
  private static final String EB_DB_USER_ADDRESS = "db.user";

  private static final String ENV_PRIVATE_KEY = "PRIVATE_KEY";
  private static final String ENV_PUBLIC_KEY = "PUBLIC_KEY";
  private static final String JWT_ALGORITHM = "RS256";

  @Override
  public Completable rxStart() {
    LOGGER.info("deploying webVerticle");
    String privateKey = System.getenv(ENV_PRIVATE_KEY);
    String publicKey = System.getenv(ENV_PUBLIC_KEY);

    if(privateKey == null || publicKey == null) {
      LOGGER.error("No public or private key set in env");
      return Completable.error(new Exception("No public or private key set in env"));
    }

    var httpServer = vertx.createHttpServer();

    var jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
        .addPubSecKey(new PubSecKeyOptions()
            .setAlgorithm(JWT_ALGORITHM)
            .setBuffer(publicKey))
        .addPubSecKey(new PubSecKeyOptions()
            .setAlgorithm(JWT_ALGORITHM)
            .setBuffer(privateKey)));

    var userDatabaseService = UserDatabaseService.createProxy(vertx.getDelegate(),
      config().getJsonObject(EB_ADDRESSES).getString(EB_DB_USER_ADDRESS));

    var bodyHandler = BodyHandler.create();
    var jwtHandler = JWTAuthHandler.create(jwtAuth);

    var router = Router.router(vertx);

    router.post().handler(bodyHandler);
    router.put().handler(bodyHandler);

    router.post(REGISTER_NEW_USER)
        .handler(HttpRequestValidator.validateUser())
        .handler(UserApi.validateRegisterUser(userDatabaseService))
        .handler(UserApi.registerUser(userDatabaseService));

    router.post(AUTHENTICATE_USER).handler(UserApi.authenticate(userDatabaseService));
    router.post(GENERATE_TOKEN).handler(UserApi.token(userDatabaseService, jwtAuth));
    router.get(GET_USERS).handler(UserApi.findAll(userDatabaseService));

    router.route().failureHandler(new FailureHandler());

    int httpServerPort = config().getJsonObject(HTTP_KEY).getInteger(PORT_KEY);

    httpServer
      .requestHandler(router)
      .rxListen(httpServerPort)
      .subscribe(
        rx -> LOGGER.info("success: {}", rx.actualPort()),
        error -> LOGGER.error("error", error)
      );

    return Completable.complete();
  }
}
