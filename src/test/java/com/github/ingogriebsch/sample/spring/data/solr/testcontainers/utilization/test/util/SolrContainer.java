/*
 * Copyright 2019 Ingo Griebsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.ingogriebsch.sample.spring.data.solr.testcontainers.utilization.test.util;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.IOException;

import com.github.dockerjava.api.command.InspectContainerResponse;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.testcontainers.containers.GenericContainer;

public class SolrContainer extends GenericContainer<SolrContainer> {

    private static final Integer SOLR_PORT = 8983;
    public static final String SOLR_IMAGE_NAME = "solr";

    private String coreName;

    public SolrContainer() {
        this(SOLR_IMAGE_NAME + ":latest");
    }

    public SolrContainer(@NonNull String dockerImageName) {
        super(dockerImageName);
    }

    public SolrContainer withCore(@NonNull String coreName) {
        this.coreName = coreName;
        return self();
    }

    public String getHost() {
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(getContainerIpAddress());
        sb.append(":");
        sb.append(getMappedPort(SOLR_PORT));
        sb.append("/solr");
        return sb.toString();
    }

    @Override
    protected void configure() {
        addExposedPort(SOLR_PORT);
    }

    @Override
    @SneakyThrows
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        super.containerIsStarted(containerInfo);

        if (!isEmpty(coreName)) {
            assertCoreIsCreated(createCore(coreName), coreName);
        }
    }

    private ExecResult createCore(String coreName) throws IOException, InterruptedException {
        String[] commands = { "/bin/sh", "-c", "bin/solr create_core -c " + coreName };
        return execInContainer(commands);
    }

    private static void assertCoreIsCreated(ExecResult execResult, String core) {
        if (execResult == null || execResult.getStdout() == null
                || !execResult.getStdout().contains("Creating new core '" + core + "' using command")) {
            throw new IllegalStateException("Could not create core '" + core + "' on solr server!");
        }
    }
}
