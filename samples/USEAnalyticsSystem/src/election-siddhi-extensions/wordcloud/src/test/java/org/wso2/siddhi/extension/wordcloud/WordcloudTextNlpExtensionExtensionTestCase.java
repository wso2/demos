package org.wso2.siddhi.extension.wordcloud;

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
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.extension.wordcloud.test.util.SiddhiTestHelper;
import org.wso2.siddhi.core.util.EventPrinter;

public class WordcloudTextNlpExtensionExtensionTestCase {
    static final Logger log = Logger.getLogger(WordcloudTextNlpExtensionExtensionTestCase.class);
    private AtomicInteger count = new AtomicInteger(0);
    private volatile boolean eventArrived;

    @Before
    public void init() {
        count.set(0);
        eventArrived = false;
    }

    @Test
    public void TestContainsFunctionExtension() throws InterruptedException {
        log.info("worldcloudTextExtensionExtensionTestCase TestCase ");
        SiddhiManager siddhiManager = new SiddhiManager();
        String inStreamDefinition = "@config(async = 'true')define stream inputStream (text string,Rt int ,Ft int);";
        String query = ("@info(name = 'query1') " + "from inputStream#Cloud:getSNouns(text, 'NLP') "
                + "select ProcessedWords as processedText,1 as id " + "insert into outputStream;");
        ExecutionPlanRuntime executionPlanRuntime = siddhiManager
                .createExecutionPlanRuntime(inStreamDefinition + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                System.out.println();
                for (Event inEvent : inEvents) {
                    if (count.incrementAndGet() == 1) {
//                        Assert.assertEquals("fair complaint computer science research", inEvent.getData(0));
                    }
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();
        inputHandler
                .send(new Object[]{
                        "Very fair complaint about Computer Science research, http://neverworkintheory.org/2016/04/26/perception-productivity.html#research",
                        10, 5});
        inputHandler
                .send(new Object[]{
                        "We're having a Q&A - one of UoJ the students shares the trial and error process of an Asana project they did a while ago.",
                        1, 50});
        inputHandler
                .send(new Object[]{
                        "A connector allows the WSO2 platform to connect to other services, like Twitter. That's part of what our Platform Extension team works on.",
                        10, 90});
        inputHandler
                .send(new Object[]{
                        "Malaka Silva of @wso2 talking about @Google's Summer of Code to folks from the University of Jaffna, especially on getting in.",
                        94, 75});

        SiddhiTestHelper.waitForEvents(100, 4, count, 10000);
        Assert.assertEquals(4, count.get());
        Assert.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
    }

}
