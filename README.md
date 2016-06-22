# venderion
This repository contains a small piece of isolated code from my current project. 
This project is pool of web services pool of web services(application back-end) to perform main operations used in a typical 
payment application, that is, payment, payment cancellation, operation search, operation detail, money collecting, etc.
This pool of web services is developed using Axis 2, JPA and Hibernate for persistence and ant as project builder.
The code commited to this repository is just a sample of these web services located in the /EaliaAppIsolated project. 
Concretly, there is common abstract class called AbstractSkeleton.java ,containning common logic for every services, and another classes
generated with Axis 2 within the directory EaliaAppIsolated/src/es/cecabank/ealiaapp/servicios/wsimpl/dispositivos/wsf_dispositivos_actualizar/.
Moreover there are another service classes implmenting persistence layer (within /EaliaAppIsolated/src/es/cecabank/ealiaapp/accesobd directory),
classes for Security and Ciphering methods (/EaliaAppIsolated/src/es/cecabank/ealiaapp/logica/logicaimpl/). 
The project /EaliaComunIsolated is for common utility classes for traccing and loggin.
