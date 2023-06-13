package su.puzzle.pay.gui.Oauth2;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import su.puzzle.pay.PuzzlePayMod;
import su.puzzle.pay.gui.Message.MessageScreen;

public class Oauth2Screen extends BaseUIModelScreen<FlowLayout> {
    public AuthHttpServer server;

    public Oauth2Screen(AuthHttpServer server) {
        super(FlowLayout.class, DataSource.asset(new Identifier("puzzlepay:oauth")));
        this.server = server;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.childById(ButtonComponent.class, "oauth-button").onPress(button -> {
            try {
                Util.getOperatingSystem().open("https://puzzlemc.site/pay/oauth2");
                server.start();
           } catch (Exception e) {
                e.printStackTrace();
                PuzzlePayMod.LOGGER.error("Cannot open browser! Error: " + e.getMessage());
                MinecraftClient.getInstance().setScreen(new MessageScreen(Text.translatable("gui.puzzlepay.text.error_message_name"), Text.literal("Cannot open browser!")));
            }
        });

        rootComponent.childById(ButtonComponent.class, "cancel-button").onPress(button -> {
            server.stop();
            MinecraftClient.getInstance().setScreen(null);
        });
    }
}
