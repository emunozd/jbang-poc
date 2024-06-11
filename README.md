
# JBang Hello World example

JBang simplifies Java programming, offering easy creation, editing, and running of self-contained Java programs in local environments. 

With just one download and command, JBang eliminates Java's setup, making Java development as seamless as Python or JavaScript. JBang supports multiple files and dependencies from any Maven repository, compatible with Java 8 to the latest Java 17 and beyond.

## Prerequisites

 - Red Hat AMQ Broker 7.X

## Installation

To install JBang locally, please excecute the command:
```bash
$ curl -Ls https://sh.jbang.dev | bash -s - app setup
```
Finally, install the Camel JBang component with:
```bash
$ jbang app install camel@apache/camel
```
Create and start a base AMQ 7 broker:

## AMQ 7 Broker Setup

Install your AMQ 7 Broker downloading the [latest release](https://access.redhat.com/jbossnetwork/restricted/listSoftware.html?product=jboss.amq.broker).
```bash
$ unzip amq-broker-7.12.0-bin.zip
$ cd apache-artemis-2.33.0.redhat-00010/
$ bin/artemis create amq-test --user=admin --password=admin --allow-anonymous
$ amq-test/bin/artemis run
...
2024-06-11 06:41:44,291 INFO  [org.apache.activemq.artemis.core.server] AMQ221001: Apache ActiveMQ Artemis Message Broker version 2.33.0.redhat-00010 [0.0.0.0, nodeID=932ca622-27e7-11ef-b7ce-ae104b4798f1] 
2024-06-11 06:41:44,299 INFO  [org.apache.activemq.artemis] AMQ241003: Starting embedded web server
2024-06-11 06:41:44,698 INFO  [org.apache.amq.hawtio.branding.PluginContextListener] Initialized redhat-branding plugin
2024-06-11 06:41:44,789 INFO  [org.apache.activemq.hawtio.plugin.PluginContextListener] Initialized artemis-plugin plugin
2024-06-11 06:41:45,383 INFO  [io.hawt.HawtioContextListener] Initialising hawtio services
2024-06-11 06:41:45,401 INFO  [io.hawt.system.ConfigManager] Configuration will be discovered via system properties
2024-06-11 06:41:45,403 INFO  [io.hawt.jmx.JmxTreeWatcher] Welcome to hawtio 2.0.0.fuse-7_12_0-00019-redhat-00001
2024-06-11 06:41:45,410 INFO  [io.hawt.web.auth.AuthenticationConfiguration] Starting hawtio authentication filter, JAAS realm: "activemq" authorized role(s): "amq" role principal classes: "org.apache.activemq.artemis.spi.core.security.jaas.RolePrincipal"
2024-06-11 06:41:45,423 INFO  [io.hawt.web.auth.LoginRedirectFilter] Hawtio loginRedirectFilter is using 1800 sec. HttpSession timeout
2024-06-11 06:41:45,489 INFO  [org.apache.activemq.artemis] AMQ241001: HTTP Server started at http://localhost:8161
2024-06-11 06:41:45,492 INFO  [org.apache.activemq.artemis] AMQ241002: Artemis Jolokia REST API available at http://localhost:8161/console/jolokia
2024-06-11 06:41:45,492 INFO  [org.apache.activemq.artemis] AMQ241004: Artemis Console available at http://localhost:8161/console
```


## Running the POC

 -  Create a camel route:
```java
$ vi Hello.java

import org.apache.camel.builder.RouteBuilder;

public class Hello extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from ("timer:java?period=1000")
        .setBody ()
        .simple ("Hello, World")
        .log ("${body}")
        .to("jms:foo");
    }
}
```
 -  Create a properties file:
 ```bash
$ vi application.properties

#Artemis Client Runtime
camel.beans.artemisCF = #class:org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory
camel.beans.artemisCF.brokerURL = tcp://localhost:61616
# Pool Connection Factory
camel.beans.poolCF = #class:org.messaginghub.pooled.jms.JmsPoolConnectionFactory
camel.beans.poolCF.connectionFactory = #bean:artemisCF
camel.beans.poolCF.maxSessionsPerConnection = 5
camel.beans.poolCF.connectionIdleTimeout = 20000
camel.component.jms.connection-factory = #bean:poolCF
```
 - Ejecutar el proyecto:
```bash
 $ camel run Hello.java application.properties 
 ...
2024-06-11 06:58:53.320  INFO 2229 --- [ - timer://java] Hello.java:9   : Hello, World
2024-06-11 06:58:54.324  INFO 2229 --- [ - timer://java] Hello.java:9   : Hello, World
2024-06-11 06:58:55.326  INFO 2229 --- [ - timer://java] Hello.java:9   : Hello, World
...
```
  - Los mensajes se pueden ver dentro del broker ejecutando:
```bash
$ amq-test/bin/artemis consumer --message-count 10 --destination foo --data /dev/tty
 ```