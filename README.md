# Fetcher

[![Build Status](https://travis-ci.org/lieuu/fetcher-framework.svg?branch=master)](https://travis-ci.org/lieuu/fetcher-framework) [![Maven 
Central](https://maven-badges.herokuapp.com/maven-central/com.lieuu/fetcher-framework/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.lieuu/fetcher-framework)

Fetcher is a Java framework for effortlessly writing loosely-coupled concurrent code designed for failure.

## Requirements

* Java 8 or higher, 64-bit
* Maven 3.3.9+ (for building)

## Maven Central

To include Fetcher in your project, add the following [entry](https://search.maven.org/#artifactdetails%7Cnet.lieuu%7Cfetcher-framework%7C1.3%7Cjar) to your `pom.xml`:

```
<dependency>
  <groupId>com.lieuu</groupId>
  <artifactId>fetcher-framework</artifactId>
  <version>1.3</version>
</dependency>
```

## Building Fetcher

Fetcher is a standard Maven project. Simply run the following command from the project root directory:

    mvn clean install

On the first build, Maven will download all the dependencies from the internet and cache them in the local repository (`~/.m2/repository`), which can take a considerable amount of time. Subsequent builds will be faster.

Fetcher has a comprehensive set of unit tests that can take several minutes to run. You can disable the tests when building:

    mvn clean install -DskipTests

## Getting Started

Before going any further, take a look at the most integral class in this library: [CachingFetcher](src/main/java/com/lieuu/fetcher/CachingFetcher.java). This serves as the backbone for most in-memory caching Fetcher implementations.

Many times with Java projects, developers end up creating numerous static variables for each class, which ends up making application initialization take considerably longer. Lazy-loaded singletons are a common practice, and the typical [initialization-on-demand holder](https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom) is too verbose for the simple goal it is accomplishing. It can also cause memory issues, since the singleton will not be garbage collected after instantiation. This is a dangerous practice in applications where memory is limited.

The common practice is:

```java
public class ExpensiveSomething {
    private ExpensiveSomething() {}

    private static class LazyHolder {
        final static List<String> INSTANCE = SomeClass.expensiveOperation();
    }

    public static List<String> getInstance() {
        return LazyHolder.INSTANCE;
    }
}
```

An alternative to this is the Fetcher framework; we can now use lambda expressions, in a considerably easier and safer way:

```java
final static Fetcher<List<String>> fetcher = Fetchers.getCachingFetcher(() -> SomeClass.expensiveOperation());
```

The cached object is referenced using a `SoftReference`, making it eligible for garbage collection if needed. It is also thread-safe and exceptions thrown during creation are thrown each time a thread attempts to fetch the object - this makes multiple threads behave the same way when accessing the some object.

Since this project was also created to simplify the (longwinded) process of creating concurrent processes in Java, we'll start with a few (oversimplified) examples.

```java
private static final Fetcher<String> FETCHER = Fetchers.getBlockingConcurrentFetcher(
() -> {    
    try {
        Thread.sleep(1000); // simulated IO
    }
    catch (InterruptedException e) {
        throw new FetcherException(e);
    }    
    return "fetched";    
});
```

In only nine lines of code, we've created a concurrent lazy-loading "singleton" which is thread-safe and will block when called.

```java
try {
    String fetcherValue = FETCHER.fetch();
}
catch (FetcherException e) {
    // TODO Implement error handling
}
```

This will block for one second and return `"fetched"`.

Let's now say that we want to attempt to access a resource, such as a web API or local file. To be resistant to failure, we want to have a backup in case networking is unavailable or the file system is unable to be accessed. Typically this is a complicated mess of launching Threads and checking their respective Futures, with even more complicated error handling.

With Fetcher, this is easy.

```java
final Fetcher<String> stringExceptionFetcher = Fetchers.getBlockingMultiConcurrentFetcher(
() -> {
    try {
        Thread.sleep(1000);
        throw new IOException("Networking unavailable.");
    }
    catch (final InterruptedException | IOException e) {
        throw new FetcherException(e);
    }
},
() -> {
    return "backup value";
});
```

This operation is also "non-blocking" for each individual thread, which in Fetcher means that it will block for 1 millisecond before attempting to run the next thread. The above example will run the first Lambda expression, block for 1 millisecond, then move to the next expression. If neither operation completes in 1 millisecond, the Fetchers will be checked for completion again, in order.

Once one of them completes or the timeout is reached (whichever comes first), then it will return the corresponding value or throw a `FetcherNotReadyException`. The default timeout value is ten seconds.

```java
final Fetcher<String> stringFetcher = Fetchers.getBlockingMultiConcurrentFetcher(
() -> {
    try {
        Thread.sleep(1000);
        return "primary";
    }
    catch (final InterruptedException e) {
        throw new FetcherException(e);
    }
},
() -> {
    try {
        Thread.sleep(500);
        return "secondary";
    }
    catch (final InterruptedException e) {
        throw new FetcherException(e);
    }
});
```

This shows the behavior of the `BlockingMultiConcurrentFetcher` more clearly. When we fetch the value, it will block for 500 ms until the "secondary" expression returns. When the fetcher is called again after one second, the "primary" expression will now be returned, since it is higher in the priority list.

## Feedback

*We are actively maintaining this repository - if you have any bugs, feature requests, pull requests, feedback, please [create an issue](https://github.com/lieuu/fetcher-framework/issues), and feel free to [visit us](https://www.lieuu.com) (still in beta).*