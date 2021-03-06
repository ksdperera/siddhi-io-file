/*
 * Copyright (c)  2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.io.file;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.AssertJUnit;
import org.testng.TestException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;
import org.wso2.siddhi.core.util.SiddhiTestHelper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test cases for siddhi-io-file source.
 * */
public class FileSourceTextFullModeTestCase {
    private static final Logger log = Logger.getLogger(FileSourceTextFullModeTestCase.class);
    private AtomicInteger count = new AtomicInteger();
    private int waitTime = 2000;
    private int timeout = 30000;

    private String dirUri, moveAfterProcessDir;
    private File sourceRoot, newRoot, movedFiles;
    
    @BeforeClass
    public void init() {
        ClassLoader classLoader = FileSourceTextFullModeTestCase.class.getClassLoader();
        String rootPath = classLoader.getResource("files").getFile();
        sourceRoot = new File(rootPath + "/repo");
        dirUri = rootPath + "/new";
        newRoot = new File(dirUri);
        moveAfterProcessDir = rootPath + "/moved_files";
    }

    @BeforeMethod
    public void doBeforeMethod() {
        count.set(0);
        try {
            FileUtils.copyDirectory(sourceRoot, newRoot);
            movedFiles = new File(moveAfterProcessDir);
            FileUtils.forceMkdir(movedFiles);
        } catch (IOException e) {
            throw new TestException("Failed to copy files from " +
                    sourceRoot.getAbsolutePath() +
                    " to " +
                    newRoot.getAbsolutePath() +
                    " which are required for tests. Hence aborting tests.", e);
        }
    }

    @AfterMethod
    public void doAfterMethod() {
        try {
            FileUtils.forceDelete(newRoot);
            FileUtils.forceDelete(movedFiles);
        } catch (IOException e) {
            throw new TestException(e.getMessage(), e);
        }
    }

    /**
    * Test cases for 'mode = text.full'.
    * */
    @Test
    public void siddhiIoFileTest1() throws InterruptedException {
        log.info("test SiddhiIoFile [mode = text.full] 1");
        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='file',mode='text.full'," +
                "dir.uri='" + dirUri + "/text_full', " +
                "action.after.process='move', " +
                "move.after.process='" + moveAfterProcessDir + "', " +
                "@map(type='json'))" +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                int n = count.incrementAndGet();
                for (Event event : events) {
                    switch (n) {
                        case 1:
                            AssertJUnit.assertEquals("apache", event.getData(0));
                            break;
                        case 2:
                            AssertJUnit.assertEquals("cloudbees", event.getData(0));
                            break;
                        case 3:
                            AssertJUnit.assertEquals("google", event.getData(0));
                            break;
                        case 4:
                            AssertJUnit.assertEquals("ibm", event.getData(0));
                            break;
                        case 5:
                            AssertJUnit.assertEquals("intel", event.getData(0));
                            break;
                        case 6:
                            AssertJUnit.assertEquals("microsoft", event.getData(0));
                            break;
                        case 7:
                            AssertJUnit.assertEquals("redhat", event.getData(0));
                            break;
                        case 8:
                            AssertJUnit.assertEquals("wso2", event.getData(0));
                            break;
                        default:
                            AssertJUnit.fail("More events received than expected.");
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        SiddhiTestHelper.waitForEvents(waitTime, 8, count, timeout);

        File file = new File(moveAfterProcessDir);
        AssertJUnit.assertEquals(8, file.list().length);

        //assert event count
        AssertJUnit.assertEquals("Number of events", 8, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void siddhiIoFileTest2() throws InterruptedException {
        log.info("test SiddhiIoFile [mode = text.full] 2");
        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='file', mode='text.full'," +
                "dir.uri='" + dirUri + "/text_full', " +
                "action.after.process='delete', " +
                "@map(type='json'))" +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                int n = count.incrementAndGet();
                for (Event event : events) {
                    switch (n) {
                        case 1:
                            AssertJUnit.assertEquals("apache", event.getData(0));
                            break;
                        case 2:
                            AssertJUnit.assertEquals("cloudbees", event.getData(0));
                            break;
                        case 3:
                            AssertJUnit.assertEquals("google", event.getData(0));
                            break;
                        case 4:
                            AssertJUnit.assertEquals("ibm", event.getData(0));
                            break;
                        case 5:
                            AssertJUnit.assertEquals("intel", event.getData(0));
                            break;
                        case 6:
                            AssertJUnit.assertEquals("microsoft", event.getData(0));
                            break;
                        case 7:
                            AssertJUnit.assertEquals("redhat", event.getData(0));
                            break;
                        case 8:
                            AssertJUnit.assertEquals("wso2", event.getData(0));
                            break;
                        default:
                            AssertJUnit.fail("More events received than expected.");
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        SiddhiTestHelper.waitForEvents(waitTime, 8, count, timeout);

        File file = new File(dirUri + "/text_full");
        AssertJUnit.assertEquals(0, file.list().length);

        //assert event count
        AssertJUnit.assertEquals("Number of events", 8, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void siddhiIoFileTest3() throws InterruptedException {
        log.info("test SiddhiIoFile [mode = text.full] 3");
        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='file', mode='text.full'," +
                "dir.uri='" + dirUri + "/text_full_single', " +
                "action.after.process='delete', " +
                "@map(type='json'))" +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                int n = count.incrementAndGet();
                for (Event event : events) {
                    switch (n) {
                        case 1:
                            AssertJUnit.assertEquals("apache", event.getData(0));
                            break;
                        default:
                            AssertJUnit.fail("More events received than expected.");
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        SiddhiTestHelper.waitForEvents(waitTime, 1, count, timeout);

        File file = new File(dirUri + "/text_full_single");
        AssertJUnit.assertEquals(0, file.list().length);

        //assert event count
        AssertJUnit.assertEquals("Number of events", 1, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void siddhiIoFileTest4() throws InterruptedException {
        log.info("test SiddhiIoFile [mode = text.full] 4");
        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='file', mode='text.full'," +
                "dir.uri='" + dirUri + "/text_full_single', " +
                "action.after.process='delete', " +
                "@map(type='json'))" +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                int n = count.incrementAndGet();
                for (Event event : events) {
                    switch (n) {
                        case 1:
                            AssertJUnit.assertEquals("apache", event.getData(0));
                            break;
                        case 2 :
                            AssertJUnit.assertEquals("google", event.getData(0));
                            break;
                        default:
                            AssertJUnit.fail("More events received than expected.");
                    }
                }
            }
        });

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                siddhiAppRuntime.start();
            }
        });
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(t1);

        SiddhiTestHelper.waitForEvents(waitTime, 1, count, timeout);

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                File source = new File(dirUri + "/text_full/google.json");
                File dest = new File(dirUri + "/text_full_single/google.json");
                while (true) {
                    if (count.intValue() == 1) {
                        try {
                            FileUtils.copyFile(source, dest);
                            break;
                        } catch (IOException e) {
                            AssertJUnit.fail("Failed to add a new file to directory '" +
                                    dirUri + "/text_full_single'.");
                        }
                    }
                }
            }
        });

        executorService.execute(t2);

        SiddhiTestHelper.waitForEvents(waitTime, 2, count, timeout);

        executorService.shutdown();

        File file = new File(dirUri + "/text_full_single");
        AssertJUnit.assertEquals(0, file.list().length);

        //assert event count
        AssertJUnit.assertEquals("Number of events", 2, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void siddhiIoFileTest5() throws InterruptedException {
        log.info("test SiddhiIoFile [mode = text.full] 5");
        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='file', mode='text.full'," +
                "file.uri='" + dirUri + "/text_full_single/apache.json', " +
                "action.after.process='delete', " +
                "@map(type='json'))" +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                int n = count.incrementAndGet();
                for (Event event : events) {
                    switch (n) {
                        case 1:
                            AssertJUnit.assertEquals("apache", event.getData(0));
                            break;
                        default:
                            AssertJUnit.fail("More events received than expected.");
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        SiddhiTestHelper.waitForEvents(waitTime, 1, count, timeout);

        //assert event count
        AssertJUnit.assertEquals("Number of events", 1, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void siddhiIoFileTest6() throws InterruptedException {
        log.info("test SiddhiIoFile [mode = text.full] 6");
        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='file', mode='text.full'," +
                "file.uri='" + dirUri + "/text_full_single/apache.json', " +
                "action.after.process='move', " +
                "move.after.process='" + moveAfterProcessDir + "/apache.json', " +
                "@map(type='json'))" +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                int n = count.incrementAndGet();
                for (Event event : events) {
                    switch (n) {
                        case 1:
                            AssertJUnit.assertEquals("apache", event.getData(0));
                            break;
                        default:
                            AssertJUnit.fail("More events received than expected.");
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        SiddhiTestHelper.waitForEvents(waitTime, 1, count, timeout);

        File file = new File(dirUri + "/text_full_single");
        AssertJUnit.assertEquals(0, file.list().length);

        File movedFile = new File(moveAfterProcessDir);
        AssertJUnit.assertEquals(1, movedFile.list().length);

        //assert event count
        AssertJUnit.assertEquals("Number of events", 1, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void siddhiIoFileTest7() throws InterruptedException {
        log.info("test SiddhiIoFile [mode = text.full] 7");
        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='file', mode='text.full'," +
                "file.uri='" + dirUri + "/text_full_single/apache.json', " +
                "@map(type='json'))" +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                int n = count.incrementAndGet();
                for (Event event : events) {
                    switch (n) {
                        case 1:
                            AssertJUnit.assertEquals("apache", event.getData(0));
                            break;
                        default:
                            AssertJUnit.fail("More events received than expected.");
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        SiddhiTestHelper.waitForEvents(waitTime, 1, count, timeout);

        File file = new File(dirUri + "/text_full_single");
        AssertJUnit.assertEquals(0, file.list().length);

        //assert event count
        AssertJUnit.assertEquals("Number of events", 1, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class)
    public void siddhiIoFileTest8() throws InterruptedException {
        log.info("test SiddhiIoFile [mode = text.full] 8");
        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='file', mode='text.full'," +
                "file.uri='" + dirUri + "/text_full_single/apache.json', " +
                "tailing='true', " +
                "@map(type='json'))" +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);
        siddhiAppRuntime.start();
        Thread.sleep(1000);
        siddhiAppRuntime.shutdown();
    }
}
