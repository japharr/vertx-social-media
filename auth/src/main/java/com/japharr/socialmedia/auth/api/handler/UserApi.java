package com.japharr.socialmedia.auth.api.handler;

import com.japharr.socialmedia.auth.database.rxjava3.service.UserService;
import com.japharr.socialmedia.auth.entity.User;
import com.japharr.socialmedia.common.exception.BadRequestException;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.japharr.socialmedia.common.util.RestApiUtil.decodeBodyToObject;
import static com.japharr.socialmedia.common.util.RestApiUtil.restResponse;

public class UserApi {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserApi.class);

  public static Handler<RoutingContext> registerUser(UserService userService) {
    return ctx -> {
      User user = decodeBodyToObject(ctx, User.class);

      userService.register(user)
        .subscribe(
          () -> restResponse(ctx, 200),
          throwable -> ctx.fail(new BadRequestException(throwable))
        );
    };
  }

  public static Handler<RoutingContext> findAll(UserService userService) {
    return ctx -> {
      userService.findAll()
          .subscribe(
              array -> restResponse(ctx, 200, array.encodePrettily()),
              throwable -> ctx.fail(new BadRequestException(throwable))
          );
    };
  }

  public static Handler<RoutingContext> authenticate(UserService userService) {
    return ctx -> {
      User user = decodeBodyToObject(ctx, User.class);

      userService.rxAuthenticate(user)
          .subscribe(
              () -> restResponse(ctx, 200),
              throwable -> ctx.fail(new BadRequestException(throwable))
          );
    };
  }
}
