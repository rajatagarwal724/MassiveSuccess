# JSON Web Tokens (JWTs), Access Tokens, and Refresh Tokens

## How JSON Web Tokens (JWTs) Work

JSON Web Tokens (JWTs) are a compact, URL-safe means of representing claims to be transferred between two parties. They are commonly used for authentication and authorization in web applications and APIs.

### Structure of a JWT

A JWT consists of three parts, separated by dots (`.`):

1.  **Header:** Contains metadata about the token, such as the type of token (JWT) and the signing algorithm being used (e.g., HMAC SHA256 or RSA).
    ```json
    {
      "alg": "HS256", // Algorithm
      "typ": "JWT"    // Type
    }
    ```
    This JSON is Base64Url encoded to form the first part of the JWT.

2.  **Payload (Claims):** Contains the claims. Claims are statements about an entity (typically, the user) and additional data. There are three types of claims:
    *   **Registered claims:** A set of predefined claims which are not mandatory but recommended, such as `iss` (issuer), `exp` (expiration time), `sub` (subject), `aud` (audience).
    *   **Public claims:** These can be defined at will by those using JWTs. But to avoid collisions, they should be defined in the IANA JSON Web Token Registry or be defined as a URI that contains a collision resistant namespace.
    *   **Private claims:** These are custom claims created to share information between parties that agree on using them and are neither registered nor public claims.

    ```json
    {
      "sub": "1234567890", // Subject (e.g., user ID)
      "name": "John Doe",
      "admin": true,
      "iat": 1516239022, // Issued at (timestamp)
      "exp": 1516242622  // Expiration time (timestamp)
    }
    ```
    This JSON is Base64Url encoded to form the second part of the JWT.
    **Important:** The payload is encoded, not encrypted. Anyone can decode it, so don't put sensitive information in the payload unless it's encrypted separately.

3.  **Signature:** To create the signature part, you have to take the encoded header, the encoded payload, a secret (if using HMAC algorithms) or a private key (if using RSA/ECDSA), and sign it using the algorithm specified in the header.
    For example, if you want to use the HMAC SHA256 algorithm, the signature will be created in the following way:
    ```
    HMACSHA256(
      base64UrlEncode(header) + "." +
      base64UrlEncode(payload),
      secretOrPrivateKey
    )
    ```
    The signature is used to verify that the sender of the JWT is who it says it is and to ensure that the message wasn't changed along the way.

**Putting it all together:** `xxxxx.yyyyy.zzzzz` (EncodedHeader.EncodedPayload.Signature)

### How JWT Authentication Works (Typical Flow):

1.  **User Login:** The user provides their credentials (e.g., username and password) to the authentication server.
2.  **Verification:** The server verifies the credentials.
3.  **JWT Creation & Issuance:**
    *   If credentials are valid, the server generates a JWT.
    *   It creates the header and payload.
    *   It signs the token using a secret key (for symmetric algorithms like HS256) or a private key (for asymmetric algorithms like RS256) that is known only to the server.
    *   The server sends this JWT back to the client (e.g., in the HTTP response body).
4.  **Client Stores JWT:** The client (e.g., browser or mobile app) receives the JWT and stores it locally (e.g., `localStorage`, `sessionStorage`, or HTTP-only cookies).
5.  **Client Sends JWT with Requests:** For subsequent requests to protected routes or resources, the client includes the JWT in the `Authorization` header using the `Bearer` schema:
    ```
    Authorization: Bearer <token>
    ```
6.  **Server Verifies JWT:**
    *   When the server receives a request with a JWT, it first extracts the token.
    *   It then verifies the token's signature using the appropriate key (secret or public).
    *   It also checks standard claims like `exp` (expiration time).
7.  **Access Granted/Denied:**
    *   If the signature is valid and claims are acceptable, the server trusts the claims and processes the request.
    *   Otherwise, the server rejects the request (e.g., `401 Unauthorized`).

### Key Advantages of JWTs:

*   **Stateless:** Server doesn't need to store session info. JWT contains necessary info.
*   **Self-contained:** Payload can carry user identity/permissions, reducing DB lookups.
*   **Portability:** Easily passed between services (e.g., microservices).
*   **Security (when used correctly):** Signature ensures integrity and authenticity.

### Important Security Considerations:

*   **Always use HTTPS.**
*   **Keep secrets/private keys secure.**
*   **Set token expiration (`exp` claim).**
*   **Don't put sensitive data in the payload (it's only encoded).**
*   **Validate all relevant claims.**
*   **Prevent token replay if necessary.**
*   **Secure client-side storage.**

---

## Access Tokens and Refresh Tokens with JWTs

The Access Token / Refresh Token pattern is a standard security strategy used with JWTs to balance user experience and security. It addresses the drawback of a single, long-lived JWT: if stolen, it can be used by an attacker for a long time.

### The Two Types of Tokens

1.  **Access Token (JWT)**
    *   **Purpose:** To access protected resources (APIs). This is the token you send with every API request.
    *   **Lifespan:** **Very short** (e.g., 5-15 minutes).
    *   **Content:** It's a standard JWT containing user identity (`sub`), permissions (`scope` or custom claims), and a short expiration time (`exp`).
    *   **Security:** Because it's sent frequently, it has a higher risk of being intercepted. Its short lifespan minimizes the damage if it is stolen.
    *   **Statelessness:** The resource server can verify it without contacting the authentication server.

2.  **Refresh Token (JWT or Opaque String)**
    *   **Purpose:** **Only** to get a new access token. It is *never* sent to a resource API.
    *   **Lifespan:** **Long** (e.g., 7 days, 30 days, or even longer).
    *   **Content:** It typically contains information identifying the user and the session, and its own long expiration time.
    *   **Security:** It is sent very infrequently and only to a dedicated token endpoint. It must be stored securely on the client (e.g., in an `HttpOnly` cookie).
    *   **Statefulness (Often):** The authentication server often keeps a record of issued refresh tokens. This allows a specific session to be **revoked** on the server side.

### The Authentication Flow with Access/Refresh Tokens

**Step 1: Initial Login**
1.  User submits credentials.
2.  Authentication server validates them.
3.  Server generates both a short-lived `accessToken` and a long-lived `refreshToken`.
4.  Server may store the `refreshToken` (or a reference) in its database.
5.  Both tokens are sent to the client.

**Step 2: Making API Calls**
1.  Client stores both tokens.
2.  Client includes the `accessToken` in the `Authorization: Bearer <accessToken>` header for API calls.
3.  API server validates the `accessToken` (signature, expiration).

**Step 3: The Access Token Expires**
1.  The `accessToken` expires (e.g., after 15 minutes).
2.  Client makes an API call with the expired `accessToken`.
3.  API server rejects with `401 Unauthorized`.

**Step 4: Seamlessly Refreshing the Session**
1.  Client's logic catches the `401`.
2.  Client makes a request to the token refresh endpoint (e.g., `POST /api/auth/refresh`), sending its `refreshToken`.
3.  Authentication server validates the `refreshToken` (signature, expiration, existence in DB).
4.  If valid, the server generates a **new `accessToken`** (and optionally a new `refreshToken` - token rotation) and sends it back.

**Step 5: Retrying the Failed API Call**
1.  Client receives the new `accessToken` and replaces the old one.
2.  Client automatically retries the originally failed API call with the new `accessToken`.
3.  The API call now succeeds. The user experience is seamless.

### Why This Pattern is Powerful

*   **Enhanced Security:** Short-lived `accessToken` limits damage if stolen. `refreshToken` is exposed less.
*   **Great User Experience:** Users remain logged in for long periods without re-authenticating.
*   **Server-Side Control & Revocation:** `refreshTokens` can be invalidated on the server (e.g., by deleting from DB), effectively ending a session immediately.

---

## JWT Generation for B2B Applications (Client Credentials Grant)

For Business-to-Business (B2B) applications where a customer's server-side application needs to authenticate to access your APIs, the OAuth 2.0 Client Credentials Grant flow is commonly used.

**Participants:**

1.  **Your Application (Authorization Server & Resource Server):**
    *   **Authorization Server:** Authenticates the B2B client application and issues JWTs.
    *   **Resource Server:** Hosts protected APIs.
2.  **B2B Customer's Application (Client Application):** The customer's server-side application.

**Process (OAuth 2.0 Client Credentials Grant Flow):**

**Step 1: Client Registration (One-time Setup)**
*   You register the B2B customer's application.
*   Provide them with:
    *   `client_id`: Public unique identifier.
    *   `client_secret`: Confidential secret (like a password for their application). Must be stored securely by the customer.

**Step 2: Customer's Application Requests an Access Token (JWT)**
1.  The B2B application needs an access token to call your APIs.
2.  It makes a POST request to your token endpoint (e.g., `/oauth/token`) over HTTPS.
3.  It authenticates itself using `client_id` and `client_secret` (e.g., via HTTP Basic Auth or request body parameters).
4.  The `grant_type` is `client_credentials`.

**Step 3: Your Authorization Server Validates and Issues a JWT**
1.  Your server validates `client_id` and `client_secret`.
2.  If valid, it generates an Access Token (JWT):
    *   **Header:** `{"alg": "HS256" or "RS256", "typ": "JWT"}`
    *   **Payload:**
        *   `sub`: `client_id` (identifies the B2B customer).
        *   `iss`: Your server's identifier.
        *   `aud`: Your API's identifier.
        *   `exp`: Short expiration time (e.g., 1 hour).
        *   `iat`: Issued at timestamp.
        *   `jti`: JWT ID (unique token identifier).
        *   Custom claims (e.g., `tenant_id`, API scopes).
    *   **Signature:**
        *   **HS256 (Symmetric):** Signed with a single secret key known *only* to your authorization and resource servers. **This is NOT the `client_secret`.**
        *   **RS256 (Asymmetric):** Signed with your server's **private key**. Your resource servers use the corresponding **public key** to verify.
3.  The JWT is sent back to the B2B customer's application.

**Step 4: Customer's Application Uses the JWT**
1.  The B2B application receives the JWT.
2.  It includes the JWT in the `Authorization: Bearer <jwt>` header when calling your APIs.
3.  The B2B application treats the JWT as opaque; it doesn't decrypt or modify it.

**Step 5: Your Resource Server (API) Verifies the JWT**
1.  Your API receives the request with the JWT.
2.  It verifies the JWT's signature (using the shared secret for HS256 or the public key for RS256).
3.  It validates claims (`exp`, `aud`, `iss`, etc.).
4.  If valid, the API processes the request. Otherwise, it returns `401 Unauthorized` or `403 Forbidden`.

**Key Principles:**
*   **Server Signs:** Your server creates and signs the JWT. The client never signs it.
*   **Client Uses As-Is:** The client uses the received JWT without modification until it expires.
*   **HTTPS is Mandatory:** All communication involving secrets and tokens must be over HTTPS.
*   **Client Secret for Authentication:** The `client_secret` is used by the client application to authenticate itself to your authorization server when requesting a JWT. It's not directly used in the JWT's signing or verification by the resource server.
*   **Payload Encoding, Not Encryption (Default):** JWT payload is Base64Url encoded. The signature protects against tampering, not against viewing the payload.

---

## Mitigating Compromised JWTs in B2B Scenarios

If a B2B customer's JWT (or more likely, their `client_secret` leading to JWT issuance) is compromised:

**1. Immediate Actions:**

*   **Rotate `client_secret`:**
    *   This is the **most effective immediate step** for the Client Credentials flow.
    *   Issue a new `client_secret` to the B2B customer and invalidate the old one on your authorization server.
    *   This prevents the compromised party from obtaining *new* JWTs.
    *   *Limitation:* Doesn't invalidate already issued, unexpired JWTs.

*   **Token Denylist / Blocklist (If Implemented):**
    *   Maintain a list (e.g., in Redis) of JWT IDs (`jti` claim) or `client_id`s whose tokens are considered invalid.
    *   Your resource servers check this list before processing requests.
    *   Add the `jti` of compromised tokens or the affected `client_id` to this list.
    *   *Trade-off:* Introduces some statefulness. Entries should persist at least until the token's natural expiration.

**2. Rely on Short Token Expiration (Preventative & Mitigating):**

*   Access tokens should have a short lifespan (e.g., 30 minutes to a few hours).
*   This naturally limits the window of opportunity for an attacker if a token is compromised.
*   Once expired, the attacker needs the (now rotated) `client_secret` to get a new token.

**3. Monitoring and Alerting:**

*   **Audit Logs:** Log token issuance (client_id, IP, timestamp) and API access.
*   **Anomaly Detection:** Monitor for unusual request patterns (spikes, unexpected IPs/geolocations for a `client_id`), failed authentications.
*   **Rate Limiting:** On token endpoint and APIs.

**4. Communication with Affected Customer:**

*   Inform the B2B customer immediately.
*   Guide them through securing their systems and using the new `client_secret`.
*   Advise them to review their logs.

**5. Advanced & Long-Term Strategies:**

*   **IP Address Pinning/Restrictions:** Allow customers to whitelist IPs for accessing your token endpoint/APIs.
*   **Mutual TLS (mTLS) for Token Endpoint:** Client application authenticates with its own X.509 certificate in addition to `client_id`/`client_secret`.
*   **Continuous Access Evaluation Protocol (CAEP):** Emerging standard for real-time communication of security events like token revocation.

**Summary of Mitigation:**
1.  **Primary:** Force `client_secret` rotation.
2.  **Secondary:** Use a token denylist.
3.  **Rely On:** Short token expiration.
4.  **Monitor & Alert.**
5.  **Communicate.**
