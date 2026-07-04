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


## Environment Variables
| Variable             | Description                                                                       |
|----------------------|-----------------------------------------------------------------------------------|
| AUTH_JWK_ISSUER_URI  | The URI of the JSON Web Key Set (JWK) issuer for validating JWT tokens.           |
| KAFKA_BOOTSTRAP_SERVERS | The bootstrap servers for connecting to the Kafka cluster.                        |
| KAFKA_TRUSTSTORE_PATH | The file path to the Kafka truststore for SSL connections.                        |
| KAFKA_KEYSTORE_PATH  | The file path to the Kafka keystore for SSL connections.                          |
| STRIPE_SECRET_KEY     | The secret key for Stripe API to process payments.                                 |
| STRIPE_PUBLISHABLE_KEY | The publishable key for Stripe API to process payments.                              |
| STRIPE_WHSECRET     | The webhook secret for Stripe to verify incoming webhook events.                   |
| RAZORPAY_KEY | The key for Razorpay API to process payments.                                      |
| RAZORPAY_SECRET_KEY | The secret key for Razorpay API to process payments.                                 |
| RAZORPAY_WHSECRET | The webhook secret for Razorpay to verify incoming webhook events.                   |
| PAYMENT_REDIRECT_URL | The URL to redirect users after a successful payment. |