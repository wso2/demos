package org.wso2.siddhi.extension.worldcloudcombined;

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
import org.wso2.siddhi.extension.worldcloudcombined.test.util.SiddhiTestHelper;
import org.wso2.siddhi.core.util.EventPrinter;

public class WorldcloudcombinedExtensionExtensionTestCase {
    static final Logger log = Logger.getLogger(WorldcloudcombinedExtensionExtensionTestCase.class);
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
        String inStreamDefinition = "@config(async = 'true')define stream inputStream (TRUMP string,CLINTON string,CRUZ string,BERNIE string,Rt int ,Ft int);";
        String query = ("@info(name = 'query1') "
                + "from inputStream#CombinedWCloud:getCombinedCloud(1,50,TRUMP,CLINTON,CRUZ,BERNIE) "
                + "select 1 as id, TrumpTopWords as TRUMP, ClintonTopWords as CLINTON ,BernieTopWords as BERNIE ,CruzTopWords as CRUZ "
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
                        Assert.assertEquals("", inEvent.getData(1).toString().split(",")[0]);
                        Assert.assertEquals("", inEvent.getData(2).toString().split(",")[0]);
                        Assert.assertEquals("Value: INTERNET-BERNIE", inEvent.getData(3).toString().split(",")[0]);
                        Assert.assertEquals("", inEvent.getData(4).toString().split(",")[0]);
                    }
                    if (count.get() == 2) {
                        Assert.assertEquals("Value: WIN-TRUMP", inEvent.getData(1).toString().split(",")[0]);
                        Assert.assertEquals("", inEvent.getData(2).toString().split(",")[0]);
                        Assert.assertEquals("", inEvent.getData(3).toString().split(",")[0]);
                        Assert.assertEquals("", inEvent.getData(4).toString().split(",")[0]);
                    }

                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[] { "very:2;;education:2;;", "gop:3;;liberty:1;;", "coutry:1;;education:1;;",
                "war:1;;internet:3;;", 10, 5 });
        inputHandler.send(new Object[] { "win:8;;education:2;;", "gop:3;;liberty:1;;", "coutry:1;;education:1;;",
                "war:1;;internet:1;;", 1, 50 });

        SiddhiTestHelper.waitForEvents(100, 2, count, 60000);
        Assert.assertEquals(2, count.get());
        Assert.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }
}
