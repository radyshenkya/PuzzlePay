package su.puzzle.pay.gui.bank;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.core.Insets;
import net.fabricmc.loader.api.*;
import net.minecraft.text.*;
import su.puzzle.pay.*;
import su.puzzle.pay.api.exceptions.*;

import java.util.*;
import java.util.List;

public class NavigationBar {
    private final List<Component> buttons = new ArrayList<>();
    private final ScreenRouter router = new ScreenRouter();

    public Component navbar;

    public NavigationBar(int pageIndex) {
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.main"), button -> {
            try {
                router.route(0);
            } catch (ApiCallException | ApiResponseException e) {
                throw new RuntimeException(e);
            }
        }).active(pageIndex != 0).margins(Insets.left(14)));
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.transactions"), button -> {
            try {
                router.route(1);
            } catch (ApiCallException | ApiResponseException e) {
                throw new RuntimeException(e);
            }
        }).active(pageIndex != 1).margins(Insets.left(4)));
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.banker"), button -> {
            try {
                router.route(2);
            } catch (ApiCallException | ApiResponseException e) {
                throw new RuntimeException(e);
            }
        }).active(pageIndex != 2).margins(Insets.left(4)));
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.interpol"), button -> {
            try {
                router.route(3);
            } catch (ApiCallException | ApiResponseException e) {
                throw new RuntimeException(e);
            }
        }).active(pageIndex != 3).margins(Insets.left(4)));

        navbar = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(
                        Components.label(Text.literal("Puzzle Pay " + FabricLoader.getInstance().getModContainer(PuzzlePayMod.MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString()))
                                .shadow(true)
                                .margins(io.wispforest.owo.ui.core.Insets.of(15, 12, 14, 0))
                )
                .child(
                        Containers.horizontalFlow(Sizing.fill(100), Sizing.content())
                                .child(buttons.get(0))
                                .child(buttons.get(1))
                                .child(buttons.get(2))
                                .child(buttons.get(3))
                                .margins(Insets.bottom(8))
                )
                .surface(Surface.OPTIONS_BACKGROUND);
    }
}