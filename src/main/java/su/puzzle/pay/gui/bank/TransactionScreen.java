package su.puzzle.pay.gui.bank;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;

public class TransactionScreen extends BaseOwoScreen<FlowLayout> {
    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.child(
                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                .child(
                                        new NavigationBar(1).navbar
                                )
                )
                .child(Components.label(Text.literal("TransactionScreen")))
                .surface(Surface.VANILLA_TRANSLUCENT);
    }
}
