package com.vicheak.bank.gatewayserver.filter;

import java.util.UUID;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

//this class will become Pre Filter

//will be firstly executed
@Order(1)
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestTracingFilter implements GlobalFilter {
	
	private final FilterUtility filterUtility; 

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		HttpHeaders requestHeader = exchange.getRequest().getHeaders();
		if(isCorrelationIdPresent(requestHeader)) {
			log.debug("vicheakbank-correlation-id found in RequestTracingFilter {}",
					filterUtility.getCorrelationId(requestHeader));
		} else {
			String correlationId = generateCorrelationId(); 
			exchange = filterUtility.setCorrelationId(exchange, correlationId); 
			log.debug("vicheakbank-correlation-id generated in RequestTracingFilter {}",
					correlationId); 
		}
		
		return chain.filter(exchange);  
	}
	
	private String generateCorrelationId() {
		return UUID.randomUUID().toString(); 
	}
	
	private boolean isCorrelationIdPresent(HttpHeaders requestHeader) {
		if(filterUtility.getCorrelationId(requestHeader) != null) {
			return true; 
		}
		return false;
	}
	
}
