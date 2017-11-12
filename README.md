# ctxsh
#### A demonstration app which illustrates how to expose a Spring ApplicationContext to Groovy as an executable shell

Requires
* Maven
* Java 8

The application here uses Spring Data JPA, but that's more of a party trick
than anything else.  My motivation in doing this is more because I have
a lot of operations to perform that are MUCH easier to do if I have 
direct access to the Service tier of my application (without having
to write a UI). For example, I occasionally have to fetch arbitrary
datasets via web services, run business rules, trigger scheduled
tasks, manage queues, and manage users.

All of the data here is stored in an embedded H2 database.  Any JDBC
configuration should be fine.  I'm using Spring @Transaction
support and a TransactionTemplate.

Many of the above tasks are well suited to a Groovy DSL.

Running
```
mvn clean install
mvn exec:java
```

If you're serious about using this, you can modify `ctxsh` to point to
the compiled "-with-dependencies.jar" file.  And place `ctxsh` on
your `$PATH`.  From there you'll be able to run it like any other
shell (except it's not a shell - it's *not* interpreting things one
line at a time, it's really expecting file input until EOF).

```
$ ctxsh
2017-11-11 19:54:32 INFO  Shell:27 - Using groovy
2017-11-11 19:54:32 INFO  AnnotationConfigApplicationContext:589 - Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@4141d797: startup date [Sat Nov 11 19:54:32 CST 2017]; root of context hierarchy
2017-11-11 19:54:32 INFO  AutowiredAnnotationBeanPostProcessor:154 - JSR-330 'javax.inject.Inject' annotation found and supported for autowiring
2017-11-11 19:54:32 INFO  PostProcessorRegistrationDelegate$BeanPostProcessorChecker:326 - Bean 'myConfig' of type [be.shouldyou.example.MyConfig$$EnhancerBySpringCGLIB$$7b5ad687] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
......
2017-11-11 19:54:32 INFO  EmbeddedDatabaseFactory:189 - Starting embedded database: url='jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false', username='sa'
2017-11-11 19:54:33 INFO  LocalContainerEntityManagerFactoryBean:354 - Building JPA container EntityManagerFactory for persistence unit 'default'
2017-11-11 19:54:35 INFO  Shell:37 - Bound org.springframework.orm.jpa.SharedEntityManagerCreator#0 to Shared EntityManager proxy for target factory [org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean@6ddd71cc]
2017-11-11 19:54:35 INFO  Shell:51 - txStatus variable bound
```
Note that you'll need to send an EOF (ctrl-d) to exit the "shell."

A script would look like this:

```
#!/usr/bin/env ctxsh
println "Hello World!"
Submission s = new Submission();
em.persist(s);
commit=true
```

## Transactions
All work happens within the scope of a single transaction, which will
be rolled back unless `commit=true` is specified somewhere
within the script.

You also have access to a Spring TransactionStatus variable `txStatus.`  This
example goes through and exposes every Spring Bean as a variable
to the script.  For convenience, an EntityManager is included as `em.`

To make scripts more concise, you may add any imports you need
to import.sh.  In this example, the application's (JPA) entity
classes are imported.

## JSR 223
The examples here use Groovy.  They just as easily could have been any
other language with JSR223 support.  Using the ECMAScript implementation
included with the JDK works quite well, but I was unable to find a
decent "import" mechanism.  

## Wishlist Items
* Make this interpret as a shell, one line at a time.
* Make this a REPL.
* Do it in Javascript.
* Do this from a web app.

