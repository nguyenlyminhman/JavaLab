package com.lab.modules.activemq.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage implements Serializable {  // Serializable bắt buộc!

    private static final long serialVersionUID = 1L;

    private String orderId;

    @NotNull(message = "Customer ID không được null")
    private String customerId;

    @NotNull
    private String productName;

    @Positive(message = "Quantity phải > 0")
    private Integer quantity;

    private BigDecimal totalAmount;

    private String status;    // PENDING, PROCESSING, COMPLETED, FAILED

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // Factory method tiện lợi
    public static OrderMessage createNew(String customerId, String product, int qty, BigDecimal amount) {
        return OrderMessage.builder()
                .orderId(java.util.UUID.randomUUID().toString())
                .customerId(customerId)
                .productName(product)
                .quantity(qty)
                .totalAmount(amount)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
