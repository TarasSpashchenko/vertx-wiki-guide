package io.vertx.guides.wiki.investigation.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.util.Random;

public class InvestigationServiceImpl implements InvestigationService {

  private Context context;

  public InvestigationServiceImpl(Context context, Handler<AsyncResult<InvestigationServiceImpl>> readyHandler) {
    this.context = context;
    readyHandler.handle(Future.succeededFuture(this));
  }

  @Override
  public InvestigationService handleData(String route, String data, Handler<AsyncResult<JsonObject>> resultHandler) {
    System.out.println("InvestigationService.handle(" + route + ", " + data + ") @ " + Thread.currentThread().getName());

    JsonObject response = new JsonObject();

    context.<JsonObject>executeBlocking(future -> {
      int workTime = new Random().nextInt(7000) + 3001;
      System.out.println("InvestigationService.handle(" + route + ", " + data + ") @ " + Thread.currentThread().getName() + " -> Sleep... " + workTime);
      try {
        Thread.sleep(workTime);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
      response.put("route", route);
      response.put("data", data);
      response.put("workTime", workTime);
      future.complete(response);
    }, false, asyncResult -> {
      System.out.println("InvestigationService.handle(" + route + ", " + data + ") @ " + Thread.currentThread().getName() + " -> with result");
      resultHandler.handle(asyncResult);
    });

    return this;
  }
}
