FROM hseeberger/scala-sbt:graalvm-ce-21.3.0-java17_1.6.2_3.1.1
WORKDIR /memory
ADD . /memory
CMD sbt run

# docker build -t se-memory .
# docker run -it --rm se-memory