package su.puzzle.pay.gui.bank;

import io.wispforest.owo.ui.component.*;
import io.wispforest.owo.ui.container.*;
import io.wispforest.owo.ui.core.*;
import net.minecraft.text.*;

public class TransactionPage {
    public Component transactionPage = Containers.verticalFlow(Sizing.content(), Sizing.content())
            .child(
                    Containers.grid(Sizing.content(), Sizing.content(), 1, 1)
                            .child(
                                    Containers.verticalFlow(Sizing.content(), Sizing.content())
                                            .child(Components.label(Text.literal("helloy")))
                                            .horizontalAlignment(HorizontalAlignment.CENTER)
                                            .verticalAlignment(VerticalAlignment.CENTER)
                                    ,
                                    0,
                                    0
                            )
            );
}
