package su.puzzle.pay.ui.bank;

import net.minecraft.client.*;
import net.minecraft.client.gui.screen.*;
import su.puzzle.pay.api.exceptions.*;
import su.puzzle.pay.ui.oauth2.Oauth2Screen;

public class ScreenRouter {
    public void route(int pageIndex) throws ApiCallException, ApiResponseException {
        Screen screen = switch (pageIndex) {
            case (1) -> new TransactionScreen();
            case (2) -> new BankerScreen();
            case (3) -> new InterpolScreen();
            case (4) -> new Oauth2Screen();
            default -> new BankScreen();
        };
        MinecraftClient.getInstance().setScreen(screen);
    }
}
