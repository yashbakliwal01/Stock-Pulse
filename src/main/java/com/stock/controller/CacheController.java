package com.stock.controller;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
public class CacheController {
	
	@DeleteMapping("/clear-stocks-cache")
	@CacheEvict(value = "stocksCache", allEntries = true)
	public String clearStocksCache() {
		return "Stocks cache cleared!!!";
	}
}
