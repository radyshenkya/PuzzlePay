package su.puzzle.pay.ui.oauth2;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import su.puzzle.pay.PuzzlePayClient;
import su.puzzle.pay.api.PlasmoApi;

public class Oauth2Screen extends BaseOwoScreen<FlowLayout> {
    public Oauth2Screen() {}

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .child(
                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                .child(
                                        Components.label(Text.translatable("ui.puzzlepay.text.oauth2.title"))
                                                .shadow(true)
                                                .horizontalSizing(Sizing.content())
                                                .margins(Insets.bottom(10))
                                )
                                .child(
                                        Components.label(Text.translatable("ui.puzzlepay.text.oauth2.description"))
                                                .color(Color.ofRgb(Colors.GRAY))
                                                .horizontalSizing(Sizing.content())
                                                .margins(Insets.bottom(10))
                                )
                                .child(
                                        Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                                .child(
                                                        Components.button(Text.translatable("ui.puzzlepay.button.link"), button -> {
                                                                    int port = generateFreePort();
                                                                    PuzzlePayClient.server.start(port);
                                                                    Util.getOperatingSystem().open(getOauthLink(port));
                                                                    MinecraftClient.getInstance().setScreen(new Oauth2WaitScreen());
                                                                })
                                                                .horizontalSizing(Sizing.fixed(120))
                                                )
                                                .child(
                                                        Components.box(Sizing.fixed(1), Sizing.fixed(14))
                                                                .color(Color.ofArgb(0))
                                                                .margins(Insets.horizontal(5))
                                                )
                                                .child(
                                                        Components.button(Text.translatable("ui.puzzlepay.button.cancel"), button -> {
                                                                    MinecraftClient.getInstance().setScreen(null);
                                                                })
                                                                .horizontalSizing(Sizing.fixed(120))
                                                )
                                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                                .verticalAlignment(VerticalAlignment.CENTER)
                                )
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .verticalAlignment(VerticalAlignment.CENTER)
                                .padding(Insets.of(15))

                )
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER)
                .surface(Surface.VANILLA_TRANSLUCENT);
    }

    protected int generateFreePort() {
        for (int i = 6969; i < AuthHttpServer.MAX_PORT_NUMBER; i++) {
            if (AuthHttpServer.isPortFree(i)) return i;
        }

        return 6969;
    }

    protected String getOauthLink(int port) {
        String link = "https://plasmorp.com/oauth2?client_id=dZkmuEPjmuX5pkvM3Sz2FLXKCS34GaF4KFnfPsE5QUWjEjbS&redirect_uri=http://localhost:" + port + "/auth&response_type=token&scope=";
        String scopes = String.join(" ", PuzzlePayClient.NEEDED_SCOPES);

        link += PlasmoApi.encodeURLQuery(scopes);

        return link;
    }
}
