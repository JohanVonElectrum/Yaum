package com.johanvonelectrum.yaum.settings;

import com.johanvonelectrum.yaum.YaumSettings;
import com.johanvonelectrum.yaum.lang.Language;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ParsedRuleImpl<T> implements ParsedRule<T> {

    private final Field field;
    private final String name;
    private final String[] categories;
    private final String[] options;
    private final boolean strict;
    private final List<Validator<T>> validators;
    private final Rule.RuleSide ruleSide;
    private final FromStringConverter<T> converter;
    private final Class<T> type;
    private T value;
    private T defaultValue;

    @SuppressWarnings({"rawtypes"})
    public ParsedRuleImpl(Field field, String name, String[] categories, String[] options, boolean strict, Class<? extends Validator>[] validators, Rule.RuleSide ruleSide) {
        this.field = field;
        this.name = name;
        this.categories = categories;
        this.validators = Arrays.stream(validators).map(this::instantiateValidator).collect(Collectors.toList());
        this.ruleSide = ruleSide;
        this.value = value();
        this.defaultValue = value();

        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) ClassUtils.primitiveToWrapper(field.getType());
        this.type = type;

        FromStringConverter<T> converter = null;
        if (this.type.equals(Boolean.class)) {
            this.options = new String[] {"true", "false"};
            this.strict = true;
        } else if (this.type.isEnum()) {
            this.options = (String[]) Arrays.stream(this.type.getEnumConstants()).map(e -> ((Enum<?>) e).name().toLowerCase(Locale.ROOT)).toArray();
            this.strict = true;

            converter = str -> {
                try {
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    T ret = (T)Enum.valueOf((Class<? extends Enum>) type, str.toUpperCase(Locale.ROOT));
                    return ret;
                } catch (IllegalArgumentException e) {
                    throw new InvalidRuleValueException("Valid values for this rule are: " + Arrays.toString(this.options));
                }
            };
        } else {
            this.options = options;
            this.strict = strict;
        }

        if (converter == null) {
            @SuppressWarnings("unchecked")
            FromStringConverter<T> converterFromMap = (FromStringConverter<T>)FromStringConverter.CONVERTER_MAP.get(type);
            if (converterFromMap == null) throw new UnsupportedOperationException("Unsupported type for ParsedRule" + type);
            converter = converterFromMap;
        }

        this.converter = converter;
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
    public void castAndSet(ServerCommandSource source, String value, boolean setDefault) throws InvalidRuleValueException {
        this.set(source, this.converter.convert(value), setDefault);
    }

    @Override
    public void set(ServerCommandSource source, T value, boolean setDefault) throws InvalidRuleValueException {
        if (!validate(source, value, true)) return;

        try {
            this.field.set(null, value);
            this.value = value;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (setDefault) {
            this.defaultValue = value;
            SettingsManager.save();
        }
    }

    @Override
    public boolean validate(ServerCommandSource source, T value) {
        try {
            return validate(source, value, false);
        } catch (InvalidRuleValueException e) {
            return false;
        }
    }

    public boolean validate(ServerCommandSource source, T value, boolean throwExceptions) throws InvalidRuleValueException {
        if (this.strict && this.options != null && Arrays.stream(this.options).noneMatch(opt -> opt.equals(value.toString()))) {
            if (throwExceptions)
                throw new InvalidRuleValueException(Language.tryTranslate(YaumSettings.defaultLanguage, "error.yaum.invalid-rule-value.options", Arrays.toString(this.options)));
            return false;
        }

        if (this.validators == null) return true;

        for (Validator<T> validator : this.validators) {
            if (!validator.validate(source, this, value, "")) { //TODO: user input = full command
                validator.notifyFailure(source, this, value.toString());
                if (throwExceptions)
                    throw new InvalidRuleValueException(Language.tryTranslate(YaumSettings.defaultLanguage, "error.yaum.invalid-rule-value.validator", value.toString(), validator.name(), validator.description()));
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
