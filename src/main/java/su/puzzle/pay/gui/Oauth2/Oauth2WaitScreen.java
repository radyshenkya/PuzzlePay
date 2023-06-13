package su.puzzle.pay.gui.Oauth2;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import su.puzzle.pay.PuzzlePayMod;
import su.puzzle.pay.gui.Message.MessageScreen;

public class Oauth2WaitScreen extends BaseUIModelScreen<FlowLayout> {
    public AuthHttpServer server;

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public Oauth2WaitScreen(AuthHttpServer server) {
        super(FlowLayout.class, DataSource.asset(new Identifier("puzzlepay:oauth_wait")));
        this.server = server;
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.childById(ButtonComponent.class, "cancel-button").onPress(button -> {
            server.stop();
            MinecraftClient.getInstance().setScreen(null);
        });
    }
}
