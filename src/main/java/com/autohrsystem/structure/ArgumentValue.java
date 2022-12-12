package com.autohrsystem.structure;

public class ArgumentValue {
    public enum ArgumentType {
        OPTION,
        ARGUMENT,
        INPUT_RESOURCE,
        UNDEFINED,
    }

    ArgumentType type;
    Object value;

    private ArgumentValue(ArgumentType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public static ArgumentValue of(ArgumentType type, Object value) {return new ArgumentValue(type, value);}

    public ArgumentType getType() {
        return type;
    }

    public String getValue() {
        return null == value ? null : value.toString();
    }

    public <T> T value(Class<T> clz) { return clz.cast(value); }
}
