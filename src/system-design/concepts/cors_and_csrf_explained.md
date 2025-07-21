# CORS (Cross-Origin Resource Sharing) vs. CSRF (Cross-Site Request Forgery)

CORS and CSRF are two important web security concepts that are often discussed together but address different issues. Understanding their differences is crucial for building secure web applications.

## CORS (Cross-Origin Resource Sharing)

*   **What it is:** CORS is a security mechanism implemented by web browsers. It controls how a web page loaded from **one origin** (defined by its scheme/protocol, domain, and port) can request and interact with resources (like APIs, fonts, images) located at a **different origin**.
*   **The Problem it Solves (or Manages):** By default, web browsers enforce the **Same-Origin Policy (SOP)**. The SOP is a strict security measure that prevents a script on one web page from accessing data on another web page if they don't share the same origin. This is vital to prevent malicious sites from reading sensitive data from other sites you might be logged into (e.g., your online bank or email).
    However, modern web applications often need to legitimately access resources from different origins. For instance, a frontend application hosted at `https://myapp.com` might need to fetch data from an API backend at `https://api.mybackend.com`. CORS provides a controlled way for servers to tell browsers, "It's okay for *this specific* other origin (or *any* other origin) to access my resources."
*   **How it Works:**
    1.  **Client-Side (Browser Initiates):** When JavaScript code (e.g., using `fetch` or `XMLHttpRequest`) in a web page attempts to make a request to a different origin, the browser automatically adds an `Origin` HTTP header to the request. This header indicates the origin of the page making the request (e.g., `Origin: https://frontend.example.com`).
    2.  **Server-Side (Responds to Request):** The server at the target origin (e.g., `https://api.example.com`) receives this request and inspects the `Origin` header.
    3.  Based on its configuration (e.g., a list of allowed origins), the server decides if it wants to allow the request from that specific origin.
    4.  If the request is allowed, the server includes specific **CORS response headers** in its HTTP response. The most critical one is:
        *   `Access-Control-Allow-Origin`: This header specifies which origins are permitted to access the resource.
            *   Example: `Access-Control-Allow-Origin: https://frontend.example.com` (allows only this specific origin)
            *   Example: `Access-Control-Allow-Origin: *` (allows any origin – this is less secure and should be used cautiously, especially if the request involves credentials like cookies).
    5.  **Browser Enforcement:** The browser receives the server's response. If the `Access-Control-Allow-Origin` header is present and its value matches the requesting page's origin (or is `*`), the browser allows the JavaScript code to access the response. If the header is missing, or if its value doesn't permit access, the browser blocks the JavaScript code from accessing the response, typically resulting in a CORS error message in the browser's developer console.
*   **Preflight Requests (`OPTIONS` method):**
    For requests that could potentially modify data on the server (e.g., HTTP methods like `PUT`, `POST` with certain `Content-Type`s, `DELETE`) or requests that include custom HTTP headers, browsers first send an HTTP `OPTIONS` request to the target origin. This is known as a "preflight request."
    The preflight request essentially asks the server for permission *before* sending the actual, more complex request. The server responds to the `OPTIONS` request with CORS headers like:
    *   `Access-Control-Allow-Methods`: Specifies which HTTP methods (e.g., `GET, POST, PUT`) are allowed for the actual request.
    *   `Access-Control-Allow-Headers`: Specifies which HTTP headers can be used in the actual request.
    *   `Access-Control-Max-Age`: Indicates how long the results of a preflight request can be cached by the browser.
    If the server approves the preflight request (by sending appropriate headers), the browser then proceeds to send the actual request.
*   **Key Takeaway:** CORS is primarily a **server-side configuration** that provides instructions to **browsers** on how to handle cross-origin requests. It's about the server explicitly granting permission for its resources to be accessed from other origins.

## CSRF (Cross-Site Request Forgery)

*   **What it is:** CSRF (often pronounced "sea-surf") is an **attack vector** that tricks a victim's web browser into making an unwanted, malicious request to a web application where the victim is already authenticated.
*   **The Problem it Exploits:** Web applications commonly use cookies to maintain user sessions. A fundamental behavior of web browsers is to automatically include any relevant cookies (associated with a domain) with every HTTP request sent to that domain. This happens regardless of where the request originated – whether it was initiated by the user directly on the trusted site, or by a script or form on a completely different (potentially malicious) site.
*   **How an Attack Works (Example):**
    1.  **Victim Logs In:** A user logs into their trusted banking website, `https://mybank.com`. Their browser stores a session cookie for `mybank.com`, which authenticates them for that session.
    2.  **Victim Visits Malicious Site:** Later, without logging out of `mybank.com`, the user visits a malicious website, `https://evil-site.com` (or opens a malicious email with HTML content).
    3.  **Malicious Action Triggered:** `https://evil-site.com` might contain hidden HTML or JavaScript code that automatically causes the victim's browser to send an HTTP request to `https://mybank.com`. For example, it could be a hidden form that submits itself:
        ```html
        <!-- This code is on evil-site.com -->
        <form id="csrf-form" action="https://mybank.com/transfer_funds" method="POST">
          <input type="hidden" name="to_account" value="attacker_account_number">
          <input type="hidden" name="amount" value="10000">
          <input type="submit" value="View my cute kittens!" /> <!-- User might click this, or JS submits it -->
        </form>
        <script>
          // Optionally, the form can be submitted automatically without user interaction
          // document.getElementById('csrf-form').submit();
        </script>
        ```
    4.  **Browser Sends Request with Cookies:** When the victim's browser submits this form to `https://mybank.com` (either by the user clicking something deceptive or automatically via JavaScript), it automatically includes the valid session cookie for `mybank.com` because the request is targeted at that domain.
    5.  **Server Processes Malicious Request:** The `mybank.com` server receives the request. Since the request includes the victim's valid session cookie, the server believes the request is a legitimate action initiated by the authenticated user. It processes the fund transfer to the attacker's account, without the victim's actual consent or knowledge at that moment.
*   **Key Takeaway:** CSRF exploits the trust a web application has in a user's browser, specifically the automatic sending of authentication cookies. The attack leverages an authenticated user's session to perform actions they did not intend.
*   **Mitigation Techniques:** The goal of CSRF mitigation is to ensure that requests are genuinely initiated by the user from within the application.
    *   **Anti-CSRF Tokens (Synchronizer Token Pattern):** This is a common and effective defense.
        *   The server generates a unique, unpredictable, and session-specific token (the anti-CSRF token).
        *   This token is embedded in hidden form fields for traditional HTML forms. For AJAX/API requests (especially those made by single-page applications), the token might be sent in a custom HTTP header.
        *   When the user submits a form or makes a state-changing request, the server checks if the submitted token matches the one it expects for that user's session. If the tokens match, the request is considered legitimate. If the token is missing or incorrect, the server rejects the request.
        *   An attacker cannot easily guess or obtain this token for the victim's active session, so their forged request will lack a valid token.
    *   **SameSite Cookie Attribute:** This cookie attribute provides instructions to the browser about whether cookies should be sent with cross-site requests.
        *   `SameSite=Strict`: Cookies are only sent if the request originates from the same site (domain) as the cookie. This is very effective against CSRF but can sometimes break legitimate cross-site linking if sessions are expected to persist across navigations from external sites.
        *   `SameSite=Lax`: Cookies are sent with top-level navigations (e.g., when a user clicks a link to navigate from an external site to your site) and with GET requests from other sites. However, cookies are *not* sent with cross-site POST, PUT, DELETE requests (which are typically state-changing). `Lax` offers a good balance of security and usability and is the default setting in many modern browsers.
        *   `SameSite=None; Secure`: Cookies are sent with all requests, including cross-site ones. If `SameSite=None` is used, the `Secure` attribute (requiring HTTPS) *must* also be set. This is used for cases where you intentionally need cookies to be sent cross-site (e.g., for embedded content or third-party services).
    *   **Checking `Origin` or `Referer` Headers:**
        *   The server can inspect the `Origin` or `Referer` HTTP headers of incoming requests. If these headers are present, they indicate the source of the request. The server can verify if they match the application's own domain.
        *   This method can be a supplementary defense but is generally less robust than anti-CSRF tokens or SameSite cookies. The `Referer` header can be unreliable (sometimes suppressed for privacy), and while the `Origin` header is more reliable for browser-initiated requests, reliance solely on these headers for CSRF protection is not recommended.
    *   **User Interaction for Sensitive Actions:** For highly sensitive operations (e.g., changing a password, transferring large sums of money), requiring re-authentication (e.g., password entry), CAPTCHAs, or one-time passwords (OTPs) can provide an additional layer of security.

## Summary: CORS vs. CSRF

| Feature         | CORS (Cross-Origin Resource Sharing)                                  | CSRF (Cross-Site Request Forgery)                                       |
| :-------------- | :-------------------------------------------------------------------- | :---------------------------------------------------------------------- |
| **Nature**      | A browser security mechanism; Server-side configuration for granting cross-origin access to its resources. | An attack vector; Exploits browser behavior (automatic cookie sending) with authenticated user sessions.           |
| **Purpose**     | To **allow** legitimate cross-origin requests under controlled conditions.                            | To **prevent** malicious/unwanted requests that are forged by an attacker to be executed in the context of a victim's session.           |
| **Who Manages/Mitigates** | Primarily configured on the **server whose resources are being accessed** (the target origin).                | Primarily mitigated by the **application server that is being protected** from forged requests.      |
| **Analogy**     | A bouncer at a club (server) checking an ID (Origin header from browser) and deciding if someone from another club (different origin) can enter and access resources. | Someone tricking your personal assistant (browser with your session cookies) into sending an authorized email (a state-changing request) on your behalf, without your direct command at that moment. |
