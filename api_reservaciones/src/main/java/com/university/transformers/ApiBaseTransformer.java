package com.university.transformers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class ApiBaseTransformer {
    @JsonIgnore
    protected HttpStatus status;

    private int code;
    private String message;
    private Object data;
    private String warning;
    private String error;
    private List<String> errors;
    private List<String> warnings;

    public ApiBaseTransformer() {

    }

    public ApiBaseTransformer(HttpStatus status, String message, Object data, String warning, String error,
            List<String> errors,
            List<String> warnings) {
        this.status = status;
        this.code = status.value();
        this.message = message;
        this.data = data;
        this.warning = warning;
        this.error = error;
        this.errors = errors;
        this.warnings = warnings;
    }

    public ApiBaseTransformer(HttpStatus status, String message, Object data, String warning, String error) {
        this.status = status;
        this.code = status.value();
        this.message = message;
        this.data = data;
        this.warning = warning;
        this.error = error;
        this.errors = null;
        this.warnings = null;
    }

    public ResponseEntity<?> sendResponse() {
        if (this.getClass().isAssignableFrom(ApiBaseTransformer.class)) {
            return ResponseEntity.status(this.status).body((ApiBaseTransformer) this);
        } else if (this.getClass().isAssignableFrom(PaginateApiBaseTransformer.class)) {
            return ResponseEntity.status(this.status).body((PaginateApiBaseTransformer) this);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
        this.code = status.value();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }
}
