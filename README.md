# 🚀 CRM para Emprendedores - API REST

Este proyecto es una solución de **Backend** robusta diseñada para que emprendedores puedan gestionar de manera eficiente su cartera de clientes, registros de ventas y estrategias de contenido multimedia. 

La arquitectura implementa un modelo **Multi-tenant** lógico, garantizando que cada usuario acceda exclusivamente a la información vinculada a su propio negocio mediante seguridad basada en tokens.

---

## 🛠️ Stack Tecnológico

* **Lenguaje:** Java 21
* **Framework:** Spring Boot 3.x
* **Seguridad:** Spring Security + JWT (Stateless)
* **Persistencia:** Spring Data JPA + Hibernate
* **Base de Datos:** MySQL
* **Documentación:** Swagger UI / OpenAPI 3
* **Productividad:** Lombok, MapStruct

---

## ✨ Características Técnicas Principales

### 🔒 Seguridad y Control de Acceso
* **Autenticación JWT:** Implementación de Access y Refresh Tokens.
* **Logout Seguro:** Gestión de revocación de tokens mediante persistencia en base de datos.
* **RBAC (Role-Based Access Control):** Roles jerárquicos (`ADMIN`, `EMPRENDEDOR`).
* **Aislamiento de Datos:** Filtros dinámicos en los servicios para asegurar que los datos financieros y de clientes sean privados por cada emprendedor.

### 📦 Funcionalidades del Negocio
* **Gestión de Ventas:** Registro transaccional vinculado a clientes y emprendedores.
* **Cartera de Clientes:** CRUD completo con seguimiento de interacciones.
* **Administración de Contenidos:** Módulo para gestionar publicaciones y material multimedia.
* **Auditoría Automática:** Control de fechas de creación y modificación en todos los registros (JPA Auditing).

### ⚙️ Infraestructura
* **Manejo de Archivos:** Soporte para subida y visualización de imágenes (Multipart).
* **CORS:** Configurado para integración con frontends en React o Vue.
* **Inicialización:** Auto-aprovisionamiento de roles y usuario administrador maestro al primer inicio.

---
