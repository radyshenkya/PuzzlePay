package su.puzzle.pay;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PuzzlePayConfig extends ConfigWrapper<su.puzzle.pay.PuzzlePayConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.String> plasmoRpToken = this.optionForKey(this.keys.plasmoRpToken);
    private final Option<java.lang.String> lastUsedBankCard = this.optionForKey(this.keys.lastUsedBankCard);

    private PuzzlePayConfig() {
        super(su.puzzle.pay.PuzzlePayConfigModel.class);
    }

    private PuzzlePayConfig(Consumer<Jankson.Builder> janksonBuilder) {
        super(su.puzzle.pay.PuzzlePayConfigModel.class, janksonBuilder);
    }

    public static PuzzlePayConfig createAndLoad() {
        var wrapper = new PuzzlePayConfig();
        wrapper.load();
        return wrapper;
    }

    public static PuzzlePayConfig createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {
        var wrapper = new PuzzlePayConfig(janksonBuilder);
        wrapper.load();
        return wrapper;
    }

    public java.lang.String plasmoRpToken() {
        return plasmoRpToken.value();
    }

    public void plasmoRpToken(java.lang.String value) {
        plasmoRpToken.set(value);
    }

    public java.lang.String lastUsedBankCard() {
        return lastUsedBankCard.value();
    }

    public void lastUsedBankCard(java.lang.String value) {
        lastUsedBankCard.set(value);
    }


    public static class Keys {
        public final Option.Key plasmoRpToken = new Option.Key("plasmoRpToken");
        public final Option.Key lastUsedBankCard = new Option.Key("lastUsedBankCard");
    }
}

