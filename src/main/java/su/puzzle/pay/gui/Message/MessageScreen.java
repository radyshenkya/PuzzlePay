package su.puzzle.pay.gui.Message;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

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
        rootComponent.verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .surface(Surface.VANILLA_TRANSLUCENT);

        rootComponent.child(
                Containers.verticalFlow(Sizing.fill(60), Sizing.content())
                        .child(
                            Components.label(name)
                        )
                        .child(
                            Components.label(message)
                                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                                .margins(Insets.top(5))
                                .horizontalSizing(Sizing.fill(100))
                        )
                        .child(
                            Components.button(Text.translatable("gui.puzzlepay.button.ok"), button -> MinecraftClient.getInstance().setScreen(null))
                                .margins(Insets.top(5))
                                .horizontalSizing(Sizing.fill(100))
                        )
                        .padding(Insets.of(15))
                        .surface(Surface.DARK_PANEL)
                        .verticalAlignment(VerticalAlignment.CENTER)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .horizontalSizing(Sizing.fill(60))
        );
    }
}
