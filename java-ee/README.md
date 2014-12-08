Sprockets for Java EE
=====================

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
