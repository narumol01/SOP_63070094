package com.example.sop_63070094;

import org.springframework.stereotype.Service;

@Service
public class CalculatorPriceService {
    public double getPrice(double productCost, double productProfit){
        return productCost + productProfit;
    }
}
