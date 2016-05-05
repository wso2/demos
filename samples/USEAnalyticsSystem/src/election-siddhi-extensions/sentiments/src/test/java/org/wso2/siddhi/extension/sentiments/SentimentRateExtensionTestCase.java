package org.wso2.siddhi.extension.sentiments;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.extension.sentiments.test.util.SiddhiTestHelper;
import org.wso2.siddhi.core.util.EventPrinter;

public class SentimentRateExtensionTestCase {
    static final Logger log = Logger.getLogger(SentimentRateExtensionTestCase.class);
    private AtomicInteger count = new AtomicInteger(0);
    private volatile boolean eventArrived;

    @Before
    public void init() {
        count.set(0);
        eventArrived = false;
    }

    @Test
    public void testContainsFunctionExtensionStranfor() throws InterruptedException {
        log.info("SentimentRateExtensionTestCase Stanford TestCase ");
        SiddhiManager siddhiManager = new SiddhiManager();
        String inStreamDefinition = "@config(async = 'true')define stream inputStream (text string);";
        String query = ("@info(name = 'query1') " + "from inputStream "
                + "select Sentiment:getRate(text,'STANFORD') as isRate " + "insert into outputStream;");
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager
                .createExecutionPlanRuntime(inStreamDefinition + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event inEvent : inEvents) {
                    count.incrementAndGet();
                    if (count.get() == 1) {
                        Assert.assertEquals(1, inEvent.getData(0));
                    }
                    if (count.get() == 2) {
                        Assert.assertEquals(-1, inEvent.getData(0));
                    }
                    if (count.get() == 3) {
                        Assert.assertEquals(-1, inEvent.getData(0));
                    }
                    if (count.get() == 4) {
                        Assert.assertEquals(1, inEvent.getData(0));
                    }
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[] { "Trump is a good person" });
        inputHandler.send(new Object[] { "Trump is a bad person" });
        inputHandler.send(new Object[] { "Trump is a not a bad or good person" });
        inputHandler.send(new Object[] { "Trump is a good person" });
        SiddhiTestHelper.waitForEvents(100, 4, count, 60000);
        Assert.assertEquals(4, count.get());
        Assert.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }
    @Test
    public void testContainsFunctionExtension() throws InterruptedException {
        log.info("SentimentRateExtensionTestCase Affin TestCase ");
        SiddhiManager siddhiManager = new SiddhiManager();
        String inStreamDefinition = "@config(async = 'true')define stream inputStream (text string);";
        String query = ("@info(name = 'query1') " + "from inputStream "
                + "select Sentiment:getRate(text,'COMMON') as isRate " + "insert into outputStream;");
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager
                .createExecutionPlanRuntime(inStreamDefinition + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event inEvent : inEvents) {
                    count.incrementAndGet();
                    if (count.get() == 1) {
                        Assert.assertEquals(2, inEvent.getData(0));
                    }
                    if (count.get() == 2) {
                        Assert.assertEquals(0, inEvent.getData(0));
                    }
                    if (count.get() == 3) {
                        Assert.assertEquals(2, inEvent.getData(0));
                    }
                    if (count.get() == 4) {
                        Assert.assertEquals(2, inEvent.getData(0));
                    }
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[] { "Trump is a good person" });
        inputHandler.send(new Object[] { "Trump is a bad person" });
        inputHandler.send(new Object[] { "Trump is a not a bad or good person" });
        inputHandler.send(new Object[] { "Trump is a good person" });
        SiddhiTestHelper.waitForEvents(100, 4, count, 60000);
        Assert.assertEquals(4, count.get());
        Assert.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }
}
