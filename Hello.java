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
