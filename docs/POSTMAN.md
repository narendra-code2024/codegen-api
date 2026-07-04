# Postman Testing Guide

How to test authentication flows in Postman.

## Setup

Create a Postman Environment with variables `accessToken` and `refreshToken`.

All authenticated requests use `Authorization: Bearer {{accessToken}}` header.


## 1. Mobile / API (Default)

**Login** — `POST /api/auth/login`
Body:
```json
{
  "email": "test@example.com",
  "password": "password123"
}
```
Post-response script:
```javascript
var res = pm.response.json();
if (res.data) {
    if (res.data.accessToken) pm.environment.set("accessToken", res.data.accessToken);
    if (res.data.refreshToken) pm.environment.set("refreshToken", res.data.refreshToken);
}
```

**Refresh** — `POST /api/auth/refresh`
Body:
```json
{
  "refreshToken": "{{refreshToken}}"
}
```
Post-response script: same as login.

**Me** — `GET /api/auth/me`
No body needed. Returns current user profile.

**Logout** — `POST /api/auth/logout`
Body:
```json
{
  "refreshToken": "{{refreshToken}}"
}
```
Post-response script:
```javascript
pm.environment.unset("accessToken");
pm.environment.unset("refreshToken");
```

---

## 2. Web (SPA)

Add `X-Client-Type: web` header to all auth requests.

**Login** — `POST /api/auth/login`
Response body contains only `accessToken`. Refresh token is set as an `HttpOnly` cookie.
Post-response script:
```javascript
var res = pm.response.json();
if (res.data && res.data.accessToken) {
    pm.environment.set("accessToken", res.data.accessToken);
}
```

**Refresh** — `POST /api/auth/refresh`
No body needed — Postman sends the `refresh_token` cookie automatically.
Post-response script: same as web login.

**Logout** — `POST /api/auth/logout`
Server clears the `refresh_token` cookie.
Post-response script:
```javascript
pm.environment.unset("accessToken");
```
