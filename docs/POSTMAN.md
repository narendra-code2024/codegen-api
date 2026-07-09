# Postman Testing Guide

How to test authentication flows in Postman.

## Setup

Create a Postman Environment with variables `accessToken` and `refreshToken`.

All authenticated requests use `Authorization: Bearer {{accessToken}}` header.

## Flows

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
if (res.accessToken) pm.environment.set("accessToken", res.accessToken);
if (res.refreshToken) pm.environment.set("refreshToken", res.refreshToken);
```

**Refresh** — `POST /api/auth/refresh`
Body:
```json
{
  "refreshToken": "{{refreshToken}}"
}
```
Post-response script: same as login.

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
