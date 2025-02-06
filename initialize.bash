mkdir streaming-pipeline
cd streaming-pipeline

mkdir kafka-stream-app
cd kafka-stream-app

#after create java file
mvn clean package
java -jar target/kafka-stream-app-1.0-SNAPSHOT.jar
