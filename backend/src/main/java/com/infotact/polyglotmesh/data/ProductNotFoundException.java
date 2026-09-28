package com.infotact.polyglotmesh.data;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException() {
        super("Product was not found in the mock catalog.");
    }
}