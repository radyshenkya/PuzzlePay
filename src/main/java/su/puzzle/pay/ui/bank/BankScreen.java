package su.puzzle.pay.ui.bank;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;
import su.puzzle.pay.*;
import su.puzzle.pay.api.*;
import su.puzzle.pay.api.exceptions.*;
import su.puzzle.pay.api.types.*;
import su.puzzle.pay.ui.*;
import su.puzzle.pay.ui.components.*;
import su.puzzle.pay.ui.router.*;

public class BankScreen extends BaseOwoScreen<FlowLayout> implements Route {
    public CustomDropdownComponent cardList;
    public ScrollContainer<Component> historyScroll;
    public BankCard activeCard = null;
    public int count = 10;

    protected Context context;
    protected Props props;

    public BankScreen() {
    }

    public BankScreen(Context context, Props props) throws ApiCallException, ApiResponseException {
        this.context = context;
        this.props = props != null ? props : this.props;

        BankCardsResponse cards = PlasmoApi.getAllCards().unwrap();
        for (BankCard card : cards.cards()) {
            if (card.getNormalId().equals(cards.active_card())) {
                this.activeCard = card;
            }
        }
        cardList = new CustomDropdownComponent(Sizing.fill(100), Sizing.fill(80),
                Text.literal(this.activeCard == null ? "Выберите карту"
                        : this.activeCard.name() + "\n§8" + this.activeCard.getNormalId() + " — " + this.activeCard.holder()),
                false
        );
        cardList.margins(Insets.both(0, 8));

        FlowLayout layout = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        layout.horizontalAlignment(HorizontalAlignment.CENTER).verticalAlignment(VerticalAlignment.TOP);
        getNextHistory(layout);
        historyScroll = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(100), layout);
        historyScroll.horizontalAlignment(HorizontalAlignment.CENTER).verticalAlignment(VerticalAlignment.TOP);

        cards.cards().forEach((card) -> {
            cardList.button(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()), button -> {
                PuzzlePayClient.asyncTasksService.addTask(() -> {
                            PlasmoApi.updateUserActiveCard(card);
                            return null;
                        }, (result) -> {
                            activeCard = card;
                            cardList.title(Text.literal(this.activeCard == null ? "Выберите карту" : this.activeCard.name() + "\n§8" + this.activeCard.getNormalId() + " — " + this.activeCard.holder()));
                            layout.clearChildren();
                            getNextHistory(layout);
                        }, System.out::println
                );
            });
        });
    }

    public void getNextHistory(FlowLayout layout) {
        ButtonComponent button = Components.button(Text.literal("Показать больше..."), onClick -> {
        });

        button.onPress(buttonComponent -> {
            this.count += 10;
            getNextHistory(layout);
            button.remove();
        });

        button.horizontalSizing(Sizing.fill(80)).margins(Insets.of(6));

        PuzzlePayClient.asyncTasksService.addTask(() -> {
            return PlasmoApi.getCardHistory(this.activeCard, this.count).unwrap();
        }, (result) -> {
            BankCardHistoryResponse history = (BankCardHistoryResponse) result;
            if (history.total() >= 1 && this.count <= 100) {
                history.list().subList(this.count - 10, Math.min(this.count, history.total()))
                        .forEach((bankCardHistory) -> {
                            layout.child(
                                            Containers.horizontalFlow(Sizing.fill(100), Sizing.content())
                                                    .child(
                                                            Containers.verticalFlow(Sizing.fill(80), Sizing.content())
                                                                    .child(
                                                                            Components.label(Text.literal(
                                                                                    bankCardHistory.card().holder().isBlank()
                                                                                            ? "Удален"
                                                                                            : bankCardHistory.card().holder())
                                                                            )
                                                                    )
                                                                    .child(
                                                                            Components.label(Text.literal(bankCardHistory.card()
                                                                                    .getNormalId()
                                                                                    + " — "
                                                                                    + (bankCardHistory.card().name().isBlank()
                                                                                    ? "Удалена"
                                                                                    : bankCardHistory.card().name()))
                                                                            )
                                                                    )
                                                                    .child(
                                                                            !bankCardHistory.message().isBlank()
                                                                                    ? Components.label(Text.literal(
                                                                                    "§8" + bankCardHistory.message()))
                                                                                    : Components.box(Sizing.fixed(0),
                                                                                    Sizing.fixed(0)
                                                                            )
                                                                    )
                                                                    .horizontalAlignment(HorizontalAlignment.LEFT)
                                                                    .verticalAlignment(VerticalAlignment.CENTER)
                                                    )
                                                    .child(
                                                            Containers.verticalFlow(Sizing.fill(20), Sizing.content())
                                                                    .child(
                                                                            Components.label(
                                                                                    Text.literal(bankCardHistory.amount() < 0
                                                                                            ? "§c" + bankCardHistory.amount()
                                                                                            : "§a" + bankCardHistory.amount()
                                                                                    )
                                                                            )
                                                                    )
                                                                    .horizontalAlignment(HorizontalAlignment.RIGHT)
                                                                    .verticalAlignment(VerticalAlignment.CENTER))
                                                    .margins(Insets.of(6))
                                                    .horizontalAlignment(HorizontalAlignment.CENTER)
                                                    .verticalAlignment(VerticalAlignment.CENTER)
                                    )
                                    .child(
                                            Components.box(Sizing.fill(80), Sizing.fixed(1))
                                                    .color(Color.ofArgb(0x4bffffff))
                                    );
                        });
            } else {
                if (history.total() <= 0) {
                    layout.child(
                            Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100))
                                    .child(
                                            Components
                                                    .label(Text.translatable(
                                                            "ui.puzzlepay.bank.tab.no_transactions_message"))
                                                    .horizontalTextAlignment(HorizontalAlignment.CENTER)
                                                    .horizontalSizing(Sizing.fill(80))
                                    )
                                    .horizontalAlignment(HorizontalAlignment.CENTER)
                                    .verticalAlignment(VerticalAlignment.CENTER)
                    );
                }
            }
            if ((history.total() > this.count) && (this.count < 100)) {
                layout.child(button);
            }
        }, (exception) -> {

        });

    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .child(new NavigationBar(context).navbar)
                .child(
                        Containers.grid(Sizing.fill(100), Sizing.fill(100), 1, 2)
                                .child(
                                        Containers.verticalFlow(Sizing.fill(45), Sizing.fill(100))
                                                .child(
                                                        Containers.verticalFlow(Sizing.fill(100), Sizing.fill(80))
                                                                .child(
                                                                        Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                                                                                .child(
                                                                                        Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                                                                                                .child(
                                                                                                        Containers.grid(Sizing.fill(100), Sizing.content(), 1, 2)
                                                                                                                .child(
                                                                                                                        Containers.verticalFlow(Sizing.fill(50), Sizing.content())
                                                                                                                                .child(
                                                                                                                                        Components.label(Text.literal("Название"))
                                                                                                                                                .horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                                )
                                                                                                                                .child(
                                                                                                                                        Components.label(Text.literal("Баланс"))
                                                                                                                                                .horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                                )
                                                                                                                                .child(
                                                                                                                                        Components.label(Text.literal("Айди"))
                                                                                                                                                .horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                                )
                                                                                                                                .child(
                                                                                                                                        Components.label(Text.literal("Владелец"))
                                                                                                                                                .horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                                )
                                                                                                                                .horizontalAlignment(HorizontalAlignment.LEFT),
                                                                                                                        0,
                                                                                                                        0
                                                                                                                )
                                                                                                                .child(
                                                                                                                        Containers
                                                                                                                                .verticalFlow(Sizing.fill(50), Sizing.content())
                                                                                                                                .child(
                                                                                                                                        Components
                                                                                                                                                .label(activeCard == null
                                                                                                                                                        ? Text.translatable(
                                                                                                                                                        "ui.puzzlepay.bank.tab.choose_card")
                                                                                                                                                        : Text.literal(activeCard.name())
                                                                                                                                                )
                                                                                                                                                .horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                                )
                                                                                                                                .child(
                                                                                                                                        Components
                                                                                                                                                .label(activeCard == null
                                                                                                                                                        ? Text.translatable(
                                                                                                                                                        "ui.puzzlepay.bank.tab.choose_card")
                                                                                                                                                        : Text.literal(
                                                                                                                                                        String.valueOf(activeCard.value()))
                                                                                                                                                )
                                                                                                                                                .horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                                )
                                                                                                                                .child(
                                                                                                                                        Components
                                                                                                                                                .label(activeCard == null
                                                                                                                                                        ? Text.translatable(
                                                                                                                                                        "ui.puzzlepay.bank.tab.choose_card")
                                                                                                                                                        : Text.literal(activeCard.getNormalId())
                                                                                                                                                )
                                                                                                                                                .horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                                )
                                                                                                                                .child(
                                                                                                                                        Components
                                                                                                                                                .label(activeCard == null
                                                                                                                                                        ? Text.translatable(
                                                                                                                                                        "ui.puzzlepay.bank.tab.choose_card")
                                                                                                                                                        : Text.literal(activeCard.holder())
                                                                                                                                                )
                                                                                                                                                .horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                                )
                                                                                                                                .horizontalAlignment(HorizontalAlignment.RIGHT),
                                                                                                                        0,
                                                                                                                        1
                                                                                                                )
                                                                                                                .margins(Insets.of(8))
                                                                                                )
                                                                                )
                                                                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                                                                .verticalAlignment(VerticalAlignment.TOP)
                                                                                .surface(Surface.flat(0x32000000))
                                                                )
                                                                .child(
                                                                        cardList
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
                                                                        activeCard == null
                                                                                ? Containers
                                                                                .verticalFlow(Sizing.fill(100),
                                                                                        Sizing.fill(100))
                                                                                .child(Components.label(Text.translatable("ui.puzzlepay.bank.tab.choose_card")))
                                                                                .verticalAlignment(VerticalAlignment.CENTER)
                                                                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                                                                : historyScroll
                                                                )
                                                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                                                .verticalAlignment(VerticalAlignment.TOP)
                                                )
                                                .surface(Surface.flat(0x32000000)),
                                        0,
                                        1
                                )
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .verticalAlignment(VerticalAlignment.TOP)
                )
                // .child(NavigationBar.shadow())
                .surface(Surface.VANILLA_TRANSLUCENT);
    }

    @Override
    public void route(Context context, Object props) {
        try {
            MinecraftClient.getInstance().setScreen(new BankScreen(context, (Props) props));
        } catch (ApiCallException e) {
            MessageScreen.openMessage(Text.translatable("ui.puzzlepay.text.erro_message"), Text.literal(e.message));
        } catch (ApiResponseException e) {
            MessageScreen.openMessage(Text.translatable("ui.puzzlepay.text.erro_message"), Text.literal(e.error.code + ": " + e.error.msg));
        }
    }

    @Override
    public void route(Context context) {
        route(context, null);
    }

    public record Props() {
    }
}
