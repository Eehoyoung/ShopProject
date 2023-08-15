package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MarketRepository extends JpaRepository<Market, Long> {
    Optional<Market> findByName(String name);

    @Query("select m.name FROM Market m WHERE m.marketId = :marketId")
    String findMarketNameByMarketId(@Param("marketId") Long marketId);

    @Query("SELECT m.visitCount FROM Market m WHERE m.marketId = :marketId")
    int findVisitCountByMarketId(@Param("marketId") Long marketId);

    @Query("SELECT m.marketId FROM Market m WHERE m.seller.loginId = :userId")
    Long findMarketIdByLoginId(@Param("userId") String userId);


}
