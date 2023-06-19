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
    public ScrollContainer<Component> historyList;
    public BankCard thisCard = null;

    public BankScreen() throws ApiCallException, ApiResponseException {
        BankCardsResponse cards = PlasmoApi.getAllCards().unwrap();
        for (BankCard card : cards.cards()) {
            if (card.getNormalId().equals(cards.active_card())) {
                this.thisCard = card;
            }
        }
        cardList = new CustomDropdownComponent(Sizing.fixed(160), Sizing.content(),
                Text.literal(this.thisCard == null ? "Выберите карту" : this.thisCard.getNormalId()), false);

        cards.cards().forEach((card) -> {
            cardList.button(Text.literal(card.name() + "\n§8" + card.getNormalId() + " " + card.value()), button -> {
                try {
                    PlasmoApi.updateUserActiveCard(card);
                    MinecraftClient.getInstance().setScreen(new BankScreen());
                } catch (ApiCallException | ApiResponseException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        FlowLayout layout = Containers.verticalFlow(Sizing.content(), Sizing.content());
        layout.horizontalAlignment(HorizontalAlignment.CENTER);
        layout.verticalAlignment(VerticalAlignment.CENTER);
        if (this.thisCard != null) {
            FlowLayout historyLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());
            historyLayout.horizontalAlignment(HorizontalAlignment.CENTER);
            historyLayout.verticalAlignment(VerticalAlignment.TOP);
            PlasmoApi.getCardHistory(this.thisCard).unwrap().list().forEach((history) -> {
                historyLayout.child(
                        Containers.horizontalFlow(Sizing.fill(100), Sizing.content())
                                .child(
                                        Containers.verticalFlow(Sizing.fill(80), Sizing.content())
                                                .child(
                                                        Components.label(Text.literal(history.card().holder().isBlank() ? "Удален" : history.card().holder()))
                                                )
                                                .child(
                                                        Components.label(Text.literal(history.card().getNormalId() + " — " + (history.card().name().isBlank() ? "Удалена" : history.card().name())))
                                                )
                                                .child(
                                                        !history.message().isBlank() ? Components.label(Text.literal("§8" + history.message())) : Components.box(Sizing.fixed(0), Sizing.fixed(0))
                                                )
                                                .horizontalAlignment(HorizontalAlignment.LEFT)
                                                .verticalAlignment(VerticalAlignment.CENTER)
                                )
                                .child(
                                        Containers.verticalFlow(Sizing.fill(20), Sizing.content())
                                                .child(
                                                        Components.label(Text.literal(history.amount() < 0 ? "§c" + history.amount() : "§a" + history.amount()))
                                                )
                                                .horizontalAlignment(HorizontalAlignment.RIGHT)
                                                .verticalAlignment(VerticalAlignment.CENTER)
                                )
                                .margins(Insets.of(6))
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .verticalAlignment(VerticalAlignment.CENTER)
                        )
                        .child(
                                Components.box(Sizing.fill(80), Sizing.fixed(1))
                                        .color(Color.ofArgb(1275068415))
                        );
            });
            layout.child(historyLayout);
        }
        historyList = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(100), layout);
        historyList.horizontalAlignment(HorizontalAlignment.CENTER);
        historyList.verticalAlignment(VerticalAlignment.CENTER);
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
                        Containers.grid(Sizing.fill(100), Sizing.fill(100), 1, 3)
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
                                                                        : this.thisCard.getNormalId()))),
                                        0,
                                        1)
                                .child(
                                        Containers.verticalFlow(Sizing.fill(30), Sizing.fill(100))
                                                .child(
                                                        this.thisCard == null ?
                                                        Components.label(Text.literal("Выберите карту")) :
                                                        historyList
                                                )
                                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                                .verticalAlignment(VerticalAlignment.CENTER)
                                                .surface(Surface.flat(1409286144)),
                                        0,
                                        2
                                )
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .verticalAlignment(VerticalAlignment.TOP))
                .surface(Surface.VANILLA_TRANSLUCENT);
    }
}
