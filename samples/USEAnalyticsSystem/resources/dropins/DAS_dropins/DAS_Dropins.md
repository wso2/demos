Build Following extension pom files using `mvn clean install` command and put .jar files into DAS_HOME/repository/components/dropins
------------------------------------------------------------------------------------------------------------------------------------

**How To Build An Extension**

1. From the terminal _cd_ into the _src/election-siddhi-extensions/**[extension-name]**/_ folder
2. Run `mvn clean install` to build extension
3. Copy _src/election-siddhi-extensions/**[extension-name]**/target/siddhi-extension-**[extension-name]**-3.0.6-SNAPSHOT.jar_ 
to _DAS_HOME/repository/components/dropins_

<br />
**Extension Names**

1. userparty
.jar file to copy: siddhi-extension-userparty-3.0.6-SNAPSHOT.jar

2. topcount
.jar file to copy: siddhi-extension-topcount-3.0.6-SNAPSHOT.jar

