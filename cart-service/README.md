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