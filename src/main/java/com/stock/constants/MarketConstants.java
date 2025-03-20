package com.stock.constants;

import java.util.HashMap;
import java.util.Map;

public class MarketConstants {
	public static final Map<String, String> MARKET_TO_CURRENCY = new HashMap<>();
	static {
		 MARKET_TO_CURRENCY.put("US", "USD"); // United States Dollar
	        MARKET_TO_CURRENCY.put("INDIA", "INR"); // Indian Rupee
	        MARKET_TO_CURRENCY.put("EUROPE", "EUR"); // Euro
	        MARKET_TO_CURRENCY.put("UK", "GBP"); // British Pound
	        MARKET_TO_CURRENCY.put("JAPAN", "JPY"); // Japanese Yen
	        MARKET_TO_CURRENCY.put("AUSTRALIA", "AUD"); // Australian Dollar
	        MARKET_TO_CURRENCY.put("CHINA", "CNY"); // Chinese Yuan
	        MARKET_TO_CURRENCY.put("CANADA", "CAD"); // Canadian Dollar
	        MARKET_TO_CURRENCY.put("SOUTH_AFRICA", "ZAR"); // South African Rand
	}
}
