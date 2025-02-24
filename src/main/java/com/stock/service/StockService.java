package com.stock.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stock.model.Stock;


@Service
public interface StockService {
	
	List<Stock> getAllStocks();
	Optional<Stock> getStockBySymbol(String symbol);
	Stock saveStock(Stock stock);
	void deleteStock(Long id);
	void updateStockPrices();
	BigDecimal getStockPriceFromNSE(String stock);

}
