package org.wso2.siddhi.extension.rssreader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
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

/*
 * RSSReader:readTitlesStream(url,count)
 * Returns a all full texts of articles .
 * Accept Type(s): STRING. There should be only two arguments type String and integer.URL string and no of news titles to be pass to out
 * Return Type(s): STRING
 * */
public class RSSFeedTitles extends StreamProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RSSFeedTitles.class);
    private String urlString;
    private int passToOut;

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
        StreamEvent streamEvent = streamEventChunk.getFirst();
        URL rssURL;
        if (urlString != null) {
            if (((String) urlString).startsWith("https:")) {
                try {
                    rssURL = new URL((String) urlString);
                    TextArticals articals = readFeed(rssURL);
                    String[] titles = articals.getTitles();
                    String[] publishedDates = articals.getPublishedDate();
                    String[] descriptions = articals.getDescription();
                    String[] links = articals.getLinks();
                    int min = (passToOut > titles.length) ? titles.length : passToOut;
                    for (int i = 0; i < min; i++) {
                        StreamEvent clonedEvent = streamEventCloner.copyStreamEvent(streamEvent);
                        complexEventPopulater.populateComplexEvent(clonedEvent, new Object[] { titles[i],
                                descriptions[i], publishedDates[i], links[i], i + 1 });
                        returnEventChunk.add(clonedEvent);

                    }
                } catch (Exception e) {
                    LOGGER.error("error read RSS feeds in RSSReedTitles class " + e);
                }
            } else {
                throw new ExecutionPlanRuntimeException("Input to the RSS:Reader() function should contain URL");
            }
        } else {
            throw new ExecutionPlanRuntimeException("Input to the RSS:Reader() function cannot be null");
        }
        if (returnEventChunk.hasNext()) {
            nextProcessor.process(returnEventChunk);
        }

    }

    @Override
    protected List<Attribute> init(AbstractDefinition inputDefinition,
            ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (attributeExpressionExecutors.length != 2) {
            throw new IllegalArgumentException("Invalid no of arguments passed to math:sin() function, "
                    + "required 1, but found " + attributeExpressionExecutors.length);
        }
        if (attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor) {
            urlString = ((String) attributeExpressionExecutors[0].execute(null));
        } else {
            throw new IllegalArgumentException("The first parameter should be an String");
        }
        if (attributeExpressionExecutors[1] instanceof ConstantExpressionExecutor) {
            passToOut = (Integer) (attributeExpressionExecutors[1].execute(null));
        } else {
            throw new IllegalArgumentException("The 2 parameter should be an integer");
        }

        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(new Attribute("Title", Attribute.Type.STRING));
        attributeList.add(new Attribute("Dis", Attribute.Type.STRING));
        attributeList.add(new Attribute("Pub", Attribute.Type.STRING));
        attributeList.add(new Attribute("Link", Attribute.Type.STRING));
        attributeList.add(new Attribute("Count", Attribute.Type.INT));
        return attributeList;
    }

    public TextArticals readFeed(URL rssURL) {
        String[] titles = null;
        String[] publishedDates = null;
        String[] descriptions = null;
        String[] links = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(rssURL.openStream());
            NodeList items = doc.getElementsByTagName("item");
            titles = new String[items.getLength()];
            publishedDates = new String[items.getLength()];
            descriptions = new String[items.getLength()];
            links = new String[items.getLength()];
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                titles[i] = (String) getValue(item, "title");
                publishedDates[i] = (String) getValue(item, "pubDate");
                descriptions[i] = (String) getValue(item, "description");
                links[i] = (String) getValue(item, "link");
            }

        } catch (Exception e) {
            LOGGER.error("error read RSS feeds in readFeed function in RSSReedTitles class " + e);
        }
        return new TextArticals(links, titles, publishedDates, descriptions);
    }

    public String getValue(Element parent, String nodeName) {
        return parent.getElementsByTagName(nodeName).item(0).getFirstChild().getNodeValue();
    }

    private class TextArticals {
        private String[] titles;
        private String[] publishedDates;
        private String[] descriptions;
        private String[] links;

        public TextArticals(String[] links, String[] titles, String[] publishedDates, String[] descriptions) {
            this.links = links;
            this.titles = titles;
            this.publishedDates = publishedDates;
            this.descriptions = descriptions;
        }

        public String[] getLinks() {
            return this.links;

        }

        public String[] getTitles() {
            return this.titles;

        }

        public String[] getDescription() {
            return this.descriptions;

        }

        public String[] getPublishedDate() {
            return this.publishedDates;

        }
    }
}
