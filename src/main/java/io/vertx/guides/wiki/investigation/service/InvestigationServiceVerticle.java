package io.vertx.guides.wiki.investigation.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.serviceproxy.ServiceBinder;

import static io.vertx.guides.wiki.investigation.service.InvestigationService.INVESTIGATION_SERVICE_QUEUE;

public class InvestigationServiceVerticle extends AbstractVerticle {

  public void start(Future<Void> startFuture) throws Exception {
    InvestigationService.create(getVertx().getOrCreateContext(), ready -> {
      if (ready.succeeded()) {
        new ServiceBinder(vertx)
          .setAddress(INVESTIGATION_SERVICE_QUEUE)
          .register(InvestigationService.class, ready.result());
        startFuture.complete();
      } else {
        startFuture.fail(ready.cause());
      }
    });
  }
}
