package su.puzzle.pay.gui.Payment;

import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import su.puzzle.pay.PuzzlePayClient;
import su.puzzle.pay.gui.Message.MessageScreen;
import su.puzzle.pay.gui.Oauth2.*;
import su.puzzle.pay.plasmo_api.ApiCallException;
import su.puzzle.pay.plasmo_api.PlasmoApi;
import su.puzzle.pay.Utils;

import java.io.*;
import java.net.*;

public class PaymentScreen extends BaseUIModelScreen<FlowLayout> {
        private String toCard;
        private int amount;
        private String transferMessage;

        public PaymentScreen(String cardName, int amount, String paymentComment) {
                super(FlowLayout.class, DataSource.asset(new Identifier("puzzlepay:transfer_menu")));

                this.toCard = cardName;
                this.amount = amount;
                this.transferMessage = paymentComment;
        }

        @Override
        protected void build(FlowLayout rootComponent) {
                TextBoxComponent toCardText = rootComponent.childById(TextBoxComponent.class, "to-textbox")
                                .text(Utils.clearBankNumber(toCard));
                TextBoxComponent amountText = rootComponent.childById(TextBoxComponent.class, "amount-textbox")
                                .text(Integer.toString(amount));
                TextBoxComponent messageText = rootComponent.childById(TextBoxComponent.class, "message-textbox")
                                .text(transferMessage);
                TextBoxComponent fromCardText = rootComponent.childById(TextBoxComponent.class, "from-textbox")
                                .text(Utils.clearBankNumber(PuzzlePayClient.config.lastUsedBankCard()));

                rootComponent.childById(ButtonComponent.class, "transfer-button").onPress(button -> {
                        PlasmoApi.setToken(PuzzlePayClient.config.plasmoRpToken());
                        PuzzlePayClient.config.lastUsedBankCard(fromCardText.getText());

                        Text resultMessageText = null;
                        Text resultMessageName = null;

                        try {
                                PlasmoApi.newTransfer(
                                                Utils.clearBankNumber(fromCardText.getText()),
                                                Utils.clearBankNumber(toCardText.getText()),
                                                Integer.parseInt(amountText.getText()),
                                                messageText.getText());

                                resultMessageText = Text.translatable(
                                                "gui.puzzlepay.text.successful_transfer",
                                                Utils.clearBankNumber(fromCardText.getText()),
                                                Utils.clearBankNumber(toCardText.getText()),
                                                amountText.getText());

                                resultMessageName = Text.translatable("gui.puzzlepay.text.success_message_name");
                        } catch (ApiCallException apicallExc) {
                                resultMessageText = Text.translatable(
                                                "gui.puzzlepay.text.error",
                                                apicallExc.message);

                                resultMessageName = Text.translatable("gui.puzzlepay.text.error_message_name");

                                if (apicallExc.causedBy != null)
                                        apicallExc.causedBy.printStackTrace();
                        } catch (NumberFormatException parseExc) {
                                resultMessageText = Text.translatable(
                                                "gui.puzzlepay.text.error",
                                                Text.translatable("gui.puzzlepay.text.error_amount_must_be_integer")
                                                                .getString());

                                resultMessageName = Text.translatable("gui.puzzlepay.text.error_message_name");
                        }

                        MinecraftClient.getInstance()
                                        .setScreen(new MessageScreen(resultMessageName, resultMessageText));
                });

                rootComponent.childById(ButtonComponent.class, "set-token-button").onPress(button -> {
                        try {
                                AuthHttpServer server = new AuthHttpServer();
                                server.start();
                                MinecraftClient.getInstance().setScreen(new Oauth2Screen(server));
                        } catch (IOException e) {
                                throw new RuntimeException(e);
                        }
                });
        }
}
