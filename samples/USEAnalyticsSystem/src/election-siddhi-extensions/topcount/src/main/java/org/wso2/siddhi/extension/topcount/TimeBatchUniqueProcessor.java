package org.wso2.siddhi.extension.topcount;

import static java.util.concurrent.TimeUnit.HOURS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.event.ComplexEventChunk;
import org.wso2.siddhi.core.event.stream.StreamEvent;
import org.wso2.siddhi.core.event.stream.StreamEventCloner;
import org.wso2.siddhi.core.event.stream.populater.ComplexEventPopulater;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.VariableExpressionExecutor;
import org.wso2.siddhi.core.query.processor.Processor;
import org.wso2.siddhi.core.query.processor.stream.StreamProcessor;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;

public class TimeBatchUniqueProcessor extends StreamProcessor {
    private VariableExpressionExecutor userName;
    private VariableExpressionExecutor userParty;
    private Map<String, StreamEvent> usersMap = new ConcurrentHashMap<String, StreamEvent>();
    private int trumpCount, bernieCount, clintonCount, cruzCount;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void start() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                usersMap.clear();
                trumpCount = 0;
                bernieCount = 0;
                clintonCount = 0;
                cruzCount = 0;
            }

        }, 0, 24, HOURS);
    }

    @Override
    public void stop() {
        scheduler.shutdownNow();
    }

    @Override
    public Object[] currentState() {
        return new Object[] { trumpCount, bernieCount, clintonCount, cruzCount };
    }

    @Override
    public void restoreState(Object[] state) {
        // No need to maintain a state
    }

    @Override
    protected void process(ComplexEventChunk<StreamEvent> streamEventChunk, Processor nextProcessor,
            StreamEventCloner streamEventCloner, ComplexEventPopulater complexEventPopulater) {
        StreamEvent streamEvent = streamEventChunk.getFirst();
        ComplexEventChunk<StreamEvent> returnEventChunk = new ComplexEventChunk<StreamEvent>();
        while (streamEventChunk.hasNext()) {
            streamEvent = streamEventChunk.next();
            boolean isContainkey = usersMap.containsKey((String) userName.execute(streamEvent));
            if (!isContainkey) {
                usersMap.put((String) userName.execute(streamEvent), streamEvent);
                String party = (String) userParty.execute(streamEvent);
                if ("TRUMP".equals(party)) {
                    trumpCount++;
                } else if ("CLINTON".equals(party)) {
                    clintonCount++;
                } else if ("BERNIE".equals(party)) {
                    bernieCount++;
                } else if ("CRUZ".equals(party)) {
                    cruzCount++;
                }
            }
        }
        if (streamEvent != null) {
            complexEventPopulater.populateComplexEvent(streamEvent, new Object[] { trumpCount, clintonCount, cruzCount,
                    bernieCount });
            returnEventChunk.add(streamEvent);
            nextProcessor.process(returnEventChunk);
        }
        streamEventChunk.clear();
    }

    @Override
    protected List<Attribute> init(AbstractDefinition inputDefinition,
            ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {

        if (!(attributeExpressionExecutors.length == 2)) {
            throw new ExecutionPlanValidationException("Invalid number of Arguments");
        }
        if (!(attributeExpressionExecutors[0] instanceof VariableExpressionExecutor)) {
            throw new ExecutionPlanValidationException("Required a variable, but found a otherparameter");
        } else {
            userName = (VariableExpressionExecutor) attributeExpressionExecutors[0];
        }
        if (!(attributeExpressionExecutors[1] instanceof VariableExpressionExecutor)) {
            throw new ExecutionPlanValidationException("Required a variable, but found a otherparameter");
        } else {
            userParty = (VariableExpressionExecutor) attributeExpressionExecutors[1];
        }
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(new Attribute("TRUMP", Attribute.Type.INT));
        attributeList.add(new Attribute("CLINTON", Attribute.Type.INT));
        attributeList.add(new Attribute("CRUZ", Attribute.Type.INT));
        attributeList.add(new Attribute("BERNIE", Attribute.Type.INT));
        return attributeList;
    }

}
