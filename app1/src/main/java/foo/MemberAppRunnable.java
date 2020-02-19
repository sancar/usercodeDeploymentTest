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

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.spi.properties.ClusterProperty;

import java.io.Serializable;

public class MemberAppRunnable {


    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();
        config.setProperty(ClusterProperty.PHONE_HOME_ENABLED.getName(), "false");
        JoinConfig join = config.getNetworkConfig().getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(true).addMember("127.0.0.1:5701").addMember("127.0.0.1:5702");

        config.getUserCodeDeploymentConfig().setEnabled(true);
        HazelcastInstance member = Hazelcast.newHazelcastInstance(config);

        IExecutorService executorService = member.getExecutorService("test");

        while (true) {
            Thread.sleep(5000);
            final String input = "SendFrom1";
            executorService.executeOnAllMembers((Serializable & Runnable) () -> System.out.println("Lambda1 " + input + " " + input.hashCode()));
            executorService.executeOnAllMembers(new MyRunnable("SendFrom1"));
        }

    }
}
