package su.puzzle.pay;

import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import su.puzzle.pay.gui.Payment.PaymentScreen;
import su.puzzle.pay.plasmo_api.PlasmoApi;
import su.puzzle.pay.plasmo_api.types.BankCard;
import su.puzzle.pay.plasmo_api.types.BankCardsResponse;
import su.puzzle.pay.plasmo_api.types.Response;
import su.puzzle.pay.gui.Home.HomeScreen;
import su.puzzle.pay.gui.Oauth2.AuthHttpServer;

import java.io.*;

public class PuzzlePayClient implements ClientModInitializer {
    public static final PuzzlePayConfig config = PuzzlePayConfig.createAndLoad();
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
        // For transfer dialog keybind
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (transferGuiKeyBinding.wasPressed()) {
                openTransferGui("", 1, "");
            }
        });

        // For sign click
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

        try {
            int parsedAmount = Integer.parseInt(amount);
            openTransferGui(cardNumber, parsedAmount, comment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void openTransferGui(String cardNumber, int amount, String comment) {
        MinecraftClient.getInstance().setScreen(new PaymentScreen(cardNumber, amount, comment));
    }
}
