package com.japharr.socialmedia.auth.api.handler;

import am.ik.yavi.core.ConstraintViolations;
import com.japharr.socialmedia.auth.entity.User;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.ext.web.RoutingContext;

import static com.japharr.socialmedia.common.util.RestApiUtil.decodeBodyToObject;
import static com.japharr.socialmedia.common.util.RestApiUtil.restResponse;

public class HttpRequestValidator {
  public static Handler<RoutingContext> validateUser() {
    return ctx -> {
      var user = decodeBodyToObject(ctx, User.class);

      if(user == null) {
        ctx.fail(400);
        return;
      }

      var violations = User.validator.validate(user);
      if(violations.isValid()) {
        ctx.next();
        return;
      }

      JsonArray array = new JsonArray();
      violations.forEach(r -> {
        array.add(new JsonObject().put(r.messageKey(), r.message()));
      });
      restResponse(ctx, 400, array.encodePrettily());
    };
  }
}
