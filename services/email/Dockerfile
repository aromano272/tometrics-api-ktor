FROM gradle:8.5-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN gradle :services:email:installDist

FROM openjdk:17-jdk
RUN microdnf install findutils
COPY --from=builder /app/services/email/build/install/email /app
WORKDIR /app
CMD ["bin/email"]