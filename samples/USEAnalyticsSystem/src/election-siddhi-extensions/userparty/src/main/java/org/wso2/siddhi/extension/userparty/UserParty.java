package org.wso2.siddhi.extension.userparty;

import static java.util.concurrent.TimeUnit.HOURS;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mysql.jdbc.PreparedStatement;

/*
 #TweetReader:getParty(from_user,5)
 @Param: UserScreenName and Executor Schedule Time
 @Return:Highest Percentage Party and That percentage
 */
public class UserParty extends StreamProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserParty.class);
    private ComplexEventChunk<StreamEvent> returnEventChunk;
    private VariableExpressionExecutor variableExpressionURLName;
    private static String[] trumpTagsArray = { "#trump", "#donaldtrump", "#trump2016", "#makeamericagreatagain",
            "#realDonaldTrump", "#trumpforpresident", "#trumppresident2016", "#donaldtrumpforpresident",
            "#donaldtrumpforpresident2016", "#buttrump", "#WomenForTrump" };
    private static String[] clintonTagsArray = { "#hillary2016", "#hillaryclinton", "#hillaryforpresident2016",
            "#imwithher", "#hillaryforpresident", "#hillary", "#HillYes" };
    private static String[] bernieTagsArray = { "#bernie2016", "#feelthebern", "#berniesanders", "#bernie",
            "#bernieforpresident", "#bernieorbust", "#bernbots", "#berniebros", "#bernsreturns" };
    private static String[] tedTagsArray = { "#tedcruz", "#cruzcrew", "#cruz2016", "#makedclisten", "#cruzcrew",
            "#choosecruz", "#tedcruzforpresident", "#tedcruz2016", "#istandwithtedcruz", "#cruztovictory" };
    private final ReentrantLock lock = new ReentrantLock();
    private static int dbWriteScheduleTime = 2;
    private static int corePoolSize = 8;
    private static int maxPoolSize = 400;
    private static long keepAliveTime = 20000;
    private static int jobQueueSize = 10000;
    private static ExecutorService dbWriteExecutor;
    private ThreadFactory userPartyTreadsName;
    private static Map<String, String> currentUserChunk;
    private final ScheduledExecutorService dbWriteScheduler = Executors.newScheduledThreadPool(1);
    private static BlockingQueue<Runnable> dbWriteBlockingQ;

    public UserParty() {
        userPartyTreadsName = new ThreadFactoryBuilder().setNameFormat("GetParty-%d").build();
        dbWriteBlockingQ = new LinkedBlockingQueue<Runnable>(jobQueueSize);
        dbWriteExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                dbWriteBlockingQ, userPartyTreadsName);
    }

    @Override
    public void start() {
        dbWriteScheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                writeDB();
            }
        }, 0, dbWriteScheduleTime, HOURS);

    }

    public void writeDB() {
        if (currentUserChunk != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date currentDate = new Date();
            String strDate = simpleDateFormat.format(currentDate);
            Set<String> currentKeys = currentUserChunk.keySet();
            java.sql.Connection connection = null;
            java.sql.PreparedStatement preparedStatement = null;
            try {
                connection = getConnection();
                connection.setAutoCommit(false);
                String[] keysArray = (String[]) currentKeys.toArray();
                for (int i = 0; i < currentUserChunk.size(); i++) {
                    String query = "select * from tweep where name =?";
                    java.sql.PreparedStatement preStatement = connection.prepareStatement(query);
                    preStatement.setString(1, keysArray[i].toString());
                    java.sql.ResultSet resultSet = preStatement.executeQuery();
                    if (!resultSet.next() && !"T".equals(currentUserChunk.get(currentKeys.toArray()[i].toString()))) {
                        String sql = "INSERT INTO tweep VALUES (?,?,?)";
                        preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, keysArray[i].toString());
                        preparedStatement.setString(2, strDate);
                        preparedStatement.setString(3, currentUserChunk.get(keysArray[i].toString()));
                        preparedStatement.addBatch();

                    }
                }
                preparedStatement.executeBatch();
                connection.commit();
                LOGGER.info("Tweep hashmap finished write to database. ");
            } catch (SQLException e) {
                LOGGER.error("Error write to tweep table in database ", e);
            } finally {
                try {
                    if (connection != null) {
                        connection.setAutoCommit(true);
                        preparedStatement.close();
                        connection.close();
                    }
                } catch (SQLException e) {
                    LOGGER.error("SQL error when closing DB connection ", e);
                }
            }
        }
        currentUserChunk = new ConcurrentHashMap<String, String>();
    }

    @Override
    public void stop() {
        dbWriteScheduler.shutdownNow();
        dbWriteExecutor.shutdownNow();
        dbWriteBlockingQ.clear();

    }

    @Override
    public Object[] currentState() {
        Object[] arr = new Object[0];
        return arr;// No need to maintain a state.
    }

    @Override
    public void restoreState(Object[] state) {
        // No need to maintain a state
    }

    @Override
    protected void process(ComplexEventChunk<StreamEvent> streamEventChunk, Processor nextProcessor,
            StreamEventCloner streamEventCloner, ComplexEventPopulater complexEventPopulater) {
        returnEventChunk = new ComplexEventChunk<StreamEvent>();
        StreamEvent streamEvent;
        while (streamEventChunk.hasNext()) {
            streamEvent = streamEventChunk.next();
            if (currentUserChunk.containsKey((String) variableExpressionURLName.execute(streamEvent))) {
                if ("T".equals(currentUserChunk.get((String) variableExpressionURLName.execute(streamEvent)))) {
                    complexEventPopulater.populateComplexEvent(streamEvent, new Object[] { currentUserChunk
                            .get((String) variableExpressionURLName.execute(streamEvent)) });
                    returnEventChunk.add(streamEvent);
                }
            } else {
                currentUserChunk.put((String) variableExpressionURLName.execute(streamEvent), "T");
                dbWriteExecutor.submit(new UserHistoricalTweetAnalysis(streamEvent, complexEventPopulater));
            }
        }
        streamEventChunk.clear();
        sendEventChunk();
    }

    public void addEventChunk(StreamEvent event) {
        lock.lock();
        returnEventChunk.add(event);
        lock.unlock();
    }

    public synchronized void sendEventChunk() {
        lock.lock();
        if (returnEventChunk.hasNext()) {
            nextProcessor.process(returnEventChunk);
            returnEventChunk = new ComplexEventChunk<StreamEvent>();
        }
        lock.unlock();
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
            variableExpressionURLName = (VariableExpressionExecutor) attributeExpressionExecutors[0];
        }
        if (!(attributeExpressionExecutors[1] instanceof ConstantExpressionExecutor)) {
            throw new UnsupportedOperationException("Required a variable, but found a otherparameter");
        } else {
            dbWriteScheduleTime = (Integer) attributeExpressionExecutors[1].execute(null);
        }
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(new Attribute("TopName", Attribute.Type.STRING));
        return attributeList;

    }

    public java.sql.Connection getConnection() {
        try {
            String connectionURL = "jdbc:mysql://localhost:3306/use16_das_data_db";
            java.sql.Connection connection = null;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = (java.sql.Connection) DriverManager.getConnection(connectionURL, "twitter", "NCGPovBrVleB6");
            return connection;
        } catch (SQLException e) {
            LOGGER.error("SQL error get DB connection function", e);
        } catch (Exception e) {
            LOGGER.error("IO error get DB connection function", e);
        }
        return null;
    }

    class UserHistoricalTweetAnalysis implements Runnable {
        String userName;
        StreamEvent event;
        ComplexEventPopulater complexEventPopulater;

        public UserHistoricalTweetAnalysis(StreamEvent event, ComplexEventPopulater complexEventPopulater) {
            this.userName = (String) variableExpressionURLName.execute(event);
            this.event = event;
            this.complexEventPopulater = complexEventPopulater;
        }

        @Override
        public void run() {
            String tweetChunk = "";
            double trumpCount = 0, bernieCount = 0, clintonCount = 0, tedCount = 0;
            String url = "https://twitter.com/" + userName;
            Document doc;
            int maxEvents = 0;
            try {
                Connection con = Jsoup
                        .connect(url)
                        .userAgent(
                                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                        .ignoreContentType(true).ignoreHttpErrors(true).timeout(10000);
                doc = con.get();
                Elements paragraphs = doc.select("p");
                for (Element p : paragraphs) {
                    tweetChunk = tweetChunk.concat(p.text().concat(" "));
                    if (maxEvents > 100) {
                        break;
                    }
                    maxEvents++;
                }

                for (String tag : trumpTagsArray) {
                    trumpCount = trumpCount + StringUtils.countMatches(tweetChunk.toLowerCase(), tag.toLowerCase());
                }
                for (String tag : bernieTagsArray) {
                    bernieCount = bernieCount + StringUtils.countMatches(tweetChunk.toLowerCase(), tag.toLowerCase());
                }
                for (String tag : clintonTagsArray) {
                    clintonCount = clintonCount + StringUtils.countMatches(tweetChunk.toLowerCase(), tag.toLowerCase());
                }
                for (String tag : tedTagsArray) {
                    tedCount = tedCount + StringUtils.countMatches(tweetChunk.toLowerCase(), tag.toLowerCase());
                }
                double sum = trumpCount + bernieCount + clintonCount + tedCount;
                if (sum != 0) {
                    List<ElectionCandidate> list = new ArrayList<ElectionCandidate>();
                    list.add(new ElectionCandidate("CLINTON", (double) ((clintonCount / sum) * 100.00)));
                    list.add(new ElectionCandidate("TRUMP", (double) ((trumpCount / sum) * 100.00)));
                    list.add(new ElectionCandidate("BERNIE", (double) ((bernieCount / sum) * 100.00)));
                    list.add(new ElectionCandidate("CRUZ", (double) ((tedCount / sum) * 100.00)));
                    Collections.sort(list, new ElectionCandidate());
                    if (currentUserChunk.containsKey((String) variableExpressionURLName.execute(event))
                            && "T".equals(currentUserChunk.get((String) variableExpressionURLName.execute(event)))) {
                        ((ConcurrentHashMap<String, String>) currentUserChunk).replace(
                                (String) variableExpressionURLName.execute(event), new String(list.get(0)
                                        .getCandidateName()));
                    }
                    complexEventPopulater.populateComplexEvent(event, new Object[] { list.get(0).getCandidateName(),
                            list.get(0).getCandidateRank() });
                    addEventChunk(event);
                }

            } catch (IOException e) {

                LOGGER.error("Error Connecting Twitter API  ", e);
            }

        }

    }

}

class ElectionCandidate implements Comparator<ElectionCandidate>, Comparable<ElectionCandidate> {
    private String name;
    private double rank;

    ElectionCandidate() {
    }

    ElectionCandidate(String n, double a) {
        name = n;
        rank = a;
    }

    public String getCandidateName() {
        return name;
    }

    public double getCandidateRank() {
        return rank;
    }

    @Override
    public int compareTo(ElectionCandidate d) {
        return (this.name).compareTo(d.name);
    }

    @Override
    public int compare(ElectionCandidate d, ElectionCandidate d1) {
        return (int) (d1.rank - d.rank);
    }
}
