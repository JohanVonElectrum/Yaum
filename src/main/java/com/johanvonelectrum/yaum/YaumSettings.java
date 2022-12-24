package com.johanvonelectrum.yaum;

import com.johanvonelectrum.yaum.lang.Language;
import com.johanvonelectrum.yaum.settings.ParsedRule;
import com.johanvonelectrum.yaum.settings.Rule;
import com.johanvonelectrum.yaum.settings.Validator;
import net.minecraft.server.command.ServerCommandSource;

public class YaumSettings {
    private static final String CORE = "Core";

    @Rule(
            categories = { CORE },
            side = Rule.RuleSide.SERVER
    )
    public static boolean protocol = true;

    @Rule(
            categories = { CORE },
            side = Rule.RuleSide.SERVER
    )
    public static boolean requireClient = true;


    private static final class LanguageValidator extends Validator<String> {

        @Override
        public String name() {
            return "language";
        }

        @Override
        public boolean validate(ServerCommandSource source, ParsedRule<String> changingRule, String newValue, String userInput) {
            if (Language.isInstalled(newValue)) {
                Language.load(newValue);
                return true;
            }
            return false;
        }

    }
    @Rule(
            categories = { CORE },
            side = Rule.RuleSide.SERVER,
            options = {"en_us", "es_es"},
            validators = {
                    LanguageValidator.class
            }
    )
    public static String defaultLanguage = "en_us";
}
