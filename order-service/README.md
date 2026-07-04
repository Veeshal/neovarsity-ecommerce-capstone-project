# Order Service

## Functional Requirement
### Order Management
1. Order Confirmation: After making a purchase, users should receive a confirmation with order details.
2. Order History: Users should be able to view their past orders.
3. Order Tracking: Provide users with a way to track their order's delivery status.

## Microservices Architecture
### Order Management Service
1. Handles order processing, history, and tracking.
2. Uses MySQL.
3. Communicates with Payment Service and User Management Service through Kafka for order status updates, payment verifications, etc.

## Environment Variables
| Variable             | Description                                                                       |
|----------------------|-----------------------------------------------------------------------------------|
| AUTH_JWK_ISSUER_URI  | The URI of the JSON Web Key Set (JWK) issuer for validating JWT tokens.           |
| KAFKA_BOOTSTRAP_SERVERS | The bootstrap servers for connecting to the Kafka cluster.                        |
| KAFKA_TRUSTSTORE_PATH | The file path to the Kafka truststore for SSL connections.                        |
| KAFKA_KEYSTORE_PATH  | The file path to the Kafka keystore for SSL connections.                          |
| JWT_CLAIM_ROLE       | The claim in the JWT token that specifies the user's role.                        |
| JWT_CLAIM_USER_ID    | The claim in the JWT token that specifies the user's unique identifier.           |
| JWT_CLAIM_EMAIL      | The claim in the JWT token that specifies the user's email address.               |
| DB_HOST             | The hostname of the database server used by the Notification Service.             |
| DB_PORT             | The port number of the database server used by the Notification Service.          |
| DB_NAME             | The name of the database used by the Notification Service.                        |
| DB_USERNAME         | The username for authenticating with the database used by the Notification Service. |
| DB_PASSWORD         | The password for authenticating with the database used by the Notification Service. |
| PAYMENT_SERVICE_URL | The URL of the Payment Service for processing payments. |
| PRODUCT_SERVICE_URL  | The URL of the Product Service for retrieving product details. |
| USER_SERVICE_URL     | The URL of the User Service for user-related operations. |