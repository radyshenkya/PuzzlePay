package su.puzzle.pay.gui;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.*;

import org.jetbrains.annotations.*;

public class MessageScreen extends BaseOwoScreen<FlowLayout> {
    private final Text name;
    private final Text message;

    public MessageScreen(Text name, Text message) {
        this.name = name;
        this.message = message;
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
                                Components.label(name)
                                        .shadow(true)
                        )
                        .child(
                                Components.label(message)
                                        .horizontalTextAlignment(HorizontalAlignment.CENTER)
                                        .color(Color.ofRgb(Colors.GRAY))
                                        .margins(Insets.top(5))
                        )
                        .child(
                                Components.button(Text.translatable("gui.puzzlepay.button.ok"), button -> {
                                    MinecraftClient.getInstance().setScreen(null);
                                }).margins(Insets.top(5)).horizontalSizing(Sizing.fill(100))
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
