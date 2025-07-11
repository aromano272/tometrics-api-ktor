FROM gradle:8.5-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN gradle :services:socialfeed:installDist

FROM openjdk:17-jdk
RUN microdnf install findutils
COPY --from=builder /app/services/socialfeed/build/install/socialfeed /app
WORKDIR /app
CMD ["bin/socialfeed"]