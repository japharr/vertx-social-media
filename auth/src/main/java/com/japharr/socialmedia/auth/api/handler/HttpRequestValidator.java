package com.japharr.socialmedia.auth.api.handler;

import com.japharr.socialmedia.auth.entity.User;
import com.japharr.socialmedia.auth.model.Login;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;

import static com.japharr.socialmedia.common.util.RestApiUtil.decodeBodyToObject;
import static com.japharr.socialmedia.common.util.RestApiUtil.restResponse;
import static com.japharr.socialmedia.common.util.ValidationUtils.extract;

public class HttpRequestValidator {
  public static Handler<RoutingContext> validateUser() {
    return ctx -> {
      var user = decodeBodyToObject(ctx, User.class);

      if (user == null) {
        ctx.fail(400);
        return;
      }

      var violations = User.validator.validate(user);
      if (violations.isValid()) {
        ctx.next();
        return;
      }

      restResponse(ctx, 400, extract(violations).encodePrettily());
    };
  }

  public static Handler<RoutingContext> validateLogin() {
    return ctx -> {
      var login = decodeBodyToObject(ctx, Login.class);

      if (login == null) {
        ctx.fail(400);
        return;
      }

      var violations = Login.validator.validate(login);
      if (violations.isValid()) {
        ctx.next();
        return;
      }

      restResponse(ctx, 400, extract(violations).encodePrettily());
    };
  }

}
