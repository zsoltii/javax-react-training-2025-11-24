package training.springbootreactiv.controller;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class EmployeeErrorHandler {

    @ExceptionHandler
    public Mono<ProblemDetail> handle(IllegalArgumentException ex) {
        return Mono.just(ex).map(EmployeeErrorHandler::getProblemDetail);
    }

    private static ProblemDetail getProblemDetail(IllegalArgumentException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("bad-request"));
        return problemDetail;
    }
}
