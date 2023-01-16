package com.prodactivv.excelimporter.api;

public record SaveFormResult(String error, String message, String jsonResponse, String modelName, Long id) {
}
