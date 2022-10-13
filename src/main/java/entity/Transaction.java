package entity;

import enums.TransactionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Transaction {

    private static final AtomicInteger COUNTER = new AtomicInteger();
    private final int id;
    private int fromAccount;
    private int toAccount;
    private BigDecimal amount;
    private Currency currency;
    private String description = "";
    private TransactionStatus status;

    public Transaction() {
        this.id = COUNTER.getAndIncrement();
        this.status = TransactionStatus.PROCESSING;
    }

    public Transaction(int fromAccount, int toAccount, BigDecimal amount, Currency currency) {
        this.id = COUNTER.getAndIncrement();
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.currency = currency;
    }
}
