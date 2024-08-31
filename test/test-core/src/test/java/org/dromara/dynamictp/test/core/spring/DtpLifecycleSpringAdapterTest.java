/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.dynamictp.test.core.spring;

import org.dromara.dynamictp.core.lifecycle.LifeCycleManagement;
import org.dromara.dynamictp.spring.DtpLifecycleSpringAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.ApplicationContext;

/**
 * DtpLifecycleSpringAdapterTest related
 *
 * @author vzer200
 * @since 1.1.8
 */
@SpringBootTest(classes = DtpLifecycleSpringAdapterTest.TestConfig.class)
public class DtpLifecycleSpringAdapterTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DtpLifecycleSpringAdapter dtpLifecycleSpringAdapter;

    @Autowired
    private LifeCycleManagement lifeCycleManagement;

    @Test
    void testLifecycleManagementIntegration() {
        // 验证 DtpLifecycleSpringAdapter 是否注入成功
        Assertions.assertNotNull(dtpLifecycleSpringAdapter);

        // 验证 LifeCycleManagement 是否注入成功
        Assertions.assertNotNull(lifeCycleManagement);

        // 启动 lifecycle 并检查状态
        dtpLifecycleSpringAdapter.start();
        Assertions.assertTrue(dtpLifecycleSpringAdapter.isRunning());
        Mockito.verify(lifeCycleManagement).start();

        Mockito.reset(lifeCycleManagement);

        // 停止 lifecycle 并检查状态
        dtpLifecycleSpringAdapter.stop();
        Assertions.assertFalse(dtpLifecycleSpringAdapter.isRunning());
        Mockito.verify(lifeCycleManagement).stop();
    }


    @Test
    void testStopWithCallback() {
        // 使用回调方法停止 lifecycle
        Runnable callback = Mockito.mock(Runnable.class);
        dtpLifecycleSpringAdapter.stop(callback);

        // 验证 lifecycle 停止后，回调方法被执行
        Mockito.verify(lifeCycleManagement).stop();
        Mockito.verify(callback).run();
        Assertions.assertFalse(dtpLifecycleSpringAdapter.isRunning());
    }

    @Test
    void testAutoStartupAndPhase() {
        // 验证 isAutoStartup 和 getPhase 方法的行为
        Assertions.assertEquals(lifeCycleManagement.isAutoStartup(), dtpLifecycleSpringAdapter.isAutoStartup());
        Assertions.assertEquals(lifeCycleManagement.getPhase(), dtpLifecycleSpringAdapter.getPhase());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = "org.dromara.dynamictp.test.core.spring")
    public static class TestConfig {
        @Bean
        public LifeCycleManagement lifeCycleManagement() {
            return Mockito.mock(LifeCycleManagement.class);
        }

        @Bean(name = "testDtpLifecycleSpringAdapter")
        public DtpLifecycleSpringAdapter dtpLifecycleSpringAdapter(LifeCycleManagement lifeCycleManagement) {
            return new DtpLifecycleSpringAdapter(lifeCycleManagement);
        }

    }
}
