# Application Events

## User Events
- [ ] user-registered-topic
- [x] password-reset-topic
  * User Service emits this event when a user requests a password reset.
  * Notification Service listens to this topic to send a password reset email.

## Cart Events
- [ ] cart-item-added-topic
- [ ] cart-item-removed-topic, 
- [ ] cart-cleared-topic

## Product Events
- [ ] product-added-topic
- [ ] product-updated-topic, 
- [ ] product-deleted-topic, 
- [ ] product-stock-updated-topic

## Order Events

- [x] order-placed-topic
  * Cart Service listens to this topic to clear the cart after an order is placed.
- [ ] order-cancelled-topic
- [ ] order-shipped-topic
- [ ] order-delivered-topic

## Payment Events
- [ ] payment-success-topic
- [ ] payment-failure-topic

## Notification Events
- [ ] notification-sent-topic
- [ ] notification-failed-topic
