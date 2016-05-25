package org.wso2.siddhi.extension.wordcloudcombined;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;

import com.clearspring.analytics.stream.ITopK;
import com.clearspring.analytics.stream.ScoredItem;

public class CombinedWordCloud extends StreamProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombinedWordCloud.class);
    private int passToOut = 50;
    private int maxLength = 500;
    private static VariableExpressionExecutor varibleExecutorTextTrump;
    private static VariableExpressionExecutor varibleExecutorTextClinton;
    private static VariableExpressionExecutor varibleExecutorTextCruz;
    private static VariableExpressionExecutor varibleExecutorTextBernie;
    private static CustomConcurrentStreamSummary<String> topKWordsWindow;

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Object[] currentState() {
        // No need to maintain a state.;
        return null;
    }

    @Override
    public void restoreState(Object[] state) {
        // No need to maintain a restore
    }

    @Override
    protected void process(ComplexEventChunk<StreamEvent> streamEventChunk, Processor nextProcessor,
            StreamEventCloner streamEventCloner, ComplexEventPopulater complexEventPopulater) {
        String rawStringTrump;
        String rawStringCruz;
        String rawStringClinton;
        String rawStringBernie;
        ComplexEventChunk<StreamEvent> returnEventChunk = new ComplexEventChunk<StreamEvent>();
        StreamEvent streamEvent;
        while (streamEventChunk.hasNext()) {
            streamEvent = streamEventChunk.next();
            streamEventChunk.remove();
            rawStringTrump = (String) varibleExecutorTextTrump.execute(streamEvent);
            rawStringCruz = (String) varibleExecutorTextCruz.execute(streamEvent);
            rawStringClinton = (String) varibleExecutorTextClinton.execute(streamEvent);
            rawStringBernie = (String) varibleExecutorTextBernie.execute(streamEvent);
            if (rawStringTrump != null) {
                offerWords(rawStringTrump, "TRUMP");
            } else {
                LOGGER.error("TRUMP's wordcloud string is empty in CombinedWordCloud.java class");
            }
            if (rawStringCruz != null) {
                offerWords(rawStringCruz, "CRUZ");
            } else {
                LOGGER.error("CRUZ's wordcloud string is empty in CombinedWordCloud.java class");
            }
            if (rawStringClinton != null) {
                offerWords(rawStringClinton, "CLINTON");
            } else {
                LOGGER.error("CLINTON's wordcloud string is empty in CombinedWordCloud.java class");
            }
            if (rawStringBernie != null) {
                offerWords(rawStringBernie, "BERNIE");
            } else {
                LOGGER.error("BERNIE's wordcloud string is empty in CombinedWordCloud.java class");
            }
            Set<String> sendOutWordsSet = new HashSet<String>();
            int peekCount = (int) ((topKWordsWindow.size() < passToOut) ? topKWordsWindow.size() : passToOut);
            List<ScoredItem<String>> passOut = (List<ScoredItem<String>>) topKWordsWindow.peekWithScores(peekCount);
            StringBuilder processedTrump = new StringBuilder();
            StringBuilder processedClinton = new StringBuilder();
            StringBuilder processedBernie = new StringBuilder();
            StringBuilder processedCruz = new StringBuilder();
            for (int i = 0; i < peekCount; i++) {
                String[] splitedWords = passOut.get(i).getItem().split("-");
                String word = splitedWords[0].trim();
                String party = splitedWords[1];
                if (!sendOutWordsSet.contains(word)) {
                    sendOutWordsSet.add(word);
                    if ("TRUMP".equals(party)) {
                        processedTrump.append(passOut.get(i).toString() + ";");
                    } else if ("CRUZ".equals(party)) {
                        processedCruz.append(passOut.get(i).toString() + ";");
                    } else if ("CLINTON".equals(party)) {
                        processedClinton.append(passOut.get(i).toString() + ";");
                    } else if ("BERNIE".equals(party)) {
                        processedBernie.append(passOut.get(i).toString() + ";");
                    }
                }
            }
            complexEventPopulater.populateComplexEvent(streamEvent, new Object[] { processedTrump.toString(),
                    processedCruz.toString(), processedClinton.toString(), processedBernie.toString() });
            returnEventChunk.add(streamEvent);
        }
        if (returnEventChunk.hasNext()) {
            nextProcessor.process(returnEventChunk);
        }
        returnEventChunk.clear();
    }

    private void offerWords(String rawString, String party) {
        String[] wordsArray = rawString.split(";;");
        for (int i = 0; i < wordsArray.length; i++) {
            String[] wordArray = wordsArray[i].split(":");
            String word = wordArray[0].trim().toUpperCase();
            int val = Integer.parseInt(wordArray[1].trim());
            topKWordsWindow.offer(word + "-" + party, val);
        }
    }

    @Override
    protected List<Attribute> init(AbstractDefinition inputDefinition,
            ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (!(attributeExpressionExecutors.length == 6)) {
            throw new ExecutionPlanValidationException("Invalid number of Arguments");
        }
        if (attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor) {
            passToOut = ((Integer) attributeExpressionExecutors[0].execute(null));
        } else {
            throw new IllegalArgumentException("The first parameter should be an integer");
        }
        if (attributeExpressionExecutors[1] instanceof ConstantExpressionExecutor) {
            maxLength = ((Integer) attributeExpressionExecutors[1].execute(null));
        } else {
            throw new IllegalArgumentException("The secondparameter should be an integer");
        }
        if (attributeExpressionExecutors[2] instanceof VariableExpressionExecutor) {
            varibleExecutorTextTrump = (VariableExpressionExecutor) attributeExpressionExecutors[2];
        } else {
            throw new IllegalArgumentException(
                    "Required varible expression executor of trump's word cloud as 3rd argument, but found other ");
        }
        if (attributeExpressionExecutors[3] instanceof VariableExpressionExecutor) {
            varibleExecutorTextClinton = (VariableExpressionExecutor) attributeExpressionExecutors[3];
        } else {
            throw new IllegalArgumentException(
                    "Required varible expression executor of trump's word cloud as 4th argument, but found other ");
        }
        if (attributeExpressionExecutors[4] instanceof VariableExpressionExecutor) {
            varibleExecutorTextCruz = (VariableExpressionExecutor) attributeExpressionExecutors[4];
        } else {
            throw new IllegalArgumentException(
                    "Required varible expression executor of trump's word cloud as 5th argument, but found other ");
        }
        if (attributeExpressionExecutors[5] instanceof VariableExpressionExecutor) {
            varibleExecutorTextBernie = (VariableExpressionExecutor) attributeExpressionExecutors[5];
        } else {
            throw new IllegalArgumentException(
                    "Required varible expression executor of trump's word cloud as 6th argument, but found other ");
        }
        topKWordsWindow = new CustomConcurrentStreamSummary<String>(maxLength);;
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(new Attribute("TrumpTopWords", Attribute.Type.STRING));
        attributeList.add(new Attribute("CruzTopWords", Attribute.Type.STRING));
        attributeList.add(new Attribute("ClintonTopWords", Attribute.Type.STRING));
        attributeList.add(new Attribute("BernieTopWords", Attribute.Type.STRING));
        return attributeList;
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

}
