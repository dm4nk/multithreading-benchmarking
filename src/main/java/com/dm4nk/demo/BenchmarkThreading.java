package com.dm4nk.demo;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//Benchmark                                        Mode  Cnt     Score   Error  Units
//BenchmarkThreading.testCreateThread              avgt    2  5403,346          us/op
//BenchmarkThreading.testCreateVirtualThread       avgt    2   105,546          us/op
//
//BenchmarkThreading.testCachedExecutorSmallTask   avgt    2   713,265          us/op
//BenchmarkThreading.testFixedExecutorSmallTask    avgt    2  1224,389          us/op
//BenchmarkThreading.testVirtualExecutorSmallTask  avgt    2    58,070          us/op
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 1)
@Measurement(iterations = 2)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class BenchmarkThreading {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkThreading.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void testCreateVirtualThread(Blackhole blackhole) {
        for (int i = 0; i < 100; ++i) {
            int finalI = i;
            Thread.startVirtualThread(() -> blackhole.consume(finalI));
        }
    }

    @Benchmark
    public void testCreateThread(Blackhole blackhole) {
        for (int i = 0; i < 100; ++i) {
            int finalI = i;
            var thread = new Thread(() -> blackhole.consume(finalI));
            thread.start();
        }
    }

    @Benchmark
    public void testVirtualExecutorSmallTask(Blackhole blackhole) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 100; ++i) {
                int finalI = i;
                executor.submit(() -> blackhole.consume(finalI));
            }
        }
    }

    @Benchmark
    public void testCachedExecutorSmallTask(Blackhole blackhole) {
        try (var executor = Executors.newCachedThreadPool()) {
            for (int i = 0; i < 100; ++i) {
                int finalI = i;
                executor.submit(() -> blackhole.consume(finalI));
            }
        }
    }

    @Benchmark
    public void testFixedExecutorSmallTask(Blackhole blackhole) {
        try (var executor = Executors.newFixedThreadPool(20)) {
            for (int i = 0; i < 100; ++i) {
                int finalI = i;
                executor.submit(() -> blackhole.consume(finalI));
            }
        }
    }
}
