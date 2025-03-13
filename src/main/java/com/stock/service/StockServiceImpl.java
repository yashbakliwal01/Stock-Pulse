package com.stock.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.stock.model.Stock;
import com.stock.repository.StockRepository;

@Service
@Transactional
public class StockServiceImpl implements StockService{

	@Autowired
	private StockRepository stockRepository;
	
	private final RestTemplate restTemplate;
	private static final String API_KEY = "ITNUWJRO8X13STTK";
    private static final String API_URL = "https://www.alphavantage.co/query";
	
    private static final Logger LOGGER = LoggerFactory.getLogger(StockServiceImpl.class);

	public StockServiceImpl(StockRepository stockRepository) {
		super();
		this.stockRepository = stockRepository;
		this.restTemplate = new RestTemplate();
	}

	
	// Cache the result for future calls
	@Override
	@Cacheable(value = "stocksCache")
	public List<Stock> getAllStocks() {
		return stockRepository.findAll();
	}

	@Override
	public Optional<Stock> getStockBySymbol(String symbol) {
		return stockRepository.findBySymbol(symbol);
	}

	
	// When a new stock is added, clear cache so fresh data is fetched
	@Override
	@CacheEvict(value = "stocksCache", allEntries = true)
	public Stock saveStock(Stock stock) {
		return stockRepository.save(stock);
	}

	@Override
	public void deleteStock(Long id) {
		stockRepository.deleteById(id);
	}

	@Override
    public void updateStockPrices() {
        List<Stock> stocks = stockRepository.findAll();
        
        for (Stock stock : stocks) {
            BigDecimal newPrice = fetchStockPrice(stock.getSymbol());

            if (newPrice != null) {
                stock.setPrice(newPrice);
                stockRepository.save(stock);
                LOGGER.info("Updated stock: " + stock.getSymbol() + " - New Price: " + newPrice);
            } else {
                LOGGER.warn("Failed to fetch stock price for: " + stock.getSymbol());
            }
        }
    }

    private BigDecimal fetchStockPrice(String symbol) {
        if (isIndianStock(symbol)) {
            return fetchStockPriceFromNSE(symbol);
        } else {
            return fetchStockPriceFromAlphaVantage(symbol);
        }
    }

    private boolean isIndianStock(String symbol) {
        List<String> indianStocks = Arrays.asList("RELIANCE", "TCS", "INFY", "HDFCBANK", "SBIN");
        return indianStocks.contains(symbol.toUpperCase());
    }

    // ✅ Fetch from NSE India API
    private BigDecimal fetchStockPriceFromNSE(String symbol) {
        String url = "https://www.nseindia.com/api/quote-equity?symbol=" + symbol;
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        headers.set("Accept", "application/json");
        headers.set("Referer", "https://www.nseindia.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("info")) {
                Map<String, Object> info = (Map<String, Object>) response.getBody().get("info");
                return new BigDecimal(info.get("lastPrice").toString());
            }
        } catch (Exception e) {
            LOGGER.warn("Error fetching NSE price for " + symbol + ": " + e.getMessage());
        }
        return null;
    }

    // Fetch from Alpha Vantage API for US stocks
    private BigDecimal fetchStockPriceFromAlphaVantage(String symbol) {
        String url = API_URL + "?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + API_KEY;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getBody() != null && response.getBody().containsKey("Global Quote")) {
                Map<String, Object> quote = (Map<String, Object>) response.getBody().get("Global Quote");
                return new BigDecimal(quote.get("05. price").toString());
            }
        } catch (Exception e) {
            LOGGER.warn("Error fetching Alpha Vantage price for " + symbol + ": " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public BigDecimal getStockPriceFromNSE(String symbol) {
        return fetchStockPriceFromNSE(symbol);  
    }

}