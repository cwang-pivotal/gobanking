FROM java:8

COPY gradle /usr/src/javabanking/gradle
COPY gradlew /usr/src/javabanking/gradlew
COPY *.gradle /usr/src/javabanking/

COPY components/common /usr/src/javabanking/components/common

WORKDIR /usr/src/javabanking

RUN ./gradlew :components/common:compileJava

COPY components/bank /usr/src/javabanking/components/bank

CMD ["./gradlew", ":components/bank:bootRun"]