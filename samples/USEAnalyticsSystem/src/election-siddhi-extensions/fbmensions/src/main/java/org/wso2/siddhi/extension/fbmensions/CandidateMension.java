package org.wso2.siddhi.extension.fbmensions;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.event.ComplexEventChunk;
import org.wso2.siddhi.core.event.stream.StreamEvent;
import org.wso2.siddhi.core.event.stream.StreamEventCloner;
import org.wso2.siddhi.core.event.stream.populater.ComplexEventPopulater;
import org.wso2.siddhi.core.exception.ExecutionPlanRuntimeException;
import org.wso2.siddhi.core.executor.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.query.processor.Processor;
import org.wso2.siddhi.core.query.processor.stream.StreamProcessor;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;

/*
 * RSSReader:readTitlesStream(url,count)
 * Returns a all full texts of articles .
 * Accept Type(s): STRING. There should be only two arguments type String and integer.URL string and no of news titles to be pass to out
 * Return Type(s): STRING
 * */
public class CandidateMension extends StreamProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CandidateMension.class);
    private String keyWord;
    private String accessTokenString;

    @Override
    public void start() {
        // Nothing to do here
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
        // No need to restore state.
    }

    @Override
    protected void process(ComplexEventChunk<StreamEvent> streamEventChunk, Processor nextProcessor,
            StreamEventCloner streamEventCloner, ComplexEventPopulater complexEventPopulater) {
        ComplexEventChunk<StreamEvent> returnEventChunk = new ComplexEventChunk<StreamEvent>();
        StreamEvent streamEvent = streamEventChunk.getFirst();
        int takingCount = 0;
        int fanCount = 0;
        if (keyWord != null) {
            try {
                URL url = new URL("https://graph.facebook.com/v2.6/" + keyWord
                        + "?fields=talking_about_count,fan_count&access_token=" + accessTokenString);
                InputStream fbDataInputStream = url.openStream();
                JsonReader fbDataReader = Json.createReader(fbDataInputStream);
                JsonObject obj = (JsonObject) fbDataReader.readObject();
                if ((obj != null) && (obj.containsKey("talking_about_count"))) {
                    takingCount = obj.getInt("talking_about_count");
                    fanCount = obj.getInt("fan_count");
                    complexEventPopulater.populateComplexEvent(streamEvent, new Object[] { takingCount, fanCount });
                    returnEventChunk.add(streamEvent);
                    nextProcessor.process(returnEventChunk);
                }
            } catch (MalformedURLException e) {
                LOGGER.error("MalformedURLException exception in candidatemension.java file when trying to connect Facebook API"
                        + e);
            } catch (IOException e) {
                LOGGER.error("IOException exception in candidatemension.java file" + e);
            }
        } else {
            throw new ExecutionPlanRuntimeException("Input to the RSS:Reader() function cannot be null");
        }

    }

    @Override
    protected List<Attribute> init(AbstractDefinition inputDefinition,
            ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (attributeExpressionExecutors.length != 2) {
            throw new ExecutionPlanValidationException("Invalid no of arguments passed to GetPost() function, "
                    + "required 1, but found " + attributeExpressionExecutors.length);
        }
        if (attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor) {
            keyWord = ((String) attributeExpressionExecutors[0].execute(null));
        } else {
            throw new IllegalArgumentException("The first parameter should be an keyword String");
        }
        if (attributeExpressionExecutors[1] instanceof ConstantExpressionExecutor) {
            accessTokenString = (String) (attributeExpressionExecutors[1].execute(null));
        } else {
            throw new IllegalArgumentException("The 3rd parameter should be an acess token");
        }
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(new Attribute("talking_about_count", Attribute.Type.INT));
        attributeList.add(new Attribute("fan_count", Attribute.Type.INT));
        return attributeList;
    }
}
