package app.config;

import app.exceptions.ApiException;
import app.routes.Routes;
import app.security.controllers.ISecurityController;
import app.security.controllers.impl.SecurityController;
import app.security.exceptions.NotAuthorizedException;
import app.security.routes.SecurityRoutes;
import app.utils.Utils;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;



public class ApplicationConfig {
    private static Routes routes = new Routes();
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final ISecurityController securityController = SecurityController.getInstance();

    public static void configuration (JavalinConfig config) {
        config.showJavalinBanner = false;
        config.bundledPlugins.enableRouteOverview("/routes");  // Endpoint for all Routes
        config.router.contextPath = "/api/v1"; // Base Path for all endpoints
        config.router.apiBuilder(routes.getRoutes()); //Registers all routes
        config.router.apiBuilder(new SecurityRoutes().getSecurityRoute);
        config.router.apiBuilder(new SecurityRoutes().getSecuredRoutes());
    }


    public static Javalin startServer (int port) {
        routes = new Routes();
        Javalin app = Javalin.create(ApplicationConfig::configuration);

        //Global exception handlers
        app.exception(Exception.class, (e, ctx1) -> generalExceptionHandler(e, ctx1));
        app.exception(ApiException.class, (e, ctx1) -> apiExceptionHandler(e, ctx1));
        app.exception(app.security.exceptions.ApiException.class, (e, ctx1) -> apiSecurityExceptionHandler(e, ctx1));
        app.exception(NotAuthorizedException.class, (e, ctx1) -> apiNotAuthorizedExceptionHandler(e, ctx1));

        // Logging til requests
        app.before(ctx -> {logger.info("Request: " + ctx.method() + ctx.path() + " Body: " + ctx.body());});

        // Logging til responses
        app.after(ctx -> {logger.info("Response: " + ctx.method() + ctx.path() + " Status: " + ctx.status() + " Body: " + ctx.result());});

        // Check before any endpoint is reached
        app.beforeMatched(securityController.authenticate()); // check if there is a valid token in the header
        app.beforeMatched(securityController.authorize()); // check if the user has the required role

        app.start(port);
        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }

    private static void generalExceptionHandler(Exception e, Context ctx) {
        ctx.status(500);
        logger.error("An unhandled exception occurred", e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "error", e.getMessage()));
    }

    public static void apiExceptionHandler(ApiException e, Context ctx) {
        ctx.status(e.getStatusCode());
        logger.warn("An API exception occurred: Code: {}, Message: {}", e.getStatusCode(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "warning", e.getMessage()));
    }

    // TODO overvej at fjern dem
    public static void apiNotAuthorizedExceptionHandler(NotAuthorizedException e, Context ctx) {
        ctx.status(e.getStatusCode());
        logger.warn("A Not authorized Security API exception occurred: Code: {}, Message: {}", e.getStatusCode(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "warning", e.getMessage()));
    }

    public static void apiSecurityExceptionHandler(app.security.exceptions.ApiException e, Context ctx) {
        ctx.status(e.getCode());
        logger.warn("A Security API exception occurred: Code: {}, Message: {}", e.getCode(), e.getMessage());
        ctx.json(Utils.convertToJsonMessage(ctx, "warning", e.getMessage()));
    }
}
