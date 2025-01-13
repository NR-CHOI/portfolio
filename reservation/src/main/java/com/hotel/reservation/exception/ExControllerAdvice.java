package com.hotel.reservation.exception;

import com.hotel.reservation.api.member.MemberApiController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice(assignableTypes = MemberApiController.class)
//@RestControllerAdvice
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResult MethodExHandle(MethodArgumentNotValidException e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("MethodArgumentNotValidException", e.getMessage());
    }

//    @ExceptionHandler
//    public ResponseEntity<ErrorResult> MethodExHandle(MethodArgumentNotValidException e){
//        log.error("[exceptionHandle] ex", e);
//        ErrorResult errorResult = new ErrorResult("Method-EX", e.getMessage());
//        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST );
//    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> MethodExHandle(NoSuchElementException e) {
        log.error("[exceptionHandle] ex", e);
        ErrorResult errorResult = new ErrorResult("NoSuchElementException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> MethodExHandle(RuntimeException e) {
        log.error("[exceptionHandle] ex", e);
        ErrorResult errorResult = new ErrorResult("RuntimeException", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(Exception e) {
        log.error("[exceptionHandle ex", e);
        return new ErrorResult("EX", "내부 오류");
    }
}
