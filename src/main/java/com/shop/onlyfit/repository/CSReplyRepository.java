package com.shop.onlyfit.repository;

import com.shop.onlyfit.domain.CustomServiceReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CSReplyRepository extends JpaRepository<CustomServiceReply, Integer> {
}
