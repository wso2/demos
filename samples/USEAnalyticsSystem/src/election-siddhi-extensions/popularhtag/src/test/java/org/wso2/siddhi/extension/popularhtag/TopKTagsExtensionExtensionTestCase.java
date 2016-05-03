package org.wso2.siddhi.extension.popularhtag;

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
import org.wso2.siddhi.extension.popularhtag.test.util.SiddhiTestHelper;
import org.wso2.siddhi.core.util.EventPrinter;

public class TopKTagsExtensionExtensionTestCase {
    static final Logger log = Logger.getLogger(TopKTagsExtensionExtensionTestCase.class);
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
        String inStreamDefinition = "@config(async = 'true')define stream inputStream (htaglist string,Rt int ,Ft int);";
        String query = ("@info(name = 'query1') "
                + "from inputStream#HTag:getTopTag(htaglist,1,500,'TRUMP2016,MAKEAMERICAGREATAGAIN') "
                + "select Index as count,htaglist as  ishtaglist " + "insert into outputStream;");
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
                        Assert.assertEquals("TRUMP", inEvent.getData(1));
                    }
                    if (count.get() == 2) {
                        Assert.assertEquals(1, inEvent.getData(0));
                        Assert.assertEquals("BERNIE", inEvent.getData(1));
                    }

                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[] { "TRUMP , TRUMP , TRUMP , BERNIE ", 10, 5 });
        inputHandler.send(new Object[] { "TRUMP , TRUMP , TRUMP , BERNIE , BERNIE , BERNIE ", 1, 50 });
        inputHandler
                .send(new Object[] {
                        "BERNIE , BERNIE , BERNIE , BERNIE , BERNIE , BERNIE , BERNIE , BERNIE , BERNIE , BERNIE , BERNIE , BERNIE ",
                        10, 90 });
        inputHandler.send(new Object[] {
                "CLINTON , TRUMP2016 , TRUMP2016 , TRUMP2016 , TRUMP2016 , TRUMP2016 , TRUMP2016 ", 94, 75 });
        SiddhiTestHelper.waitForEvents(100, 2, count, 60000);
        Assert.assertEquals(2, count.get());
        Assert.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }
}