package fi.oph.ohjausparametrit.exception;

public class ForbiddenException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ForbiddenException() {
    super("");
  }

  public ForbiddenException(String message) {
    super(message);
  }
}
