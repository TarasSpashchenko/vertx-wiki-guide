package io.vertx.guides.wiki.investigation;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.guides.wiki.investigation.service.InvestigationService;

public class HttpRoutersSimpleComposeInvestigationVerticle extends AbstractVerticle {
  private static final String FUTURE_KEY = "responseFutureKey";

  private InvestigationService investigationService;

  public void start(Future<Void> startFuture) throws Exception {

    Vertx vertx = getVertx();

    investigationService = InvestigationService.createProxy(vertx, InvestigationService.INVESTIGATION_SERVICE_QUEUE);

    HttpServer httpServer = vertx.createHttpServer();

    configureRouter(Router.router(vertx), arRouter -> {
      if (arRouter.failed()) {
        startFuture.fail(arRouter.cause());
      }
      Router router = arRouter.result();
      httpServer
        .requestHandler(router::accept)
        .listen(7777, ar -> {
          if (ar.succeeded()) {
            startFuture.complete();
          } else {
            startFuture.fail(ar.cause());
          }
        });
    });

  }

  private Future<JsonObject> getJsonObjectFuture(RoutingContext routingContext) {
    return ((Future<JsonObject>) routingContext.get(FUTURE_KEY));
  }

  private void createRoutingContextHandler(RoutingContext routingContext) {
    Future<JsonObject> localJsonObjectFuture = Future.future();
    routingContext.put(FUTURE_KEY, getJsonObjectFuture(routingContext).compose(prevResult -> {
      investigationService.handleData(routingContext.normalisedPath(), routingContext.getBodyAsString(), ar -> {
        AsyncResult<JsonObject> localAsyncResult = ar.succeeded() ? ar.map(result -> result.put("prev", prevResult)) : ar;
        localJsonObjectFuture.handle(localAsyncResult);
      });

      return localJsonObjectFuture;
    }));
    routingContext.next();
  }


  private void createRoutingContextHandler2(RoutingContext routingContext) {
    Future<JsonObject> localJsonObjectFuture = Future.future();

    routingContext.put(FUTURE_KEY, getJsonObjectFuture(routingContext).compose(prevResult -> localJsonObjectFuture));

    investigationService.handleData(routingContext.normalisedPath(), routingContext.getBodyAsString(), ar -> {
      AsyncResult<JsonObject> localAsyncResult = ar.succeeded() ? ar.map(result -> result.put("prev", "-xxx-")) : ar;
      localJsonObjectFuture.handle(localAsyncResult);
    });

    routingContext.next();
  }

  private void configureRouter(Router router, Handler<AsyncResult<Router>> resultHandler) {
    router.route().handler(CookieHandler.create());
    router.route().handler(BodyHandler.create());

    router.route().handler(routingContext -> {
      routingContext.put(FUTURE_KEY, Future.succeededFuture(new JsonObject()));
      routingContext.next();
    });

    router.post("/:level/*").handler(this::createRoutingContextHandler2);
    router.post("/:level/:level2/*").handler(this::createRoutingContextHandler2);
    router.post("/:level/:level2/:level3/*").handler(this::createRoutingContextHandler2);
    router.post("/:level/:level2/:level3/:level4/*").handler(this::createRoutingContextHandler2);
    router.post("/:level/:level2/:level3/:level4/:level5/*").handler(this::createRoutingContextHandler2);
    router.post("/:level/:level2/:level3/:level4/:level5/:level6/*").handler(this::createRoutingContextHandler2);
    router.post("/:level/:level2/:level3/:level4/:level5/:level6/:level7/*").handler(this::createRoutingContextHandler2);

    router.route().handler(routingContext -> {
      getJsonObjectFuture(routingContext).setHandler(ar -> {
        HttpServerResponse response = routingContext.response();
        if (ar.succeeded()) {
          response
            .setStatusCode(200)
            .end(ar.result().put("finalMessage", "This this the end of response...").toBuffer());
        } else {
          response
            .setStatusCode(500)
            .end(ar.cause().toString());
        }
      });
    });

    resultHandler.handle(Future.succeededFuture(router));
  }
}
