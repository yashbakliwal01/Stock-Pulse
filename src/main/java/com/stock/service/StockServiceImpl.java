package com.stock.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.stock.exception.StockAlreadyExistsException;
import com.stock.exception.StockNotFoundException;
import com.stock.model.Stock;
import com.stock.repository.StockRepository;

@Service
@Transactional
public class StockServiceImpl implements StockService{

	@Autowired
	private StockRepository stockRepository;
	
	private final RestTemplate restTemplate;
	
	private static final String TWELVEDATA_API_KEY = "755fbb678f8b42879ba885e8ecd237e1";
	private static final String TWELVEDATA_API_URL =  "https://api.twelvedata.com/quote?symbol=";
	
//	 private static final String NSE_API_URL = "https://www.nseindia.com/api/quote-equity?symbol=";
//	 
//    private static final String ALPHA_VANTAGE_API_URL  = "https://www.alphavantage.co/query";
//    private static final String ALPHA_VANTAGE_API_KEY = "ITNUWJRO8X13STTK";
//    	
    private static final Logger LOGGER = LoggerFactory.getLogger(StockServiceImpl.class);

	public StockServiceImpl(StockRepository stockRepository) {
		super();
		this.stockRepository = stockRepository;
		this.restTemplate = new RestTemplate();
	}

	@Override
	@Cacheable(value = "stocksCache")
	public List<Stock> getAllStocks() {  // Return only the data
	    return stockRepository.findAll();
	}

	// When a new stock is added, clear cache so fresh data is fetched
	@Override
	@CacheEvict(value = "stocksCache", allEntries = true)
	public Stock saveStock(Stock stock) {
		Optional<Stock> existingStock = stockRepository.findBySymbol(stock.getSymbol());
		if(existingStock.isPresent()) {
			throw new StockAlreadyExistsException("Stock with symbol " + stock.getSymbol()+" already exists.");
		}
		return stockRepository.save(stock);
	}

	@Override
	public void deleteStock(Long id) {
		stockRepository.deleteById(id);
	}

	@Override
	@CacheEvict(value = "stocksCache", allEntries = true)  // Clears cache after update
	@Transactional
	public Stock updateStock(String symbol, BigDecimal price) {
	    Stock stock = stockRepository.findBySymbol(symbol)
	        .orElseThrow(() -> new StockNotFoundException("Stock with symbol " + symbol + " not found."));

	    stock.setPrice(price);
	    return stockRepository.save(stock);
	}


	@Override
	public Optional<Stock> getStockBySymbol(String symbol) {
		return stockRepository.findBySymbol(symbol);
	}

   //--------------------- 
	// Caching stock details
    private final Map<String, Map<String, Object>> stockCache = new ConcurrentHashMap<>();

	
	// Fetch stock price from Twelve Data API (Automatically detects NSE or Global)
    @SuppressWarnings("all")
    public Map<String, Object> fetchStockDetailsFromTwelveData(String symbol) {
        // Check cache first
        if (stockCache.containsKey(symbol)) {
            LOGGER.info("Fetching stock details from cache for {}", symbol);
            return stockCache.get(symbol);
        }

        boolean hasExchangeSuffix = symbol.contains(":"); // Check if user provided exchange

        // Check if the stock is Indian (NSE)
        boolean isIndian = isIndianStock(symbol);
        String marketSuffix = (isIndian && !hasExchangeSuffix) ? ":NSE" : ""; // Append ":NSE" only for Indian stocks without an exchange suffix

        // Construct API URL
        String url = TWELVEDATA_API_URL + symbol + marketSuffix + "&apikey=" + TWELVEDATA_API_KEY;
        LOGGER.info("Fetching stock details from URL: {}", url);

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            LOGGER.info("API Response for {}: {}", symbol, response.getBody());

            if (response.getBody() != null) {
                if (response.getBody().containsKey("status") && "error".equals(response.getBody().get("status"))) {
                    LOGGER.warn("Error fetching stock details: {}", response.getBody().get("message"));
                    return null;
                }

                // Extracting important stock details
                Map<String, Object> stockData = new HashMap<>();
                stockData.put("symbol", symbol + marketSuffix);
                stockData.put("name", response.getBody().getOrDefault("name", "N/A"));
                stockData.put("exchange", response.getBody().getOrDefault("exchange", "Unknown"));
                stockData.put("currency", response.getBody().getOrDefault("currency", isIndian ? "INR" : "USD")); // Ensure INR for Indian stocks
                stockData.put("market_price", response.getBody().getOrDefault("price", "N/A"));
                stockData.put("day_high", response.getBody().getOrDefault("high", "N/A"));
                stockData.put("day_low", response.getBody().getOrDefault("low", "N/A"));
                stockData.put("change", response.getBody().getOrDefault("change", "N/A"));
                stockData.put("percent_change", response.getBody().getOrDefault("percent_change", "N/A"));
                stockData.put("timestamp", response.getBody().getOrDefault("timestamp", "N/A"));

                // Store in cache
                stockCache.put(symbol + marketSuffix, stockData);

                return stockData;
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching stock details for {}: {}", symbol, e.getMessage());
        }
        return null;
    }
//    private String formatSymbol(String symbol) {
//		// TODO Auto-generated method stub
//    	
//    	if(symbol.contains(".") || symbol.contains(":")) return symbol;
//    	if(isIndianStock(symbol)) return symbol.toUpperCase()+".NSE";
//    	return symbol.toUpperCase();
//	}

	// Check if stock is Indian based on NSE availability via Twelve Data API
    public boolean isIndianStock(String symbol) {
        String url = TWELVEDATA_API_URL + symbol + ".NSE&apikey=" + TWELVEDATA_API_KEY;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody() != null && response.getBody().containsKey("symbol");
        } catch (Exception e) {
            LOGGER.warn("Stock " + symbol + " not found in Twelve Data API: " + e.getMessage());
            return false;
        }
    }

//	@Override
//	public Optional<Stock> getStockBySymbolOrName(String query) {
//		return stockRepository.findBySymbolIgnoreCase(query)
//				.or(()->stockRepository.findByCompanyNameIgnoreCase(query));
//	}
}