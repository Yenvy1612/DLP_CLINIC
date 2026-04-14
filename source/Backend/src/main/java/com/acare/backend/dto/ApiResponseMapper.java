package com.acare.backend.dto;

import java.util.function.Function;

public final class ApiResponseMapper {

    private ApiResponseMapper() {
    }

    public static <S, T> ApiResponse<T> map(ApiResponse<S> source, Function<S, T> dataMapper) {
        if (source == null) {
            return null;
        }

        T mappedData = dataMapper == null ? null : dataMapper.apply(source.getData());
        return new ApiResponse<>(source.getStatus(), source.isSuccess(), source.getMessage(), mappedData);
    }
}
