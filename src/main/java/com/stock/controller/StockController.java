package com.stock.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.model.Stock;
import com.stock.service.StockService;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    
    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }
    
    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }
    
    @GetMapping("/indian-prices")
    public ResponseEntity<Map<String, BigDecimal>> getIndianStockPrices() {
        List<String> indianStocks = Arrays.asList("RELIANCE", "TCS", "INFY", "HDFCBANK", "SBIN");

        Map<String, BigDecimal> stockPrices = new HashMap<>();
        for (String stock : indianStocks) {
            BigDecimal price = stockService.getStockPriceFromNSE(stock);
            stockPrices.put(stock, price != null ? price : BigDecimal.ZERO);
        }

        return ResponseEntity.ok(stockPrices);
    }

    
    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStockBySymbol(@PathVariable String symbol) {
        return stockService.getStockBySymbol(symbol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    
    @PostMapping
    public Stock addStock(@RequestBody Stock stock) {
        return stockService.saveStock(stock);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Stock stock) {
        stock.setId(id);
        return ResponseEntity.ok(stockService.saveStock(stock));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/update-prices")
    public ResponseEntity<String> updateStockPrices() {
        stockService.updateStockPrices();
        return ResponseEntity.ok("Stock prices updated!");
    }
}
