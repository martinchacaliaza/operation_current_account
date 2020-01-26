FROM openjdk:8
VOLUME /tmp
EXPOSE 8007
ADD ./target/microservicios.mongodb.banco.operation_current_account-0.0.1-SNAPSHOT.jar opercuentascorrientes.jar
ENTRYPOINT ["java","-jar","/opercuentascorrientes.jar"]