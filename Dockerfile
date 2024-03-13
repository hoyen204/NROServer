FROM frekele/java:jdk8

MAINTAINER frekele <leandro.freitas@softdevelop.com.br>

ENV ANT_VERSION 1.10.13
ENV ANT_HOME /otp/ant

# change to tmp folder
WORKDIR /tmp

# Download and extract apache ant to opt folder
RUN wget --no-check-certificate --no-cookies http://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz \
    && wget --no-check-certificate --no-cookies http://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz.sha512 \
    && echo "$(cat apache-ant-${ANT_VERSION}-bin.tar.gz.sha512) apache-ant-${ANT_VERSION}-bin.tar.gz" | sha512sum -c \
    && tar -zvxf apache-ant-${ANT_VERSION}-bin.tar.gz -C /opt/ \
    && ln -s /opt/apache-ant-${ANT_VERSION} /opt/ant \
    && rm -f apache-ant-${ANT_VERSION}-bin.tar.gz \
    && rm -f apache-ant-${ANT_VERSION}-bin.tar.gz.sha512

RUN update-alternatives --install "/usr/bin/ant" "ant" "/opt/ant/bin/ant" 1 && \
    update-alternatives --set "ant" "/opt/ant/bin/ant"

# Copy the Ant project into the container
COPY . /app

# Set working directory
WORKDIR /app

# Run the specific Ant command
RUN ant -Dnb.internal.action.name=rebuild clean jar

WORKDIR /app

ENTRYPOINT exec java -cp lib/GirlkunNetwork.jar:lib/apache-commons-lang.jar:lib/girlkundb-1.0.0.jar:lib/json_simple-1.1.jar:lib/lombok.jar:lib/okhttp-3.0.0.jar:lib/okio-1.6.0.jar:lib/rxjava-3.1.6.jar:dist/Idsolutions.jar com.girlkun.server.ServerManager