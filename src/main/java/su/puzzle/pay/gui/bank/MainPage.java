package su.puzzle.pay.gui.bank;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.*;
import su.puzzle.pay.*;
import su.puzzle.pay.api.*;
import su.puzzle.pay.api.exceptions.*;

public class MainPage {
    public Component mainPage = Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .child(
                            Containers.grid(Sizing.content(), Sizing.content(), 3, 2)
                                    .child(
                                            getCardDropdown(),
                                            1,
                                            1
                                    )
                    );

    public MainPage() throws ApiCallException, ApiResponseException {}

    public CollapsibleContainer getCardDropdown() throws ApiCallException, ApiResponseException {
        CollapsibleContainer container = Containers.collapsible(Sizing.content(), Sizing.content(), Text.literal(PuzzlePayClient.config.lastUsedBankCard()), false);
        PlasmoApi.getAllCards().unwrap().cards().forEach((card) -> {
            container.child(Components.label(Text.literal("EB-" + card.id())));
        });
        return container;
    }
}
