package su.puzzle.pay.gui.bank;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;
import su.puzzle.pay.api.*;
import su.puzzle.pay.api.exceptions.*;
import su.puzzle.pay.api.types.*;

public class BankScreen extends BaseOwoScreen<FlowLayout> {
    public CollapsibleContainer cardList = Containers.collapsible(Sizing.content(), Sizing.content(), Text.literal("Выберите карту"), false);
    public BankCard thisCard;

    public BankScreen(BankCard thisCard) throws ApiCallException, ApiResponseException {
        this.thisCard = thisCard;
        PlasmoApi.getAllCards().unwrap().cards().forEach((card) -> {
            cardList.child(
                    Components.button(Text.literal(card.name()), button -> {
                        try {
                            MinecraftClient.getInstance().setScreen(new BankScreen(card));
                        } catch (ApiCallException | ApiResponseException e) {
                            throw new RuntimeException(e);
                        }
                    }).sizing(Sizing.content(), Sizing.fixed(12))
            );
        });
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
                                        new NavigationBar(0).navbar
                                )
                )
                .child(
                        Containers.grid(Sizing.fill(100), Sizing.fill(80), 1, 3)
                                .child(
                                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                                .child(
                                                        cardList
                                                ),
                                        0,
                                        0
                                )
                                .child(
                                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                                .child(
                                                        Components.label(Text.literal(this.thisCard == null ? "Выберите карту" : "EB-" + this.thisCard.id()))
                                                ),
                                        0,
                                        1
                                )
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .verticalAlignment(VerticalAlignment.TOP)
                )
                .surface(Surface.VANILLA_TRANSLUCENT);
    }
}
