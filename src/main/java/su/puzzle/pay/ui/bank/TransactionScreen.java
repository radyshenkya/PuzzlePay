package su.puzzle.pay.ui.bank;

import java.util.List;

import javax.swing.GroupLayout.Alignment;

import io.wispforest.owo.ui.core.*;
import org.jetbrains.annotations.NotNull;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import su.puzzle.pay.api.PlasmoApi;
import su.puzzle.pay.api.exceptions.ApiCallException;
import su.puzzle.pay.api.exceptions.ApiResponseException;
import su.puzzle.pay.api.types.*;
import su.puzzle.pay.ui.MessageScreen;
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
            MessageScreen.openMessage(Text.translatable("ui.puzzlepay.text.error_message"), Text.literal(e.getMessage()));
            return;
        }

        for (BankCard card : cards.cards()) {
            if (card.getNormalId().equals(cards.active_card())) {
                this.fromCard = card;
            }
        }

        CustomDropdownComponent cardList = new CustomDropdownComponent(Sizing.fill(100), Sizing.fill(40),
                Text.literal(this.fromCard == null ? "Выберите карту"
                        : this.fromCard.name() + "\n§8" + this.fromCard.getNormalId() + " — " + this.fromCard.holder()),
                false);
        cardList.margins(Insets.both(0, 8));

        cards.cards().forEach((card) -> {
            cardList.button(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()), button -> {
                this.fromCard = card;
                cardList.title(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()));
            });
        });

        InputDropdownComponent toCardInput = new InputDropdownComponent(Sizing.fill(100), Sizing.fill(40), Text.literal("Карта не выбрана"), false, 16);

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

        TextBoxComponent amount = Components.textBox(Sizing.fill(100));
        amount.margins(Insets.top(4).add(0, 6, 0, 0));
        amount.horizontalSizing(Sizing.fill(100));
        amount.setMaxLength(6);

        TextBoxComponent comment = Components.textBox(Sizing.fill(100));
        comment.margins(Insets.top(4).add(0, 6, 0, 0));
        comment.horizontalSizing(Sizing.fill(100));
        comment.setMaxLength(200);

        ButtonComponent transfer = Components.button(Text.translatable("ui.puzzlepay.bank.tab.transactions"), (button) -> {
            if (toCard == null) return;
            if (fromCard == toCard) return;

            String commentString = comment.getText();
            String amountString = amount.getText();

            try {
				PlasmoApi.transfer(Integer.parseInt(amountString), fromCard.getNormalId(), commentString, toCard.getNormalId()).unwrap();
                MessageScreen.openMessage(Text.translatable("ui.puzzlepay.text.success_message_name"), Text.literal("Перевод был выполнен удачно"));
			} catch (NumberFormatException e) {
                e.printStackTrace();
                return;
			} catch (ApiCallException e) {
                e.printStackTrace();
                MessageScreen.openMessage(Text.translatable("ui.puzzlepay.text.error_message"), Text.literal(e.causedBy.getMessage()));
			} catch (ApiResponseException e) {
                e.printStackTrace();
                MessageScreen.openMessage(Text.translatable("ui.puzzlepay.text.error_message"), Text.literal(e.error.msg));
			}
        });

        
        transfer.horizontalSizing(Sizing.fill(100));

        rootComponent.child(
                    Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100))
                            .child(
                                    new NavigationBar(context).navbar
                            )
                            .child(
                                    Containers.grid(Sizing.fill(100), Sizing.fill(92), 1, 3)
                                            .child(
                                                    Containers.verticalFlow(Sizing.fill(27), Sizing.fill(100))
                                                            .child(
                                                                    Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                                                                            // .child(
                                                                            //         Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                                                                            //                 .child(
                                                                            //                         Components.label(Text.literal("Карта " + fromCard.getNormalId()))
                                                                            //                                 .horizontalTextAlignment(HorizontalAlignment.CENTER)
                                                                            //                                 .horizontalSizing(Sizing.fill(100))
                                                                            //                                 .margins(Insets.both(0, 8))
                                                                            //                 )
                                                                            //                 .child(
                                                                            //                         Containers.grid(Sizing.fill(100), Sizing.content(), 1, 2)
                                                                            //                                 .child(
                                                                            //                                         Containers.verticalFlow(Sizing.fill(50), Sizing.content())
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal("Название")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal("Баланс")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal("Айди")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal("Владелец")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                            //                                                 )
                                                                            //                                                 .horizontalAlignment(HorizontalAlignment.LEFT)
                                                                            //                                         , 0,
                                                                            //                                         0
                                                                            //                                 )
                                                                            //                                 .child(
                                                                            //                                         Containers.verticalFlow(Sizing.fill(50), Sizing.content())
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal(fromCard.name())).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal(String.valueOf(fromCard.value()))).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal(fromCard.getNormalId())).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal(fromCard.holder())).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                            //                                                 )
                                                                            //                                                 .horizontalAlignment(HorizontalAlignment.RIGHT)
                                                                            //                                         , 0,
                                                                            //                                         1
                                                                            //                                 )
                                                                            //                                 .margins(Insets.of(8))
                                                                            //                 )
                                                                            // )
                                                                            // .horizontalAlignment(HorizontalAlignment.CENTER)
                                                                            // .verticalAlignment(VerticalAlignment.TOP)
                                                                            // .surface(Surface.flat(0x32000000))
                                                            )
                                                            .child(
                                                                cardList
                                                            )
                                                            .verticalAlignment(VerticalAlignment.CENTER)
                                                    , 0,
                                                    0
                                            )
                                            .child(
                                                    Containers.verticalFlow(Sizing.fill(27), Sizing.fill(100))
                                                            .child(Components.label(Text.literal("Сумма")))
                                                            .child(amount)
                                                            .child(Components.label(Text.literal("Комментарий")))
                                                            .child(comment)
                                                            .child(transfer)
                                                            .verticalAlignment(VerticalAlignment.CENTER)
                                                            .horizontalAlignment(HorizontalAlignment.CENTER)
                                                    , 0,
                                                    1
                                            )
                                            .child(
                                                    Containers.verticalFlow(Sizing.fill(27), Sizing.fill(100))
                                                            .child(
                                                                    Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                                                                            // .child(
                                                                            //         Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                                                                            //                 .child(
                                                                            //                         Components.label(Text.literal(toCard != null ? "Карта " + toCard.getNormalId() : "Карта не выбрана"))
                                                                            //                                 .horizontalTextAlignment(HorizontalAlignment.CENTER)
                                                                            //                                 .horizontalSizing(Sizing.fill(100))
                                                                            //                                 .margins(Insets.both(0, 8))
                                                                            //                 )
                                                                            //                 .child(
                                                                            //                         Containers.grid(Sizing.fill(100), Sizing.content(), 1, 2)
                                                                            //                                 .child(
                                                                            //                                         Containers.verticalFlow(Sizing.fill(50), Sizing.content())
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal("Название")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal("Баланс")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal("Айди")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal("Владелец")).horizontalTextAlignment(HorizontalAlignment.LEFT)
                                                                            //                                                 )
                                                                            //                                                 .horizontalAlignment(HorizontalAlignment.LEFT)
                                                                            //                                         , 0,
                                                                            //                                         0
                                                                            //                                 )
                                                                            //                                 .child(
                                                                            //                                         Containers.verticalFlow(Sizing.fill(50), Sizing.content())
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal(toCard != null ? toCard.name() : "Карта не выбрана")).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal(String.valueOf(toCard != null ? toCard.value() : "Карта не выбрана"))).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal(toCard != null ? toCard.getNormalId() : "Карта не выбрана")).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                            //                                                 )
                                                                            //                                                 .child(
                                                                            //                                                         Components.label(Text.literal(toCard != null ? toCard.holder() : "Карта не выбрана")).horizontalTextAlignment(HorizontalAlignment.RIGHT)
                                                                            //                                                 )
                                                                            //                                                 .horizontalAlignment(HorizontalAlignment.RIGHT)
                                                                            //                                         , 0,
                                                                            //                                         1
                                                                            //                                 )
                                                                            //                                 .margins(Insets.of(8))
                                                                            //                 )
                                                                            // )
                                                                            // .horizontalAlignment(HorizontalAlignment.CENTER)
                                                                            // .verticalAlignment(VerticalAlignment.TOP)
                                                                            // .surface(Surface.flat(0x32000000))
                                                            )
                                                            .child(
                                                                    toCardInput
                                                            )
                                                            .verticalAlignment(VerticalAlignment.CENTER)
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
    public void route(Context context, Object props) {
        MinecraftClient.getInstance().setScreen(new TransactionScreen(context, (Props) props));
    }

    @Override
    public void route(Context context) {
        route(context, null);
    }

    public record Props() {

    }
}
