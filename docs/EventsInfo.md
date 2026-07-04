# Application Events

## User Events
- [ ] user-registered-topic
- [x] password-reset-topic
  * User Service emits this event when a user requests a password reset.
  * User Service generates a password reset token and includes it in the event.
  * User Service emits the event after updating the user's password reset token in the database.
  * Notification Service listens to this topic to send a password reset email to the user with the reset token.
  * Notification Service notifies the user about the password reset request completion.

## Order Events
- [x] order-placed-topic
  * Order Management Service emits this event when an order is placed.
  * Cart Service listens to this topic to clear the cart after an order is placed.
  * Notification Service listens to this topic to send order confirmation emails.
  
## Payment Events
- [x] payment-topic
  * Emitted when a payment is successful, failed, or refunded.
  * Order Management Service listens to this topic to update order status accordingly.
