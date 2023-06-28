package su.puzzle.pay.ui.oauth2;

import com.sun.net.httpserver.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.apache.http.HttpStatus;
import su.puzzle.pay.*;
import su.puzzle.pay.ui.*;
import su.puzzle.pay.api.PlasmoApi;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class AuthHttpServer {
    public HttpServer server;
    public static final int MIN_PORT_NUMBER = 1024;
    public static final int MAX_PORT_NUMBER = 65535;

    private static final String SUCCESS_REDIRECT_URL = "https://puzzlemc.site/pay/successful?nickname=%s";

    public AuthHttpServer() throws IOException { }

    public void start(int port) {
        // Из-за server.stop() наш httpserver меняет свой стейт на кринжовый,
        // и если вызвать server.start() еще раз, то он вываливается с ошибкой
        // IllegalState,
        // якобы сервер уже стопнули, он заанбиндился от порта, а мы его стартуем.
        // Поэтому создаем сервер прямо тут
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        } catch (Exception e) {
            PuzzlePayMod.LOGGER.warn("Cannot create server. Error: " + e.getMessage());
        }
        server.createContext("/oauth2", exchange -> {
            exchange.getResponseHeaders().add("Location",
                    String.format(SUCCESS_REDIRECT_URL, MinecraftClient.getInstance().getSession().getUsername()));
            sendResponseString(exchange, HttpStatus.SC_MOVED_TEMPORARILY, "Redirecting...");

            String token = exchange.getRequestURI().toString().split("=")[1].split("&")[0];

            PlasmoApi.setToken(token);
            PuzzlePayClient.config.plasmoRpToken(token);

            MinecraftClient.getInstance()
                    .execute(() -> MessageScreen.openMessage(Text.translatable("ui.puzzlepay.text.success_message_name"),
                                    Text.translatable("ui.puzzlepay.text.oauth2.success")));
            server.stop(0);
        });

        server.createContext("/auth", httpExchange -> {
            if (!"GET".equals(httpExchange.getRequestMethod())) {
                sendResponseString(httpExchange, HttpStatus.SC_METHOD_NOT_ALLOWED, "Method not allowed");
            }

            try {
                InputStream is = MinecraftClient.getInstance().getResourceManager()
                        .getResource(new Identifier("puzzlepay", "url_rebuild.html")).get().getInputStream();
                sendResponseString(httpExchange, HttpStatus.SC_OK,
                        new String(is.readAllBytes(), StandardCharsets.UTF_8));
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
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    public static void sendResponseString(HttpExchange httpExchange, int code, String response) throws IOException {
        httpExchange.sendResponseHeaders(code, response.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    // https://stackoverflow.com/questions/434718/sockets-discover-port-availability-using-java
    public static boolean isPortFree(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }
}
