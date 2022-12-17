package com.johanvonelectrum.yaum.settings;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParsedRuleImpl<T> implements ParsedRule<T> {
    private final Field field;
    private final String name;
    private final String[] categories;
    private final String[] options;
    private final boolean strict;
    private final List<Validator<T>> validators;
    private final Rule.RuleSide ruleSide;
    private T value;
    private T defaultValue;

    @SuppressWarnings({"rawtypes"})
    public ParsedRuleImpl(Field field, String name, String[] categories, String[] options, boolean strict, Class<? extends Validator>[] validators, Rule.RuleSide ruleSide) {
        this.field = field;
        this.name = name;
        this.categories = categories;
        this.options = options;
        this.strict = strict;
        this.validators = Arrays.stream(validators).map(this::instantiateValidator).collect(Collectors.toList());
        this.ruleSide = ruleSide;
        this.value = value();
        this.defaultValue = value();
    }

    public ParsedRuleImpl(Field field, Rule rule) {
        this(
                field,
                field.getName(),
                rule.categories(),
                rule.options(),
                rule.strict(),
                rule.validators(),
                rule.side()
        );
    }

    public static ParsedRuleImpl<?> of(Field field, Rule rule) {
        return new ParsedRuleImpl<>(field, rule);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Validator<T> instantiateValidator(Class<? extends Validator> cls)
    {
        try
        {
            Constructor<? extends Validator> constr = cls.getDeclaredConstructor();
            constr.setAccessible(true);
            return constr.newInstance();
        }
        catch (ReflectiveOperationException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return "rule.yaum." + this.name + ".description";
    }

    @Override
    public String[] categories() {
        return this.categories;
    }

    @Override
    public String[] options() {
        return this.options;
    }

    @Override
    public boolean strict() {
        return this.strict;
    }

    @Override
    public Rule.RuleSide ruleSide() {
        return this.ruleSide;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public T value() {
        try {
            return (T) this.field.get(null);
        } catch (IllegalAccessException e) {
            // Can't happen at regular runtime because we'd have thrown it on construction
            throw new IllegalArgumentException("Couldn't access field for rule: " + name, e);
        }
    }

    @Override
    public T defaultValue() {
        return this.defaultValue;
    }

    @Override
    public void set(ServerCommandSource source, T value, boolean setDefault) {
        if (!validate(source, value)) return;

        this.value = value;

        if (setDefault) {
            this.defaultValue = value;
            SettingsManager.save();
        }
    }

    @Override
    public boolean validate(ServerCommandSource source, T value) {
        if (this.options != null && Arrays.stream(this.options).noneMatch(opt -> opt.equals(value.toString())))
            return false;

        if (this.validators == null) return true;

        for (Validator<T> validator : this.validators) {
            if (!validator.validate(source, this, value, "")) { //TODO: user input = full command
                validator.notifyFailure(source, this, value.toString());
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return name() + "{" +
                "field=" + field +
                ", description='" + description() + '\'' +
                ", categories=" + Arrays.toString(categories()) +
                ", options=" + Arrays.toString(options()) +
                ", strict=" + strict() +
                ", validators=" + validators +
                ", side=" + ruleSide() +
                ", value=" + value() +
                ", defaultValue=" + defaultValue() +
                '}';
    }
}
