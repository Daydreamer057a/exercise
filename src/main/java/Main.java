import controllers.Accounts;
import controllers.AccountsImpl;
import controllers.Transactions;
import controllers.TransactionsImpl;
import converter.ConvertCurrency;
import entity.Account;
import entity.Transaction;
import enums.AccountOperation;
import enums.TransactionStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main extends AbstractVerticle{
    private final Map<Integer, Account> accounts = new LinkedHashMap<>();
    private final Map<Integer, Transaction> transactions = new LinkedHashMap<>();

    @Override
    public void start(Promise<Void> startPromise) {
        insertSampleData();

        /* Endpoints are exposed through a Router*/
        Router router = Router.router(vertx);

        /* Enables the reading of the request body for all routes under /accountsController */
        router.route("/accountsController").handler(BodyHandler.create());

        /* Enables the reading of the request body for all routes under /transactionsController */
        router.route("/transactionsController").handler(BodyHandler.create());

        Accounts accountsController = new AccountsImpl();
        Transactions transactionsController = new TransactionsImpl();

        /* Validate account number (id) */
        router.route("/accountsController/:id").handler(accountsController::parseAccountNumber);

        /* Get all accountsController */
        router.get("/accountsController").handler(routingContext -> accountsController.getAllAccounts(routingContext, accounts));

        /* Post a new account */
        router.post("/accountsController").handler(routingContext -> accountsController.newAccount(routingContext, accounts));

        /* Get account by Id */
        router.get("/accountsController/:id").handler(routingContext -> accountsController.getAccount(routingContext, accounts));

        /* Delete an account */
        router.delete("/accountsController/:id").handler(routingContext -> accountsController.deleteAccount(routingContext, accounts));

        /* Deposit or Withdraw */
        router.put("/accountsController/:id/deposit/:amount").handler(
                routingContext -> accountsController.accountOperation(routingContext, AccountOperation.DEPOSIT, accounts));
        router.put("/accountsController/:id/withdraw/:amount").handler(
                routingContext -> accountsController.accountOperation(routingContext, AccountOperation.WITHDRAW, accounts));

        /* Get all transactionsController */
        router.get("/transactionsController").handler(routingContext -> transactionsController.getAllTransactions(routingContext,transactions));

        /* Post a new transaction */
        router.post("/transactionsController").handler(routingContext -> transactionsController.newTransaction(routingContext,accounts,transactions));

        /* Get transaction by Id */
        router.get("/transactionsController/:id").handler(routingContext -> transactionsController.getTransaction(routingContext,transactions));

        /* Get all transactionsController of a certain account identified with the provided Id */
        router.get("/transactionsController/account/:id").handler(routingContext -> transactionsController.getTransactionOfAccount(routingContext,accounts,transactions));

        /* Check if server runs OK */
        router.get("/health").handler(rc -> rc.response().end("OK"));

        /* Start the HTTP server on port 8080 */
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(
                        8080,
                        result -> {
                            if (result.succeeded()) {
                                startPromise.complete();
                            }
                            else {
                                startPromise.fail(result.cause());
                            }
                        }
                );
    }

    /* Initialize data to test */
    private void insertSampleData() {
        Account account1 = Account.builder()
                .id(1)
                .name("account 1")
                .balance(BigDecimal.valueOf(100))
                .currency(Currency.getInstance("EUR"))
                .build();
        Account account2 = Account.builder()
                .id(2)
                .name("account 2")
                .balance(BigDecimal.valueOf(200))
                .currency(Currency.getInstance("USD"))
                .build();
        Account account3 = Account.builder()
                .id(3)
                .name("account 3")
                .balance(BigDecimal.valueOf(300))
                .currency(Currency.getInstance("GBP"))
                .build();
        accounts.put(1, account1);
        accounts.put(2, account2);
        accounts.put(3, account3);

        Transaction transaction1 = new Transaction(2, 1, BigDecimal.valueOf(12), Currency.getInstance("EUR"));
        Transaction transaction2 = new Transaction(3, 1, BigDecimal.valueOf(34), Currency.getInstance("USD"));
        transaction1.setStatus(TransactionStatus.SUCCESSFUL);
        transaction2.setStatus(TransactionStatus.SUCCESSFUL);
        transaction1.setDescription("test transaction 1");
        transaction2.setDescription("test transaction 2");
        transactions.put(transaction1.getId(), transaction1);
        transactions.put(transaction2.getId(), transaction2);
    }
}
