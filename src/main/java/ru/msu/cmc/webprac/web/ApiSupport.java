package ru.msu.cmc.webprac.web;

import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.msu.cmc.webprac.utils.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

abstract class ApiSupport {

    static final String JSON = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8";

    protected Session openSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    protected ResponseEntity<String> ok(Object value) {
        return respond(HttpStatus.OK, value);
    }

    protected ResponseEntity<String> created(Object value) {
        return respond(HttpStatus.CREATED, value);
    }

    protected ResponseEntity<String> badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    protected ResponseEntity<String> notFound(String message) {
        return error(HttpStatus.NOT_FOUND, message);
    }

    protected ResponseEntity<String> conflict(String message) {
        return error(HttpStatus.CONFLICT, message);
    }

    protected ResponseEntity<String> serverError(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    protected ResponseEntity<String> error(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", false);
        body.put("message", message);
        return respond(status, body);
    }

    protected ResponseEntity<String> respond(HttpStatus status, Object value) {
        return ResponseEntity.status(status)
                .contentType(MediaType.parseMediaType(JSON))
                .body(JsonUtil.toJson(value));
    }

    protected Map<String, Object> result(String message, Object data) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("message", message);
        body.put("data", data);
        return body;
    }

    protected String text(Map<String, String> form, String key) {
        String value = form.get(key);
        if (value == null) {
            return null;
        }
        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    protected String requiredText(Map<String, String> form, String key, String title) {
        String value = text(form, key);
        if (value == null) {
            throw new IllegalArgumentException("Поле \"" + title + "\" обязательно");
        }
        return value;
    }

    protected Long longValue(Map<String, String> form, String key) {
        String value = text(form, key);
        return value == null ? null : Long.valueOf(value);
    }

    protected Integer intValue(Map<String, String> form, String key) {
        String value = text(form, key);
        return value == null ? null : Integer.valueOf(value);
    }

    protected Short shortValue(Map<String, String> form, String key) {
        String value = text(form, key);
        return value == null ? null : Short.valueOf(value);
    }

    protected BigDecimal decimalValue(Map<String, String> form, String key) {
        String value = text(form, key);
        return value == null ? null : new BigDecimal(value);
    }

    protected Boolean boolValue(Map<String, String> form, String key) {
        String value = text(form, key);
        return value != null && ("true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value) || "1".equals(value));
    }

    protected LocalDate dateValue(Map<String, String> form, String key) {
        String value = text(form, key);
        return value == null ? null : LocalDate.parse(value);
    }

    protected LocalDateTime dateTimeValue(Map<String, String> form, String key) {
        String value = text(form, key);
        if (value == null) {
            return null;
        }
        if (value.length() == 16) {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        }
        return LocalDateTime.parse(value);
    }
}
