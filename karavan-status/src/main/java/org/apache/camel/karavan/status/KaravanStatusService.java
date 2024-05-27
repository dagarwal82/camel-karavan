/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.karavan.status;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.camel.karavan.status.docker.DockerAPI;
import org.apache.camel.karavan.status.kubernetes.KubernetesStatusService;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.jboss.logging.Logger;

import java.io.IOException;

@Startup
@Liveness
@Singleton
public class KaravanStatusService implements HealthCheck {

    private static final Logger LOGGER = Logger.getLogger(KaravanStatusService.class.getName());

    @Inject
    KubernetesStatusService kubernetesStatusService;

    @Inject
    DockerAPI dockerAPI;

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Karavan");
    }

    void onStart(@Observes StartupEvent ev) throws Exception {
        LOGGER.info("Status Listeners: starting...");
        if (ConfigService.inKubernetes()) {
            kubernetesStatusService.startInformers();
        }
        LOGGER.info("Status Listeners: started");
    }

    void onStop(@Observes ShutdownEvent ev) throws IOException  {
        LOGGER.info("Status Listeners: stopping...");
        if (ConfigService.inKubernetes()) {
            kubernetesStatusService.stopInformers();
        }
        LOGGER.info("Status Listeners: stopped");
    }
}
