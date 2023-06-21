package su.puzzle.pay.gui.oauth2;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import su.puzzle.pay.*;

public class Oauth2WaitScreen extends BaseOwoScreen<FlowLayout> {
    public Oauth2WaitScreen() {}

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.child(
                Containers.verticalFlow(Sizing.fill(60), Sizing.content())
                        .child(
                                Components.label(Text.translatable("gui.puzzlepay.text.oauth2.wait_title"))
                                        .shadow(true)
                        )
                        .child(
                                Components.button(Text.translatable("gui.puzzlepay.button.cancel"), button -> {
                                    PuzzlePayClient.server.stop();
                                    MinecraftClient.getInstance().setScreen(null);
                                }).margins(Insets.top(5)).horizontalSizing(Sizing.fixed(100))
                        )
                        .padding(Insets.of(15))
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .verticalAlignment(VerticalAlignment.CENTER)
                )
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER)
                .surface(Surface.VANILLA_TRANSLUCENT);
    }
}
