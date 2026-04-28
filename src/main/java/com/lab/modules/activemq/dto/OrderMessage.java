package com.lab.modules.activemq.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessage implements Serializable {  // Serializable bắt buộc!

    private static final long serialVersionUID = 1L;

    private String orderId;


    private String customerId;


    private String productName;


    private Integer quantity;

    private BigDecimal totalAmount;

    private String status;    // PENDING, PROCESSING, COMPLETED, FAILED



    // Factory method tiện lợi
    public static OrderMessage createNew(String customerId, String product, int qty, BigDecimal amount) {
        return OrderMessage.builder()
                .orderId(java.util.UUID.randomUUID().toString())
                .customerId(customerId)
                .productName(product)
                .quantity(qty)
                .totalAmount(amount)
                .status("PENDING")
                .build();
    }
}
