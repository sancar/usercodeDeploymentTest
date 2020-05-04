/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foo;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.query.impl.predicates.SqlPredicate;

import java.util.Collection;

public class App2 {

    public static void main(String[] args) throws InterruptedException {
        ClientConfig config = new ClientConfig();

        config.getSerializationConfig().addPortableFactory(1, new PortableFactory() {
            @Override
            public Portable create(int classId) {
                return new Person();
            }
        });

        HazelcastInstance client = HazelcastClient.newHazelcastClient(config);

        IMap<String, Person> test = client.getMap("test");

        test.put("v2", new Person("sancar2", 2));

        while (true) {
            Thread.sleep(5000);
            System.out.println("v1: " + test.get("v1"));
            System.out.println("v2: " + test.get("v2"));
            Collection values = test.values(new SqlPredicate("age > -1"));
            System.out.println(values.size());
        }
    }
}
