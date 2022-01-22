package com.japharr.socialmedia.auth.api.handler;

import com.japharr.socialmedia.auth.database.rxjava3.service.UserDatabaseService;
import com.japharr.socialmedia.auth.entity.User;
import com.japharr.socialmedia.common.exception.BadRequestException;
import com.japharr.socialmedia.common.ext.Pair;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.rxjava3.ext.auth.jwt.JWTAuth;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.japharr.socialmedia.common.util.RestApiUtil.decodeBodyToObject;
import static com.japharr.socialmedia.common.util.RestApiUtil.restResponse;

public class UserApi {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserApi.class);

  public static Handler<RoutingContext> registerUser(UserDatabaseService userDatabaseService) {
    return ctx -> {
      User user = decodeBodyToObject(ctx, User.class);

      userDatabaseService.rxRegister(user)
        .subscribe(
          () -> restResponse(ctx, 200),
          throwable -> ctx.fail(new BadRequestException(throwable))
        );
    };
  }

  public static Handler<RoutingContext> validateRegisterUser(UserDatabaseService userDatabaseService) {
    return ctx -> {
      User user = decodeBodyToObject(ctx, User.class);

      var result = Single.zip(
          userDatabaseService.rxCountByUsernameOrEmail(user.getUsername()),
          userDatabaseService.rxCountByUsernameOrEmail(user.getEmail()),
          Pair::of
      ).subscribe(
          res -> {
            JsonArray array = new JsonArray();
            if(res.x() > 0) array.add(constructError("Username already in use"));
            if(res.y() > 0) array.add(constructError("Email already in use"));

            if(!array.isEmpty()) {
              restResponse(ctx, 400, array.encodePrettily());
            } else {
              ctx.next();
            }
          }
      );
    };
  }

  public static JsonObject constructError(String message) {
    return new JsonObject().put("message", message);
  }

  public static Handler<RoutingContext> findAll(UserDatabaseService userDatabaseService) {
    return ctx -> {
      userDatabaseService.rxFindAll()
          .subscribe(
              array -> restResponse(ctx, 200, array.encodePrettily()),
              throwable -> ctx.fail(new BadRequestException(throwable))
          );
    };
  }

  public static Handler<RoutingContext> authenticate(UserDatabaseService userDatabaseService) {
    return ctx -> {
      User user = decodeBodyToObject(ctx, User.class);

      userDatabaseService.rxAuthenticate(user)
          .subscribe(
              () -> restResponse(ctx, 200),
              throwable -> ctx.fail(new BadRequestException(throwable))
          );
    };
  }

  public static Handler<RoutingContext> token(UserDatabaseService userDatabaseService, JWTAuth jwtAuth) {
    return ctx -> {
      User user = decodeBodyToObject(ctx, User.class);

      userDatabaseService.rxAuthenticate(user)
          .andThen(userDatabaseService.rxFindByUsernameOrEmail(user.getUsername()))
          .map(json -> makeJwtToken(user.getUsername(), json, jwtAuth))
          .subscribe(
              token -> restResponse(ctx, 200, token),
              throwable -> ctx.fail(new BadRequestException(throwable))
          );
    };
  }

  private static String makeJwtToken(String username, JsonObject claims, JWTAuth jwtAuth) {
    JWTOptions jwtOptions = new JWTOptions()
        .setAlgorithm("RS256")
        .setExpiresInMinutes(10_080)
        .setIssuer("social-media")
        .setSubject(username);

    return jwtAuth.generateToken(claims, jwtOptions);
  }
}
