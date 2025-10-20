package com.lakomka.dto;

public record CartItemDto(Long productId, String name, String price, Integer quantity) {
}
