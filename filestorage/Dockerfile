FROM maven:3-jdk-11

WORKDIR /filestorage
COPY . .
RUN mvn clean install

CMD mvn spring-boot:run