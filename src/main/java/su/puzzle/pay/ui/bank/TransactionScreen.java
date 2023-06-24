package su.puzzle.pay.ui.bank;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import su.puzzle.pay.api.PlasmoApi;
import su.puzzle.pay.api.exceptions.ApiCallException;
import su.puzzle.pay.api.exceptions.ApiResponseException;
import su.puzzle.pay.api.types.BankCard;
import su.puzzle.pay.ui.components.InputDropdownComponent;
import su.puzzle.pay.ui.router.Context;
import su.puzzle.pay.ui.router.Route;

public class TransactionScreen extends BaseOwoScreen<FlowLayout> implements Route {
    protected BankCard toCard;
    protected BankCard activeCard;

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
        InputDropdownComponent toCardInput = new InputDropdownComponent(Sizing.fill(100), Sizing.fill(20), Text.literal("Карта не выбрана"), false, 16);

        toCardInput.onInputChange(text -> {
            if (text.length() < 2) return;

            toCardInput.removeEntries();
            List<BankCard> searchResult;

            try {
                searchResult = PlasmoApi.searchCards(text).unwrap();
            } catch (ApiCallException | ApiResponseException e) {
                toCardInput.button(Text.literal("Ошибка обращения к серверу Plasmo RP"), onClick -> {});
                return;
            }

            List<BankCard> filteredSearchResult = new ArrayList<>();

            searchResult.forEach(card -> {
                // TODO: вот тут по сути фильтрация происходит, но т.к. пока что activeCard == null, я ее отключил
                // if (card.id() != activeCard.id()) return;
                filteredSearchResult.add(card);
            });
            
            searchResult = filteredSearchResult;

            if (searchResult.size() <= 0) {
                toCardInput.button(Text.literal("По данному запросу ничего не найдено"), onClick -> {});
                return;
            }

            searchResult.forEach(card -> {
                toCardInput.button(
                        Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()),
                        button -> {
                            toCard = card;
                            toCardInput.title(Text.literal(card.name() + "\n§8" + card.getNormalId() + " — " + card.holder() + " — " + card.value()));
                        });
            });
        });

        rootComponent.child(
                Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(
                                new NavigationBar(context).navbar))
                .child(Components.label(Text.literal("TransactionScreen")))
                .child(toCardInput)
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
