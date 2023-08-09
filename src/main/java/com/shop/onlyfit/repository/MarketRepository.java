package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.Market;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketRepository extends JpaRepository<Market, Long> {
    Optional<Market> findByName(String name);
}
