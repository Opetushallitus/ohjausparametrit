package fi.oph.ohjausparametrit.exception;

public class BadRequestException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public BadRequestException() {
    super("");
  }

  public BadRequestException(String message) {
    super(message);
  }
}
