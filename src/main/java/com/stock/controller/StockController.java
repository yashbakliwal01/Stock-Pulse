package com.stock.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.model.Stock;
import com.stock.service.StockService;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    
    private final StockService stockService;

    //constructor injection approach is better. 
    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }
    
    
    
    @GetMapping(name = "/getAllStocksList")
    @Cacheable(value = "stocksCache")		//Caches data in Redis.
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }
    
    
    //cacheEvict>>clears cache when a new stock is added.
    @PostMapping("/add")
    @CacheEvict(value = "stocksCache", allEntries = true)
	public Stock addStock(@RequestBody Stock stock) {
    	return stockService.saveStock(stock);
	}
    
//    @GetMapping("/indian-prices")
//    public ResponseEntity<Map<String, BigDecimal>> getIndianStockPrices() {
//        List<String> indianStocks = Arrays.asList("RELIANCE", "TCS", "INFY", "HDFCBANK", "SBIN");
//
//        Map<String, BigDecimal> stockPrices = new HashMap<>();
//        for (String stock : indianStocks) {
//            BigDecimal price = stockService.getStockPriceFromNSE(stock);
//            stockPrices.put(stock, price != null ? price : BigDecimal.ZERO);
//        }
//
//        return ResponseEntity.ok(stockPrices);
//    }

    
//    @GetMapping("/{symbol}")
//    public ResponseEntity<Stock> getStockBySymbol(@PathVariable String symbol) {
//        return stockService.getStockBySymbol(symbol)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//    
//    
//    
//    @PutMapping("/{id}")
//    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Stock stock) {
//        stock.setId(id);
//        return ResponseEntity.ok(stockService.saveStock(stock));
//    }
//    
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
//        stockService.deleteStock(id);
//        return ResponseEntity.noContent().build();
//    }
//    
//    @PutMapping("/update-prices")
//    public ResponseEntity<String> updateStockPrices() {
//        stockService.updateStockPrices();
//        return ResponseEntity.ok("Stock prices updated!");
//    }
}
