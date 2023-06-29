package su.puzzle.pay.ui.bank;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.core.Insets;
import net.fabricmc.loader.api.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import su.puzzle.pay.ui.router.Context;
import su.puzzle.pay.*;
import su.puzzle.pay.ui.oauth2.*;

import java.util.*;
import java.util.List;

public class NavigationBar {
    public Component navbar;
    
    private static final int GRADIENT_START = 0xFF000000;
    private static final int GRADIENT_END = 0x00000000;

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
                                                        Components.button(Text.literal("⟳"), button -> {
                                                            context.screenRouter().route(context.currentScreenName());
                                                        }).sizing(Sizing.fixed(20))
                                                )
                                                .child(
                                                        Components.button(Text.literal("\uD83D\uDD12"), button -> {
                                                            MinecraftClient.getInstance().setScreen(new Oauth2Screen());
                                                        }).sizing(Sizing.fixed(20)).margins(Insets.left(4))
                                                )
                                                .horizontalAlignment(HorizontalAlignment.RIGHT)
                                                .verticalAlignment(VerticalAlignment.CENTER)
                                )
                                .margins(Insets.horizontal(14))
                                .verticalAlignment(VerticalAlignment.CENTER)
                )
                .child(shadow())
                .allowOverflow(true)
                .surface(Surface.OPTIONS_BACKGROUND)
                .verticalAlignment(VerticalAlignment.CENTER);
    }

    public static Component shadow() {
        FlowLayout shadow = Containers.verticalFlow(Sizing.fill(100), Sizing.fixed(10));

        shadow.surface((drawContext, component) -> {
            drawContext.drawGradientRect(component.x(), component.y(), component.width(), component.height(),
                    GRADIENT_START, GRADIENT_START, GRADIENT_END, GRADIENT_END);
        });

        shadow.positioning(Positioning.across(0, 100));
        shadow.zIndex(0);

        return shadow;
    }
}
