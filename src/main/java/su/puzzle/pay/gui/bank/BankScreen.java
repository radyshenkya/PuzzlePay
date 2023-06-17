package su.puzzle.pay.gui.bank;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.loader.api.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;
import su.puzzle.pay.*;
import su.puzzle.pay.api.*;
import su.puzzle.pay.api.exceptions.*;

import java.util.*;

public class BankScreen extends BaseOwoScreen<FlowLayout> {
    private final List<Component> buttons = new ArrayList<>();
    private final List<Component> pages = new ArrayList<>();
    private final CollapsibleContainer cardList;
    private Component page;
    private int currentPage;

    public BankScreen(Integer pageIndex) throws ApiCallException, ApiResponseException {
        this.currentPage = pageIndex == null ? 0 : pageIndex;
        cardList = Containers.collapsible(Sizing.content(), Sizing.content(), Text.literal("Cards"), true);
        PlasmoApi.getAllCards().unwrap().cards().forEach((card) -> {
            cardList.child(Components.label(Text.literal(card.name())));
        });
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.main"), button -> {
            for (Component btn : buttons) {
                ((ButtonComponent) btn).active(true);
            }
            currentPage = 0;
            ((ButtonComponent) buttons.get(0)).active(false);
        }).active(false).margins(Insets.left(14)));
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.transactions"), button -> {
            for (Component btn : buttons) {
                ((ButtonComponent) btn).active(true);
            }
            currentPage = 1;
            ((ButtonComponent) buttons.get(1)).active(false);
        }).active(true).margins(Insets.left(4)));
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.banker"), button -> {
            for (Component btn : buttons) {
                ((ButtonComponent) btn).active(true);
            }
            currentPage = 2;
            ((ButtonComponent) buttons.get(2)).active(false);
        }).active(true).margins(Insets.left(4)));
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.interpol"), button -> {
            for (Component btn : buttons) {
                ((ButtonComponent) btn).active(true);
            }
            currentPage = 3;
            ((ButtonComponent) buttons.get(3)).active(false);
        }).active(true).margins(Insets.left(4)));
    }

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
                                    Containers.verticalFlow(Sizing.content(), Sizing.content())
                                            .child(
                                                    Components.label(Text.literal("Puzzle Pay " + FabricLoader.getInstance().getModContainer(PuzzlePayMod.MOD_ID).orElseThrow().getMetadata().getVersion().getFriendlyString()))
                                                            .shadow(true)
                                                            .margins(Insets.of(15, 12, 14, 0))
                                            )
                                            .child(
                                                    Containers.horizontalFlow(Sizing.fill(100), Sizing.content())
                                                            .child(buttons.get(0))
                                                            .child(buttons.get(1))
                                                            .child(buttons.get(2))
                                                            .child(buttons.get(3))
                                                            .margins(Insets.bottom(8))
                                            )
                            )
                            .surface(Surface.OPTIONS_BACKGROUND)
                )
                .child(
                        Containers.verticalScroll(
                                Sizing.content(),
                                Sizing.fill(100),
                                    cardList
                                )
                )
                .surface(Surface.VANILLA_TRANSLUCENT);
    }
}
