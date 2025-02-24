package com.stock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stock.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long>{
	Optional<Stock> findBySymbol(String symbol);
}
