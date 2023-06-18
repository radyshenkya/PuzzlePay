package su.puzzle.pay;

import io.wispforest.owo.config.annotation.Config;

@Config(name = "puzzle-pay-config", wrapperName = "PuzzlePayConfig")
public class PuzzlePayConfigModel {
    public String plasmoRpToken = "";
    public String lastUsedBankCard = "";
}
