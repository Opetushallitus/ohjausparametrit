package fi.oph.ohjausparametrit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OhjausparametritExceptionHandler {
  private static final Logger logger =
      LoggerFactory.getLogger(OhjausparametritExceptionHandler.class);

  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<String> notFound(NotFoundException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({ForbiddenException.class})
  public ResponseEntity<String> forbidden(ForbiddenException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({BadRequestException.class})
  public ResponseEntity<String> badRequest(BadRequestException e) {
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
