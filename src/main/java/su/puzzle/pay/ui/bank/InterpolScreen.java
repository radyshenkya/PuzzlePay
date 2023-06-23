package su.puzzle.pay.ui.bank;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.*;
import su.puzzle.pay.ui.bank.ScreenRouter.Context;

import org.jetbrains.annotations.*;

public class InterpolScreen extends BaseOwoScreen<FlowLayout> implements Route {
    protected ScreenRouter.Context context;
    protected Props props = new Props();
    
    public InterpolScreen() {}

    public InterpolScreen(ScreenRouter.Context context, Props props) {
        this.context = context;
        this.props = props != null ? props : this.props;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.child(
                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                .child(
                                        new NavigationBar(context).navbar
                                )
                )
                .child(Components.label(Text.literal("InterpolScreen")))
                .surface(Surface.VANILLA_TRANSLUCENT);
    }

	@Override
	public void route(Context context, Object props) {
        MinecraftClient.getInstance().setScreen(new InterpolScreen(context, (Props) props));
	}

	@Override
	public void route(Context context) {
        route(context, null);
	}

    public record Props() {

    }
}
