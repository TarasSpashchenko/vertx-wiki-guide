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
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.guides.wiki.investigation.service.InvestigationService;

public class HttpRoutersInvestigationVerticle extends AbstractVerticle {

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
        .listen(9999, ar -> {
          if (ar.succeeded()) {
            startFuture.complete();
          } else {
            startFuture.fail(ar.cause());
          }
        });
    });

  }

  private void configureRouter(Router router, Handler<AsyncResult<Router>> resultHandler) {
    router.route().handler(CookieHandler.create());
    router.route().handler(BodyHandler.create());

    router.route().handler(routingContext -> {
      System.out.println("--- - 0 The Global handler: " + routingContext.normalisedPath());
      routingContext.put(FUTURE_KEY, Future.succeededFuture(new JsonObject()));
      routingContext.next();
    });

    router.route("/").handler(routingContext -> {
      System.out.println("--- - 1 / handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.route("/*").handler(routingContext -> {
      System.out.println("--- - 2 /* handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.route("/level1").handler(routingContext -> {
      System.out.println("--- - 3 /level1 handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.route("/level1/*").handler(routingContext -> {
      System.out.println("--- - 4 /level1/* handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.route("/level1/level2").handler(routingContext -> {
      System.out.println("--- - 5 /level1/level2 handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.route("/level1/level2/*").handler(routingContext -> {
      System.out.println("--- - 6 /level1/level2/* handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.route("/level1/level2/level3").handler(routingContext -> {
      System.out.println("--- - 7 /level1/level2/level3 handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.route("/level1/level2/level3/*").handler(routingContext -> {
      System.out.println("--- - 8 /level1/level2/level3/* handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.get("/").handler(routingContext -> {
      System.out.println("-GET - 1 / handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.get("/:level").handler(routingContext -> {
      System.out.println("-GET - 1+ /:level handler: " + routingContext.normalisedPath() + " level: "
        + routingContext.request().getParam("level"));
      routingContext.next();
    });

    router.get("/:level/:level2").handler(routingContext -> {
      System.out.println("-GET - 1++ /:level/:level2 handler: " + routingContext.normalisedPath() + " level: "
        + routingContext.request().getParam("level") + " level2: "
        + routingContext.request().getParam("level2"));
      routingContext.next();
    });

    router.get("/:level/:level2/:level3").handler(routingContext -> {
      System.out.println("-GET - 1+++ /:level/:level2/:level3 handler: " + routingContext.normalisedPath() + " level: "
        + routingContext.request().getParam("level") + " level2: "
        + routingContext.request().getParam("level2") + " level3: "
        + routingContext.request().getParam("level3"));
      routingContext.next();
    });

    router.get("/*").handler(routingContext -> {
      System.out.println("-GET - 2 /* handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.get("/level1").handler(routingContext -> {
      System.out.println("-GET - 3 /level1 handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.get("/level1/:level2").handler(routingContext -> {
      System.out.println("-GET - 3+ /level1/:level2 handler: " + routingContext.normalisedPath()
        + " level2: " + routingContext.request().getParam("level2"));
      routingContext.next();
    });

    router.get("/level1/*").handler(routingContext -> {
      System.out.println("-GET - 4 /level1/* handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.get("/level1/level2").handler(routingContext -> {
      System.out.println("-GET - 5 /level1/level2 handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.get("/level1/level2/:level3").handler(routingContext -> {
      System.out.println("-GET - 5+ /level1/level2/:level3 handler: " + routingContext.normalisedPath()
        + " level3: " + routingContext.request().getParam("level3"));
      routingContext.next();
    });

    router.get("/level1/level2/*").handler(routingContext -> {
      System.out.println("-GET - 6 /level1/level2/* handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.get("/level1/level2/level3").handler(routingContext -> {
      System.out.println("-GET - 7 /level1/level2/level3 handler: " + routingContext.normalisedPath());
      routingContext.next();
    });

    router.get("/level1/level2/level3/:level4").handler(routingContext -> {
      System.out.println("-GET - 7+ /level1/level2/level3/:level4 handler: " + routingContext.normalisedPath()
        + routingContext.normalisedPath() + " level4: " + routingContext.request().getParam("level4"));
      routingContext.next();
    });

    router.get("/level1/level2/level3/*").handler(routingContext -> {
      System.out.println("-GET - 8 /level1/level2/level3/* handler: " + routingContext.normalisedPath());
      routingContext.next();
    });


    router.post("/").handler(routingContext -> {
      System.out.println("-POST - 1 / handler: " + routingContext.normalisedPath());
      System.out.println("-----body: " + routingContext.getBodyAsString());
      routingContext.next();
    });

    router.post("/:level").handler(routingContext -> {
      System.out.println("-POST - 1+ /:level handler: " + routingContext.normalisedPath() + " level: "
        + routingContext.request().getParam("level"));
      System.out.println("-----body: " + routingContext.getBodyAsString());
      System.out.println("-----:level " + routingContext.pathParam("level"));
      routingContext.next();
    });

    router.post("/:level/:level2").handler(routingContext -> {
      System.out.println("-POST - 1++ /:level/:level2 handler: " + routingContext.normalisedPath() + " level: "
        + routingContext.request().getParam("level") + " level2: "
        + routingContext.request().getParam("level2"));
      System.out.println("-----body: " + routingContext.getBodyAsString());
      System.out.println("-----:level " + routingContext.pathParam("level"));
      System.out.println("-----:level2 " + routingContext.pathParam("level2"));
      routingContext.request().params().forEach(e -> System.out.println("-----param<" + e.getKey() + "> - " + e.getValue()));
      routingContext.next();
    });

    router.post("/:level/:level2/*").handler(routingContext -> {
      Future<JsonObject> jsonObjectFuture = Future.future();
      routingContext.put(FUTURE_KEY, ((Future<JsonObject>)routingContext.get(FUTURE_KEY)).compose(x -> jsonObjectFuture));

      System.out.println("-POST - 1++* /:level/:level2/* handler: " + routingContext.normalisedPath() + " level: "
        + routingContext.request().getParam("level") + " level2: "
        + routingContext.request().getParam("level2"));
      System.out.println("-----body: " + routingContext.getBodyAsString());
      System.out.println("-----:level " + routingContext.pathParam("level"));
      System.out.println("-----:level2 " + routingContext.pathParam("level2"));
      routingContext.request().params().forEach(e -> System.out.println("-----param<" + e.getKey() + "> - " + e.getValue()));

      investigationService.handleData(routingContext.normalisedPath(), routingContext.getBodyAsString(), ar -> {
        if (ar.succeeded()) {
          System.out.println("***** : investigationService.handleData() -> " + Thread.currentThread().getName());
        }
        jsonObjectFuture.handle(ar);
      });
      routingContext.next();
    });

    router.post("/:level/:level2/:level3").handler(routingContext -> {
      System.out.println("-POST - 1+++ /:level/:level2/:level3 handler: " + routingContext.normalisedPath() + " level: "
        + routingContext.request().getParam("level") + " level2: "
        + routingContext.request().getParam("level2") + " level3: "
        + routingContext.request().getParam("level3"));
      System.out.println("-----body: " + routingContext.getBodyAsString());
      System.out.println("-----:level " + routingContext.pathParam("level"));
      System.out.println("-----:level2 " + routingContext.pathParam("level2"));
      System.out.println("-----:level3 " + routingContext.pathParam("level3"));
      routingContext.request().params().forEach(e -> System.out.println("-----param<" + e.getKey() + "> - " + e.getValue()));
      routingContext.next();
    });

    router.post("/:level/:level2/:level3/*").handler(routingContext -> {
      Future<JsonObject> jsonObjectFuture = Future.future();
      routingContext.put(FUTURE_KEY, ((Future<JsonObject>)routingContext.get(FUTURE_KEY)).compose(x -> jsonObjectFuture));

      System.out.println("-POST - 1+++* /:level/:level2/:level3/* handler: " + routingContext.normalisedPath() + " level: "
        + routingContext.request().getParam("level") + " level2: "
        + routingContext.request().getParam("level2") + " level3: "
        + routingContext.request().getParam("level3"));
      System.out.println("-----body: " + routingContext.getBodyAsString());
      System.out.println("-----:level " + routingContext.pathParam("level"));
      System.out.println("-----:level2 " + routingContext.pathParam("level2"));
      System.out.println("-----:level3 " + routingContext.pathParam("level3"));
      routingContext.request().params().forEach(e -> System.out.println("-----param<" + e.getKey() + "> - " + e.getValue()));

      investigationService.handleData(routingContext.normalisedPath(), routingContext.getBodyAsString(), ar -> {
        if (ar.succeeded()) {
          System.out.println("***** : investigationService.handleData() -> " + Thread.currentThread().getName());
        }
        jsonObjectFuture.handle(ar);
      });
      routingContext.next();
    });

    router.post("/*").handler(routingContext -> {
      System.out.println("-POST - 2 /* handler: " + routingContext.normalisedPath());
      System.out.println("-----body: " + routingContext.getBodyAsString());
      routingContext.next();
    });

    router.post("/level1").handler(routingContext -> {
      System.out.println("-POST - 3 /level1 handler: " + routingContext.normalisedPath());
      System.out.println("-----body: " + routingContext.getBodyAsString());
      routingContext.next();
    });

    router.post("/level1/:level2").handler(routingContext -> {
      System.out.println("-POST - 3+ /level1/:level2 handler: " + routingContext.normalisedPath()
        + " level2: " + routingContext.request().getParam("level2"));
      System.out.println("-----body: " + routingContext.getBodyAsString());
      routingContext.next();
    });

    router.post("/level1/*").handler(routingContext -> {
      System.out.println("-POST - 4 /level1/* handler: " + routingContext.normalisedPath());
      System.out.println("-----body: " + routingContext.getBodyAsString());
      routingContext.next();
    });

    router.post("/level1/level2").handler(routingContext -> {
      System.out.println("-POST - 5 /level1/level2 handler: " + routingContext.normalisedPath());
      System.out.println("-----body: " + routingContext.getBodyAsString());
      routingContext.next();
    });

    router.post("/level1/level2/:level3").handler(routingContext -> {
      System.out.println("-POST - 5+ /level1/level2/:level3 handler: " + routingContext.normalisedPath()
        + " level3: " + routingContext.request().getParam("level3"));
      System.out.println("-----body: " + routingContext.getBodyAsString());
      routingContext.next();
    });

    router.post("/level1/level2/*").handler(routingContext -> {
      System.out.println("-POST - 6 /level1/level2/* handler: " + routingContext.normalisedPath());
      System.out.println("-----body: " + routingContext.getBodyAsString());
      routingContext.next();
    });

    router.post("/level1/level2/level3").handler(routingContext -> {
      System.out.println("-POST - 7 /level1/level2/level3 handler: " + routingContext.normalisedPath());
      System.out.println("-----body: " + routingContext.getBodyAsString());
      routingContext.next();
    });

    router.post("/level1/level2/level3/:level4").handler(routingContext -> {
      System.out.println("-POST - 7+ /level1/level2/level3/:level4 handler: " + routingContext.normalisedPath()
        + routingContext.normalisedPath() + " level4: " + routingContext.request().getParam("level4"));
      System.out.println("-----body: " + routingContext.getBodyAsString());
      routingContext.next();
    });

    router.post("/level1/level2/level3/*").handler(routingContext -> {
      System.out.println("-POST - 8 /level1/level2/level3/* handler: " + routingContext.normalisedPath());
      System.out.println("-----body: " + routingContext.getBodyAsString());
      routingContext.next();
    });


    router.route().handler(routingContext -> {
      System.out.println("----The Final Global handler: " + routingContext.normalisedPath());
      ((Future<JsonObject>)routingContext.get(FUTURE_KEY)).setHandler(ar -> {
        System.out.println("***** : jsonObjectFuture.handler -> " + Thread.currentThread().getName());

        HttpServerResponse response = routingContext.response();
        if (ar.succeeded()) {
          response
            .setStatusCode(200)
            .end(ar.result().put("addedMessage", "This this the end of response...\n").toBuffer());
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
