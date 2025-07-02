FROM gradle:8.5-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN gradle :services:user:installDist

FROM openjdk:17-jdk
RUN microdnf install findutils
COPY --from=builder /app/services/user/build/install/user /app
WORKDIR /app
CMD ["bin/user"]