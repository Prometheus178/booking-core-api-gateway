./gradlew clean bootJar
docker image build -t booking-core-api-gateway .
docker run -d --hostname booking-core-api-gateway --name booking-core-api-gateway -p 8888:8888
booking-core-api-gateway