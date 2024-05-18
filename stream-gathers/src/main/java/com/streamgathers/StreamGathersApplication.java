package com.streamgathers;

import com.streamgathers.examples.StringGatherer;

import java.util.stream.Stream;

public class StreamGathersApplication {

    public static void main(String[] args) {
        callStringGatherer();
        System.out.println("\n");
        callParallelStringGatherer();
    }

    private static void callStringGatherer() {
        System.out.println("String Gatherer");
        final var words = Stream.of( "Hello", "World", "Java", "Stream", "Gatherer", "Example");
        final var returnedValue = words
                .gather(new StringGatherer(3))
                .toList();
        returnedValue.forEach(System.out::println);
    }

    private static void callParallelStringGatherer() {
        System.out.println("String Parallel Gatherer");
        final var words = Stream.of( "Hello", "World", "Java", "Stream", "Gatherer", "Example");
        final var returnedValue = words
                .gather(new StringGatherer(3))
                .parallel()
                .toList();
        returnedValue.forEach(System.out::println);
    }

}
