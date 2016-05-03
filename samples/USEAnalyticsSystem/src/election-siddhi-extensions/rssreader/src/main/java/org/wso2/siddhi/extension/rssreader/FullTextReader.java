package org.wso2.siddhi.extension.rssreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.exception.ExecutionPlanRuntimeException;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.executor.function.FunctionExecutor;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.definition.Attribute.Type;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.jsoup.*;
import org.jsoup.select.*;

/*
 * RSSReader:readText(url)
 * Returns a all full texts of articles .
 * Accept Type(s): STRING. There should be only one arguments.
 * Return Type(s): STRING
 * */
public class FullTextReader extends FunctionExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FullTextReader.class);
    private URL rssURL;

    @Override
    public Type getReturnType() {
        return Attribute.Type.STRING;
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
        // No need to maintain a state
        return null;
    }

    @Override
    public void restoreState(Object[] state) {
        // No need to maintain a state.
    }

    @Override
    protected void init(ExpressionExecutor[] attributeExpressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (attributeExpressionExecutors.length != 1) {
            throw new ExecutionPlanValidationException("Invalid no of arguments passed to math:sin() function, "
                    + "required 1, but found " + attributeExpressionExecutors.length);
        }

    }

    @Override
    protected Object execute(Object[] data) {
        Object arr = new Object[0];
        return arr;// No need to maintain a state.
    }

    @Override
    protected Object execute(Object data) {
        StringBuilder formatedString = new StringBuilder();
        if (data != null) {
            if (((String) data).startsWith("https:")) {
                try {
                    rssURL = new URL((String) data);
                    FullTextArticals articals = readFeed();
                    String[] links = articals.getLinks();
                    String[] titles = articals.getTitles();
                    for (int i = 0; i < links.length; i++) {
                        formatedString.append(titles[i].concat(" ").concat(readText(links[i])).concat(" "));
                    }
                    return formatedString;
                } catch (Exception e) {
                    LOGGER.error("error read RSS feeds in FullTextReader class " + e);
                }
            } else {
                throw new ExecutionPlanRuntimeException("Input to the RSS:Reader() function should contain URL");
            }
        } else {
            throw new ExecutionPlanRuntimeException("Input to the RSS:Reader() function cannot be null");
        }
        return formatedString;
    }

    private String readText(String link) {
        String text = "";
        try {
            String url = link;
            org.jsoup.nodes.Document doc = (org.jsoup.nodes.Document) Jsoup.connect(url).get();
            Elements paragraphs = doc.select("p");
            for (org.jsoup.nodes.Element p : paragraphs) {
                text = text.concat(p.text().concat(" "));
            }
        } catch (IOException ex) {
            LOGGER.error("error read RSS feeds in ReadText method FullTextReader class " + ex);
        }
        return text;
    }

    private FullTextArticals readFeed() {
        String[] links = null;
        String[] titles = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(rssURL.openStream());
            NodeList items = doc.getElementsByTagName("item");
            links = new String[items.getLength()];
            titles = new String[items.getLength()];
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                links[i] = (String) getValue(item, "link");
                titles[i] = (String) getValue(item, "title");
            }
        } catch (Exception e) {
            LOGGER.error("error read RSS feeds in FullTextReader class " + e);
        }
        return new FullTextArticals(links, titles);
    }

    private String getValue(Element parent, String nodeName) {
        return parent.getElementsByTagName(nodeName).item(0).getFirstChild().getNodeValue();
    }

    private class FullTextArticals {
        private String[] links;
        private String[] titles;

        public FullTextArticals(String[] links, String[] titles) {
            this.links = links;
            this.titles = titles;
        }

        public String[] getLinks() {
            return this.links;

        }

        public String[] getTitles() {
            return this.titles;

        }
    }
}
