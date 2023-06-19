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
import su.puzzle.pay.gui.components.CustomDropdownComponent;

public class BankScreen extends BaseOwoScreen<FlowLayout> {
    public CustomDropdownComponent cardList;
    public BankCard thisCard;

    public BankScreen(BankCard thisCard) throws ApiCallException, ApiResponseException {
        this.thisCard = thisCard;
        cardList = new CustomDropdownComponent(Sizing.fixed(160), Sizing.content(),
                Text.literal(this.thisCard == null ? "Выберите карту" : "EB-" + this.thisCard.id()), false);

        PlasmoApi.getAllCards().unwrap().cards().forEach((card) -> {
            cardList.button(Text.literal(card.name() + "\n§8EB-" + card.id() + " " + card.value()), button -> {
                try {
                    MinecraftClient.getInstance().setScreen(new BankScreen(card));
                } catch (ApiCallException | ApiResponseException e) {
                    throw new RuntimeException(e);
                }
            });
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
                                new NavigationBar(0).navbar))
                .child(
                        Containers.grid(Sizing.fill(100), Sizing.fill(80), 1, 3)
                                .child(
                                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                                .child(
                                                        cardList),
                                        0,
                                        0)
                                .child(
                                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                                .child(
                                                        Components.label(
                                                                Text.literal(this.thisCard == null ? "Выберите карту"
                                                                        : "EB-" + this.thisCard.id()))),
                                        0,
                                        1)
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .verticalAlignment(VerticalAlignment.TOP))
                .surface(Surface.VANILLA_TRANSLUCENT);
    }
}
