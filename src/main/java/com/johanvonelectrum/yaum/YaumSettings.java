package com.johanvonelectrum.yaum;

import com.johanvonelectrum.yaum.settings.Rule;

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


    @Rule(
            categories = { CORE },
            side = Rule.RuleSide.SERVER //TODO: check that lang exists with a validator
    )
    public static String defaultLanguage = "en_us";
}
