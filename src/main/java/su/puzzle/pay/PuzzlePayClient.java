package su.puzzle.pay;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import su.puzzle.pay.api.exceptions.*;
import su.puzzle.pay.api.PlasmoApi;
import su.puzzle.pay.gui.Oauth2.AuthHttpServer;
import su.puzzle.pay.gui.bank.*;

import java.io.*;

public class PuzzlePayClient implements ClientModInitializer {
    public static final su.puzzle.pay.PuzzlePayConfig config = su.puzzle.pay.PuzzlePayConfig.createAndLoad();
    private static KeyBinding transferGuiKeyBinding;
    public static AuthHttpServer server;

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
        transferGuiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.puzzlepay.open_transfer_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "category.puzzlepay.puzzlepay"));
    }

    private void registerCallbacks() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (transferGuiKeyBinding.wasPressed()) {
                try {
                    new ScreenRouter().route(0);
                } catch (ApiCallException | ApiResponseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
