Build Following extension pom files using `mvn clean install` command and put .jar files into CEP_HOME/repository/components/dropins folder
-------------------------------------------------------------------------------------------------------------------------------------------

**How To Build An Extension**

1. From the terminal _cd_ into the _src/election-siddhi-extensions/**[extension-name]**/_ folder
2. Run `mvn clean install` to build extension
3. Copy _src/election-siddhi-extensions/**[extension-name]**/target/siddhi-extension-**[extension-name]**-3.0.6-SNAPSHOT.jar_ 
to _CEP_HOME/repository/components/dropins_

<br />
**Extension Names**

1. popularlist
.jar file to copy: siddhi-extension-popularlist-3.0.6-SNAPSHOT.jar

2. rssreader
.jar file to copy: siddhi-extension-rssreader-3.0.6-SNAPSHOT.jar

3. sentiments
.jar file to copy: siddhi-extension-sentiments-3.0.6-SNAPSHOT.jar

4. wordcloud
.jar file to copy: siddhi-extension-wordcloud-3.0.6-SNAPSHOT.jar
