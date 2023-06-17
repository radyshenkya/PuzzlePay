package su.puzzle.pay.gui.Oauth2;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import su.puzzle.pay.PuzzlePayClient;

import java.io.*;

public class Oauth2Screen extends BaseUIModelScreen<FlowLayout> {
    public Oauth2Screen() throws IOException {
        super(FlowLayout.class, DataSource.asset(new Identifier("puzzlepay:oauth")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.childById(ButtonComponent.class, "oauth-button").onPress(button -> {
            PuzzlePayClient.server.start();
            Util.getOperatingSystem().open("https://puzzlemc.site/pay/oauth2");
            MinecraftClient.getInstance().setScreen(new Oauth2WaitScreen());
        });

        rootComponent.childById(ButtonComponent.class, "cancel-button").onPress(button -> {
            MinecraftClient.getInstance().setScreen(null);
        });
    }
}
