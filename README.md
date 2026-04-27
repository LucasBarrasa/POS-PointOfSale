# ** Chester POS - Android Portfolio Showcase **📱

Este repositorio es una vitrina técnica que muestra las funcionalidades centrales y la arquitectura de Chester App, una solución integral de gestión comercial. El proyecto nace de una colaboración activa con un desarrollador Backend para resolver la operativa de ventas en movilidad, permitiendo la gestión de catálogos, clientes y pedidos en tiempo real y offline.


# 🎯 **Propósito del Proyecto**

El objetivo principal es proporcionar a los vendedores una herramienta de alta confiabilidad que elimine la dependencia constante de internet. El sistema está diseñado para escalar hacia una plataforma multi-rol (Administrador, Vendedor, Repartidor, Cliente), donde cada perfil visualiza información específica basada en permisos gestionados desde el servidor.

# 🚀 **Características Destacadas**

Enfoque Offline-First: Las órdenes se generan localmente y se marcan como "borradores" si no hay conexión, asegurando que el flujo de venta nunca se detenga.

Gestión Flexible de Clientes: Capacidad de consumir el listado maestro desde la API o crear pedidos para clientes casuales (registrando únicamente nombre y dirección), permitiendo una operación ágil en el punto de venta.

Catálogo con Búsqueda Local: Sincronización de stock y catálogo para consultas instantáneas sin latencia de red.

Arquitectura Escalable: El proyecto está preparado para la implementación inminente de Role-Based Access Control (RBAC) y lógica de permisos diferenciada.

# 🛠️ **Stack Tecnológico**

Lenguaje: Kotlin.

Arquitectura: Clean Architecture + MVVM (Model-View-ViewModel).

Modularización: Estructura por capas y features para facilitar el mantenimiento y el trabajo en equipo.

Persistencia Local: Room Database (Caché y Single Source of Truth).

Networking: Retrofit + OkHttp para el consumo de la API REST de Chester.

Testing: Unit Tests en la capa de modelos y casos de uso para asegurar la integridad de la lógica de negocio. JUnit 5 y Mock

# 📂 **Estructura de Módulos**

:core: Cimientos de la app (Data, Network, Models, Navigation).

:features:create_order: Flujo completo de navegación para la toma de pedidos.

:features:order_detail: Gestión y revisión de órdenes existentes.

:features:seller: Panel principal para el vendedor (Listados, catálogo y gestión rápida).

:app: Orquestador principal que ensambla las piezas del sistema.

# 🔜 **Roadmap de Desarrollo**

[ ] Sincronización Inteligente: Implementación de WorkManager para el envío automático de pedidos en segundo plano cuando se recupere la conexión.

[ ] Seguridad y Perfiles: Integración de autenticación y lógica de navegación basada en roles (Admin/Repartidor/Vendedor).

[ ] Gestión de Pedidos: Funcionalidad de edición de órdenes enviadas y sincronización manual para usuarios avanzados.
