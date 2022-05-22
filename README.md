# Gcloud Pub/Sub
Este repositorio se ha creado para facilitar la configuración e interacción con el **emulador de Gcloud Pub/Sub**.

Toda la documentación en [Gcloud Pub/Sub](https://cloud.google.com/pubsub/docs/emulator)

# Carpetas
Se agrega la descripción de cada carpeta y su contenido, los requisitos necesarios para ejecutar el emulador y los proyectos que realizan la publicación, subscripción y lectura de los mensajes.

## 1. Docker-PubSub
Esta carpeta contiene los siguientes archivos:
1. **Dockerflie** en el cual se puede visualizar el paso a paso para realizar las instalaciones necesaria para el correcto funcionamiento del emulador **PUB/SUB**.
2. **docker-compose.yml** archivo que permite crear la imagen de forma local y renombrar toda la configuración.
3. **.dockerignore** en este docuemtno se puede agregar las carpetas y archivos que se desean ignorar al momento de trabajar.
4. **README** Se visualizan los comandos para compilar la imagen y lanzar el contenedor mediante el **docker-compose** en la terminal.


## 2. demo-pubsub
Proyecto en Java con **Spring Boot** en el cual se muestra como se realiza la conexión al emulador **Pub/Sub**, la creación de un **Tema/Topic**, un **Subscriptor/Subscription** y un **Publicador/Publisher** el cual envia dos mensaje de ejemplo.

## 3. demo-pubsub-read
Proyecto en Java con **Spring Boot** en el cual se realiza la conexión al emulador **Pub/Sub** y se realiza la subscripción para leer los mensaje publicados por el proyecto **demo-pubsub**.

# Asesoría / Problemas
Puedes escribir a sneyt04@gmail.com
