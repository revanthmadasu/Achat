package com.achat.app.utils;
public class Utils {
    public static boolean isTruthy(Object value) {
        if (value == null) {
            return false;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        } else if (value instanceof String) {
            return !((String) value).isEmpty();
        } else if (value instanceof Character) {
            return ((Character) value) != '\u0000';
        } else {
            return true;
        }
    }
}
