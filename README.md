# Pesa API

A backend REST API built with Spring Boot that integrates with **Safaricom's M-Pesa Daraja API** to initiate STK Push payments, receive asynchronous callbacks, and persist transaction data in PostgreSQL.

This project was built as part of my backend engineering learning roadmap to gain hands-on experience with third-party API integration, asynchronous workflows, cloud deployment, and secure backend development.

---

## Features

* Generate and cache OAuth access tokens from the Daraja API
* Initiate Lipa Na M-Pesa Online (STK Push) requests
* Receive and process asynchronous payment callbacks
* Persist payment transactions in PostgreSQL
* Update transaction status automatically (PENDING, SUCCESS, FAILED)
* Store callback payloads for auditing and troubleshooting
* Database versioning with Flyway migrations
* REST API documentation using Swagger/OpenAPI
* Dockerized application
* Deployed on Render with PostgreSQL hosted on Railway

---

## Tech Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* PostgreSQL
* Flyway
* Docker
* Maven
* Swagger / OpenAPI
* Render
* Railway
* Safaricom Daraja API

---

## Project Structure

```text
src
├── controller
├── service
├── repository
├── model
├── dto
├── mpesa
├── config
└── resources
```

---

## API Endpoints

| Method | Endpoint                                  | Description                                       |
| ------ | ----------------------------------------- | ------------------------------------------------- |
| POST   | `/mpesa/stk-push`                         | Initiate an STK Push request                      |
| POST   | `/mpesa/callback`                         | Receive payment callback from Safaricom           |
| GET    | `/mpesa/token-status`                     | Check whether the OAuth token is currently cached |
| GET    | `/mpesa/transactions`                     | Retrieve all payment transactions                 |
| GET    | `/mpesa/transactions/{id}`                | Retrieve a transaction by ID                      |
| GET    | `/mpesa/transactions/phone/{phoneNumber}` | Retrieve transactions for a phone number          |

---

## Payment Flow

1. A client sends an STK Push request to the API.
2. The backend requests an OAuth access token from Safaricom if one is not already cached.
3. The access token is cached until shortly before it expires.
4. The backend sends an STK Push request to Safaricom.
5. Safaricom responds immediately to acknowledge receipt of the request.
6. The customer receives the M-Pesa payment prompt on their phone.
7. After the customer enters their PIN, Safaricom sends an asynchronous callback to the application.
8. The callback updates the transaction in the database with its final status and receipt number.

---

## Database

Flyway is used for database versioning.

The initial migration creates the `mpesa_transactions` table used to store:

* Phone number
* Amount
* Transaction status
* Merchant Request ID
* Checkout Request ID
* M-Pesa receipt number
* Result code
* Result description
* Callback payload
* Created and updated timestamps

---

## Security

Sensitive values are **never committed to source control**.

The application loads configuration from environment variables, including:

* `DATABASE_URL`
* `DATABASE_USERNAME`
* `DATABASE_PASSWORD`
* `MPESA_CONSUMER_KEY`
* `MPESA_CONSUMER_SECRET`
* `MPESA_SHORTCODE`
* `MPESA_PASSKEY`
* `MPESA_CALLBACK_URL`

No API keys, passwords, or secrets are stored in the repository.

---

## Running the Project

Clone the repository:

```bash
git clone https://github.com/<margaretnjoki>/pesa-api.git
cd pesa-api
```

Configure the required environment variables.

Run the application:

```bash
./mvnw spring-boot:run
```

Or build the project:

```bash
./mvnw clean package
```

---

## API Documentation

Swagger UI

```
https://pesa-api.onrender.com/swagger-ui/index.html
```

---

## Deployment

* **Application:** Render
* **Database:** Railway PostgreSQL



## What I Learned

Building this project helped me gain practical experience with:

* REST API development using Spring Boot
* OAuth authentication
* Access token caching
* Third-party API integration
* Asynchronous callback processing
* PostgreSQL persistence with Spring Data JPA
* Database migrations using Flyway
* Docker containerization
* Cloud deployment
* Secure configuration using environment variables
* Logging and debugging distributed systems


## Future Improvements

* JWT authentication and authorization
* Pagination and filtering for transactions
* Integration tests
* Payment reversal support
* Transaction search by receipt number
* Monitoring and metrics
* Rate limiting

---

## Author

**Margaret Njoki**

Computer Science Student | Backend Developer

I'm documenting my backend engineering journey by building real-world APIs and learning modern backend development practices one project at a time.

---

## Links

* GitHub Repository: https://github.com/margaretnjoki/pesa-api
* Live API: https://pesa-api.onrender.com
* Swagger UI:  https://pesa-api.onrender.com/swagger-ui/index.html
