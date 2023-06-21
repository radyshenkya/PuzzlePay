package su.puzzle.pay.ui.bank;

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
import su.puzzle.pay.ui.components.CustomDropdownComponent;

public class BankScreen extends BaseOwoScreen<FlowLayout> {
    public CustomDropdownComponent cardList;
    public ScrollContainer<Component> historyScroll;
    public BankCard thisCard = null;
    public int count = 10;

    public void getNextHistory(FlowLayout layout) throws ApiCallException, ApiResponseException {
        ButtonComponent button = Components.button(Text.literal("Показать больше..."), onClick -> {});
        button.onPress(buttonComponent -> {
            try {
                this.count += 10;
                getNextHistory(layout);
                button.remove();
            } catch (ApiCallException | ApiResponseException e) {
                throw new RuntimeException(e);
            }
        });
        button.horizontalSizing(Sizing.fill(80)).margins(Insets.of(6));
        BankCardHistoryResponse history = PlasmoApi.getCardHistory(this.thisCard, this.count).unwrap();

        if (history.total() >= 1 && this.count <= 100) {
            history.list().subList(this.count - 10, Math.min(this.count, history.total())).forEach((bankCardHistory) -> {
                layout.child(
                                Containers.horizontalFlow(Sizing.fill(100), Sizing.content())
                                        .child(
                                                Containers.verticalFlow(Sizing.fill(80), Sizing.content())
                                                        .child(
                                                                Components.label(Text.literal(bankCardHistory.card().holder().isBlank() ? "Удален" : bankCardHistory.card().holder()))
                                                        )
                                                        .child(
                                                                Components.label(Text.literal(bankCardHistory.card().getNormalId() + " — " + (bankCardHistory.card().name().isBlank() ? "Удалена" : bankCardHistory.card().name())))
                                                        )
                                                        .child(
                                                                !bankCardHistory.message().isBlank() ? Components.label(Text.literal("§8" + bankCardHistory.message())) : Components.box(Sizing.fixed(0), Sizing.fixed(0))
                                                        )
                                                        .horizontalAlignment(HorizontalAlignment.LEFT)
                                                        .verticalAlignment(VerticalAlignment.CENTER)
                                        )
                                        .child(
                                                Containers.verticalFlow(Sizing.fill(20), Sizing.content())
                                                        .child(
                                                                Components.label(Text.literal(bankCardHistory.amount() < 0 ? "§c" + bankCardHistory.amount() : "§a" + bankCardHistory.amount()))
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
        } else {
            if (history.total() <= 0) {
                layout.child(
                        Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100))
                                .child(
                                        Components.label(Text.literal("Вы не совершали платежи. Обратитесь к банкиру, чтобы положить алмазы на счет.")).horizontalTextAlignment(HorizontalAlignment.CENTER).horizontalSizing(Sizing.fill(80))
                                )
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .verticalAlignment(VerticalAlignment.CENTER)
                );
            }
        }
        if ((history.total() > this.count) && (this.count < 100)) {
            layout.child(
                    button
            );
        }
    }

    public BankScreen() throws ApiCallException, ApiResponseException {
        BankCardsResponse cards = PlasmoApi.getAllCards().unwrap();
        for (BankCard card : cards.cards()) {
            if (card.getNormalId().equals(cards.active_card())) {
                this.thisCard = card;
            }
        }
        cardList = new CustomDropdownComponent(Sizing.fill(100), Sizing.fill(80),
                Text.literal(this.thisCard == null ? "Выберите карту" : this.thisCard.name() + "\n§8" + this.thisCard.getNormalId() + " — " + this.thisCard.holder()), false);
        cardList.margins(Insets.both(0, 8));
        cards.cards().forEach((card) -> {
            cardList.button(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()), button -> {
                try {
                    PlasmoApi.updateUserActiveCard(card);
                    MinecraftClient.getInstance().setScreen(new BankScreen());
                } catch (ApiCallException | ApiResponseException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        FlowLayout layout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        layout.horizontalAlignment(HorizontalAlignment.CENTER).verticalAlignment(VerticalAlignment.TOP);
        getNextHistory(layout);
        historyScroll = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(100), layout);
        historyScroll.horizontalAlignment(HorizontalAlignment.CENTER).verticalAlignment(VerticalAlignment.TOP);
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
                        Containers.grid(Sizing.fill(100), Sizing.fill(100), 1, 2)
                                .child(
                                        Containers.verticalFlow(Sizing.fill(45), Sizing.fill(100))
                                                .child(
                                                        Containers.verticalFlow(Sizing.fill(100), Sizing.fill(80))
                                                                .child(
                                                                        cardList
                                                                )
                                                                .child(
                                                                        Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                                                                                .child(
                                                                                        Components.label(Text.literal("Карта " + thisCard.getNormalId()))
                                                                                                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                                                                                                .horizontalSizing(Sizing.fill(80))
                                                                                                .margins(Insets.both(0, 8))
                                                                                )
                                                                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                                                                .verticalAlignment(VerticalAlignment.TOP)
                                                                                .surface(Surface.flat(838860800))
                                                                )
                                                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                                                .verticalAlignment(VerticalAlignment.TOP)
                                                ),
                                        0,
                                        0
                                )
                                .child(
                                        Containers.verticalFlow(Sizing.fill(45), Sizing.fill(100))
                                                .child(
                                                        Containers.verticalFlow(Sizing.fill(100), Sizing.fill(80))
                                                                .child(
                                                                        thisCard == null ?
                                                                                Components.label(Text.literal("Выберите карту")) :
                                                                                historyScroll
                                                                )
                                                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                                                .verticalAlignment(VerticalAlignment.TOP)
                                                )
                                                .surface(Surface.flat(838860800)),
                                        0,
                                        1
                                )
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .verticalAlignment(VerticalAlignment.TOP)
                )
                .surface(Surface.VANILLA_TRANSLUCENT);
    }
}
