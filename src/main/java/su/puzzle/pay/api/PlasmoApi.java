package su.puzzle.pay.api;

import com.google.common.reflect.*;
import com.google.gson.*;
import su.puzzle.pay.*;
import su.puzzle.pay.api.exceptions.*;
import su.puzzle.pay.api.types.*;

import java.lang.reflect.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.*;
import java.net.http.HttpResponse.*;
import java.nio.charset.*;
import java.util.*;

public class PlasmoApi {
    public static final String API_URL = "https://plasmorp.com/api";
    private static final Gson gson = new Gson();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    public static String token = null;

    public static void setToken(String token) {
        PlasmoApi.token = token;
    }

    public static void assertTokenNotNull() {
        if (PlasmoApi.token == null || PlasmoApi.token.equals("")) {
            throw new IllegalStateException("Token must be set!");
        }
    }

    public static Response<List<BankCard>> searchCards(String query) throws ApiCallException {
        Type type = new TypeToken<Response<List<BankCard>>>() {
        }.getType();

        return request("/bank/search/cards?value=" + encodeURLQuery(query), "GET", type, null);
    }

    public static Response<BankCardsResponse> getAllCards() throws ApiCallException {
        Type type = new TypeToken<Response<BankCardsResponse>>() {
        }.getType();
        return request("/bank/cards", "GET", type, null);
    }

    public static Response<Object> transfer(int amount, String from, String message, String to) throws ApiCallException {
        TransferRequest req = new TransferRequest(amount, from, message, to);
        Type type = new TypeToken<Response<Object>>() {
        }.getType();
        return request("/bank/transfer", "POST", type, req);
    }

    public static Response<BankCardHistoryResponse> getCardHistory(BankCard card, int count) throws ApiCallException {
        Type type = new TypeToken<Response<BankCardHistoryResponse>>() {
        }.getType();
        return request("/bank/cards/" + card.getNormalId() + "/history?count=" + count, "GET", type);
    }

    public static Response<BankCardHistoryResponse> getCardHistory(BankCard card, int count, int to) throws ApiCallException {
        Type type = new TypeToken<Response<BankCardHistoryResponse>>() {
        }.getType();
        return request("/bank/cards/" + card.getNormalId() + "/history?count=" + count, "GET", type);
    }

    public static void updateUserActiveCard(BankCard card) throws ApiCallException {
        PatchCard req = new PatchCard(card.getNormalId());
        Type type = new TypeToken<Response<Object>>() {
        }.getType();
        request("/bank/cards/active", "PATCH", type, req);
    }

    public static Response<TokenInfoResponse> getTokenInfo() throws ApiCallException {
        Type type = new TypeToken<Response<TokenInfoResponse>>() {
        }.getType();
        return request("/oauth2/token", "GET", type, null);
    }

    public static String request(String endpoint, String method, String requestBody) throws ApiCallException {
        try {
            assertTokenNotNull();

            Builder request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL.concat(endpoint)))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Authorization", String.format("Bearer %s", PlasmoApi.token))
                    .method(method, requestBody == null ? BodyPublishers.noBody() : BodyPublishers.ofString(requestBody));

            HttpResponse<String> response = httpClient.send(request.build(), BodyHandlers.ofString());

            System.out.println(response.body());

            return response.body();
        } catch (Exception e) {
            PuzzlePayMod.LOGGER.warn(e.getMessage());
            throw new ApiCallException(e.getMessage(), e);
        }
    }

    public static JsonObject request(String endpoint, String method, JsonObject requestBody) throws ApiCallException {
        if (requestBody != null)
            return gson.fromJson(request(endpoint, method, gson.toJson(requestBody)), JsonObject.class);
        else
            return gson.fromJson(request(endpoint, method, (String) null), JsonObject.class);
    }

    public static <B, T> T request(String endpoint, String method, Type responseType, B requestBody)
            throws ApiCallException {
        String requestBodyString = null;

        if (requestBody != null)
            requestBodyString = gson.toJson(requestBody, requestBody.getClass());

        String response = request(endpoint, method, requestBodyString);
        return gson.fromJson(response, responseType);
    }

    public static <T> T request(String endpoint, String method, Type responseType) throws ApiCallException {
        return request(endpoint, method, responseType, null);
    }

    public static String encodeURLQuery(String query) {
        try {
            return URLEncoder.encode(query, StandardCharsets.UTF_8).replace("+", "%20");
        } catch (Exception e) {
            return query;
        }
    }
}
