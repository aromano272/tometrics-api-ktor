FROM gradle:8.5-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN gradle :services:servicediscovery:installDist

FROM openjdk:17-jdk
RUN microdnf install findutils
COPY --from=builder /app/services/servicediscovery/build/install/servicediscovery /app
WORKDIR /app
CMD ["bin/servicediscovery"]
