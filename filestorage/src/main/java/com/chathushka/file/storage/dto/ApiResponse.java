package com.chathushka.file.storage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the standard API error or result envelope.
 *
 * @param <T> type of the optional result data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final String errorCode;
    private final String title;
    private final String description;
    private final T result;
    private final List<String> errorList;
    private final LocalDateTime timeStamp;

    /**
     * Creates an API response from its builder values.
     *
     * @param builder configured response builder
     */
    private ApiResponse(Builder<T> builder) {
        this.errorCode = builder.errorCode;
        this.title = builder.title;
        this.description = builder.description;
        this.result = builder.result;
        this.errorList = builder.errorList;
        this.timeStamp = LocalDateTime.now();
    }

    /**
     * Creates a builder for an API response.
     *
     * @param <T> type of the optional result data
     * @return a new empty response builder
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * Returns the API error code.
     *
     * @return error code, if present
     */
    public String getErrorCode() {
        return this.errorCode;
    }

    /**
     * Returns the response title.
     *
     * @return response title, if present
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the response description.
     *
     * @return response description, if present
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the optional result data.
     *
     * @return result data, if present
     */
    public T getResult() {
        return this.result;
    }

    /**
     * Returns validation or request error details.
     *
     * @return error details, if present
     */
    public List<String> getErrorList() {
        return this.errorList;
    }

    /**
     * Returns when this response was created.
     *
     * @return response timestamp
     */
    public LocalDateTime getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * Collects values used to create an API response.
     *
     * @param <T> type of the optional result data
     */
    public static final class Builder<T> {

        private String errorCode;
        private String title;
        private String description;
        private T result;
        private List<String> errorList;

        /**
         * Sets the API error code.
         *
         * @param errorCode error code
         * @return this builder
         */
        public Builder<T> errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        /**
         * Sets the response title.
         *
         * @param title response title
         * @return this builder
         */
        public Builder<T> title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the response description.
         *
         * @param description response description
         * @return this builder
         */
        public Builder<T> description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Sets the optional result data.
         *
         * @param result result data
         * @return this builder
         */
        public Builder<T> result(T result) {
            this.result = result;
            return this;
        }

        /**
         * Sets request or validation error details.
         *
         * @param errorList error details
         * @return this builder
         */
        public Builder<T> errorList(List<String> errorList) {
            this.errorList = errorList;
            return this;
        }

        /**
         * Builds the configured response and assigns its creation timestamp.
         *
         * @return the completed response
         */
        public ApiResponse<T> build() {
            return new ApiResponse<>(this);
        }
    }
}
