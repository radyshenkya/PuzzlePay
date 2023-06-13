package su.puzzle.pay.gui.Oauth2;

import com.sun.net.httpserver.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.apache.http.HttpStatus;
import su.puzzle.pay.*;
import su.puzzle.pay.gui.Message.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class AuthHttpServer {
    private static final String SUCCESS_REDIRECT_URL = "https://puzzlemc.site/pay/successful?nickname=%s";
    HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 6969), 0);

    public AuthHttpServer() throws IOException {
    }

    public void start() {
        server.createContext("/oauth2", exchange -> {
            exchange.getResponseHeaders().add("Location", String.format(SUCCESS_REDIRECT_URL, MinecraftClient.getInstance().getSession().getUsername()));
            sendResponseString(exchange, HttpStatus.SC_MOVED_TEMPORARILY, "Redirecting...");

            String token = exchange.getRequestURI().toString().split("=")[1].split("&")[0];
            PuzzlePayClient.config.plasmoRpToken(token);

            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new MessageScreen(Text.literal("OAuth2"), Text.literal("authed"))));
            server.stop(0);
        });

        server.createContext("/auth", httpExchange -> {
            if (!"GET".equals(httpExchange.getRequestMethod())) {
                sendResponseString(httpExchange, HttpStatus.SC_METHOD_NOT_ALLOWED, "Method not allowed");
            }

            try {
                InputStream is = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("puzzlepay", "url_rebuild.html")).get().getInputStream();
                sendResponseString(httpExchange, HttpStatus.SC_OK, new String(is.readAllBytes(), StandardCharsets.UTF_8));
                is.close();
            } catch (Exception e) {
                PuzzlePayMod.LOGGER.warn(e.getMessage());
                sendResponseString(httpExchange, HttpStatus.SC_INTERNAL_SERVER_ERROR, "Internal server error");
                server.stop(0);
            }
        });

        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static void sendResponseString(HttpExchange httpExchange, int code, String response) throws IOException {
        httpExchange.sendResponseHeaders(code, response.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
