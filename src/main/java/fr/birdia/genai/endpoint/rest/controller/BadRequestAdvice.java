package fr.birdia.genai.endpoint.rest.controller;

import static org.springframework.http.MediaType.TEXT_PLAIN;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Without this, a body/param conversion failure is a 400 with an empty body. */
@RestControllerAdvice
public class BadRequestAdvice {

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> handle(HttpMessageNotReadableException e) {
    return badRequest(e.getMostSpecificCause().getMessage());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<String> handle(MethodArgumentTypeMismatchException e) {
    return badRequest("Invalid value for '" + e.getName() + "': " + e.getValue());
  }

  private static ResponseEntity<String> badRequest(String message) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(TEXT_PLAIN).body(message);
  }
}
