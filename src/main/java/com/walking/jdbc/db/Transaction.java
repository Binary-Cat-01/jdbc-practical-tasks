package com.walking.jdbc.db;

public class Transaction {
    private final Object object;
    private final Transactional method;


    public Transaction(
            Object object, Transactional method) {
        this.object = object;
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public Transactional getMethod() {
        return method;
    }
}
