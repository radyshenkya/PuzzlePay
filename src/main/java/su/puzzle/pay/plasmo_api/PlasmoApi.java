package su.puzzle.pay.plasmo_api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import su.puzzle.pay.PuzzlePayMod;

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

    public static void newTransfer(String from, String to, int amount, String message) throws ApiCallException {
        // Plasmo Error: Status-code: 400. Error message: Мать хокаге перевернулась в
        // гробу и эта проверка добавилась автоматически
        if (Objects.equals(from, to))
            throw new ApiCallException("Cannot transfer to the same card", null);

        // Составить JSON объект
        JsonObject requestJsonBody = new JsonObject();
        requestJsonBody.addProperty("from", from);
        requestJsonBody.addProperty("to", to);
        requestJsonBody.addProperty("amount", amount);
        requestJsonBody.addProperty("message", message);

        // Если перевод не удался то выпадет ApiCallException, поэтому ничего не делаем
        request("/bank/transfer", "POST", requestJsonBody);
    }

    protected static void assertPlasmoApiResponse(JsonObject response) throws ApiCallException {
        if (!response.get("status").getAsBoolean()) {
            throw new ApiCallException(response.get("error").getAsJsonObject().get("msg").getAsString(), null);
        }
    }

    public static JsonObject request(String endpoint, String method, JsonObject requestBody) throws ApiCallException {
        try {
            URL url = new URL(API_URL.concat(endpoint));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            connection.setDoOutput(true);
            connection.setDoInput(true);

            if (method != "GET") {
                assertTokenNotNull();

                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Authorization", String.format("Bearer %s", PlasmoApi.token));
            }

            try (OutputStream outStream = connection.getOutputStream()) {
                outStream.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                PuzzlePayMod.LOGGER.warn(e.getMessage());
                throw new ApiCallException("Error while sending request", e);
            }

            // Получить ответ от connection'а
            String responseBody;

            try (InputStream inStream = connection.getInputStream()) {
                responseBody = new String(inStream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException exception) {
                responseBody = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            }

            // Конвертировать ответ в JsonObject
            JsonObject responseBodyJson = (new Gson()).fromJson(responseBody, JsonObject.class);

            // Проверить его на кайфовость
            assertPlasmoApiResponse(responseBodyJson);

            return responseBodyJson;
        } catch (ApiCallException apiCallException) {
            throw apiCallException;
        } catch (Exception e) {
            PuzzlePayMod.LOGGER.warn(e.getMessage());
            throw new ApiCallException(e.getMessage(), e);
        }
    }
}
