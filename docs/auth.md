# Authentication Design

This document describes the authentication architecture for the Codegen API.

## Overview

- **Access token**: Short-lived JWT (15 min), used for authenticating API requests.
- **Refresh token**: Long-lived opaque token (7 days), stored in the database, used to obtain new access tokens. Rotated on every use.

## Token Delivery

| Detail                      | Mechanism                         |
|:----------------------------|:----------------------------------|
| **Access token delivery**   | JSON body                         |
| **Refresh token delivery**  | `Set-Cookie` (HttpOnly, Secure)   |
| **Access token on requests**| `Authorization: Bearer` header    |
| **Refresh cookie on request**| Sent automatically by browser    |

## Security

- Refresh tokens are **HttpOnly, Secure cookies** — never accessible to JavaScript.
- Access tokens are **never stored in cookies**. The client keeps them in memory and sends via the `Authorization: Bearer` header, eliminating CSRF risk.
- Refresh tokens are **rotated on every use** — the old token is deleted and a new one is issued.
- Only one refresh token per user is active at a time (single-session enforcement).
- Cookie path is restricted to `/api/auth`.

## Standard Flow

```
1. POST /api/auth/login
   ← Set-Cookie: refresh_token=<token>; HttpOnly; Secure; Path=/api/auth; Max-Age=604800
   ← Body: { "accessToken": "<jwt>" }

2. GET /api/projects  (Authorization: Bearer <accessToken>)
   ← 200 OK

3. POST /api/auth/refresh  (cookie sent automatically)
   ← Set-Cookie: refresh_token=<new-token>; HttpOnly; Secure; Path=/api/auth; Max-Age=604800
   ← Body: { "accessToken": "<new-jwt>" }

4. POST /api/auth/logout  (cookie sent automatically)
   ← Set-Cookie: refresh_token=; Max-Age=0
   ← 204 No Content
```

## Notes

- The client must use `withCredentials: true` (Axios) or `credentials: "include"` (fetch) to send cookies.
- The `refresh` endpoint does not accept a request body. It reads the refresh token from the cookie.
- The `logout` endpoint does not accept a request body. It reads the refresh token from the cookie.
