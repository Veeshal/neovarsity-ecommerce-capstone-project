# Notification Service

## Microservices Architecture
1. Manages email and potentially other notifications (e.g., SMS).
2. Consumes Kafka messages for events that require user notifications (like registration confirmations, order updates).
3. Integrates with third-party platforms like Amazon SES for actual email delivery.