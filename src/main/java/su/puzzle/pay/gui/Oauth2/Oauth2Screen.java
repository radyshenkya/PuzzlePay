package su.puzzle.pay.gui.Oauth2;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

public class Oauth2Screen extends BaseOwoScreen<FlowLayout> {
    public AuthHttpServer server;

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public Oauth2Screen(AuthHttpServer server) {
        this.server = server;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.verticalAlignment(VerticalAlignment.CENTER);
        rootComponent.horizontalAlignment(HorizontalAlignment.CENTER);
        rootComponent.surface(Surface.VANILLA_TRANSLUCENT);

        MutableText text = Text.literal("gui.puzzlepay.text.oauth2.link");
        text.setStyle(text.getStyle().withFormatting(Formatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://rp.plo.su/oauth2?client_id=dZkmuEPjmuX5pkvM3Sz2FLXKCS34GaF4KFnfPsE5QUWjEjbS&redirect_uri=http://localhost:6969/auth&response_type=token&scope=bank%3Abalance%20bank%3Abanker%20bank%3Abanker%3Acard%20bank%3Abill%20bank%3Ahistory%20bank%3Amanage%20bank%3Asearch%20bank%3Atransfer")));

        rootComponent.child(
                Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(
                                Components.label(Text.translatable("gui.puzzlepay.text.oauth2.title"))
                        )
                        .child(
                                Components.label(Text.translatable("gui.puzzlepay.text.oauth2.description"))
                                        .horizontalTextAlignment(HorizontalAlignment.CENTER)
                                        .margins(Insets.top(10))
                                        .horizontalSizing(Sizing.fill(100))
                        )
                        .child(
                                Components.label(text)
                                        .margins(Insets.top(5))
                        )
                        .child(
                                Components.button(Text.translatable("gui.puzzlepay.text.oauth2.button.close"), button -> {
                                    server.stop();
                                    MinecraftClient.getInstance().setScreen(null);
                                })
                        )
                        .verticalAlignment(VerticalAlignment.CENTER)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
        );
    }
}
