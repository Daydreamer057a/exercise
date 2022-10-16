# Reactive Money Transfer Application

A RESTful API that allows transfer money from one Bank Account to another in any currency.

The API was developed using Java 8 based on Vert.x.

Eclipse Vert.x is a tool-kit for building reactive applications on the JVM.
Eclipse Vert.x is event driven and non blocking. This means your app can handle a lot of concurrency using a small number of kernel threads. Vert.x lets your app scale with minimal hardware.

## Requires
* Java 8
* Maven

## API Definition

### Account
The bank account entity which has balance in the specified currency and could transfer the money
if there is enough money.

#### Structure
    {
        "id"        : <number>,
        "name"      : <string>,
        "balance"   : <BigDecimal>,
        "currency"  : <Currency>
    }

#### Create Bank Account

The following creates bank account and returns the created entity with `ID` specified

    POST /accounts
    {
        "id"        : 12345678
        "name"      : "Account Name - Description",
        "balance"   : 12.6,
        "currency"  : "EUR"
    }

#### List all Bank Accounts

The following gets all the bank accounts that exist in the system

    GET /accounts

#### Get Bank Account details

The following gets the particular account if it exists in the system

    GET /accounts/1


#### Perform a Withdraw or Deposit

The following gets the particular account if it exists in the system

    PUT /accounts/1/deposit/1000
    PUT /accounts/1/withdraw/1000

#### Delete Account

Delete an account from the DB

    DELETE /accounts/1

### Transaction
The money transfer transaction used to initialize the transaction. Once created
will be executed automatically. If transaction can not be created by some reason the Error
will be returned with details in the body.

#### Structure
    {
        "id"            : <number>,
        "fromAccount"   : <number>,
        "toAccount"     : <number>,
        "amount"        : <BigDecimal>,
        "currency"      : <Currency>,
        "status"        : <string - one from "WRONG_DATA", "SUCCESSFUL", "FAILED", "PROCESSING">,
        "description"   : <string>
    }

#### Create a transaction

The following creates a new transaction if possible (valid Bank Accounts and parameters should be provided).
Once `id`, `creationDate`, `updateDate` or `status` provided they  will be ignored.
You can obtain the generated values of these fields in the response of this call.

    POST /transactions
    {
        "fromAccount"   : 2,
        "toAccount"     : 1,
        "amount"        : 16.1,
        "currency"      : "EUR"
    }

#### Get all transactions

    GET /transactions

#### Get a specific transaction by its ID

    GET /transactions/1

#### Get transactions for a certain account

Check transactions where the account number provided is the source account or the destination account.

    GET /transactions/account/2222

### Exception Handing
    If any error will be thrown by some reason the Error will be returned with details in the body.