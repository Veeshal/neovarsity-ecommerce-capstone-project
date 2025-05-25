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