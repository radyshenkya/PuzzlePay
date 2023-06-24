package su.puzzle.pay.ui.bank;

import java.util.List;

import io.wispforest.owo.ui.core.*;
import org.jetbrains.annotations.NotNull;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import su.puzzle.pay.api.PlasmoApi;
import su.puzzle.pay.api.exceptions.ApiCallException;
import su.puzzle.pay.api.exceptions.ApiResponseException;
import su.puzzle.pay.api.types.*;
import su.puzzle.pay.ui.components.*;
import su.puzzle.pay.ui.router.Context;
import su.puzzle.pay.ui.router.Route;

public class TransactionScreen extends BaseOwoScreen<FlowLayout> implements Route {
    protected BankCard toCard;
    protected BankCard fromCard;

    protected Context context;
    protected Props props = new Props();

    public TransactionScreen() { }

    public TransactionScreen(Context context, Props props) {
        this.context = context;
        this.props = props != null ? props : this.props;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        BankCardsResponse cards;
        try {
            cards = PlasmoApi.getAllCards().unwrap();
        } catch (ApiResponseException | ApiCallException e) {
            throw new RuntimeException(e);
        }
        for (BankCard card : cards.cards()) {
            if (card.getNormalId().equals(cards.active_card())) {
                this.fromCard = card;
            }
        }
        CustomDropdownComponent cardList = new CustomDropdownComponent(Sizing.fill(100), Sizing.fill(80),
                Text.literal(this.fromCard == null ? "Выберите карту"
                        : this.fromCard.name() + "\n§8" + this.fromCard.getNormalId() + " — " + this.fromCard.holder()),
                false);
        cardList.margins(Insets.both(0, 8));

        cards.cards().forEach((card) -> {
            cardList.button(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()), button -> {
                this.fromCard = card;
            });
        });

        InputDropdownComponent toCardInput = new InputDropdownComponent(Sizing.fill(100), Sizing.fill(20), Text.literal("Карта не выбрана"), false, 16);

        toCardInput.onInputChange(text -> {
            if (text.length() < 2) return;

            toCardInput.removeEntries();
            List<BankCard> searchResult;

            try {
                searchResult = PlasmoApi.searchCards(text).unwrap();
                searchResult.removeIf((card) -> card == this.fromCard);
            } catch (ApiCallException | ApiResponseException e) {
                toCardInput.button(Text.literal("Ошибка обращения к серверу Plasmo RP"), onClick -> {});
                return;
            }

            if (searchResult.size() == 0) {
                toCardInput.button(Text.literal("По данному запросу ничего не найдено"), onClick -> {});
                return;
            }

            searchResult.forEach(card -> toCardInput.button(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()), button -> {
                        toCard = card;
                        toCardInput.title(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()));
                    }
            ));
        });

        rootComponent.child(
                    Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100))
                            .child(
                                    new NavigationBar(context).navbar
                            )
                            .child(
                                    Containers.grid(Sizing.fill(81), Sizing.fill(100), 1, 3)
                                            .child(
                                                    Containers.verticalFlow(Sizing.fill(27), Sizing.fill(50))
                                                            .child(
                                                                    Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                                                                            .child(
                                                                                    Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                                                                                            .child(
                                                                                                    Components.label(Text.literal("Карта " + fromCard.getNormalId()))
                                                                                                            .horizontalTextAlignment(HorizontalAlignment.CENTER)
                                                                                                            .horizontalSizing(Sizing.fill(100))
                                                                                                            .margins(Insets.both(0, 8))
                                                                                            )
                                                                                            .child(
                                                                                                    Containers.grid(Sizing.fill(100), Sizing.content(), 1, 2)
                                                                                                            .child(
                                                                                                                    Containers.verticalFlow(Sizing.fill(50), Sizing.content())
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal("Название")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal("Баланс")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal("Айди")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal("Владелец")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                            )
                                                                                                                            .horizontalAlignment(HorizontalAlignment.LEFT)
                                                                                                                    , 0,
                                                                                                                    0
                                                                                                            )
                                                                                                            .child(
                                                                                                                    Containers.verticalFlow(Sizing.fill(50), Sizing.content())
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal(fromCard.name())).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal(String.valueOf(fromCard.value()))).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal(fromCard.getNormalId())).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal(fromCard.holder())).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                            )
                                                                                                                            .horizontalAlignment(HorizontalAlignment.RIGHT)
                                                                                                                    , 0,
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
                                                    , 0,
                                                    0
                                            )
                                            .child(
                                                    Containers.verticalFlow(Sizing.fill(27), Sizing.fill(50))
                                                    , 0,
                                                    1
                                            )
                                            .child(
                                                    Containers.verticalFlow(Sizing.fill(27), Sizing.fill(50))
                                                            .child(
                                                                    Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                                                                            .child(
                                                                                    Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                                                                                            .child(
                                                                                                    Components.label(Text.literal(toCard != null ? "Карта " + toCard.getNormalId() : "Карта не выбрана"))
                                                                                                            .horizontalTextAlignment(HorizontalAlignment.CENTER)
                                                                                                            .horizontalSizing(Sizing.fill(100))
                                                                                                            .margins(Insets.both(0, 8))
                                                                                            )
                                                                                            .child(
                                                                                                    Containers.grid(Sizing.fill(100), Sizing.content(), 1, 2)
                                                                                                            .child(
                                                                                                                    Containers.verticalFlow(Sizing.fill(50), Sizing.content())
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal("Название")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal("Баланс")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal("Айди")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal("Владелец")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                                                                            )
                                                                                                                            .horizontalAlignment(HorizontalAlignment.LEFT)
                                                                                                                    , 0,
                                                                                                                    0
                                                                                                            )
                                                                                                            .child(
                                                                                                                    Containers.verticalFlow(Sizing.fill(50), Sizing.content())
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal(toCard != null ? toCard.name() : "Карта не выбрана")).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal(String.valueOf(toCard != null ? toCard.value() : "Карта не выбрана"))).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal(toCard != null ? toCard.getNormalId() : "Карта не выбрана")).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    Components.label(Text.literal(toCard != null ? toCard.holder() : "Карта не выбрана")).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                                                                            )
                                                                                                                            .horizontalAlignment(HorizontalAlignment.RIGHT)
                                                                                                                    , 0,
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
                                                                    toCardInput
                                                            )
                                                    , 0,
                                                    2
                                            )
                                            .horizontalAlignment(HorizontalAlignment.CENTER)
                                            .verticalAlignment(VerticalAlignment.CENTER)
                            )
                            .horizontalAlignment(HorizontalAlignment.CENTER)
                            .verticalAlignment(VerticalAlignment.CENTER)
                )
                .surface(Surface.VANILLA_TRANSLUCENT);
    }

    @Override
    public void route(Context context, Object props) throws ApiCallException, ApiResponseException {
        MinecraftClient.getInstance().setScreen(new TransactionScreen(context, (Props) props));
    }

    @Override
    public void route(Context context) throws ApiCallException, ApiResponseException {
        route(context, null);
    }

    public record Props() {

    }
}
