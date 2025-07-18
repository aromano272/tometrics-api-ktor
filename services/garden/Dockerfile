FROM gradle:8.5-jdk21 AS builder
COPY . /app
WORKDIR /app
RUN gradle :services:garden:installDist

FROM eclipse-temurin:21-jdk
RUN microdnf install findutils
COPY --from=builder /app/services/garden/build/install/garden /app
WORKDIR /app
CMD ["bin/garden"]