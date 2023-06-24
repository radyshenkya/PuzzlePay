package su.puzzle.pay.ui.router;

import su.puzzle.pay.api.exceptions.*;

public interface Route {
    public void route(Context context, Object props) throws ApiCallException, ApiResponseException;
    public void route(Context context) throws ApiCallException, ApiResponseException;
}
