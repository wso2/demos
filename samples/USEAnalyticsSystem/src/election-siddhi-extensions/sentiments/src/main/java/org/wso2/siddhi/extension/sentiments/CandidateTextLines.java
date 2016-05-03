package org.wso2.siddhi.extension.sentiments;

import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.exception.ExecutionPlanRuntimeException;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.function.FunctionExecutor;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.definition.Attribute.Type;

/*
 *#Sentiment:trackWord(text,"Trump","Donald","He")
 * Sample Query:
 * from inputStream#Sentiment:trackWord(text,"Trump","Donald","He")
 * select attribute1, attribute2
 * insert into outputStream;
 */

public class CandidateTextLines extends FunctionExecutor {

    @Override
    public Type getReturnType() {
        return Type.STRING;
    }

    @Override
    public void start() {
        // Nothing to do here.
    }

    @Override
    public void stop() {
        // Nothing to do here.
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

    @Override
    protected void init(ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (attributeExpressionExecutors.length != 4) {
            throw new IllegalArgumentException(
                    "Invalid no of arguments passed to CandidateTextLines.java class, "
                            + "required 2, but found " + attributeExpressionExecutors.length);
        }
        Attribute.Type attributeType1 = attributeExpressionExecutors[0].getReturnType();
        if (!(attributeType1 == Attribute.Type.STRING)) {
            throw new IllegalArgumentException(
                    "Invalid parameter type found for the argument 1 of CandidateTextLines.java class function");
        }
        Attribute.Type attributeType2 = attributeExpressionExecutors[1].getReturnType();
        if (!(attributeType2 == Attribute.Type.STRING)) {
            throw new IllegalArgumentException(
                    "Invalid parameter type found for the argument 2 of CandidateTextLines.java class function");
        }
        Attribute.Type attributeType3 = attributeExpressionExecutors[2].getReturnType();
        if (!(attributeType3 == Attribute.Type.STRING)) {
            throw new IllegalArgumentException(
                    "Invalid parameter type found for the argument 3 of CandidateTextLines.java class function");
        }
        Attribute.Type attributeType4 = attributeExpressionExecutors[3].getReturnType();
        if (!(attributeType4 == Attribute.Type.STRING)) {
            throw new IllegalArgumentException(
                    "Invalid parameter type found for the argument 4 of CandidateTextLines.java class function");
        }
    }

    @Override
    protected Object execute(Object[] data) {
        StringBuilder text = new StringBuilder();
        if (data != null) {
            String[] arr = ((String) data[0]).split("\\.");
            for (String line : arr) {
                if ((line.toLowerCase().contains(((String) data[1]).toLowerCase())) || (line.toLowerCase().contains(((String) data[2]).toLowerCase()))
                        || (line.toLowerCase().contains(((String) data[3]).toLowerCase()))) {
                    text.append(" ".concat(line));
                }
            }
        } else {
            throw new ExecutionPlanRuntimeException("Input to the ClearSentiment:TrackWord() function cannot be null");
        }
        return text.toString().trim();
    }

    @Override
    protected Object execute(Object data) {
        // This method is not call
        return null;
    }

}
