package org.wso2.siddhi.extension.popularlist;

import static java.util.concurrent.TimeUnit.HOURS;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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

public class MaxByKLinks extends StreamProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaxByKLinks.class);
    private final ReentrantLock lock = new ReentrantLock();
    private Map<String, String> currentTitleContainerMap = new ConcurrentHashMap<String, String>();
    private Map<String, StreamEvent> currentTopURLContainerMap = new ConcurrentHashMap<String, StreamEvent>();
    private int passToOut;
    private int lengthToKeep;
    private List<StreamEvent> sortedWindow = Collections.synchronizedList(new ArrayList<StreamEvent>());
                                                                                                        
    private EventComparator eventComparator;
    private VariableExpressionExecutor variableExpressionExecutorURL;
    private VariableExpressionExecutor variableExpressionRank;
    private VariableExpressionExecutor variableExpressionRetweetCount;
    private VariableExpressionExecutor variableExpressionFavouriteCount;
    private VariableExpressionExecutor variableExpressionCreatedAt;
    private VariableExpressionExecutor variableExpressionOwnersList;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService titleContanerscheduler = Executors.newScheduledThreadPool(1);

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

    public MaxByKLinks() {
        titleContanerscheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                currentTopURLContainerMap.clear();
                currentTitleContainerMap.clear();
            }
        }, 0, 24, HOURS);
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                if (sortedWindow.size() > 0) {
                    double rank = 0;
                    for (int i = 0; i < sortedWindow.size(); i++) {
                        try {
                            rank = getRank((Integer) variableExpressionRetweetCount.execute(sortedWindow.get(i)),
                                    (Integer) variableExpressionFavouriteCount.execute(sortedWindow.get(i)),
                                    (String) variableExpressionCreatedAt.execute(sortedWindow.get(i)));
                        } catch (ParseException e) {
                            LOGGER.error("Error in maxByKLinks when cast rank to double of current window events ", e);
                        }
                        setAttribute(variableExpressionRank, sortedWindow.get(i), rank);
                    }
                    windowSort();
                    LOGGER.info("Calcutaled sorted window rank to recent time in maxByKLinks");
                }
            }
        }, 0, 15, TimeUnit.MINUTES);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        scheduler.shutdownNow();
        titleContanerscheduler.shutdownNow();

    }

    private void windowSort() {
        lock.lock();
        Collections.sort(sortedWindow, eventComparator);
        lock.unlock();
    }

    @Override
    public Object[] currentState() {

        return new Object[] { sortedWindow };
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void restoreState(Object[] state) {
        sortedWindow = (ArrayList<StreamEvent>) state[0];
    }

    @Override
    protected void process(ComplexEventChunk<StreamEvent> streamEventChunk, Processor nextProcessor,
            StreamEventCloner streamEventCloner, ComplexEventPopulater complexEventPopulater) {
        ComplexEventChunk<StreamEvent> returnEventChunk = new ComplexEventChunk<StreamEvent>();
        StreamEvent streamEvent;
        Double rank = -1.0;
        String title = "";
        boolean addNewEvent = false;
        while (streamEventChunk.hasNext()) {
            streamEvent = streamEventChunk.next();
            try {
                rank = getRank((Integer) variableExpressionRetweetCount.execute(streamEvent),
                        (Integer) variableExpressionFavouriteCount.execute(streamEvent),
                        (String) variableExpressionCreatedAt.execute(streamEvent));
            } catch (ParseException e) {
                LOGGER.error("ClassCastException in maxByKLinks when cast rank to double of new event ", e);
            }
            setAttribute(variableExpressionRank, streamEvent, rank);
            if (sortedWindow.size() < lengthToKeep) {
                if (!isDuplicate(streamEvent)) {
                    sortedWindow.add(streamEvent);
                    addNewEvent = true;
                    windowSort();
                }
            } else if (((Double) variableExpressionRank.execute(sortedWindow.get(sortedWindow.size() - 1)) > (Double) variableExpressionRank
                    .execute(streamEvent)) || ("null".equals(variableExpressionExecutorURL.execute(streamEvent)))) {
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
                StreamEvent event = currentTopURLContainerMap.put(
                        (String) variableExpressionExecutorURL.execute(sortedWindow.get(j)), sortedWindow.get(j));
                if (event == null) {
                    title = getLink((String) variableExpressionExecutorURL.execute(sortedWindow.get(j)));
                    currentTitleContainerMap.put((String) variableExpressionExecutorURL.execute(sortedWindow.get(j)),
                            title);
                } else {
                    title = currentTitleContainerMap.get((String) variableExpressionExecutorURL.execute(sortedWindow
                            .get(j)));
                }
                complexEventPopulater.populateComplexEvent(sortedWindow.get(j), new Object[] { j + 1, title });
                returnEventChunk.add(sortedWindow.get(j));
            }

        }

        nextProcessor.process(returnEventChunk);
        streamEventChunk.clear();
    }

    public List<StreamEvent> sortHashMapByValues(Map<String, StreamEvent> returnList) {
        List<StreamEvent> mapValues = new ArrayList<StreamEvent>(returnList.values());
        windowSort();
        return mapValues;
    }

    private String getLink(String url) {
        String text = null;
        StringBuilder sb = new StringBuilder();
        Connection con = Jsoup
                .connect(url)
                .userAgent(
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .ignoreContentType(true).validateTLSCertificates(false).ignoreHttpErrors(true).timeout(10000);
        Document doc;
        try {
            doc = con.get();
            Elements metaOgTitle = doc.select("meta[property=og:title]");
            if (metaOgTitle != null) {
                text = metaOgTitle.attr("content");
            } else {
                text = doc.title();
            }
            if ("".equals(text)) {
                text = doc.title();
            }
            if (text != null) {
                sb.append(text);
            }
        } catch (IOException e) {
            LOGGER.error("Error Extracting Link Data in PageTitle extension ", e);
        }

        return sb.toString();
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
            LOGGER.error("Error in update " + attribute.toString() + " in maxByKLinks class");
        }
    }

    private boolean isDuplicate(StreamEvent event) {
        boolean duplicate = false;
        for (int i = sortedWindow.size() - 1; i >= 0; i--) {
            if (variableExpressionExecutorURL.execute(sortedWindow.get(i)).equals(
                    variableExpressionExecutorURL.execute(event))
                    && ((String) variableExpressionOwnersList.execute(sortedWindow.get(i)))
                            .contains((String) variableExpressionOwnersList.execute(event))) {
                setAttribute(variableExpressionRetweetCount, sortedWindow.get(i),
                        1 + (Integer) variableExpressionRetweetCount.execute(sortedWindow.get(i)));
                double rank = 0;
                try {
                    rank = getRank((Integer) variableExpressionRetweetCount.execute(sortedWindow.get(i)),
                            (Integer) variableExpressionFavouriteCount.execute(sortedWindow.get(i)),
                            (String) variableExpressionCreatedAt.execute(sortedWindow.get(i)));
                } catch (ParseException e) {
                    LOGGER.error(
                            "ClassCastException in maxByKLinks when cast rank to double of new event in isDuplicate method if clause ",
                            e);
                }
                setAttribute(variableExpressionRank, sortedWindow.get(i), (Double) rank);
                windowSort();
                duplicate = true;
            } else if (variableExpressionExecutorURL.execute(sortedWindow.get(i)).equals(
                    variableExpressionExecutorURL.execute(event))) {
                setAttribute(variableExpressionRetweetCount, sortedWindow.get(i),
                        (Integer) variableExpressionRetweetCount.execute(event)
                                + (Integer) variableExpressionRetweetCount.execute(sortedWindow.get(i)));
                setAttribute(variableExpressionFavouriteCount, sortedWindow.get(i),
                        (Integer) variableExpressionRetweetCount.execute(event)
                                + (Integer) variableExpressionRetweetCount.execute(sortedWindow.get(i)));
                setAttribute(variableExpressionOwnersList, sortedWindow.get(i),
                        (String) variableExpressionOwnersList.execute(sortedWindow.get(i)) + " , "
                                + (String) variableExpressionOwnersList.execute(event));
                double rank = 0;
                try {
                    rank = getRank((Integer) variableExpressionRetweetCount.execute(sortedWindow.get(i)),
                            (Integer) variableExpressionFavouriteCount.execute(sortedWindow.get(i)),
                            (String) variableExpressionCreatedAt.execute(sortedWindow.get(i)));
                } catch (ParseException e) {
                    LOGGER.error(
                            "ClassCastException in maxByKLinks when cast rank to double of new event in isDuplicate method else clause ",
                            e);
                }
                setAttribute(variableExpressionRank, sortedWindow.get(i), rank);
                windowSort();
                duplicate = true;
            }

        }
        return duplicate;
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
    protected List<Attribute> init(AbstractDefinition inputDefinition,
            ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (!(attributeExpressionExecutors.length == 8)) {
            throw new ExecutionPlanValidationException("Invalid number of Arguments in MaxByKLinks class");
        }
        if (attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor) {
            lengthToKeep = ((Integer) attributeExpressionExecutors[0].execute(null));
        } else {
            throw new IllegalArgumentException("The first 1st parameter should be an integer in MaxByKLinks class");
        }
        if (attributeExpressionExecutors[1] instanceof ConstantExpressionExecutor) {
            passToOut = ((Integer) attributeExpressionExecutors[1].execute(null));
        } else {
            throw new IllegalArgumentException("The first 2nd parameter should be an integer in MaxByKLinks class");
        }
        if (!(attributeExpressionExecutors[2] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException(
                    "Required 3rd parameter should be Varible Executor, but found a other Function Type in MaxByKLinks class");
        } else {
            variableExpressionCreatedAt = (VariableExpressionExecutor) attributeExpressionExecutors[2];
        }
        if (!(attributeExpressionExecutors[3] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException(
                    "Required 4th  parameter should be variable, but found a string parameter");
        } else {
            variableExpressionExecutorURL = (VariableExpressionExecutor) attributeExpressionExecutors[3];
        }
        if (!(attributeExpressionExecutors[4] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException(
                    "Required 5th  parameter should be  variable, but found a otherparameter");
        } else {
            variableExpressionRank = (VariableExpressionExecutor) attributeExpressionExecutors[4];
        }

        if (!(attributeExpressionExecutors[5] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException(
                    "Required 6th  parameter should be  variable, but found a otherparameter");
        } else {
            variableExpressionFavouriteCount = (VariableExpressionExecutor) attributeExpressionExecutors[5];
        }
        if (!(attributeExpressionExecutors[6] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException("Required a variable, but found a otherparameter");
        } else {
            variableExpressionRetweetCount = (VariableExpressionExecutor) attributeExpressionExecutors[6];
        }
        if (!(attributeExpressionExecutors[7] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException("Required a variable, but found a otherparameter");
        } else {
            variableExpressionOwnersList = (VariableExpressionExecutor) attributeExpressionExecutors[7];
        }
        eventComparator = new EventComparator();
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(new Attribute("Index", Attribute.Type.INT));
        attributeList.add(new Attribute("Title", Attribute.Type.STRING));
        return attributeList;

    }

}
