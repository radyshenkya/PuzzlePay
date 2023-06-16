package su.puzzle.pay.gui.Home;

import io.wispforest.owo.ui.base.*;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.util.Identifier;

public class HomeScreen extends BaseUIModelScreen<FlowLayout> {
    public HomeScreen() {
        super(FlowLayout.class, DataSource.asset(new Identifier("puzzlepay:home")));
    }

    @Override
    protected void build(FlowLayout rootComponent) {

    }
}
