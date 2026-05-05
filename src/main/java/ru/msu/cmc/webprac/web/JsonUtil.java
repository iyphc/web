package ru.msu.cmc.webprac.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;

final class JsonUtil {

    private JsonUtil() {
    }

    static String toJson(Object value) {
        StringBuilder out = new StringBuilder();
        append(out, value);
        return out.toString();
    }

    @SuppressWarnings("unchecked")
    private static void append(StringBuilder out, Object value) {
        if (value == null) {
            out.append("null");
        } else if (value instanceof String || value instanceof Character
                || value instanceof LocalDate || value instanceof LocalDateTime
                || value instanceof Enum<?>) {
            appendString(out, String.valueOf(value));
        } else if (value instanceof Number || value instanceof Boolean) {
            out.append(value);
        } else if (value instanceof Map<?, ?>) {
            appendMap(out, (Map<String, Object>) value);
        } else if (value instanceof Iterable<?>) {
            appendIterable(out, (Iterable<?>) value);
        } else {
            appendString(out, String.valueOf(value));
        }
    }

    private static void appendMap(StringBuilder out, Map<String, Object> map) {
        out.append('{');
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            appendString(out, entry.getKey());
            out.append(':');
            append(out, entry.getValue());
            if (iterator.hasNext()) {
                out.append(',');
            }
        }
        out.append('}');
    }

    private static void appendIterable(StringBuilder out, Iterable<?> values) {
        out.append('[');
        Iterator<?> iterator = values.iterator();
        while (iterator.hasNext()) {
            append(out, iterator.next());
            if (iterator.hasNext()) {
                out.append(',');
            }
        }
        out.append(']');
    }

    private static void appendString(StringBuilder out, String value) {
        out.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"':
                    out.append("\\\"");
                    break;
                case '\\':
                    out.append("\\\\");
                    break;
                case '\b':
                    out.append("\\b");
                    break;
                case '\f':
                    out.append("\\f");
                    break;
                case '\n':
                    out.append("\\n");
                    break;
                case '\r':
                    out.append("\\r");
                    break;
                case '\t':
                    out.append("\\t");
                    break;
                default:
                    if (c < 32) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        out.append('"');
    }
}
