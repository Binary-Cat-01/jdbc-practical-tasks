package com.walking.jdbc.db;

public class TransactionalData {
    private final Object objectForTransaction;
    private final TransactionalExecutor methodForTransaction;


    public TransactionalData(
            Object objectForTransaction, TransactionalExecutor methodForTransaction) {
        this.objectForTransaction = objectForTransaction;
        this.methodForTransaction = methodForTransaction;
    }

    public Object getObjectForTransaction() {
        return objectForTransaction;
    }

    public TransactionalExecutor getMethodForTransaction() {
        return methodForTransaction;
    }
}
