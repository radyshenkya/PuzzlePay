package su.puzzle.pay.gui.Oauth2;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import su.puzzle.pay.*;
import su.puzzle.pay.gui.Message.MessageScreen;

public class Oauth2WaitScreen extends BaseUIModelScreen<FlowLayout> {
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public Oauth2WaitScreen() {
        super(FlowLayout.class, DataSource.asset(new Identifier("puzzlepay:oauth_wait")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.childById(ButtonComponent.class, "cancel-button").onPress(button -> {
            PuzzlePayClient.server.stop();
            MinecraftClient.getInstance().setScreen(null);
        });
    }
}
