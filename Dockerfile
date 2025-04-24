FROM gradle:8.5-jdk17 AS builder
COPY . /app
WORKDIR /app
RUN gradle installDist

FROM openjdk:17-jdk
COPY --from=builder /app/build/install/sproutscout /app
WORKDIR /app
CMD ["bin/sproutscout"]