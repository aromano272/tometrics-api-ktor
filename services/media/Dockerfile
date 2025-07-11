FROM gradle:8.5-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN gradle :services:media:installDist

FROM openjdk:17-jdk
RUN microdnf install findutils
COPY --from=builder /app/services/media/build/install/media /app
WORKDIR /app
CMD ["bin/media"]