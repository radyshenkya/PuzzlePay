package su.puzzle.pay.ui.bank;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import su.puzzle.pay.ui.router.Context;
import su.puzzle.pay.ui.router.Route;
import su.puzzle.pay.ui.components.InputDropdownComponent;

import org.jetbrains.annotations.*;

public class TransactionScreen extends BaseOwoScreen<FlowLayout> implements Route {
    protected Context context;
    protected Props props = new Props();

    public TransactionScreen() {
    }

    public TransactionScreen(Context context, Props props) {
        this.context = context;
        this.props = props != null ? props : this.props;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        InputDropdownComponent testInputDropdown = new InputDropdownComponent(Sizing.fill(100), Sizing.fill(20), Text.literal("test\ntest\ntest"), false);

        testInputDropdown.onInputChange(changedString -> {
            testInputDropdown.title(Text.literal("New string:\n" + changedString));
            testInputDropdown.button(Text.literal(changedString), onClick -> testInputDropdown.removeEntries());
        });

        rootComponent.child(
                Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(
                                new NavigationBar(context).navbar))
                .child(Components.label(Text.literal("TransactionScreen")))
                .child(testInputDropdown)
                .surface(Surface.VANILLA_TRANSLUCENT);
    }

    @Override
    public void route(Context context, Object props) {
        MinecraftClient.getInstance().setScreen(new TransactionScreen(context, (Props) props));
    }

    @Override
    public void route(Context context) {
        route(context, null);
    }

    public record Props() {

    }
}
