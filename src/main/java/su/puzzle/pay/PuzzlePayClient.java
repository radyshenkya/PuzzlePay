package su.puzzle.pay;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import su.puzzle.pay.api.types.BankCard;
import su.puzzle.pay.api.types.TokenInfoResponse;
import su.puzzle.pay.api.AsyncTasksService;
import su.puzzle.pay.api.PlasmoApi;
import su.puzzle.pay.api.exceptions.ApiCallException;
import su.puzzle.pay.api.exceptions.ApiResponseException;
import su.puzzle.pay.ui.oauth2.AuthHttpServer;
import su.puzzle.pay.ui.oauth2.Oauth2Screen;
import su.puzzle.pay.ui.bank.*;
import su.puzzle.pay.ui.router.ScreenRouter;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.Executors;

public class PuzzlePayClient implements ClientModInitializer {
    public static final su.puzzle.pay.PuzzlePayConfig config = su.puzzle.pay.PuzzlePayConfig.createAndLoad();
    private static KeyBinding transferGuiKeyBinding;
    public static final AsyncTasksService asyncTasksService = new AsyncTasksService(Executors.newCachedThreadPool());
    public static AuthHttpServer server;
    public static ScreenRouter screenRouter;

    public static class ScreenRouteNames {
        public static final String MAIN = "ui.puzzlepay.bank.tab.main";
        public static final String TRANSACTION = "ui.puzzlepay.bank.tab.transactions";
    }

    public static final HashSet<String> NEEDED_SCOPES = new HashSet<>(Arrays.asList(
            "bank:manage"
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
        transferGuiKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.puzzlepay.open_bank_screen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "Puzzle Pay"));
    }

    private void registerCallbacks() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try { 
                asyncTasksService.updateTasks();
            } catch (Exception e) {
                e.printStackTrace();
            }

            asyncTasksService.removeDone();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (transferGuiKeyBinding.wasPressed()) {
                asyncTasksService.addTask(() -> {
                    return PlasmoApi.getTokenInfo().unwrap();
                }, (result) -> {
                    HashSet<String> scopes = new HashSet<>(((TokenInfoResponse) result).scopes());

                    if (scopes.containsAll(NEEDED_SCOPES) && NEEDED_SCOPES.containsAll(scopes))
                        screenRouter.route(ScreenRouteNames.MAIN);
                    else MinecraftClient.getInstance().setScreen(new Oauth2Screen());
                }, (exception) -> {
                    MinecraftClient.getInstance().setScreen(new Oauth2Screen());
                });
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient()) {
                return ActionResult.PASS;
            }

            BlockState block = world.getBlockState(hitResult.getBlockPos());

            if (block.getBlock() instanceof SignBlock || block.getBlock() instanceof HangingSignBlock || block.getBlock() instanceof WallHangingSignBlock) {
                SignBlockEntity signBlockEntity = (SignBlockEntity) world.getBlockEntity(hitResult.getBlockPos());
                if (signBlockEntity.isWaxed()) {
                    if (tryParseSignPayment(signBlockEntity, player)) {
                        return ActionResult.SUCCESS;
                    }
                }
            }

            return ActionResult.PASS;
        });
    }

    private boolean tryParseSignPayment(SignBlockEntity signBlockEntity, PlayerEntity player) {
        if (!signBlockEntity.getTextFacing(player).getMessage(0, false).getString().startsWith("PuzzlePay")) {
            return false;
        }

        String cardNumber = signBlockEntity.getTextFacing(player).getMessage(1, false).getString();
        String amount = signBlockEntity.getTextFacing(player).getMessage(2, false).getString();
        String comment = signBlockEntity.getTextFacing(player).getMessage(3, false).getString();

        if (!cardNumber.startsWith("EB-")) return false;

        asyncTasksService.addTask(() -> { return PlasmoApi.searchCards(cardNumber).unwrap().get(0); },
            (result) -> {
                BankCard to = (BankCard) result;
                int parsedAmount = Integer.parseInt(amount);

                if (to == null) { return; }

                screenRouter.route(ScreenRouteNames.TRANSACTION, new TransactionScreen.Props(to, parsedAmount, comment));
            }, (exception) -> {
                if (exception instanceof ApiResponseException || exception instanceof ApiCallException) {
                    MinecraftClient.getInstance().setScreen(new Oauth2Screen());
                } else { }
            });

        return true;
    }
}
