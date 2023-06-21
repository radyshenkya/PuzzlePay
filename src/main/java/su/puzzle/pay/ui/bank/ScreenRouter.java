package su.puzzle.pay.ui.bank;

import net.minecraft.client.*;
import net.minecraft.client.gui.screen.*;
import su.puzzle.pay.api.exceptions.*;

public class ScreenRouter {
    public void route(int pageIndex) throws ApiCallException, ApiResponseException {
        Screen screen = switch (pageIndex) {
            case (1) -> new TransactionScreen();
            case (2) -> new BankerScreen();
            case (3) -> new InterpolScreen();
            default -> new BankScreen();
        };
        MinecraftClient.getInstance().setScreen(screen);
    }
}
