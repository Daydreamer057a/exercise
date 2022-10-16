package controllers;

import converter.ConvertCurrency;
import enums.TransactionStatus;
import entity.Account;
import entity.Transaction;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static exception.Exception.error;

public class TransactionsImpl implements Transactions {
    private final String JSON_CONTENT_TYPE = "application/json; charset=utf-8";

    /**
     * Get the transactions for a certain account (source or destination account)
     * @param routingContext Represents the context for the handling of a request in Vert.x-Web
     * @param accounts in-memory storage of accounts
     * @param transactions  in-memory storage of transactions
     */
    @Override
    public void getTransactionOfAccount(RoutingContext routingContext, Map<Integer, Account> accounts, Map<Integer, Transaction> transactions) {
        final String id = routingContext.request().getParam("id");
        final int accountNumber = Integer.parseInt(id);
        Account fromAccount = accounts.get(accountNumber);
        if (fromAccount == null) {
            error(routingContext, 404, "Source Account does not exist!");
        }
        else {
            Predicate<Transaction> isSourceAccount = e -> e.getFromAccount() == accountNumber;
            Predicate<Transaction> isDestinationAccount = e -> e.getToAccount() == accountNumber;

            List<Transaction> transactionList = transactions
                .values()
                .stream()
                .filter(isSourceAccount.or(isDestinationAccount))
                .collect(Collectors.toUnmodifiableList());

            routingContext.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE)
                .setStatusCode(200)
                .end(Json.encodePrettily(transactionList));
        }
    }

    /**
     * Get transaction by Id
     * @param routingContext Represents the context for the handling of a request in Vert.x-Web
     * @param transactions  in-memory storage of transactions
     */
    @Override
    public void getTransaction(RoutingContext routingContext, Map<Integer, Transaction> transactions) {
        final String id = routingContext.request().getParam("id");
        final Integer transactionId = Integer.valueOf(id);
        Transaction transaction = transactions.get(transactionId);
        if (transaction == null) {
            error(routingContext, 404, "Transaction not found in the DB: " + id);
        }
        else {
            sendTransactionResponse(routingContext, transaction, 200);
        }
    }

    /**
     * Create a new transaction
     * @param routingContext Represents the context for the handling of a request in Vert.x-Web
     * @param accounts in-memory storage of accounts
     * @param transactions  in-memory storage of transactions
     */
    @Override
    public void newTransaction(RoutingContext routingContext, Map<Integer, Account> accounts, Map<Integer, Transaction> transactions) {
        try {
            final Transaction transaction = Json.decodeValue(routingContext.getBodyAsString(), Transaction.class);
            if (transactions.containsKey(transaction.getId())) {
                error(routingContext, 409, "Transaction already exists in the DB!");
            }

            Account fromAccount = accounts.get(transaction.getFromAccount());
            if (fromAccount == null) {
                error(routingContext, 404, "Source Account does not exist!");
            }
            Account toAccount = accounts.get(transaction.getToAccount());
            if (toAccount == null) {
                error(routingContext, 404, "Destination Account does not exist!");
            }
            BigDecimal amount = transaction.getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
                error(routingContext, 409, "Incorrect transaction amount!");
            }

            BigDecimal amountChange = ConvertCurrency.convertCurrency(routingContext, fromAccount.getCurrency().getCurrencyCode(), toAccount.getCurrency().getCurrencyCode(), amount.toString());

            if (fromAccount != null && toAccount != null && amount!=null && amountChange != null) {
                if (fromAccount.getBalance().compareTo(amount) < 0) {
                    error(routingContext, 409, "Insufficient funds from account! Unable to process the transfer!");
                }

                fromAccount.withdraw(amount);
                toAccount.deposit(amountChange);
                transaction.setStatus(TransactionStatus.SUCCESSFUL);
                transactions.put(transaction.getId(), transaction);
                sendTransactionResponse(routingContext, transaction, 201);
            }
        } catch (RuntimeException exception) {
            error(routingContext, 415, "Unable to parse Transaction JSON request body! Cause: " + exception.getCause());
        }
    }

    /**
     * Get all transactions
     * @param routingContext Represents the context for the handling of a request in Vert.x-Web
     * @param transactions  in-memory storage of transactions
     */
    @Override
    public void getAllTransactions(RoutingContext routingContext, Map<Integer, Transaction> transactions) {
        routingContext.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE)
            .setStatusCode(200)
            .end(Json.encodePrettily(transactions.values()));
    }

    /**
     * Send transaction details to the client as a HttpServerResponse
     * @param routingContext Represents the context for the handling of a request in Vert.x-Web
     * @param transaction  the transaction to return as a JSON to the client
     * @param statusCode http status code
     */
    @Override
    public void sendTransactionResponse(RoutingContext routingContext, Transaction transaction, int statusCode) {
        routingContext.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE)
            .setStatusCode(statusCode)
            .end(Json.encodePrettily(transaction));
    }
}
