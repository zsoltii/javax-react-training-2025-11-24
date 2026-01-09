package training.javaxreact.emplees;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ReactorTimeoutMain {

    static void main(String[] args) {
        Mono.fromCallable(ReactorTimeoutMain::blockingCall)
                .subscribeOn(Schedulers.newParallel("p1"))
                .timeout(Duration.ofSeconds(2))
                .onErrorResume(TimeoutException.class, t -> Mono.just(-1))
                .map(Object::toString)
                .subscribe(log::info);
    }

    private static Integer blockingCall() {
        // FIXME never do this on Flux!!!! completely blocking the whole application
        try {
            Random random = SecureRandom.getInstanceStrong();
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                random.nextLong();
            }
            return random.nextInt();
            //            Thread.sleep(Duration.ofSeconds(5));
            //            log.info("blocking call");
            //            return 110;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
