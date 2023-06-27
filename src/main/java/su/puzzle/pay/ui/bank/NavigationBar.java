package su.puzzle.pay.ui.bank;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.core.Insets;
import net.fabricmc.loader.api.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import su.puzzle.pay.ui.router.Context;
import su.puzzle.pay.*;
import su.puzzle.pay.ui.oauth2.*;

import java.util.*;
import java.util.List;

public class NavigationBar {
    public Component navbar;

    public NavigationBar(Context context) {
        List<Component> buttons = new ArrayList<>();
        
        context.screenRouter().routes.forEach((name, route) -> {
            buttons.add(Components.button(Text.translatable(name), button -> {
                context.screenRouter().route(name);
            }).active(!context.currentScreenName().equals(name)).margins(Insets.left(14)));
        });


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
                                                .children(buttons)
                                                .margins(Insets.vertical(8))
                                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                                .verticalAlignment(VerticalAlignment.CENTER)
                                )
                                .child(
                                        Containers.horizontalFlow(Sizing.fill(25), Sizing.content())
                                                .child(
                                                        Components.button(Text.literal("↓").formatted(Formatting.UNDERLINE), button -> {
                                                            context.screenRouter().route(context.currentScreenName());
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
