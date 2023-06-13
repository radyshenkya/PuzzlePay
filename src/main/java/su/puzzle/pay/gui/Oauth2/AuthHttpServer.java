package su.puzzle.pay.gui.Oauth2;

import com.sun.net.httpserver.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import su.puzzle.pay.*;
import su.puzzle.pay.gui.Message.*;

import java.io.*;
import java.net.*;

public class AuthHttpServer {
    HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 6969), 0);

    public AuthHttpServer() throws IOException {}

    public void start() {
        server.createContext("/auth", exchange -> {
            System.out.println(exchange.getRequestURI().toString());
            String token = exchange.getRequestURI().toString().split("=")[1].split("&")[0];
            PuzzlePayClient.config.plasmoRpToken(token);
            MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new MessageScreen(Text.literal("OAuth2"), Text.literal("authed"))));
            server.stop(0);
        });
        server.start();
    }
    public void stop() {
        server.stop(0);
    }
}
