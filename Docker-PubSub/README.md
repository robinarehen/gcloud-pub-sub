# Compilar la imagen
```
docker-compose build
```

# Ejecutar el contener
```
docker-compose up -d
```

# Confirmar que el contenedor esta corriendo
```
docker ps
```

## Salida
```
CONTAINER ID   IMAGE                       COMMAND                  CREATED             STATUS             PORTS                    NAMES
4e1e861b5583   sneyt04/gcloud-pubsub:1.0   "/bin/sh -c './googl…"   About an hour ago   Up About an hour   0.0.0.0:8085->8085/tcp   pubsub_v1
```

# validar que esta corriendo el PUB/SUB
```
docker logs -f pubsub_v1
```

## Salida, ultima linea
```
[pubsub] INFO: Server started, listening on 8085
```
