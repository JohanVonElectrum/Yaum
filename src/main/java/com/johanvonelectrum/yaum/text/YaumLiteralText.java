package com.johanvonelectrum.yaum.text;

import com.johanvonelectrum.yaum.text.format.YaumFormatter;
import net.minecraft.text.Text;

public class YaumLiteralText implements YaumText {
    private final String content;

    public YaumLiteralText(String content) {
        this.content = content;
    }

    @Override
    public Text asText() {
        return new YaumFormatter(this.content).parse();
    }
}
