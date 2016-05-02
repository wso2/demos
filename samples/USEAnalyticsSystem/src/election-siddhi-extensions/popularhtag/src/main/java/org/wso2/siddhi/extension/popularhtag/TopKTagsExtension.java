package org.wso2.siddhi.extension.popularhtag;

import static java.util.concurrent.TimeUnit.HOURS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
import org.wso2.siddhi.core.query.processor.stream.window.WindowProcessor;

import com.clearspring.analytics.stream.ITopK;
import com.clearspring.analytics.stream.ScoredItem;

import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;

public class TopKTagsExtension extends StreamProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopKTagsExtension.class);
    private VariableExpressionExecutor varibleExecutorTagList;
    private String tagsToBeRemoved;
    private int passToOut = 50;
    private int maxLength = 500;
    private static CustomConcurrentStreamSummary<String> topTags;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static Set<String> stopTagSet;

    @Override
    public void start() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                LOGGER.info("Window stared for hashtags exetension");
                topTags = new CustomConcurrentStreamSummary<String>(maxLength);
            }
        }, 0, 24, HOURS);
    }

    @Override
    public void stop() {
        // Nothing to do here
    }

    @Override
    public Object[] currentState() {
        // No need to maintain a state.
        return null;
    }

    @Override
    public void restoreState(Object[] state) {
        // No need to maintain a state.
    }

    private void setAttributeHTag(StreamEvent event, String val) {
        switch (varibleExecutorTagList.getPosition()[2]) {
        case 0:
            event.setBeforeWindowData(val, varibleExecutorTagList.getPosition()[3]);
            break;
        case 1:
            event.setOnAfterWindowData(val, varibleExecutorTagList.getPosition()[3]);
            break;
        case 2:
            event.setOutputData(val, varibleExecutorTagList.getPosition()[3]);
            break;
        default:
            LOGGER.error("Error in update text in wordcloudslidingwndow class");
        }
    }

    class CustomConcurrentStreamSummary<T> implements ITopK<T> {
        private final int capacity;
        private final Map<T, ScoredItem<T>> itemMap;
        private final AtomicReference<ScoredItem<T>> minVal;
        private final AtomicLong size;
        private final AtomicBoolean reachCapacity;

        public CustomConcurrentStreamSummary(final int capacity) {
            this.capacity = capacity;
            this.minVal = new AtomicReference<ScoredItem<T>>();
            this.size = new AtomicLong(0);
            this.itemMap = new ConcurrentHashMap<T, ScoredItem<T>>(capacity);
            this.reachCapacity = new AtomicBoolean(false);
        }

        @Override
        public boolean offer(final T element) {
            return offer(element, 1);
        }

        @Override
        public boolean offer(final T element, final int incrementCount) {
            long val = incrementCount;
            ScoredItem<T> value = new ScoredItem<T>(element, incrementCount);
            ScoredItem<T> oldVal = ((ConcurrentHashMap<T, ScoredItem<T>>) itemMap).putIfAbsent(element, value);
            if (oldVal != null) {
                val = oldVal.addAndGetCount(incrementCount);
            } else if (reachCapacity.get() || size.incrementAndGet() > capacity) {
                reachCapacity.set(true);

                ScoredItem<T> oldMinVal = minVal.getAndSet(value);
                itemMap.remove(oldMinVal.getItem());

                while (oldMinVal.isNewItem()) {
                    // Wait for the oldMinVal so its error and value are completely up to date.
                    // no thread.sleep here due to the overhead of calling it - the waiting time will be microseconds.
                }
                long count = oldMinVal.getCount();
                value.addAndGetCount(count);
                value.setError(count);
            }
            value.setNewItem(false);
            minVal.set(getMinValue());

            return val != incrementCount;
        }

        private ScoredItem<T> getMinValue() {
            ScoredItem<T> minValList = null;
            for (ScoredItem<T> entry : itemMap.values()) {
                if (minValList == null || (!entry.isNewItem() && entry.getCount() < minValList.getCount())) {
                    minValList = entry;
                }
            }
            return minValList;
        }

        public long size() {
            return size.get();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (ScoredItem<T> entry : itemMap.values()) {
                sb.append("(" + entry.getCount() + ": " + entry.getItem() + ", e: " + entry.getError() + "),");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
            return sb.toString();
        }

        @Override
        public List<T> peek(final int k) {
            List<T> toReturn = new ArrayList<T>(k);
            List<ScoredItem<T>> values = peekWithScores(k);
            for (ScoredItem<T> value : values) {
                toReturn.add(value.getItem());
            }
            return toReturn;
        }

        public List<ScoredItem<T>> peekWithScores(final int k) {
            List<ScoredItem<T>> values = new ArrayList<ScoredItem<T>>();
            for (Map.Entry<T, ScoredItem<T>> entry : itemMap.entrySet()) {
                ScoredItem<T> value = entry.getValue();
                values.add(new ScoredItem<T>(value.getItem(), value.getCount(), value.getError()));
            }
            Collections.sort(values);
            values = values.size() > k ? values.subList(0, k) : values;
            return values;
        }

    }

    @Override
    protected void process(ComplexEventChunk<StreamEvent> streamEventChunk, Processor nextProcessor,
            StreamEventCloner streamEventCloner, ComplexEventPopulater complexEventPopulater) {
        String rawTagList;
        ComplexEventChunk<StreamEvent> returnEventChunk = new ComplexEventChunk<StreamEvent>();
        StreamEvent streamEvent = streamEventChunk.getFirst();
        while (streamEventChunk.hasNext()) {
            streamEvent = streamEventChunk.next();
            rawTagList = (String) varibleExecutorTagList.execute(streamEvent);
            List<String> stringList = new ArrayList<String>(Arrays.asList(rawTagList.toUpperCase().replaceAll(" ", "")
                    .split(",")));
            stringList.removeAll(stopTagSet);
            String newTagList = stringList.toString().substring(1, stringList.toString().length() - 1);
            String[] hashtagArray = newTagList.split(",");
            for (int i = 0; i < hashtagArray.length; i++) {
                if (!(" ".equals(hashtagArray[i])) && (hashtagArray[i].length() != 0)) {
                    topTags.offer(hashtagArray[i].trim().toUpperCase());
                }
            }
        }
        int peek = (int) ((topTags.size() < passToOut) ? topTags.size() : passToOut);
        List<ScoredItem<String>> passOut = (List<ScoredItem<String>>) topTags.peekWithScores(peek);
        for (int i = 0; i < peek; i++) {
            String val = (String) passOut.get(i).getItem();
            StreamEvent clonedEvent = streamEventCloner.copyStreamEvent(streamEvent);
            setAttributeHTag(clonedEvent, val);
            complexEventPopulater.populateComplexEvent(clonedEvent, new Object[] { i + 1 });
            returnEventChunk.add(clonedEvent);
        }
        if (returnEventChunk.hasNext()) {
            nextProcessor.process(returnEventChunk);
        }
        streamEventChunk.clear();

    }

    @Override
    protected List<Attribute> init(AbstractDefinition inputDefinition,
            ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (!(attributeExpressionExecutors.length == 4)) {
            throw new ExecutionPlanValidationException("Invalid number of Arguments");
        }
        if (!(attributeExpressionExecutors[0] instanceof VariableExpressionExecutor)) {
            throw new IllegalArgumentException("Required a variable, but found a otherparameter");
        } else {
            varibleExecutorTagList = (VariableExpressionExecutor) attributeExpressionExecutors[0];
        }
        if (attributeExpressionExecutors[1] instanceof ConstantExpressionExecutor) {
            passToOut = ((Integer) attributeExpressionExecutors[1].execute(null));
        } else {
            throw new IllegalArgumentException("The first parameter should be an integer");
        }
        if (attributeExpressionExecutors[2] instanceof ConstantExpressionExecutor) {
            maxLength = ((Integer) attributeExpressionExecutors[2].execute(null));
        } else {
            throw new IllegalArgumentException("The first parameter should be an integer");
        }
        if (attributeExpressionExecutors[3] instanceof ConstantExpressionExecutor) {
            tagsToBeRemoved = ((String) attributeExpressionExecutors[3].execute(null));
        } else {
            throw new IllegalArgumentException("The first parameter should be an string");
        }
        if (!"".equals(tagsToBeRemoved)) {
            String[] seperatedTagsArray = tagsToBeRemoved.toUpperCase().replaceAll(" ", "").split(",");
            stopTagSet = new HashSet<String>();
            for (int i = 0; i < seperatedTagsArray.length; i++) {
                stopTagSet.add(seperatedTagsArray[i]);
            }

        }
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(new Attribute("Index", Attribute.Type.INT));
        return attributeList;
    }
}
