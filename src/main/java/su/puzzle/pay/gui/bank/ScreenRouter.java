package su.puzzle.pay.gui.bank;

import net.minecraft.client.*;
import net.minecraft.client.gui.screen.*;

public class ScreenRouter {
    public void route(int pageIndex) {
        Screen screen = switch (pageIndex) {
            case (1) -> new TransactionScreen();
            case (2) -> new BankerScreen();
            case (3) -> new InterpolScreen();
            default -> new BankScreen();
        };
        MinecraftClient.getInstance().setScreen(screen);
    }
}
