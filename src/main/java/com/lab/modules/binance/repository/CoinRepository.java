package com.lab.modules.binance.repository;

import com.lab.entity.CoinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoinRepository extends JpaRepository<CoinEntity, Long> {

    Optional<CoinEntity> findBySymbol(String symbol);
}
