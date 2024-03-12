FROM openjdk:20-jdk

ENV ANT_VERSION 1.10.13
ENV ANT_HOME /usr/share/ant

# change to tmp folder
WORKDIR /tmp

# Download and extract apache ant to opt folder
RUN apk --no-cache add curl && \
    curl -fsSL -o /tmp/apache-ant-$ANT_VERSION-bin.tar.gz https://downloads.apache.org/ant/binaries/apache-ant-$ANT_VERSION-bin.tar.gz && \
    tar xzf /tmp/apache-ant-$ANT_VERSION-bin.tar.gz -C /tmp && \
    mv /tmp/apache-ant-$ANT_VERSION $ANT_HOME && \
    rm /tmp/apache-ant-$ANT_VERSION-bin.tar.gz && \
    ln -s $ANT_HOME/bin/ant /usr/bin/ant && \
    apk del curl

# add executables to path
RUN update-alternatives --install "/usr/bin/ant" "ant" "/opt/ant/bin/ant" 1 && \
    update-alternatives --set "ant" "/opt/ant/bin/ant"

# Copy the Ant project into the container
COPY . /app

# Set working directory
WORKDIR /app

# Run the specific Ant command
CMD ["ant", "-Dnb.internal.action.name=rebuild", "clean", "jar"]

WORKDIR /app/dist
CMD ["java", "-jar", "Idsolutions.jar"]

