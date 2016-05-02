package org.wso2.siddhi.extension.topcount;

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
import org.wso2.siddhi.extension.topcount.test.util.SiddhiTestHelper;
import org.wso2.siddhi.core.util.EventPrinter;

public class TimeBatchUniqueProcessorExtensionTestCase {
    static final Logger log = Logger.getLogger(TimeBatchUniqueProcessorExtensionTestCase.class);
    private AtomicInteger count = new AtomicInteger(0);
    private volatile boolean eventArrived;

    @Before
    public void init() {
        count.set(0);
        eventArrived = false;
    }

    @Test
    public void testContainsFunctionExtension() throws InterruptedException {
        log.info("TimeBatchUniqueProcessorExtensionTestCase TestCase ");
        SiddhiManager siddhiManager = new SiddhiManager();
        String inStreamDefinition = "@config(async = 'true')define stream inputStream (user string,userparty String );";
        String query = ("@info(name = 'query1') " + "from inputStream#Count:getCountCandidate(user,userparty) "
                + "select TRUMP as isTRUMP,CLINTON as isCLINTON,BERNIE as isBERNIE, CRUZ as isCRUZ "
                + "insert into outputStream;");
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
                        Assert.assertEquals(0, inEvent.getData(1));
                        Assert.assertEquals(0, inEvent.getData(2));
                        Assert.assertEquals(0, inEvent.getData(3));
                    }
                    if (count.get() == 2) {
                        Assert.assertEquals(2, inEvent.getData(0));
                        Assert.assertEquals(1, inEvent.getData(1));
                        Assert.assertEquals(0, inEvent.getData(2));
                        Assert.assertEquals(0, inEvent.getData(3));
                    }
                    
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[] { "USER_A","TRUMP"});
        inputHandler.send(new Object[] { "USER_B","TRUMP"});
        inputHandler.send(new Object[] { "USER_A","TRUMP"});
        inputHandler.send(new Object[] { "USER_C","CLINTON" });
        SiddhiTestHelper.waitForEvents(100, 2, count, 60000);
        Assert.assertEquals(2, count.get());
        Assert.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }
}
