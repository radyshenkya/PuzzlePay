package su.puzzle.pay.api.exceptions;

import su.puzzle.pay.api.types.Error;

public class ApiResponseException extends Exception {
    public final Error error;

    public ApiResponseException(Error error) {
        this.error = error;
    }
}
