FROM ubuntu:22.04

# Instalaciones de python : https://cloud.google.com/python/docs/setup#linux
RUN \
 apt update && \
 apt install curl default-jre python3 python3-dev python3-venv -y && \
 apt-get install wget -y

RUN \
 wget https://bootstrap.pypa.io/get-pip.py && \
 python3 get-pip.py

# Instalacion de CLI Gcloud : https://cloud.google.com/sdk/docs/install
RUN curl -O https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-cli-385.0.0-linux-x86_64.tar.gz

RUN tar -xf google-cloud-cli-385.0.0-linux-x86_64.tar.gz

RUN ./google-cloud-sdk/install.sh

RUN ./google-cloud-sdk/bin/gcloud init --skip-diagnostics && make; exit 0

RUN \
 ./google-cloud-sdk/bin/gcloud components install beta pubsub-emulator && \
 ./google-cloud-sdk/bin/gcloud components update

# Ejecutar el emulador
EXPOSE 8085

# Toda la información de CLOUDSDK_CORE_DISABLE_PROMPTS en: https://cloud.google.com/sdk/gcloud/reference#--quiet
ENV CLOUDSDK_CORE_DISABLE_PROMPTS=1

ARG PUBSUB_PROJECT_ID='default'

RUN ./google-cloud-sdk/bin/gcloud config set project $PUBSUB_PROJECT_ID

CMD ./google-cloud-sdk/bin/gcloud beta emulators pubsub start --project=$PUBSUB_PROJECT_ID --host-port=0.0.0.0:8085 -q
