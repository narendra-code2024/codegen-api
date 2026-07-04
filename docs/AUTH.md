# Authentication Design

This document describes the authentication architecture for the Codegen API.

## Overview

- **Access token**: Short-lived JWT (15 min), used for authenticating API requests.
- **Refresh token**: Long-lived opaque token (7 days), stored in the database, used to obtain new access tokens. Rotated on every use.

## Token Delivery Model

A single set of endpoints (`/api/auth/*`) serves both web and mobile clients. The `X-Client-Type` header determines how tokens are delivered:

| Aspect                         | Mobile / API (default)                  | Web (`X-Client-Type: web`)              |
|:-------------------------------|:----------------------------------------|:----------------------------------------|
| **Access token delivery**      | JSON body                               | JSON body                               |
| **Refresh token delivery**     | JSON body                               | `HttpOnly` cookie (path: `/api/auth`)   |
| **Access token on requests**   | `Authorization: Bearer` header          | `Authorization: Bearer` header          |
| **Refresh token on `/refresh`**| JSON body `{ "refreshToken": "..." }`   | Cookie (sent automatically)             |
| **Refresh token on `/logout`** | JSON body `{ "refreshToken": "..." }`   | Cookie (sent automatically)             |

## Security

- Access tokens are **never stored in cookies**. All clients send them via the `Authorization: Bearer` header, eliminating CSRF risk on data endpoints.
- The refresh token `HttpOnly` cookie is scoped to `/api/auth` and uses `SameSite=Strict; Secure`.
- Refresh tokens are **rotated on every use** — the old token is deleted and a new one is issued.
- Only one refresh token per user is active at a time (single-session enforcement).

## Web SPA Flow

```
1. POST /api/auth/login  (X-Client-Type: web)
   ← Body: { "accessToken": "..." }
   ← Set-Cookie: refresh_token=...; HttpOnly; Secure; SameSite=Strict; Path=/api/auth

2. GET /api/projects  (Authorization: Bearer <accessToken>)
   ← 200 OK

3. POST /api/auth/refresh  (X-Client-Type: web)
   → Cookie: refresh_token=... (sent automatically by browser)
   ← Body: { "accessToken": "..." }
   ← Set-Cookie: refresh_token=<new>; ...

4. POST /api/auth/logout
   → Cookie: refresh_token=... (sent automatically by browser)
   ← Set-Cookie: refresh_token=; Max-Age=0
```

## Mobile / API Flow

```
1. POST /api/auth/login
   ← Body: { "accessToken": "...", "refreshToken": "..." }

2. GET /api/projects  (Authorization: Bearer <accessToken>)
   ← 200 OK

3. POST /api/auth/refresh
   → Body: { "refreshToken": "..." }
   ← Body: { "accessToken": "...", "refreshToken": "..." }

4. POST /api/auth/logout
   → Body: { "refreshToken": "..." }
   ← 204 No Content
```
