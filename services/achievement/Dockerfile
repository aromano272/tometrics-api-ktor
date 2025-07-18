FROM gradle:8.5-jdk21 AS builder
COPY . /app
WORKDIR /app
RUN gradle :services:achievements:installDist

FROM eclipse-temurin:21-jdk
RUN microdnf install findutils
COPY --from=builder /app/services/achievements/build/install/achievements /app
WORKDIR /app
CMD ["bin/achievements"]