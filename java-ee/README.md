Sprockets for Java EE [![Maven Central][2]][3]
==============================================

Create Servlets that automatically read and write JSON objects.

* [Install](#install)
* [Javadoc][1]

Example
-------

Your Servlet will be sent an order in JSON and it will respond with a confirmation in JSON.

```java
public class OrderServlet extends JsonServlet<Order, Confirmation> {
    @Override
    protected Confirmation jsonPost(Order order,
            HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Confirmation confirmation = process(order);
        return confirmation;
    }
}
```

Install
-------

1\. Add the dependency.

```xml
    <dependency>
        <groupId>net.sf.sprockets</groupId>
        <artifactId>sprockets-ee</artifactId>
        <version>2.0.0</version>
    </dependency>
```

[1]: https://pushbit.github.io/sprockets/java-ee/apidocs/
[2]: https://img.shields.io/maven-central/v/net.sf.sprockets/sprockets-ee.svg
[3]: https://search.maven.org/#search|ga|1|g%3Anet.sf.sprockets%20a%3Asprockets-ee
