# Dropwizard 2.x with OAuth 2 Demo

Simple demo using the [OAuth 2 mechanism in Dropwizard 2.x](https://www.dropwizard.io/en/latest/manual/auth.html) to
protect an endpoint. It goes without saying that this is not production-ready code, m'kay?

Inspired by the Dropwizard 0.x demo [dropwizard-oauth2-provider](https://github.com/remmelt/dropwizard-oauth2-provider).

## How to build
```
mvn clean package
```

## How to run
```
java -jar target/dropwizard2-oauth2-1.0.0-SNAPSHOT.jar server config.yml
```

## How to test

Just ping it without any authentication:
```
curl localhost:8080/ping
{"message":"pong"}
```
Try to access protected endpoint (requires access token and role ADMIN):
```
curl localhost:8080/ping/auth
Credentials are required to access this resource.
```
Get an access token for existing user "alice" (has role ADMIN):
```
curl -d "grant_type=password&username=alice&password=secret&client_id=1" localhost:8080/oauth2/token
dc7bc015-10ea-4051-a27a-a2680e2d7396
```
Use the access token to access the protected endpoint:
```
 curl -H "Authorization: Bearer dc7bc015-10ea-4051-a27a-a2680e2d7396" localhost:8080/ping/auth
{"message":"Authenticated pong for user alice"}
```
Now get token for existing user "bob" (has role PARIAH):
```
 curl -d "grant_type=password&username=bob&password=secret&client_id=1" localhost:8080/oauth2/token
68eb946b-5712-4d90-a8d0-f2e81959ff8c
```
Try to access the protected endpoint with bob's token:
```
curl -H "Authorization: Bearer 68eb946b-5712-4d90-a8d0-f2e81959ff8c" localhost:8080/ping/auth
{"code":403,"message":"User not authorized."}
```
Sorry, bob. No can do.

That's it folks!