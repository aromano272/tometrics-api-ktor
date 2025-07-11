FROM gradle:8.5-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN gradle :services:cronjob:installDist

FROM openjdk:17-jdk
RUN microdnf install findutils
COPY --from=builder /app/services/cronjob/build/install/cronjob /app
WORKDIR /app
CMD ["bin/cronjob"]