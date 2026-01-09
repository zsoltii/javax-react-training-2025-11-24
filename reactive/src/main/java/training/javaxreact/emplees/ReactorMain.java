package training.javaxreact.emplees;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class ReactorMain {

    static void main(String[] args) {
        // Stream.of(new Empleyee("John Doe", 1990), new Empleyee("Jane Smith",
        // 1985)).forEach(System.out::println);
        // non blocking backpressure = nem árasztjuk el a fogyasztót
        Flux.just(new Empleyee("John Doe", 1990), new Empleyee("Jane Smith", 1985))
                // .filter(empleyee -> empleyee.yearOfBirth() > 1987)
                .map(Empleyee::name)
                .singleOrEmpty() // FIXME ATTENTION: here doesn't throw exception if more than one
                // element!!!!!
                .subscribe(System.out::println);

        Flux.just(new Empleyee("John Doe", 1990), new Empleyee("Jane Smith", 1985))
                .map(empleyee -> empleyee.getAge(1987))
                .doOnError(
                        err ->
                                System.out.println(
                                        err.getMessage())) // order of the operators matters
                .onErrorReturn(-1)
                .subscribe(System.out::println);

        Flux.just(new Empleyee("John Doe", 1990), new Empleyee("Jane Smith", 1985))
                .map(empleyee -> empleyee.getAge(1980))
                .doOnError(err -> log.error(err.getMessage(), err))
                .onErrorReturn(-2)
                .subscribe(
                        System.out
                                ::println); // if there is a wrong element, the stream stops at the
        // first error

        Flux.just(new Empleyee("John Doe", 1990), new Empleyee("Jane Smith", 1985))
                .map(empleyee -> empleyee.getAge(1987))
                .onErrorResume(e -> Mono.just(-4))
                .subscribe(System.out::println);

        // Optional.of(new Empleyee("John Doe", 1990)).ifPresent(System.out::println);
        // non blocking backpressure = nem árasztjuk el a fogyasztót
        Mono.just(new Empleyee("John Doe", 1990)).flux().subscribe(System.out::println);

        Flux.just(new Empleyee("John Doe", 1990), new Empleyee("Jane Smith", 1985))
                .flatMap(
                        e ->
                                Mono.just(e)
                                        .map(e1 -> e1.getAge(1987))
                                        .doOnError(err -> log.error(err.getMessage(), err))
                                        .onErrorResume(err -> Mono.empty()))
                .subscribe(System.out::println);
    }
}
