package com.stock.model;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stock.constants.MarketConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // Lombok generates getter, setter, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stocks")
public class Stock {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String symbol;
	
	@Column(nullable = false)
	private String companyName;
	
	@Column(nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private BigDecimal price;
	
	@Column(nullable = false)
	private String market;
	
	@JsonProperty("price")
	public String getFormattedPrice() {
	    if (market == null || price == null) {
	        return price != null ? price.toString() : "0.00";
	    }

	    String currencyCode = MarketConstants.MARKET_TO_CURRENCY.getOrDefault(market.toUpperCase(), "USD");
	    
	    try {
	        Currency currency = Currency.getInstance(currencyCode);
	        String currencySymbol = currency.getSymbol(Locale.getDefault());
	        return currencySymbol + " " + price;
	    } catch (IllegalArgumentException e) {
	        return "$ " + price;  // Default to USD if currency not found
	    }
	}
}
