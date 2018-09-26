# WSO2 New Relic Insight Handler

WSO2 New Relic Insight Handler is responsable to extend WSO2 using [New Relic API](https://docs.newrelic.com/docs/agents/java-agent/api-guides/guide-using-java-agent-api) and send information from WSO2 ESB to New Relic Insight. This way we can verify and analyze ESB metrics in New Relic Dashboard.  


## Building from source

1. Download and install JDK 8 or later
2. Get a clone or download the source from this repository (https://github.com/fabcipriano/wso2-algoservice.git)
3. Run the Maven command ``mvn clean  install`` from within the wso2-newrelic directory.

## Running the extension inseide WSO2 ESB

### Running WSO2 New Relic Insight Handler
1. Go to your WSO2 ESB 5.0.0 dir ${WSO2-ESB-HOME}/repository/components/lib directory and copy the ``wso2-newrelic-1.1-SNAPSHOT.jar`` to this folder (to create this file execute the steps in **Building from source** section)
2. Go to your WSO2 ESB 5.0.0 dir ${WSO2-ESB-HOME}/repository/conf and edit the ``synapse-handlers.xml`` file. See exemple below.:
```xml
<handlers>
    <!-- Other handlers here -->
    <handler name="NewRelicInsightsHandler" class="br.com.facio.wso2.handler.NewRelicInsightsHandler"/>
</handlers>
```
3. Go to your WSO2 ESB 5.0.0 dir ${WSO2-ESB-HOME}/repository/conf and edit the file log4j.properties and add the line below.:
```
log4j.category.br.com.facio.wso2=DEBUG
```

#### Note:

* Make sure you stop WSO2 ESB 5.0.0 before install this extension
* After this start you WSO2 ESB execute some transactions and you will see the logs
* Tested with WSO2 ESB 5.0.0
* Dont forget to configure New Relic in your WSO2 5.0.0 enviroment
