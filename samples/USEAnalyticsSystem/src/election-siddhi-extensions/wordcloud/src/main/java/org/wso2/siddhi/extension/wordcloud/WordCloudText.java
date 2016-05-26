package org.wso2.siddhi.extension.wordcloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class WordCloudText extends StreamProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordCloudText.class);
    private final static String[] STOPWORD = {"trump", "donaldtrump", "realdonaldtrump", "via", "wow", "10", "b4",
            "gt", "get", "gets", "come", "go", "ben", "carson", "rubio", "bencarson", "cruzcrew", "feelthebern",
            "sanders", "voteTrump", "clinton", "cruz", "tedcruz", "bernie", "berniesanders", "makeamericagreatagain",
            "trumptrain", "donald", "one", "two", "new", "man", "rt", "i", "me", "my", "myself", "we", "us", "our",
            "just", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his",
            "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs",
            "themselves", "what", "which", "who", "whom", "whose", "this", "that", "these", "those", "am", "is", "are",
            "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "will",
            "would", "should", "can", "could", "ought", "m", "you", "re", "he", "s", "she's", "it's", "we're",
            "they're", "i've", "you've", "we've", "ve", "s", "d", "ll", "you'll", "he'll", "she'll", "we'll",
            "they'll", "isn't", "aren't", "wasn", "weren", "hasn", "haven", "hadn", "doesn", "don", "didn", "won",
            "wouldn", "shan", "shouldn", "can", "t", "cannot", "couldn", "mustn", "let", "that", "who", "what", "here",
            "there", "when", "where", "why", "how's", "a", "an", "the", "and", "but", "if", "or", "because", "as",
            "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through",
            "during", "before", "after", "above", "below", "to", "from", "up", "upon", "down", "in", "out", "on",
            "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how",
            "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only",
            "own", "same", "so", "than", "too", "very", "say", "says", "said", "shall", "trump", "donaldtrump",
            "hillary", "clinton", "hillaryclinton", "ted", "cruz", "tedcruz", "rick", "santorum", "ricksantorum",
            "marco", "rubio", "marcorubio", "mike", "huckabee", "mikehuckabee", "martin", "omalley", "martinomalley",
            "carly", "fiorina", "carlyfiorina", "rand", "paul", "randpaul", "john", "kasich", "johnkasich", "ben",
            "carson", "bencarson", "lindsley", "graham", "lindsleygraham", "scott", "walker", "scottwalker", "jim",
            "gilmore", "jimgilmore", "jeb", "bush", "jebbush", "http", "https", "chris", "christie", "chrischristie",
            "pataki", "george", "georgepataki", "election", "election2016"};
    private static Set<String> stopWordSet;
    private static Tagger tagger;
    private static VariableExpressionExecutor variableExpressionText;
    private static String option = "NLP";

    @Override
    public void start() {
        // No need to maintain a state.
    }

    @Override
    public void stop() {
        // No need to maintain a state.
    }

    @Override
    public Object[] currentState() {
        Object[] arr = new Object[0];
        return arr;// No need to maintain a state.
    }

    @Override
    public void restoreState(Object[] state) {
        // No need to maintain a state.
    }

    @Override
    protected void process(ComplexEventChunk<StreamEvent> streamEventChunk, Processor nextProcessor,
                           StreamEventCloner streamEventCloner, ComplexEventPopulater complexEventPopulater) {
        ComplexEventChunk<StreamEvent> returnEventChunk = new ComplexEventChunk<StreamEvent>();
        StreamEvent streamEvent;
        while (streamEventChunk.hasNext()) {
            streamEvent = streamEventChunk.next();
            streamEventChunk.remove();
            String tweetText;
            tweetText = (String) variableExpressionText.execute(streamEvent);
            String sendOutTextChunk = "";
            if ("NLP".equalsIgnoreCase(option)) {
                sendOutTextChunk = applyNlp(tweetText);
            } else if ("COMMON".equalsIgnoreCase(option)) {
                sendOutTextChunk = applyCommon(tweetText);
            }
            complexEventPopulater.populateComplexEvent(streamEvent, new Object[]{sendOutTextChunk});
           returnEventChunk.add(streamEvent);
        }
        nextProcessor.process(returnEventChunk);
        streamEventChunk.clear();
    }

    private String applyNlp(String Processed) {
        String sendOut = "";
        List<TaggedToken> taggedTokens = tagger.tokenizeAndTag(Processed);
        for (TaggedToken token : taggedTokens) {
            if (token.tag.contains("N") || token.tag.contains("A")) {
                sendOut = sendOut.concat(token.token.concat(" "));
            }
        }
        return sendOut;
    }

    private String applyCommon(String source) {
        String newString = "";
        String sourceRemoveRegex = source.replaceAll("#[A-Za-z0-9]*", " ")
                .replaceAll("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]", " ")
                .replaceAll("@[A-Za-z0-9]*", " ").toLowerCase().replaceAll("[^a-z]", " ").replaceAll("[0-9]+", " ");
        List<String> stringList = new ArrayList<String>(Arrays.asList(sourceRemoveRegex.split(" ")));
        stringList.removeAll(stopWordSet);
        newString = stringList.toString().replaceAll(",", "");
        if (newString.length() > 2) {
            return newString.substring(1, newString.length() - 1);
        } else {
            return "";
        }
    }

    @Override
    protected List<Attribute> init(AbstractDefinition inputDefinition,
                                   ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (!(attributeExpressionExecutors.length == 2)) {
            throw new UnsupportedOperationException("Invalid number of Arguments");
        }
        if (!(attributeExpressionExecutors[0] instanceof VariableExpressionExecutor)) {
            throw new UnsupportedOperationException("Required a variable, but found a otherparameter");
        } else {
            variableExpressionText = (VariableExpressionExecutor) attributeExpressionExecutors[0];
        }
        if (!(attributeExpressionExecutors[1] instanceof ConstantExpressionExecutor)) {
            throw new UnsupportedOperationException("Required a String NLP or COMMON, but found a other parameter");
        } else {
            if ("NLP".equalsIgnoreCase(attributeExpressionExecutors[1].execute(null).toString())
                    || "COMMON".equalsIgnoreCase(attributeExpressionExecutors[1].execute(null).toString())) {
                option = (String) attributeExpressionExecutors[1].execute(null);
            } else {
                throw new UnsupportedOperationException("Invalid Option Selection. Option shoud be  NLP or COMMON ");
            }
        }
        if ("NLP".equalsIgnoreCase(option)) {
            tagger = new Tagger();
            try {
                tagger.loadModel("/cmu/arktweetnlp/model.20120919");
            } catch (IOException e) {
                LOGGER.error("Error in load Model in word cloud extension ", e);
            }

        } else {
            stopWordSet = new HashSet<String>();
            int len = STOPWORD.length;
            for (int i = 0; i < len; i++) {
                stopWordSet.add(STOPWORD[i]);
            }

        }
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(new Attribute("ProcessedWords", Attribute.Type.STRING));
        return attributeList;
    }

}
