package com.johanvonelectrum.yaum.text;

import com.johanvonelectrum.yaum.text.format.YaumFormatter;
import net.minecraft.text.Text;

public class YaumLiteralText implements YaumText {
    private final String content;
    private final Object[] args;

    public YaumLiteralText(String content) {
        this.content = content;
        this.args = null;
    }

    public YaumLiteralText(String content, Object... args) {
        this.content = content;
        this.args = args;
    }

    @Override
    public Text asText() {
        return new YaumFormatter(String.format(this.content, args)).parse();
    }
}
