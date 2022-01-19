package com.japharr.socialmedia.auth.api.handler;

import com.japharr.socialmedia.auth.database.rxjava3.service.UserService;
import com.japharr.socialmedia.auth.entity.User;
import com.japharr.socialmedia.common.exception.BadRequestException;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;

import static com.japharr.socialmedia.common.util.RestApiUtil.decodeBodyToObject;
import static com.japharr.socialmedia.common.util.RestApiUtil.restResponse;

public class UserApi {
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
}
