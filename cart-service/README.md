# Cart Service

## Functional Requirement
### Cart & Checkout
1. Add to Cart: Users should be able to add products to their cart.
2. Cart Review: View selected items in the cart with price, quantity, and total details.
3. Checkout: Seamless process to finalize the purchase, including specifying delivery address and payment method.

## Microservices Architecture
### Cart Service
1. Manages user's shopping cart.
2. Uses MongoDB for flexibility in cart structures.
3. Uses Redis for fast, in-memory data access (e.g., to quickly retrieve a user’s cart).

## Environment Variables
| Variable             | Description |
|----------------------|-------------|
| AUTH_JWK_ISSUER_URI  | The URI of the JSON Web Key Set (JWK) issuer for validating JWT tokens. |
| MONGO_HOST           | The hostname of the MongoDB server. |
| MONGO_USERNAME       | The username for authenticating with MongoDB. |
| MONGO_PASSWORD       | The password for authenticating with MongoDB. |
| REDIS_HOST           | The hostname of the Redis server. |
| REDIS_PORT           | The port number of the Redis server. |
| REDIS_PASSWORD       | The password for authenticating with Redis. |
| KAFKA_BOOTSTRAP_SERVERS | The bootstrap servers for connecting to the Kafka cluster. |
| KAFKA_TRUSTSTORE_PATH | The file path to the Kafka truststore for SSL connections. |
| KAFKA_KEYSTORE_PATH  | The file path to the Kafka keystore for SSL connections. |
| ORDER_SERVICE_URL    | The URL of the Order Service for processing orders. |
| PRODUCT_SERVICE_URL  | The URL of the Product Service for retrieving product details. |
| USER_SERVICE_URL     | The URL of the User Service for user-related operations. |
| JWT_CLAIM_ROLE       | The claim in the JWT token that specifies the user's role. |
| JWT_CLAIM_USER_ID    | The claim in the JWT token that specifies the user's unique identifier. |
| JWT_CLAIM_EMAIL      | The claim in the JWT token that specifies the user's email address. |