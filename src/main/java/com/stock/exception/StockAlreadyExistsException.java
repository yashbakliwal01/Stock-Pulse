package com.stock.exception;

public class StockAlreadyExistsException extends RuntimeException {
	public StockAlreadyExistsException(String message) {
		super(message);
	}
}
