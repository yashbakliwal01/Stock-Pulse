package com.stock.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stock.model.Stock;
import com.stock.service.StockService;
import com.stock.service.StockServiceImpl;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    
   // private final StockService stockService;
    private final StockServiceImpl stockService;
    private static final Logger LOGGER = LoggerFactory.getLogger(StockController.class);
    
    //constructor injection approach is better. 
    @Autowired
    public StockController(StockService stockService) {
        this.stockService = (StockServiceImpl) stockService;
    }
    
    @GetMapping("/getAllStocksList")
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks =  stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addStock(@RequestBody Stock stock) {
        try {
            if (stock.getPrice() == null) {
                return ResponseEntity.badRequest().body("Price cannot be null");
            }
            stockService.saveStock(stock);
            return ResponseEntity.ok("Stock added successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    
    @PutMapping("/update")
    public ResponseEntity<String> updateStock(@RequestParam String symbol, @RequestParam BigDecimal price) {
        Stock updatedStock = stockService.updateStock(symbol, price);
        return ResponseEntity.ok("Stock with symbol " + symbol + " updated successfully to price " + price);
    }
//------------------------------------------------
    
//    @GetMapping("/fetchStockDetails")
//    public ResponseEntity<?> fetchStockDetails(@RequestParam String symbol) {
////    	Optional<Stock> stockOptional = stockService.getStockBySymbolOrName(query);
//    	if(!symbol.contains(":")) {
//    		symbol = symbol + ":NSE";
//    	}
//    	
////    	if(stockOptional.isEmpty()) {
////    		return ResponseEntity.badRequest().body("Invalid stock name or symbol: " + query);
////    	}
//    	
//    	 BigDecimal price = stockService.fetchFromTwelveData(symbol);
//
//        if (price != null) {
//            LOGGER.info("Stock Price for {}: {}", symbol, price);
//            return ResponseEntity.ok(price);
//        } else {
//            LOGGER.warn("Stock price not found for symbol: {}", symbol);
//            return ResponseEntity.badRequest().body("Stock price not available for symbol: " + symbol);
//        }
//    }
    
    
    @GetMapping("/fetchStockDetails")
    public ResponseEntity<?> fetchStockDetails(@RequestParam String symbol) {
        LOGGER.info("Received stock request for symbol: {}", symbol);

        // Fetch stock details from the service
        Map<String, Object> stockDetails = stockService.fetchStockDetailsFromTwelveData(symbol);

        if (stockDetails != null && !stockDetails.isEmpty()) {
            LOGGER.info("Stock Details for {}: {}", symbol, stockDetails);
            return ResponseEntity.ok(stockDetails);
        } else {
            LOGGER.warn("Stock details not found for symbol: {}", symbol);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Stock details not available for symbol: " + symbol));
        }
    }
    
    
    
    
    
    
//    
//    @GetMapping("/price")
//    public ResponseEntity<?> fetchStockPrice(@RequestParam String symbol){
//    	return stockService.getStockPrice(symbol)
//    }
//    
//    @GetMapping("/fetchStockPrice")
//    public ResponseEntity<?> fetchStockPrice(@RequestParam String symbol) {
//        BigDecimal price = stockService.fetchStockPrice(symbol);
//        
//        if (price == null) {
//            return ResponseEntity.badRequest().body("Stock price not available for symbol: " + symbol);
//        }
//
//        Optional<Stock> stockOpt = stockService.getStockBySymbol(symbol);
//        
//        if (stockOpt.isPresent()) {
//            Stock stock = stockOpt.get();
//            return ResponseEntity.ok(stock.getFormattedPrice()); // Returns formatted price with currency symbol
//        } else {
//            return ResponseEntity.badRequest().body("Stock with symbol " + symbol + " not found in the database.");
//        }
//    }
    
    

    
    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStockBySymbol(@PathVariable String symbol) {
        return stockService.getStockBySymbol(symbol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
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
    
//    @PutMapping("/update-prices")
//    public ResponseEntity<String> updateStockPrices() {
//        stockService.updateStockPrices();
//        return ResponseEntity.ok("Stock prices updated!");
//    }
}
