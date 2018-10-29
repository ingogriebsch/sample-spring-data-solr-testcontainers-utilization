/*
 * Copyright 2018 Ingo Griebsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.ingogriebsch.sample.spring.data.solr.testcontainers.utilization.test.util;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;

import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.GenericContainer;

import com.github.dockerjava.api.command.InspectContainerResponse;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

@NoArgsConstructor(access = PRIVATE)
public final class SolrTestUtils {

    @SuppressWarnings("rawtypes")
    public static GenericContainer<?> createSolrContainer(@NonNull String image, @NonNull Integer port, @NonNull String core) {
        GenericContainer<?> container = new GenericContainer(image) {

            @Override
            @SneakyThrows
            protected void containerIsStarted(@NonNull InspectContainerResponse containerInfo) {
                assertCoreIsCreated(createCore(core), core);
            }

            private ExecResult createCore(String core) throws IOException, InterruptedException {
                String[] commands = { "/bin/sh", "-c", "bin/solr create_core -c " + core };
                return execInContainer(commands);
            }

        };
        container.withExposedPorts(port);
        return container;
    }

    public static String getSolrUrl(@NonNull GenericContainer<?> solrContainer, @NonNull Integer solrPort) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(solrContainer.getContainerIpAddress());
        sb.append(":");
        sb.append(solrContainer.getMappedPort(solrPort));
        sb.append("/solr");
        return sb.toString();
    }

    private static void assertCoreIsCreated(ExecResult execResult, String core) {
        if (execResult == null || execResult.getStdout() == null
            || !execResult.getStdout().contains("Creating new core '" + core + "' using command")) {
            throw new IllegalStateException("Could not create core '" + core + "' on solr server!");
        }
    }

}
