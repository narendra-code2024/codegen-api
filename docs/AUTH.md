# Authentication Design

This document describes the authentication architecture for the Codegen API.

## Overview

- **Access token**: Short-lived JWT (15 min), used for authenticating API requests.
- **Refresh token**: Long-lived opaque token (7 days), stored in the database, used to obtain new access tokens. Rotated on every use.

## Token Delivery

All tokens are returned in the JSON body. No httpOnly cookies. The client is responsible for secure token storage (in-memory for SPAs, OS keychain for mobile).

| Detail                      | Mechanism                         |
|:----------------------------|:----------------------------------|
| **Access token delivery**   | JSON body                         |
| **Refresh token delivery**  | JSON body                         |
| **Access token on requests**| `Authorization: Bearer` header    |
| **Refresh on `/refresh`**   | JSON body `{ "refreshToken" }`    |
| **Refresh on `/logout`**    | JSON body `{ "refreshToken" }`    |

## Security

- Access tokens are **never stored in cookies**. All clients send them via the `Authorization: Bearer` header, eliminating CSRF risk.
- Refresh tokens are **rotated on every use** — the old token is deleted and a new one is issued.
- Only one refresh token per user is active at a time (single-session enforcement).

## Standard Flow

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
