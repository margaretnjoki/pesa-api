# NOTES_day23

## Difference between `@RestController` and `RestClient`

Although both `@RestController` and `RestClient` are involved in HTTP communication, they do completely different jobs.

### `@RestController`

`@RestController` is used when **my Spring Boot application receives HTTP requests** from another client.

For example:

* A mobile app sends a request to my API.
* Swagger UI sends a request.
* Postman sends a request.

My controller receives the request, calls the service layer, and sends a response back.

In other words, **`@RestController` makes my application act as a server.**

Example:

```java
@RestController
@RequestMapping("/mpesa")
public class MpesaController {

    @PostMapping("/stk-push")
    public StkPushResponse stkPush(...) {
        ...
    }
}
```

Here, my application is waiting for someone to call `/mpesa/stk-push`.

---

### `RestClient`

`RestClient` is the opposite.

It is used when **my Spring Boot application needs to call another server**.

Instead of waiting for requests, it creates and sends HTTP requests.

For example, during an STK Push:

1. My backend asks Safaricom for an access token.
2. My backend sends the STK Push request to Safaricom.
3. Later, Safaricom calls my callback URL.

The first two steps are done using `RestClient`.

Example:

```java
restClient.post()
        .uri("/mpesa/stkpush/v1/processrequest")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .body(request)
        .retrieve()
        .body(StkPushResponse.class);
```

Here, **my backend is acting as the client**, while Safaricom is the server.

---

### Simple way to remember

* `@RestController` → receives requests.
* `RestClient` → sends requests.

One waits for incoming HTTP requests.

The other creates outgoing HTTP requests.

---

# Why the timezone matters for the password hash

When sending an STK Push request, Safaricom requires two fields:

* `Timestamp`
* `Password`

The password is **not typed manually**.

It is generated like this:

```
Password = Base64(
    BusinessShortCode +
    Passkey +
    Timestamp
)
```

This means the timestamp becomes part of the password.

If the timestamp is wrong, the password will also be wrong.

Safaricom expects the timestamp to be in **Kenya time (Africa/Nairobi)** and in this exact format:

```
yyyyMMddHHmmss
```

Example:

```
20260729143025
```

If my application uses another timezone, such as UTC or my computer's local timezone, the timestamp will be different.

Since the timestamp changes, the generated password also changes.

Safaricom generates the expected password using the correct Nairobi timestamp. If my password was generated using a different time, the two passwords will not match, and Safaricom will reject the request.

That is why my code explicitly uses:

```java
ZoneId.of("Africa/Nairobi")
```

This guarantees that the timestamp and the password are generated exactly as Safaricom expects, regardless of where my application is running.

---

## points to remember

* `@RestController` receives HTTP requests.
* `RestClient` sends HTTP requests.
* My backend changes roles depending on the situation:

    * When calling Safaricom, my backend is the **client**.
    * When Safaricom sends the callback, my backend becomes the **server**.
* The STK Push password is generated from the Business Short Code, Passkey, and Timestamp.
* The timestamp **must** use the `Africa/Nairobi` timezone and the format `yyyyMMddHHmmss`.
* A wrong timezone or incorrect timestamp format produces a different password, causing Safaricom to reject the request.
