package com.johanvonelectrum.yaum.text;

import net.minecraft.text.Text;

public class YaumLiteralText implements YaumText {
    private final String text;

    public YaumLiteralText(String text) {
        this.text = text;
    }

    @Override
    public Text asText() {
        return Text.of(this.text);
    }
}
