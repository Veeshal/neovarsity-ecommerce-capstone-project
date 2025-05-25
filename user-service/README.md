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
