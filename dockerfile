FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/otel.jar

COPY target/paymentauth-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-javaagent:/app/otel.jar","-Dotel.service.name=paymentauth","-Dotel.metrics.exporter=otlp","-Dotel.traces.exporter=otlp","-Dotel.logs.exporter=otlp","-jar","/app/app.jar"]