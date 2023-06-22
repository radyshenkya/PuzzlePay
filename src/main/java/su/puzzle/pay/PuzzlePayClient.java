package su.puzzle.pay;

import org.apache.http.HttpStatus;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import su.puzzle.pay.api.exceptions.*;
import su.puzzle.pay.api.types.TokenInfoResponse;
import su.puzzle.pay.api.PlasmoApi;
import su.puzzle.pay.ui.oauth2.AuthHttpServer;
import su.puzzle.pay.ui.oauth2.Oauth2Screen;
import su.puzzle.pay.ui.MessageScreen;
import su.puzzle.pay.ui.bank.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

public class PuzzlePayClient implements ClientModInitializer {
    public static final su.puzzle.pay.PuzzlePayConfig config = su.puzzle.pay.PuzzlePayConfig.createAndLoad();
    private static KeyBinding transferGuiKeyBinding;
    public static AuthHttpServer server;

    public static final HashSet<String> NEEDED_SCOPES = new HashSet<>(Arrays.asList(
            // "bank:balance",
            // "bank:search",
            // "bank:history",
            // "bank:bill",
            // "bank:transfer",
            "bank:manage",
            "bank:banker",
            // "bank:banker:card",
            "bank:penalties"
            // "bank:penalties:card"
    ));

    @Override
    public void onInitializeClient() {
        registerKeys();
        registerCallbacks();
        initApi();
        try {
            server = new AuthHttpServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initApi() {
        PlasmoApi.setToken(PuzzlePayClient.config.plasmoRpToken());
    }

    private void registerKeys() {
        transferGuiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.puzzlepay.open_transfer_gui",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "category.puzzlepay.puzzlepay"));
    }

    private void registerCallbacks() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (transferGuiKeyBinding.wasPressed()) {
                try {
                    TokenInfoResponse tokenCheck = PlasmoApi.getTokenInfo().unwrap();

                    HashSet<String> scopes = new HashSet<>(tokenCheck.scopes());

                    scopes.forEach(System.out::println);

                    if (scopes.containsAll(NEEDED_SCOPES) && NEEDED_SCOPES.containsAll(scopes))
                        new ScreenRouter().route(0);
                    else
                        new ScreenRouter().route(4);
                } catch (ApiResponseException e) {
                    if (e.error.code == HttpStatus.SC_UNAUTHORIZED || e.error.code == 0){
                        MinecraftClient.getInstance().setScreen(new Oauth2Screen());
                    } else {
                        MinecraftClient.getInstance().setScreen(new MessageScreen(Text.translatable("ui.puzzlepay.text.error_message"), Text.literal(e.error.msg)));
                    }
                } catch (ApiCallException e) {
                    MinecraftClient.getInstance().setScreen(new MessageScreen(Text.translatable("ui.puzzlepay.text.error_message"), Text.literal(e.message)));
                }
            }
        });
    }
}
