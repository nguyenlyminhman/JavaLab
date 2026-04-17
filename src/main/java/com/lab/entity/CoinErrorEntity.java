package com.lab.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Data
@Entity
@Table(name = "coin_error")
public class CoinErrorEntity {
    @Id
    @Column(name = "id")
    private int id;

    private String symbol;

    private String pricing;

    @Column(name = "created_dt")
    private Date createdDt;

    @Column(name = "raw_data")
    private String rawData;
}
