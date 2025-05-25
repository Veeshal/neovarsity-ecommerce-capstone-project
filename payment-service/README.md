# Payment Service

## Functional Requirement
### Payment
1. Multiple Payment Options: Support for credit/debit cards, online banking, and other popular payment methods.
2. Secure Transactions: Ensure user trust by facilitating secure payment transactions.
3. Payment Receipt: Provide users with a receipt after a successful payment.

## Microservices Architecture
### Payment Service
1. Manages payment gateways and transaction logs.
2. Uses MySQL.
3. Once the payment is confirmed, it produces a message on Kafka to notify the Order Management Service.