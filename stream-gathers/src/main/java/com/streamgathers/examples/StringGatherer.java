package com.streamgathers.examples;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

public record StringGatherer(int wordLimit) implements Gatherer<String, List<String>, String> {

    /*
     BUILDED METHODS
     stream.gather(Gatherer.fold()) - perform specific action on each element and next on the result and next element.
     stream.gather(Gatherer.mapConcurrent()) - perform specific action on each element in parallel.
     stream.gather(Gatherer.scan()) - Something like fold but returns all the lambda results.
     stream.gather(Gatherer.windowFixed()), stream.gather(Gatherer.windowSliding()) - mapping to arrays with
     windows size and eventually move it by 1 when sliding.
    */

    @Override
    // This method is called before the stream is processed.
    public Supplier<List<String>> initializer() {
        return ArrayList::new;
    }

    @Override
    // This method is called for each element in the stream for parallel processing
    // or for each element until the condition is met for sequential processing.
    public Integrator<List<String>, String, String> integrator() {
        return Integrator.of(
                (combined, element, downstream) -> {
                    if (StringUtils.hasText(element)) {
                        System.out.println(Thread.currentThread());
                        combined.add(element);
                    }
                    return combineAndPushIfWordLimitReached(combined, downstream);
                }
        );
    }

    @Override
    // This method is called for combining the results of the parallel processing.
    public BinaryOperator<List<String>> combiner() {
        return (wordsLeft, wordsRight) -> {
            if (wordsLeft.size() < wordLimit) {
                wordsLeft.addAll(wordsRight);
            }
            return wordsLeft;
        };
    }

    @Override
    // This method is called after the stream is FULLY processed.
    public BiConsumer<List<String>, Downstream<? super String>> finisher() {
        return (words, downstream) -> {
            if (words.isEmpty()) {
                downstream.push("Provided input is empty.");
            } else {
                downstream.push(String.join(", ", words));
                downstream.push("This are the words matching the limit.");
            }
        };
    }

    private boolean combineAndPushIfWordLimitReached(List<String> words, Downstream<? super String> downstream) {
        if (words.size() >= wordLimit) {
            downstream.push(String.join(", ", words));
            downstream.push("This are the words matching the limit.");
            return false;
        }
        return true;
    }
}
