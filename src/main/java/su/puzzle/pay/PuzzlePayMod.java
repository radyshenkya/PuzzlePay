package su.puzzle.pay;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuzzlePayMod implements ModInitializer {
    public static final String MOD_ID = "puzzlepay";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Puzzle Pay initialized");
    }
}
