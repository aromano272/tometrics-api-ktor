FROM gradle:8.5-jdk21 AS builder
COPY . /app
WORKDIR /app
RUN gradle :services:socialgraph:installDist

FROM eclipse-temurin:21-jdk
RUN microdnf install findutils
COPY --from=builder /app/services/socialgraph/build/install/socialgraph /app
WORKDIR /app
CMD ["bin/socialgraph"]