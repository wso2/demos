package org.wso2.siddhi.extension.sentiments;


import java.io.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.executor.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.function.FunctionExecutor;
import org.wso2.siddhi.query.api.definition.Attribute.Type;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

/*#Sentiment:getRate(text,option)
 * text:String chunk to be calculate sentiment
 * Option: method to calculate sentiment ('COMMON' or 'AFFIN' or 'STANFORD')
 * Sample Query:
 * from inputStream#Sentiment:getRate(text)
 * select attribute1, attribute2
 * insert into outputStream;
 */
public class SentimentRate extends FunctionExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SentimentRate.class);
    private String contentText;
    private static String Option = "COMMON";
    private String[] positiveWordBucket;
    private String[] negativeWordBucket;
    private String[] affinWordBucket;

    @Override
    public Type getReturnType() {
        return Type.INT;
    }

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
        // No need to maintain a state
    }

    @Override
    protected void init(ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (attributeExpressionExecutors.length != 2) {
            throw new IllegalArgumentException(
                    "Invalid no of arguments passed to ClearSentiment:TrackWord() function, "
                            + "required 2, but found " + attributeExpressionExecutors.length);
        }

        if (!(attributeExpressionExecutors[1] instanceof ConstantExpressionExecutor)) {
            throw new IllegalArgumentException("Required a variable, but found a otherparameter");
        } else {
            Option = (String) attributeExpressionExecutors[1].execute(null);
        }

        if ("COMMON".equals(Option)) {
            try {
                negativeWordBucket = getWordsBuckets("negativewords.txt");
            } catch (IOException e) {
                LOGGER.error("Failed to load  negativewords.txt  file ");
            }
            try {
                positiveWordBucket = getWordsBuckets("positivewords.txt");
            } catch (IOException e) {
                LOGGER.error("Failed to load positivewords.txt  file ");
            }

        } else if ("AFFIN".equals(Option)) {
            try {
                affinWordBucket = getWordsBuckets("affinwords.txt");
            } catch (IOException e) {
                LOGGER.error("Failed to load affinwords.txt file ");
            }
        } else if ("STANFORD".equals(Option)) {
            // No need any word set
        } else {
            LOGGER.error("Invalid option of sentimentrate.java clas. Option can be only COMMON,AFFIN or STANFORD");
        }

    }

    protected String[] getWordsBuckets(String fileName) throws IOException {
        String[] wordList = null;
        StringBuilder textChunk = new StringBuilder();
        try{
            InputStream in = getClass().getResourceAsStream("/" + fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                textChunk.append(line).append("\n");
            }
            in.close();
        }catch (Exception ex){
            LOGGER.error("Error Reading " + fileName);
            ex.printStackTrace();
        }
        wordList = textChunk.toString().split(",");
        return wordList;
    }

    @Override
    protected Object execute(Object[] data) {
        int rank = 0;
        contentText = (String) data[0];
        if ("COMMON".equals(Option)) {
            rank = getCommonSentimentRate(contentText);
        } else if ("AFFIN".equals(Option)) {
            rank = getAffinSentimentRate(contentText);
        } else if ("STANFORD".equals(Option)) {
            rank = getStanfordSentimentRate(contentText);
        } else {
            LOGGER.error("Invalid option of sentimentrate.java class. Option can be only COMMON,AFFIN or STANFORD");
        }

        return rank;
    }

    protected int getCommonSentimentRate(String contentText) {
        int positiveRank = 0;
        for (int i = 0; i < positiveWordBucket.length; i++) {
            Matcher m = Pattern.compile("\\b" + positiveWordBucket[i] + "\\b").matcher(contentText);
            while (m.find()) {
                positiveRank++;
            }
        }
        int negativeRank = 0;
        for (int i = 0; i < negativeWordBucket.length; i++) {
            Matcher m = Pattern.compile("\\b" + negativeWordBucket[i] + "\\b").matcher(contentText);
            while (m.find()) {
                negativeRank++;
            }
        }
        return positiveRank - negativeRank;

    }

    protected int getAffinSentimentRate(String contentText) {
        int rank = 0;
        String[] split;
        for (int i = 0; i < affinWordBucket.length; i++) {
            split = affinWordBucket[i].split(" ");
            String word = split[0];
            int val = Integer.parseInt(split[1].trim());
            Matcher m = Pattern.compile("\\b" + word + "\\b").matcher(contentText);
            while (m.find()) {
                rank = rank + val;
            }
        }
        return rank;
    }

    protected int getStanfordSentimentRate(String sentimentText) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        int totalRate = 0;
        String[] linesArr = sentimentText.split("\\.");
        for (int i = 0; i < linesArr.length; i++) {
            if (linesArr[i] != null) {
                Annotation annotation = pipeline.process(linesArr[i]);
                for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                    Tree tree = sentence.get(SentimentAnnotatedTree.class);
                    int score = RNNCoreAnnotations.getPredictedClass(tree);
                    totalRate = totalRate + (score - 2);
                }
            }
        }

        return totalRate;
    }

    @Override
    protected Object execute(Object data) {
        // does not call comment
        return null;

    }
}
