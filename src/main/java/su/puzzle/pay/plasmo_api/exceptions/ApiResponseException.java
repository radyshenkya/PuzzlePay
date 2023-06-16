package su.puzzle.pay.plasmo_api.exceptions;

import su.puzzle.pay.plasmo_api.types.Error;

public class ApiResponseException extends Exception {
    public final Error error;

    public ApiResponseException(Error error) {
        this.error = error;
    }
}
