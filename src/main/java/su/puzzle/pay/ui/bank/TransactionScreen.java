package su.puzzle.pay.ui.bank;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.*;
import net.minecraft.sound.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;
import su.puzzle.pay.*;
import su.puzzle.pay.api.*;
import su.puzzle.pay.api.exceptions.*;
import su.puzzle.pay.api.types.*;
import su.puzzle.pay.ui.*;
import su.puzzle.pay.ui.components.*;
import su.puzzle.pay.ui.router.*;

import java.util.*;

public class TransactionScreen extends BaseOwoScreen<FlowLayout> implements Route {
    private static final int MAX_CARDS_IN_SEARCH = 50;
    protected BankCard fromCard;
    protected Context context;
    protected Props props = new Props(null, 1, "");

    public TransactionScreen() {
    }

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

        CustomDropdownComponent cardList = new CustomDropdownComponent(Sizing.fill(100), Sizing.content(),
                this.fromCard == null ? Text.translatable("ui.puzzlepay.bank.tab.choose_card")
                        :
                        Text.literal(
                                this.fromCard.name() + "\n§8" +
                                        this.fromCard.getNormalId() + " — " +
                                        this.fromCard.holder() + " — " +
                                        this.fromCard.value()
                        ),
                false);

        cards.cards().forEach((card) -> {
            cardList.button(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()), button -> {
                this.fromCard = card;
                cardList.title(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()));
            });
        });

        InputDropdownComponent toCardInput = new InputDropdownComponent(
                Sizing.fill(100),
                Sizing.content(),
                props.to() == null ? Text.translatable("ui.puzzlepay.bank.tab.choose_card") : Text.literal(props.to().name() + "\n§8" + props.to().getNormalId() + " — " + props.to().holder() + " — " + props.to().getStringValue().getString()),
                false,
                16);

        toCardInput.onInputChange(text -> {
            if (text.length() < 2) return;

            toCardInput.removeEntries();

            for (int i = 0; i < 5; i++) {
                toCardInput.button(Text.literal("Поиск\n§8Поиск"), (btn) -> {
                });
            }

            PuzzlePayClient.asyncTasksService.addTask(() -> {
                return PlasmoApi.searchCards(text).unwrap();
            }, (result) -> {
                List<BankCard> searchResult = (List<BankCard>) result;

                toCardInput.removeEntries();

                if (searchResult.size() >= MAX_CARDS_IN_SEARCH) {
                    searchResult.subList(MAX_CARDS_IN_SEARCH, searchResult.size()).clear();
                    toCardInput.button(Text.literal("Найдено слишком много карт, чтобы отобразить их все"), onClick -> {
                    });
                }

                if (fromCard != null) searchResult.removeIf((card) -> card.id() == fromCard.id());

                if (searchResult.size() == 0) {
                    toCardInput.button(Text.literal("По данному запросу ничего не найдено"), onClick -> {
                    });
                    return;
                }

                searchResult.forEach(card -> toCardInput.button(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()), button -> {
                            props = new Props(card, props.amount(), props.comment());
                            toCardInput.title(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()));
                        }
                ));
            }, (exception) -> {
                toCardInput.removeEntries();
                toCardInput.button(Text.literal("Ошибка обращения к серверу Plasmo RP"), onClick -> {
                });
            });
        });


        TextBoxComponent amount = Components.textBox(Sizing.fill(100));
        amount.text(String.valueOf(props.amount()));
        amount.margins(Insets.top(4).add(0, 6, 0, 0));
        amount.horizontalSizing(Sizing.fill(100));
        amount.setMaxLength(6);

        TextBoxComponent comment = Components.textBox(Sizing.fill(100));
        comment.text(String.valueOf(props.comment()));
        comment.margins(Insets.top(4).add(0, 6, 0, 0));
        comment.horizontalSizing(Sizing.fill(100));
        comment.setMaxLength(350);

        ButtonComponent transfer = Components.button(Text.translatable("ui.puzzlepay.bank.tab.transactions"), (button) -> {
            if (props.to() == null) return;
            if (fromCard == null) return;
            if (fromCard.id() == props.to().id()) return;

            String commentString = comment.getText();
            String amountString = amount.getText();

            try {
                if (Integer.parseInt(amountString) <= 0) return;

                PlasmoApi.transfer(Integer.parseInt(amountString), fromCard.getNormalId(), commentString, props.to().getNormalId()).unwrap();
                PlasmoApi.updateUserActiveCard(fromCard);
                MinecraftClient.getInstance().setScreen(null);
                MinecraftClient.getInstance().player.sendMessage(Text.literal(String.format("\n§a✔ Перевод выполнен:§r\n- Отправлено %sалм. на карту §7%s§r (%s) игрока §7%s§r\n", amountString, props.to.getNormalId(), props.to.name(), props.to.holder())));
                MinecraftClient.getInstance().player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 2);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (ApiCallException e) {
                e.printStackTrace();
                MessageScreen.openMessage(Text.translatable("ui.puzzlepay.text.error_message"), Text.literal(e.message));
            } catch (ApiResponseException e) {
                e.printStackTrace();
                MessageScreen.openMessage(Text.translatable("ui.puzzlepay.text.error_message"), Text.literal(e.error.msg));
            }
        });


        transfer.horizontalSizing(Sizing.fill(100));

        rootComponent.surface(Surface.VANILLA_TRANSLUCENT);
        rootComponent.child(
                Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100))
                        .child(new NavigationBar(context).navbar)
                        .child(
                                Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100))
                                        .child(
                                                Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100))
                                                        .child(
                                                                Containers.verticalFlow(Sizing.fill(40), Sizing.content())
                                                                        // Top margin
                                                                        .child(Containers.verticalFlow(Sizing.fill(100), Sizing.fill(20)))
                                                                        // Transfer components
                                                                        .child(Components.label(Text.literal("Сумма")))
                                                                        .child(amount)
                                                                        .child(Components.label(Text.literal("Комментарий")))
                                                                        .child(comment)
                                                                        .child(transfer)
                                                                        // Card selection titles
                                                                        .child(
                                                                                Containers.horizontalFlow(Sizing.fill(100), Sizing.content())
                                                                                        .child(Components.label(Text.literal("Откуда")).horizontalSizing(Sizing.fill(45)))
                                                                                        .child(Containers.horizontalFlow(Sizing.fill(10), Sizing.fixed(0)))
                                                                                        .child(Components.label(Text.literal("Куда")).horizontalSizing(Sizing.fill(45)))
                                                                                        .padding(Insets.top(10))
                                                                                        .verticalAlignment(VerticalAlignment.TOP)
                                                                        )
                                                                        // Dropdowns
                                                                        .child(
                                                                                Containers.horizontalFlow(Sizing.fill(100), Sizing.content())
                                                                                        .child(cardList.horizontalSizing(Sizing.fill(45)))
                                                                                        .child(Components.label(Text.literal("→"))
                                                                                                .verticalTextAlignment(VerticalAlignment.CENTER)
                                                                                                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                                                                                                .sizing(Sizing.fill(10), Sizing.fixed(24))
                                                                                        )
                                                                                        .child(toCardInput.horizontalSizing(Sizing.fill(45)))
                                                                                        .padding(Insets.top(5))
                                                                                        .verticalAlignment(VerticalAlignment.TOP)
                                                                        )
                                                        )
                                                        .horizontalAlignment(HorizontalAlignment.CENTER)
                                        )
                        )
        );
    }

    @Override
    public void route(Context context, Object props) {
        MinecraftClient.getInstance().setScreen(new TransactionScreen(context, (Props) props));
    }

    @Override
    public void route(Context context) {
        route(context, null);
    }

    public record Props(BankCard to, int amount, String comment) {
    }
}
