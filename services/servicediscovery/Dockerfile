FROM gradle:8.5-jdk21 AS builder
COPY . /app
WORKDIR /app
RUN gradle :services:servicediscovery:installDist

FROM eclipse-temurin:21-jdk
RUN microdnf install findutils
COPY --from=builder /app/services/servicediscovery/build/install/servicediscovery /app
WORKDIR /app
CMD ["bin/servicediscovery"]
