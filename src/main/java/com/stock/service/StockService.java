package com.stock.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stock.model.Stock;


@Service
public interface StockService {
	List<Stock> getAllStocks();
	Stock saveStock(Stock stock);
	void deleteStock(Long id);
	Stock updateStock(String symbol, BigDecimal price);
	
	Optional<Stock> getStockBySymbol(String symbol);
	boolean isIndianStock(String symbol);
	 Map<String, Object> fetchStockDetailsFromTwelveData(String symbol);
   
//	BigDecimal getStockPrice(String symbol);

}
