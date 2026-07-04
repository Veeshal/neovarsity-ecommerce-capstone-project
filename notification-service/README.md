# Notification Service

## Microservices Architecture
1. Manages email and potentially other notifications (e.g., SMS).
2. Consumes Kafka messages for events that require user notifications (like registration confirmations, order updates).
3. Integrates with third-party platforms like Amazon SES for actual email delivery.

## Environment Variables
| Variable   | Description                                                                       |
|------------|-----------------------------------------------------------------------------------|
| AWS_REGION | The AWS region for services like SES and SNS.                                      |
| JWT_CLAIM_ROLE       | The claim in the JWT token that specifies the user's role.                        |
| JWT_CLAIM_USER_ID    | The claim in the JWT token that specifies the user's unique identifier.           |
| JWT_CLAIM_EMAIL      | The claim in the JWT token that specifies the user's email address.               |
| EMAIL_STRATEGY      | The strategy for sending emails (e.g., AWS_MAIL_STRATEGY).        |
| EMAIL_FROM      | The email address used as the sender for outgoing emails.                         |
| SMS_STRATEGY      | The strategy for sending SMS messages (e.g., AWS_SMS_STRATEGY).                   |
| SMS_SENDER_ID     | The sender ID used for outgoing SMS messages.                                       |
| AUTH_JWK_ISSUER_URI  | The URI of the JSON Web Key Set (JWK) issuer for validating JWT tokens.           |
| DB_HOST             | The hostname of the database server used by the Notification Service.             |
| DB_PORT             | The port number of the database server used by the Notification Service.          |
| DB_NAME             | The name of the database used by the Notification Service.                        |
| DB_USERNAME         | The username for authenticating with the database used by the Notification Service. |
| DB_PASSWORD         | The password for authenticating with the database used by the Notification Service. |
| KAFKA_BOOTSTRAP_SERVERS | The bootstrap servers for connecting to the Kafka cluster.                        |
| KAFKA_TRUSTSTORE_PATH | The file path to the Kafka truststore for SSL connections.                        |
| KAFKA_KEYSTORE_PATH  | The file path to the Kafka keystore for SSL connections.                          |
