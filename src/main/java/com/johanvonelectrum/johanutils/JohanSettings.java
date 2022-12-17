package com.johanvonelectrum.johanutils;

import com.johanvonelectrum.johanutils.settings.Rule;

public class JohanSettings {
    private static final String CORE = "Core";

    @Rule(
            categories = { CORE },
            side = Rule.RuleSide.SERVER
    )
    public static boolean requireClient = false;
}
