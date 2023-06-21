package su.puzzle.pay.ui.bank;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.core.Insets;
import net.fabricmc.loader.api.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import su.puzzle.pay.*;
import su.puzzle.pay.api.exceptions.*;
import su.puzzle.pay.ui.oauth2.*;

import java.util.*;
import java.util.List;

public class NavigationBar {
    private final ScreenRouter router = new ScreenRouter();

    public Component navbar;

    public NavigationBar(int pageIndex) {
        List<Component> buttons = new ArrayList<>();
        buttons.add(Components.button(Text.translatable("ui.puzzlepay.bank.tab.main"), button -> {
            try {
                router.route(0);
            } catch (ApiCallException | ApiResponseException e) {
                throw new RuntimeException(e);
            }
        }).active(pageIndex != 0).margins(Insets.left(14)));
        buttons.add(Components.button(Text.translatable("ui.puzzlepay.bank.tab.transactions"), button -> {
            try {
                router.route(1);
            } catch (ApiCallException | ApiResponseException e) {
                throw new RuntimeException(e);
            }
        }).active(pageIndex != 1).margins(Insets.left(4)));
        // TODO: в будущем сделать хрени для банкиров и интерполяций
        /* buttons.add(Components.button(Text.translatable("ui.puzzlepay.bank.tab.banker"), button -> {
            try {
                router.route(2);
            } catch (ApiCallException | ApiResponseException e) {
                throw new RuntimeException(e);
            }
        }).active(pageIndex != 2).margins(Insets.left(4)));
        buttons.add(Components.button(Text.translatable("ui.puzzlepay.bank.tab.interpol"), button -> {
            try {
                router.route(3);
            } catch (ApiCallException | ApiResponseException e) {
                throw new RuntimeException(e);
            }
        }).active(pageIndex != 3).margins(Insets.left(4))); */

        navbar = Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                .child(
                        Containers.horizontalFlow(Sizing.fill(100), Sizing.content())
                                .child(
                                        Containers.horizontalFlow(Sizing.fill(25), Sizing.content())
                                                .child(
                                                        Components.label(Text.literal("Puzzle Pay " + FabricLoader.getInstance().getModContainer(PuzzlePayMod.MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString()))
                                                                .shadow(true)
                                                )
                                                .horizontalAlignment(HorizontalAlignment.LEFT)
                                                .verticalAlignment(VerticalAlignment.CENTER)
                                )
                                .child(
                                        Containers.horizontalFlow(Sizing.fill(50), Sizing.content())
                                                .child(buttons.get(0))
                                                .child(buttons.get(1))
                                                .margins(Insets.vertical(8))
                                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                                .verticalAlignment(VerticalAlignment.CENTER)
                                )
                                .child(
                                        Containers.horizontalFlow(Sizing.fill(25), Sizing.content())
                                                .child(
                                                        Components.button(Text.literal("↓").formatted(Formatting.UNDERLINE), button -> {
                                                            try {
                                                                router.route(pageIndex);
                                                            } catch (ApiCallException | ApiResponseException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                        }).sizing(Sizing.fixed(20))
                                                )
                                                .child(
                                                        Components.button(Text.literal("Auth"), button -> {
                                                            MinecraftClient.getInstance().setScreen(new Oauth2Screen());
                                                        }).sizing(Sizing.fixed(20)).margins(Insets.left(4))
                                                )
                                                .horizontalAlignment(HorizontalAlignment.RIGHT)
                                                .verticalAlignment(VerticalAlignment.CENTER)
                                )
                                .margins(Insets.horizontal(14))
                                .verticalAlignment(VerticalAlignment.CENTER)
                )
                .surface(Surface.OPTIONS_BACKGROUND)
                .verticalAlignment(VerticalAlignment.CENTER);
    }
}
