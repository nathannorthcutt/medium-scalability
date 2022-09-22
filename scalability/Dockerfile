FROM amazoncorretto:18
LABEL org.opencontainers.image.authors="nathan@telefrek.com"

COPY app/build/libs /tmp/scalability
CMD ["java", "-jar", "/tmp/scalability/scalability.jar"]