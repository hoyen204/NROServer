FROM openjdk:20-jdk

ENV ANT_VERSION 1.10.13
ENV ANT_HOME /usr/share/ant

# change to tmp folder
WORKDIR /tmp

# Download and extract apache ant to opt folder
RUN apt-get update && \
    apt-get install -y --no-install-recommends wget && \
    wget -q https://downloads.apache.org/ant/binaries/apache-ant-$ANT_VERSION-bin.tar.gz -O /tmp/apache-ant-$ANT_VERSION-bin.tar.gz && \
    tar xzf /tmp/apache-ant-$ANT_VERSION-bin.tar.gz -C /tmp && \
    mv /tmp/apache-ant-$ANT_VERSION $ANT_HOME && \
    rm /tmp/apache-ant-$ANT_VERSION-bin.tar.gz && \
    ln -s $ANT_HOME/bin/ant /usr/bin/ant && \
    apt-get remove -y wget && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

# Copy the Ant project into the container
COPY . /app

# Set working directory
WORKDIR /app

# Run the specific Ant command
CMD ["ant", "-Dnb.internal.action.name=rebuild", "clean", "jar"]

WORKDIR /app/dist
CMD ["java", "-jar", "Idsolutions.jar"]

