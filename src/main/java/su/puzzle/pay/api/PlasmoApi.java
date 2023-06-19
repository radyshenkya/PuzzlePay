package su.puzzle.pay.api;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import su.puzzle.pay.PuzzlePayMod;
import su.puzzle.pay.api.exceptions.ApiCallException;
import su.puzzle.pay.api.types.*;

public class PlasmoApi {
    public static final String API_URL = "https://plasmorp.com/api";
    public static String token = null;

    private static HttpClient httpClient = HttpClient.newHttpClient();

    public static void setToken(String token) {
        PlasmoApi.token = token;
    }

    public static void assertTokenNotNull() {
        if (PlasmoApi.token == null || PlasmoApi.token.equals("")) {
            throw new IllegalStateException("Token must be set!");
        }
    }

    public static Response<BankCardsResponse> getAllCards() throws ApiCallException {
        Type type = new TypeToken<Response<BankCardsResponse>>() {
        }.getType();
        return request("/bank/cards", "GET", type, null);
    }

    public static Response<Object> transfer(int amount, String from, String message, String to)
            throws ApiCallException {
        TransferRequest req = new TransferRequest(amount, from, message, to);
        Type type = new TypeToken<Response<Object>>() {
        }.getType();
        return request("/bank/transfer", "POST", type, req);
    }

    public static Response<BankCardHistoryResponse> getCardHistory(BankCard card) throws ApiCallException {
        Type type = new TypeToken<Response<BankCardHistoryResponse>>() {
        }.getType();
        return request("/bank/cards/" + card.getNormalId() + "/history?count=24", "GET", type);
    }

    public static Response<BankCardHistoryResponse> getCardHistory(String card_id) throws ApiCallException {
        Type type = new TypeToken<Response<BankCardHistoryResponse>>() {
        }.getType();
        return request("/bank/cards/" + "EB-" + card_id + "/history?count=24", "GET", type);
    }

    public static Response<Object> updateUserActiveCard(BankCard card) throws ApiCallException {
        PatchCard req = new PatchCard(card.getNormalId());
        Type type = new TypeToken<Response<Object>>() {
        }.getType();
        return request("/bank/cards/active", "PATCH", type, req);
    }

    public static Response<Object> updateUserActiveCard(String card_id) throws ApiCallException {
        PatchCard req = new PatchCard(card_id);
        Type type = new TypeToken<Response<Object>>() {
        }.getType();
        return request("/bank/cards/active", "PATCH", type, req);
    }

    public static Response<ProfileResponse> getUser() throws ApiCallException {
        Type type = new TypeToken<Response<ProfileResponse>>() {
        }.getType();
        return request("/user", "GET", type, null);
    }

    public static String request(String endpoint, String method, String requestBody) throws ApiCallException {
        try {
            assertTokenNotNull();

            Builder request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL.concat(endpoint)))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Authorization", String.format("Bearer %s", PlasmoApi.token));

            if (requestBody == null) {
                request.method(method, BodyPublishers.noBody());
            } else {
                request.method(method, BodyPublishers.ofString(requestBody));
            }

            HttpResponse<String> response = httpClient.send(request.build(), BodyHandlers.ofString());

            return response.body();
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

    public static <B, T> T request(String endpoint, String method, Type responseType, B requestBody)
            throws ApiCallException {
        Gson gson = new Gson();
        String requestBodyString = null;

        if (requestBody != null)
            requestBodyString = gson.toJson(requestBody, requestBody.getClass());

        String response = request(endpoint, method, requestBodyString);
        return gson.fromJson(response, responseType);
    }

    public static <T> T request(String endpoint, String method, Type responseType) throws ApiCallException {
        return request(endpoint, method, responseType, null);
    }
}
