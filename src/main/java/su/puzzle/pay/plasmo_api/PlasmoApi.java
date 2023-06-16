package su.puzzle.pay.plasmo_api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import su.puzzle.pay.PuzzlePayMod;
import su.puzzle.pay.plasmo_api.exceptions.ApiCallException;
import su.puzzle.pay.plasmo_api.types.*;

public class PlasmoApi {
    public static final String API_URL = "https://rp.plo.su/api";
    public static String token = null;

    public static void setToken(String token) {
        PlasmoApi.token = token;
    }

    public static void assertTokenNotNull() {
        if (PlasmoApi.token == null || PlasmoApi.token == "") {
            throw new IllegalArgumentException("PlasmoApi.token must be set!");
        }
    }

    public static Response<BankCardsResponse> getAllCards() throws ApiCallException {
        Type type = new TypeToken<Response<BankCardsResponse>>() {}.getType();
        Response<BankCardsResponse> resp = request("/bank/cards", "GET", type, (Object) null);
        return resp;
    }

    public static Response<Object> transfer(int amount, String from, String message, String to)
            throws ApiCallException {
        TransferRequest req = new TransferRequest(amount, from, message, to);
        Type type = new TypeToken<Response<Object>>() {}.getType();
        Response<Object> resp = request("/bank/transfer", "POST", type, req);
        return resp;
    }

    public static Response<ProfileResponse> getUser() throws ApiCallException {
        Type type = new TypeToken<Response<ProfileResponse>>() {}.getType();
        Response<ProfileResponse> resp = request("/user", "GET", type, (Object) null);
        return resp;
    }

    public static String request(String endpoint, String method, String requestBody) throws ApiCallException {
        try {
            URL url = new URL(API_URL.concat(endpoint));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            connection.setDoOutput(true);
            connection.setDoInput(true);

            assertTokenNotNull();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", String.format("Bearer %s", PlasmoApi.token));

            if (requestBody != null) {
                try (OutputStream outStream = connection.getOutputStream()) {
                    outStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    PuzzlePayMod.LOGGER.warn(e.getMessage());
                    throw new ApiCallException("Error while sending request", e);
                }
            }

            // Получить ответ от connection'а
            String responseBody;

            try (InputStream inStream = connection.getInputStream()) {
                responseBody = new String(inStream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException exception) {
                responseBody = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            }

            return responseBody;
        } catch (ApiCallException apiCallException) {
            throw apiCallException;
        } catch (Exception e) {
            PuzzlePayMod.LOGGER.warn(e.getMessage());
            throw new ApiCallException(e.getMessage(), e);
        }
    }

    public static JsonObject request(String endpoint, String method, JsonObject requestBody) throws ApiCallException {
        Gson gson = new Gson();
        if (requestBody != null)
            return gson.fromJson(request(endpoint, method, (new Gson()).toJson(requestBody)), JsonObject.class);
        else
            return gson.fromJson(request(endpoint, method, (String) null), JsonObject.class);
    }

    public static <B, T> T request(String endpoint, String method, Type responseType, B requestBody) throws ApiCallException {
        Gson gson = new Gson();
        String requestBodyString = null;

        if (requestBody != null)
            requestBodyString = gson.toJson(requestBody, requestBody.getClass());

        String response = request(endpoint, method, requestBodyString);
        return gson.fromJson(response, responseType);
    }

    public static <T> T request(String endpoint, String method, Type responseType) throws ApiCallException {
        return request(endpoint, method, responseType, (Object) null);
    }
}
