# Product Service

## Functional Requirement
### Product Catalog
1. Browsing: Users should be able to browse products by different categories.
2. Product Details: Detailed product pages with product images, descriptions, specifications, and other relevant information.
3. Search: Users must be able to search for products using keywords.


## Microservices Architecture
### Product Catalog Service
1. Manages product listings, details, categorization.
2. Uses MySQL.
3. Incorporates Elasticsearch for fast product searches, providing features like full-text search and typo correction.

## Environment Variables
| Variable             | Description                                                                       |
|----------------------|-----------------------------------------------------------------------------------|
| AUTH_JWK_ISSUER_URI  | The URI of the JSON Web Key Set (JWK) issuer for validating JWT tokens.           |
| KAFKA_BOOTSTRAP_SERVERS | The bootstrap servers for connecting to the Kafka cluster.                        |
| KAFKA_TRUSTSTORE_PATH | The file path to the Kafka truststore for SSL connections.                        |
| KAFKA_KEYSTORE_PATH  | The file path to the Kafka keystore for SSL connections.                          |
| ELASTICSEARCH_URIS | The URIs of the Elasticsearch cluster for product search. |
| ELASTICSEARCH_USERNAME | The username for authenticating with the Elasticsearch cluster. |
| ELASTICSEARCH_PASSWORD | The password for authenticating with the Elasticsearch cluster. |
| DB_HOST             | The hostname of the database server used by the Product Service.             |
| DB_PORT             | The port number of the database server used by the Product Service.          |
| DB_NAME             | The name of the database used by the Product Service.                        |
| DB_USERNAME         | The username for authenticating with the database used by the Product Service. |
| DB_PASSWORD         | The password for authenticating with the database used by the Product Service.|

