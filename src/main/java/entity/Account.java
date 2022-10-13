package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {

    private int id;
    private String name;
    private BigDecimal balance;
    private Currency currency;

    public void withdraw(BigDecimal amount) {
        this.balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.balance = balance.add(amount);
    }
}
