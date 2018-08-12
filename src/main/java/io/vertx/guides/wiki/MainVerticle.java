/*
 *  Copyright (c) 2017 Red Hat, Inc. and/or its affiliates.
 *  Copyright (c) 2017 INSA Lyon, CITI Laboratory.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vertx.guides.wiki;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.guides.wiki.database.WikiDatabaseVerticle;
import io.vertx.guides.wiki.http.AuthInitializerVerticle;
import io.vertx.guides.wiki.investigation.HttpRoutersComposeFutureInvestigationVerticle;
import io.vertx.guides.wiki.investigation.HttpRoutersInvestigationVerticle;
import io.vertx.guides.wiki.investigation.HttpRoutersSimpleComposeInvestigationVerticle;
import io.vertx.guides.wiki.investigation.service.InvestigationServiceVerticle;

/**
 * @author <a href="https://julien.ponge.org/">Julien Ponge</a>
 */
// tag::code[]
public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Future<String> dbVerticleDeployment = Future.future();
    vertx.deployVerticle(new WikiDatabaseVerticle(), dbVerticleDeployment);

    dbVerticleDeployment.compose(id -> {
      Future<String> authInitDeployment = Future.future();
      vertx.deployVerticle(new AuthInitializerVerticle(), authInitDeployment);
      return authInitDeployment;
    }).compose(id -> {
      Future<String> httpVerticleDeployment = Future.future();
      vertx.deployVerticle(
        "io.vertx.guides.wiki.http.HttpServerVerticle",
        new DeploymentOptions().setInstances(2),
        httpVerticleDeployment.completer());
      return httpVerticleDeployment;
    }).compose(id -> {
      Future<String> httpRoutersInvestigationVerticleDeployment = Future.future();
      vertx.deployVerticle(new HttpRoutersInvestigationVerticle(), httpRoutersInvestigationVerticleDeployment);
      return httpRoutersInvestigationVerticleDeployment;
    }).compose(id -> {
      Future<String> investigationServiceVerticleDeployment = Future.future();
      vertx.deployVerticle(new InvestigationServiceVerticle(), investigationServiceVerticleDeployment);
      return investigationServiceVerticleDeployment;
    }).compose(id -> {
      Future<String> http7777 = Future.future();
      vertx.deployVerticle(new HttpRoutersSimpleComposeInvestigationVerticle(), http7777);
      return http7777;
    }).compose(id -> {
      Future<String> http8888 = Future.future();
      vertx.deployVerticle(new HttpRoutersComposeFutureInvestigationVerticle(), http8888);
      return http8888;
    }).setHandler(ar -> {
      if (ar.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail(ar.cause());
      }
    });
  }
}
// end::code[]
