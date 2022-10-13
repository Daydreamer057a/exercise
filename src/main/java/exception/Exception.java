package exception;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Exception handler.
 * Sends a message easyly interpreted
 */
public class Exception {
    public static void error(RoutingContext routingContext, int status, String cause) {
        JsonObject error = new JsonObject()
            .put("error", cause)
            .put("code", status)
            .put("path", routingContext.request().path());
        routingContext.response()
            .putHeader("Content-Type", "application/json")
            .setStatusCode(status)
            .end(error.encodePrettily());
    }
}
