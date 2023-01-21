package com.example.mainproject012.dto.responose;

public record SimplePostResponse(
        Long id,
        String postThumbnailUrl
) {
    public static SimplePostResponse of(Long id, String postThumbnailUrl) {
        return new SimplePostResponse(id, postThumbnailUrl);
    }
}
