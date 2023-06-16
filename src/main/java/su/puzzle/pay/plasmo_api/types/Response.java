package su.puzzle.pay.plasmo_api.types;

import su.puzzle.pay.plasmo_api.exceptions.ApiResponseException;

public class Response<T> {
    public T data;
    public boolean status;
    public Error error;

    public void assertStatus() throws ApiResponseException {
        if (!status)
            throw new ApiResponseException(error);
    }

    public T unwrap() throws ApiResponseException {
        if (!status) throw new ApiResponseException(error);
        return data;
    }

    public T unwrap_or(T value) {
        if (!status) return value;
        return data;
    }
}
