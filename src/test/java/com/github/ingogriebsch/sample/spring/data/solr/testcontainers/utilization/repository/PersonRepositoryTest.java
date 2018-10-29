/*
 * Copyright 2018 Ingo Griebsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.ingogriebsch.sample.spring.data.solr.testcontainers.utilization.repository;

import static com.github.ingogriebsch.sample.spring.data.solr.testcontainers.utilization.solr.SolrConstants.SOLR_CORE_NAME;
import static com.github.ingogriebsch.sample.spring.data.solr.testcontainers.utilization.test.util.SolrContainer.SOLR_IMAGE_NAME;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.support.TestPropertySourceUtils.addInlinedPropertiesToEnvironment;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.ingogriebsch.sample.spring.data.solr.testcontainers.utilization.model.Person;
import com.github.ingogriebsch.sample.spring.data.solr.testcontainers.utilization.test.util.SolrContainer;

@ContextConfiguration(initializers = PersonRepositoryTest.SolrRelatedPropertiesInitializer.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @ClassRule
    public static SolrContainer solrContainer = new SolrContainer(SOLR_IMAGE_NAME + ":6.6").withCore(SOLR_CORE_NAME);

    @After
    public void after() {
        personRepository.deleteAll();
    }

    @Test
    public void findOne_should_return_null_if_person_not_available() throws Exception {
        assertThat(personRepository.findOne(randomUUID().toString())).isNull();
    }

    @Test
    public void save_should_store_person_in_index() throws Exception {
        Person person = new Person(randomUUID().toString(), "Peter", "peter@domain.com", 54);
        person = personRepository.save(person);

        assertThat(personRepository.findOne(person.getId())).isNotNull().isEqualTo(person);
    }

    @Test
    public void findByNameLike_should_return_matching_persons() throws Exception {
        String name = "Stefanie";

        Person person = new Person(randomUUID().toString(), name, "stefanie@domain.com", 32);
        person = personRepository.save(person);

        String like = name;
        Iterable<Person> found = personRepository.findByNameLike(like);
        assertThat(found).isNotNull().hasSize(1);
        assertThat(found.iterator().next()).isNotNull().isEqualTo(person);

        like = name.substring(0, nextInt(1, name.length()));
        found = personRepository.findByNameLike(like);
        assertThat(found).isNotNull().hasSize(1);
        assertThat(found.iterator().next()).isNotNull().isEqualTo(person);
    }

    public static class SolrRelatedPropertiesInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            String springDataSolrHost = "spring.data.solr.host=" + solrContainer.getHost();
            addInlinedPropertiesToEnvironment(configurableApplicationContext, springDataSolrHost);
        }
    }
}
