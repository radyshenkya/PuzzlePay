package su.puzzle.pay.api.exceptions;

public class ApiCallException extends Exception {
    public final String message;
    public final Exception causedBy;

    public ApiCallException(String message, Exception causedBy) {
        this.message = message;
        this.causedBy = causedBy;
    }
}
