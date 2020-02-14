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
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;

import java.io.Serializable;
import java.util.Map;

public class App1EntryProcessor {

    public static class MyEntryProcessor1 implements EntryProcessor<String, Person, Object>, Serializable {

        @Override
        public Object process(Map.Entry<String, Person> entry) {
            System.out.println("Entry processor from App 1 : " + entry);
            if(entry != null) {
                System.out.println("Entry processor from App 1 : entry.getValue().getName() " + entry.getValue().getName());
            }
            return null;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ClientConfig config = new ClientConfig();
        config.getUserCodeDeploymentConfig().addClass(MyEntryProcessor1.class).setEnabled(true);

        config.getSerializationConfig().addPortableFactory(1, new PortableFactory() {
            @Override
            public Portable create(int classId) {
                return new Person();
            }
        });

        HazelcastInstance client = HazelcastClient.newHazelcastClient(config);

        IMap<String, Person> test = client.getMap("test");

        test.put("v1", new Person("sancar1"));

        while (true) {
            Thread.sleep(5000);
            test.executeOnKey("v1", new MyEntryProcessor1());
            test.executeOnKey("v2", new MyEntryProcessor1());
        }
    }
}
