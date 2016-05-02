package org.wso2.siddhi.extension.popularlist;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.event.ComplexEventChunk;
import org.wso2.siddhi.core.event.stream.StreamEvent;
import org.wso2.siddhi.core.event.stream.StreamEventCloner;
import org.wso2.siddhi.core.event.stream.populater.ComplexEventPopulater;
import org.wso2.siddhi.core.executor.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.VariableExpressionExecutor;
import org.wso2.siddhi.core.query.processor.Processor;
import org.wso2.siddhi.core.query.processor.stream.StreamProcessor;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;

public class MaxByK extends StreamProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(MaxByK.class);
    private final ReentrantLock lock = new ReentrantLock();
    private int passToOut;
    private int lengthToKeep;
    private List<StreamEvent> sortedWindow = Collections.synchronizedList(new ArrayList<StreamEvent>());
    private EventComparator eventComparator;
    private VariableExpressionExecutor variableExpressionExecutor;
    private VariableExpressionExecutor variableExpressionRank;
    private VariableExpressionExecutor variableExpressionRetweetCount;
    private VariableExpressionExecutor variableExpressionFavouriteCount;
    private VariableExpressionExecutor variableExpressionCreatedAt;
    private final ScheduledExecutorService rankReCalculateScheduler = Executors.newScheduledThreadPool(1);

    private class EventComparator implements Comparator<StreamEvent> {
        @SuppressWarnings({ "unchecked" })
        @Override
        public int compare(StreamEvent e1, StreamEvent e2) {
            int[] variablePosition = ((VariableExpressionExecutor) variableExpressionRank).getPosition();
            Comparable<Double> comparableVariable1 = (Comparable<Double>) e1.getAttribute(variablePosition);
            Comparable<Double> comparableVariable2 = (Comparable<Double>) e2.getAttribute(variablePosition);
            return comparableVariable2.compareTo((Double) comparableVariable1);
        }
    }

    private void windowSort() {
        lock.lock();
        Collections.sort(sortedWindow, eventComparator);
        lock.unlock();
    }

    @Override
    public void start() {
        rankReCalculateScheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (sortedWindow.size() > 0) {
                    double rank = 0;
                    for (int i = 0; i < sortedWindow.size(); i++) {
                        try {
                            rank = getRank((Integer) variableExpressionRetweetCount.execute(sortedWindow.get(i)),
                                    (Integer) variableExpressionFavouriteCount.execute(sortedWindow.get(i)),
                                    (String) variableExpressionCreatedAt.execute(sortedWindow.get(i)));
                        } catch (ParseException e) {

                            LOG.error(
                                    "ClassCastException in maxByK when cast rank to double of current window events ",
                                    e);
                        }
                        setAttribute(variableExpressionRank, sortedWindow.get(i), rank);
                    }
                    windowSort();
                    LOG.info("Calcutaled sorted window rank to recent time in maxByk");
                }
            }

        }, 0, 15, TimeUnit.MINUTES);
    }

    @Override
    public void stop() {
        rankReCalculateScheduler.shutdownNow();
    }

    @Override
    public Object[] currentState() {
        return new Object[] { sortedWindow };
    }

    @SuppressWarnings("unchecked")
    @Override
    public void restoreState(Object[] state) {
        sortedWindow = (ArrayList<StreamEvent>) state[0];
    }

    private double getRank(Integer retweet_count, Integer favorite_count, String created_at) throws ParseException {
        lock.lock();
        Date currentDate = new Date();
        DateFormat tweetDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
        Date tweetDate = tweetDateFormat.parse(created_at);
        long lifeTimeInHours = (currentDate.getTime() - tweetDate.getTime()) / (1000 * 60 * 60);
        double rank = ((2 * retweet_count + favorite_count + 1000 - 1) / (Math.pow((lifeTimeInHours + 2), 1.3)));
        lock.unlock();
        return rank;
    }

    @Override
    protected void process(ComplexEventChunk<StreamEvent> streamEventChunk, Processor nextProcessor,
            StreamEventCloner streamEventCloner, ComplexEventPopulater complexEventPopulater) {
        ComplexEventChunk<StreamEvent> returnEventChunk = new ComplexEventChunk<StreamEvent>();
        StreamEvent streamEvent;
        Double rank = -1.0;
        boolean addNewEvent = false;
        while (streamEventChunk.hasNext()) {
            streamEvent = streamEventChunk.next();
            try {
                rank = getRank((Integer) variableExpressionRetweetCount.execute(streamEvent),
                        (Integer) variableExpressionFavouriteCount.execute(streamEvent),
                        (String) variableExpressionCreatedAt.execute(streamEvent));
            } catch (ParseException e) {
                LOG.error("ClassCastException in maxByK when cast rank to double of new events ", e);
            }
            setAttribute(variableExpressionRank, streamEvent, rank);
            if (sortedWindow.size() < lengthToKeep) {
                if (!isDuplicate(streamEvent)) {
                    sortedWindow.add(streamEvent);
                    addNewEvent = true;
                    windowSort();
                }
            } else if ((Double) variableExpressionRank.execute(sortedWindow.get(sortedWindow.size() - 1)) > (Double) variableExpressionRank
                    .execute(streamEvent) || "null".equals(variableExpressionExecutor.execute(streamEvent))) {
                continue;
            } else {
                if (!isDuplicate(streamEvent)) {
                    sortedWindow.remove(sortedWindow.size() - 1);
                    sortedWindow.add(streamEvent);
                    addNewEvent = true;
                    windowSort();
                }
            }
        }
        if (addNewEvent && (sortedWindow.size() > passToOut)) {
            for (int j = 0; j < passToOut; j++) {
                StreamEvent clonedEvent = streamEventCloner.copyStreamEvent(sortedWindow.get(j));
                complexEventPopulater.populateComplexEvent(clonedEvent, new Object[] { j + 1 });
                returnEventChunk.add(clonedEvent);
            }
            nextProcessor.process(returnEventChunk);
        }
        streamEventChunk.clear();
    }

    private void setAttribute(VariableExpressionExecutor attribute, StreamEvent event, Object value) {
        switch (attribute.getPosition()[2]) {
        case 0:
            event.setBeforeWindowData(value, attribute.getPosition()[3]);
            break;
        case 1:
            event.setOnAfterWindowData(value, attribute.getPosition()[3]);
            break;
        case 2:
            event.setOutputData(value, attribute.getPosition()[3]);
            break;
        default:
            LOG.error("Error in update " + attribute.toString() + " in maxByKLinks class");
        }

    }

    private boolean isDuplicate(StreamEvent event) {
        boolean duplicate = false;
        for (int i = sortedWindow.size() - 1; i >= 0; i--) {
            if (variableExpressionExecutor.execute(sortedWindow.get(i)).equals(
                    variableExpressionExecutor.execute(event))) {
                if ((Double) variableExpressionRank.execute(sortedWindow.get(i)) < (Double) variableExpressionRank
                        .execute(event)) {
                    setAttribute(variableExpressionRank, sortedWindow.get(i),
                            (Double) variableExpressionRank.execute(event));
                }
                if ((Integer) variableExpressionRetweetCount.execute(sortedWindow.get(i)) < (Integer) variableExpressionRetweetCount
                        .execute(event)) {
                    setAttribute(variableExpressionRetweetCount, sortedWindow.get(i),
                            (Integer) variableExpressionRetweetCount.execute(event));
                }
                if ((Integer) variableExpressionFavouriteCount.execute(sortedWindow.get(i)) < (Integer) variableExpressionFavouriteCount
                        .execute(event)) {
                    setAttribute(variableExpressionFavouriteCount, sortedWindow.get(i),
                            (Integer) variableExpressionFavouriteCount.execute(event));
                }
                windowSort();
                duplicate = true;
            }
        }
        return duplicate;
    }

    @Override
    protected List<Attribute> init(AbstractDefinition inputDefinition,
            ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (!(attributeExpressionExecutors.length == 7)) {
            throw new ExecutionPlanValidationException("Invalid number of Arguments");
        }
        if (attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor) {
            lengthToKeep = ((Integer) attributeExpressionExecutors[0].execute(null));
        } else {
            throw new IllegalArgumentException("The 1st parameter should be an integer");
        }
        if (attributeExpressionExecutors[1] instanceof ConstantExpressionExecutor) {
            passToOut = ((Integer) attributeExpressionExecutors[1].execute(null));
        } else {
            throw new IllegalArgumentException("The 2nd parameter should be an integer");
        }
        if (!(attributeExpressionExecutors[2] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException(
                    "Required an varible expression Executor in 3rd parameter, but found a other Function Type");
        } else {
            variableExpressionCreatedAt = (VariableExpressionExecutor) attributeExpressionExecutors[2];
        }
        if (!(attributeExpressionExecutors[3] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException(
                    "Required a variable expression Executor in 4th parameter, but found a string parameter");
        } else {
            variableExpressionExecutor = (VariableExpressionExecutor) attributeExpressionExecutors[3];
        }
        if (!(attributeExpressionExecutors[4] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException(
                    "Required a variable expression Executor in 5th parameter, but found a otherparameter");
        } else {
            variableExpressionRank = (VariableExpressionExecutor) attributeExpressionExecutors[4];
        }

        if (!(attributeExpressionExecutors[5] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException(
                    "Required a variable expression Executor in 6th parameter, but found a otherparameter");
        } else {
            variableExpressionFavouriteCount = (VariableExpressionExecutor) attributeExpressionExecutors[5];
        }
        if (!(attributeExpressionExecutors[6] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException(
                    "Required a variable expression Executor in 7th parameter, but found a otherparameter");
        } else {
            variableExpressionRetweetCount = (VariableExpressionExecutor) attributeExpressionExecutors[6];
        }
        eventComparator = new EventComparator();
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(new Attribute("Index", Attribute.Type.INT));
        return attributeList;

    }

}
