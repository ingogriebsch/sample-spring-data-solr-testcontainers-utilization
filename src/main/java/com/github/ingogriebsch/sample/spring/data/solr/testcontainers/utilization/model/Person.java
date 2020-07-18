/*
 * Copyright 2019 Ingo Griebsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.ingogriebsch.sample.spring.data.solr.testcontainers.utilization.model;

import static com.github.ingogriebsch.sample.spring.data.solr.testcontainers.utilization.solr.SolrConstants.SOLR_CORE_NAME;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@AllArgsConstructor
@Data
@NoArgsConstructor
@SolrDocument(solrCoreName = SOLR_CORE_NAME)
public class Person {

    @Id
    private String id;

    @Indexed(required = true)
    private String name;

    @Indexed(required = true)
    private String email;

    @Indexed(required = true)
    private Integer age;
}
