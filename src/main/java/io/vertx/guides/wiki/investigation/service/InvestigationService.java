package io.vertx.guides.wiki.investigation.service;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
public interface InvestigationService {
  static String INVESTIGATION_SERVICE_QUEUE = "Investigation_Service_Queue";

  static InvestigationService create(Context context, Handler<AsyncResult<InvestigationServiceImpl>> readyHandler) {
    return new InvestigationServiceImpl(context, readyHandler);
  }

  static InvestigationService createProxy(Vertx vertx, String address) {
    return new InvestigationServiceVertxEBProxy(vertx, address);
  }

  @Fluent
  InvestigationService handleData(String route, String data, Handler<AsyncResult<JsonObject>> resultHandler);
}
