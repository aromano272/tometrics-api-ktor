# Tometrics

## Local Development Setup

### Accessing Internal Services

To access internal services using the same URLs from both inside and outside Docker, follow these steps:

1. Add the following entries to your `/etc/hosts` file:

```
127.0.0.1 tometrics-user
127.0.0.1 tometrics-servicediscovery
127.0.0.1 tometrics-socialgraph
127.0.0.1 tometrics-main
```

2. Start the Docker containers:

```bash
docker-compose up -d
```

3. Now you can access the services using the same URLs from both inside and outside Docker:

- User service: `http://tometrics-user/internal/user`
- Service Discovery: `http://tometrics-servicediscovery/internal/servicediscovery`
- Social Graph: `http://tometrics-socialgraph/internal/socialgraph`
- Main service: `http://tometrics-main/internal/main`

This configuration allows you to use the same code for both local development and Docker environments without changing URLs.

## How It Works

The solution works by:

1. Adding server blocks in nginx.conf that listen for requests to service hostnames
2. Adding host entries to map service hostnames to localhost
3. Using nginx as a reverse proxy to route all traffic to the appropriate services

When you make a request to `http://tometrics-user/internal/user` from your local machine:

1. The host entry maps `tometrics-user` to `127.0.0.1`
2. The request goes to nginx running on port 80
3. Nginx matches the server_name `tometrics-user` and forwards the request to the user service
4. The user service processes the request and returns the response

This allows you to use the same URLs in your code regardless of whether it's running inside or outside Docker.
