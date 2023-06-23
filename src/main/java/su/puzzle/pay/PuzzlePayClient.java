package su.puzzle.pay;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import su.puzzle.pay.api.exceptions.*;
import su.puzzle.pay.api.types.TokenInfoResponse;
import su.puzzle.pay.api.PlasmoApi;
import su.puzzle.pay.ui.oauth2.AuthHttpServer;
import su.puzzle.pay.ui.oauth2.Oauth2Screen;
import su.puzzle.pay.ui.bank.*;
import su.puzzle.pay.ui.router.ScreenRouter;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

public class PuzzlePayClient implements ClientModInitializer {
    public static final su.puzzle.pay.PuzzlePayConfig config = su.puzzle.pay.PuzzlePayConfig.createAndLoad();
    private static KeyBinding transferGuiKeyBinding;
    public static AuthHttpServer server;
    public static ScreenRouter screenRouter;

    public static class ScreenRouteNames {
        public static final String MAIN = "ui.puzzlepay.bank.tab.main";
        public static final String TRANSACTION = "ui.puzzlepay.bank.tab.transactions";
    }

    public static final HashSet<String> NEEDED_SCOPES = new HashSet<>(Arrays.asList(
            // "bank:balance",
            // "bank:search",
            // "bank:history",
            // "bank:bill",
            // "bank:transfer",
            "bank:manage", "bank:banker",
            // "bank:banker:card",
            "bank:penalties"
            // "bank:penalties:card"
    ));

    @Override
    public void onInitializeClient() {
        registerKeys();
        registerCallbacks();
        initApi();
        initScreens();

        try {
            server = new AuthHttpServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initScreens() {
        screenRouter = new ScreenRouter();

        screenRouter.add_route(ScreenRouteNames.MAIN, new BankScreen()).add_route(ScreenRouteNames.TRANSACTION, new TransactionScreen());
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
                    TokenInfoResponse tokenCheck = PlasmoApi.getTokenInfo().unwrap();

                    HashSet<String> scopes = new HashSet<>(tokenCheck.scopes());

                    scopes.forEach(System.out::println);

                    if (scopes.containsAll(NEEDED_SCOPES) && NEEDED_SCOPES.containsAll(scopes))
                        screenRouter.route(ScreenRouteNames.MAIN);
                    else MinecraftClient.getInstance().setScreen(new Oauth2Screen());
                } catch (ApiResponseException | ApiCallException e) {
                    MinecraftClient.getInstance().setScreen(new Oauth2Screen());
                }
            }
        });
    }
}
