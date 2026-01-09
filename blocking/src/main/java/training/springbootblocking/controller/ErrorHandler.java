package training.springbootblocking.controller;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    public ProblemDetail handle(Exception ex) {
        return getProblemDetail(ex);
    }

    @ExceptionHandler
    public ProblemDetail handle(IllegalArgumentException ex) {
        return getProblemDetail(ex);
    }

    private static ProblemDetail getProblemDetail(IllegalArgumentException e) {
        log.error("Bad request error: {}", e.getMessage(), e);
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("bad-request"));
        return problemDetail;
    }

    private static ProblemDetail getProblemDetail(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setType(URI.create("internal-server-error"));
        return problemDetail;
    }
}
