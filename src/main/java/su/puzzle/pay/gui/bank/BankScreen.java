package su.puzzle.pay.gui.bank;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.loader.api.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;
import su.puzzle.pay.*;
import su.puzzle.pay.api.exceptions.*;

import java.util.*;

public class BankScreen extends BaseOwoScreen<FlowLayout> {
    private final List<Component> buttons = new ArrayList<>();
    private final List<Component> pages = new ArrayList<>();
    private Component page;
    private int currentPage;

    public BankScreen(Integer pageIndex) throws ApiCallException, ApiResponseException {
        this.currentPage = pageIndex == null ? PuzzlePayClient.config.latestPageIndex() : pageIndex;
        pages.add(new MainPage().mainPage);
        pages.add(new TransactionPage().transactionPage);
        pages.add(new MainPage().mainPage);
        pages.add(new TransactionPage().transactionPage);
        this.page = pages.get(this.currentPage);
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.main"), button -> {
            for (Component btn : buttons) {
                ((ButtonComponent) btn).active(true);
            }
            page = pages.get(0);
            currentPage = 0;
            PuzzlePayClient.config.latestPageIndex(0);
            ((ButtonComponent) buttons.get(0)).active(false);
        }).margins(Insets.left(14)));
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.transactions"), button -> {
            for (Component btn : buttons) {
                ((ButtonComponent) btn).active(true);
            }
            page = pages.get(1);
            currentPage = 1;
            PuzzlePayClient.config.latestPageIndex(1);
            ((ButtonComponent) buttons.get(1)).active(false);
        }).margins(Insets.left(4)));
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.banker"), button -> {
            for (Component btn : buttons) {
                ((ButtonComponent) btn).active(true);
            }
            page = pages.get(2);
            currentPage = 2;
            PuzzlePayClient.config.latestPageIndex(2);
            ((ButtonComponent) buttons.get(2)).active(false);
        }).margins(Insets.left(4)));
        buttons.add(Components.button(Text.translatable("gui.puzzlepay.bank.tab.interpol"), button -> {
            for (Component btn : buttons) {
                ((ButtonComponent) btn).active(true);
            }
            page = pages.get(3);
            currentPage = 3;
            PuzzlePayClient.config.latestPageIndex(3);
            ((ButtonComponent) buttons.get(3)).active(false);
        }).margins(Insets.left(4)));
        ((ButtonComponent) buttons.get(this.currentPage)).active(false);
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
                                    page
                                )
                )
                .surface(Surface.VANILLA_TRANSLUCENT);
    }
}
