package su.puzzle.pay.ui;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import su.puzzle.pay.ui.router.*;

public class MessageScreen extends BaseOwoScreen<FlowLayout> implements Route {
    protected Props props;

    public MessageScreen() {
    }

    public MessageScreen(Props props) {
        this.props = props;
    }

    public static void openMessage(Text title, Text message) {
        new MessageScreen().route(null, new Props(title, message));
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
                                        Components.label(props.title())
                                                .shadow(true)
                                )
                                .child(
                                        Components.label(props.message())
                                                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                                                .color(Color.ofRgb(Colors.GRAY))
                                                .margins(Insets.top(5))
                                )
                                .child(
                                        Components.button(Text.translatable("ui.puzzlepay.button.ok"), button -> {
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

    @Override
    public void route(Context context, Object props) {
        MinecraftClient.getInstance().setScreen(new MessageScreen((Props) props));
    }

    @Override
    public void route(Context context) {
        throw new UnsupportedOperationException("Unimplemented method 'route'");
    }

    public record Props(Text title, Text message) {
    }
}
