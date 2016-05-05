
package org.wso2.siddhi.extension.popularhtag.test.util;

import java.util.concurrent.atomic.AtomicInteger;

public class SiddhiTestHelper {
    public static void waitForEvents(long sleepTime, int expectedCount, AtomicInteger actualCount, long timeout)
            throws InterruptedException {
        long currentWaitTime = 0;
        long startTime = System.currentTimeMillis();
        while ((actualCount.get() < expectedCount) && (currentWaitTime <= timeout)) {
            Thread.sleep(sleepTime);
            currentWaitTime = System.currentTimeMillis() - startTime;
        }
    }
}
