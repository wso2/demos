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

public class CandidateTextLineExtensionTestCase {
    static final Logger log = Logger.getLogger(CandidateTextLineExtensionTestCase.class);
    private AtomicInteger count = new AtomicInteger(0);
    private volatile boolean eventArrived;

    @Before
    public void init() {
        count.set(0);
        eventArrived = false;
    }

    @Test
    public void testContainsFunctionExtension() throws InterruptedException {
        log.info("TopKTagsExtensionExtensionTestCase TestCase ");
        SiddhiManager siddhiManager = new SiddhiManager();
        String inStreamDefinition = "@config(async = 'true')define stream inputStream (text string,hashtags string);";
        String query = ("@info(name = 'query1') " + "from inputStream "
                + "select Sentiment:trackWord(text,'Trump','Donald','He') as istext " + "insert into outputStream;");
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager
                .createExecutionPlanRuntime(inStreamDefinition + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                for (Event inEvent : inEvents) {
                    count.incrementAndGet();
                    if (count.get() == 1) {
                        Assert.assertEquals("Trump is my favourite candidate  So I vote trump", inEvent.getData(0));
                    }
                    if (count.get() == 2) {
                        Assert.assertEquals("Trump is my favourite candidate", inEvent.getData(0));
                    }
                    if (count.get() == 3) {
                        Assert.assertEquals("Trump is my favourite candidate  So I vote trump", inEvent.getData(0));
                    }
                    if (count.get() == 4) {
                        Assert.assertEquals("Trump is my favourite candidate", inEvent.getData(0));
                    }
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[] {
                "Trump is my favourite candidate. But Clinton also a good one. So I vote trump", "#TRUMP" });
        inputHandler.send(new Object[] { "Trump is my favourite candidate. But Clinton also a good one.", "#TRUMP" });
        inputHandler.send(new Object[] {
                "Trump is my favourite candidate. But Clinton also a good one.Bernie is a useless. So I vote trump",
                "#TRUMP" });
        inputHandler.send(new Object[] { "Trump is my favourite candidate.", "#TRUMP" });
        SiddhiTestHelper.waitForEvents(100, 4, count, 60000);
        Assert.assertEquals(4, count.get());
        Assert.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }
}
