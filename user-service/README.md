# User Service

## Functional Requirement
### User Management
1. Registration: Allow new users to create an account using their email or social media profiles.
2. Login: Users should be able to securely log in using their credentials.
3. Profile Management: Users should have the ability to view and modify their profile details.
4. Password Reset: Users must have the option to reset their password through a secure link.


### Authentication
1. Secure Authentication: Ensure that user data remains private and secure during login and throughout their session.
2. Session Management: Users should remain logged in for a specified duration or until they decide to log out.


## Microservices Architecture
### User Management Service
1. Handles user registration, login, profile management, and password reset.
2. Uses MySQL as the primary database for structured user data.
3. Uses Kafka to communicate relevant user activities to other services (e.g., a new user registration event can trigger welcome emails or offers).


## Environment Variables
| Variable             | Description                                                                       |
|----------------------|-----------------------------------------------------------------------------------|
| AUTH_JWK_ISSUER_URI  | The URI of the JSON Web Key Set (JWK) issuer for validating JWT tokens.           |
| GOOGLE_CLIENT_IDENTIFIER | The client identifier for Google OAuth2 authentication. |
| TOKEN_DURATION_IN_SECONDS | The duration (in seconds) for which the JWT token remains valid. |
| KAFKA_BOOTSTRAP_SERVERS | The bootstrap servers for connecting to the Kafka cluster.                        |
| KAFKA_TRUSTSTORE_PATH | The file path to the Kafka truststore for SSL connections.                        |
| KAFKA_KEYSTORE_PATH  | The file path to the Kafka keystore for SSL connections.                          |
| DB_HOST             | The hostname of the database server used by the Product Service.             |
| DB_PORT             | The port number of the database server used by the Product Service.          |
| DB_NAME             | The name of the database used by the Product Service.                        |
| DB_USERNAME         | The username for authenticating with the database used by the Product Service. |
| DB_PASSWORD         | The password for authenticating with the database used by the Product Service.|

