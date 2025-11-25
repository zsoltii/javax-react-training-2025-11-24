package training.javaxreact.emplees;

import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Slf4j
public class ReactorThreadMain {

    static void main(String[] args) {

        Scheduler pool = Schedulers.newParallel("p2", 4);

        Flux.just(new Empleyee("John Doe", 1990), new Empleyee("Jane Smith", 1985))
                .subscribeOn(Schedulers.newParallel("s1")) // the source publisher thread
                .doOnNext(e -> log("Filtering employee: ", e.name()))
                .filter(e -> e.yearOfBirth() > 1980)
                .publishOn(
                        Schedulers.newParallel(
                                "p1",
                                Runtime.getRuntime()
                                        .availableProcessors())) // cpu core number the default
                .doOnNext(e -> log("Mapping employee: ", e.name()))
                .map(Empleyee::name)
                .doOnNext(e -> log("Upper case employee name: ", e))
                .timeout(Duration.ofSeconds(1))
                .map(String::toUpperCase)
                .publishOn(pool)
                .subscribe(e -> log("Final employee name: ", e));

        /**
         * Expected result:<br>
         * 13:29:29.577 [s1-3] INFO training.javaxreact.emplees.ReactorThreadMain -- John
         * DoeFiltering employee: on thread s1-3 13:29:29.581 [p1-2] INFO
         * training.javaxreact.emplees.ReactorThreadMain -- John DoeMapping employee: on thread p1-2
         * 13:29:29.581 [s1-3] INFO training.javaxreact.emplees.ReactorThreadMain -- Jane
         * SmithFiltering employee: on thread s1-3 13:29:29.581 [p1-2] INFO
         * training.javaxreact.emplees.ReactorThreadMain -- John DoeUpper case employee name: on
         * thread p1-2 13:29:29.581 [p1-2] INFO training.javaxreact.emplees.ReactorThreadMain --
         * Jane SmithMapping employee: on thread p1-2 13:29:29.582 [p1-2] INFO
         * training.javaxreact.emplees.ReactorThreadMain -- Jane SmithUpper case employee name: on
         * thread p1-2 13:29:29.582 [p2-1] INFO training.javaxreact.emplees.ReactorThreadMain --
         * JOHN DOEFinal employee name: on thread p2-1 13:29:29.582 [p2-1] INFO
         * training.javaxreact.emplees.ReactorThreadMain -- JANE SMITHFinal employee name: on thread
         * p2-1
         */
    }

    private static void log(String x, String e) {
        log.info("{}{} on thread {}", e, x, Thread.currentThread().getName());
    }
}
