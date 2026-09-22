<p align="center">
  <img src="src/main/resources/images/redpill-icon.png" alt="RedPill logo" width="300">
</p>

# RedPill

**RedPill** es una aplicación de escritorio multiplataforma escrita en Java e inspirada en *The Matrix*.

Su propósito es mantener el sistema despierto simulando una cantidad mínima de actividad, ofreciendo una interfaz gráfica simple, detección de actividad humana y control sobre su ejecución.

> **“You take the red pill — you stay in Wonderland.”**

---

## 🧠 ¿Qué hace?

- Simula actividad mediante movimientos del cursor y pulsaciones de **SHIFT**
- Permite configurar el intervalo entre acciones
- Ejecuta una acción inmediatamente al iniciar
- Detecta actividad real del mouse y teclado
- Pausa automáticamente al detectar actividad humana
- Permite decidir si detener o continuar la simulación
- Continúa automáticamente si no hay respuesta después de **10 segundos**
- Muestra un registro de las acciones realizadas
- Puede permanecer disponible desde el área de notificación del sistema
- Permite detener y reanudar la simulación sin cerrar la aplicación
- Ayuda a evitar que el sistema entre en reposo o bloquee la sesión

Cuando la simulación se detiene, RedPill puede permanecer ejecutándose en segundo plano y accesible desde el área de notificación del sistema:

- En **Windows**, desde los iconos del área de notificación junto al reloj
- En **macOS**, desde la barra de menús

Desde allí puedes volver a abrir RedPill y reactivar la simulación cuando la necesites.

RedPill no recopila información, no envía datos y no instala servicios en el sistema.

---

## 📦 Descarga

RedPill está disponible como aplicación empaquetada y autocontenida.

**No necesitas instalar Java, Gradle ni configurar un entorno de desarrollo para utilizarla.**

### RedPill v1.0.0

La primera versión estable incluye paquetes para:

- 🪟 **Windows x64**
- 🍎 **macOS ARM64 (Apple Silicon)**

👉 [Descargar la última versión de RedPill](https://github.com/scuitinob/redpill/releases/latest)

También puedes consultar todas las versiones publicadas en:

👉 [GitHub Releases](https://github.com/scuitinob/redpill/releases)

> Actualmente no se incluyen builds para Linux ni macOS Intel debido a que estas plataformas todavía no han sido probadas.

La versión de macOS puede solicitar permisos del sistema durante la primera ejecución para permitir la detección y simulación de actividad.

---

## ▶️ Uso

1. Descarga el paquete correspondiente a tu sistema operativo
2. Instala o ejecuta **RedPill**
3. Selecciona el intervalo entre acciones
4. Presiona **START**
5. RedPill realizará inmediatamente la primera acción
6. Mientras permanezca activo, repetirá la secuencia según el intervalo seleccionado

Si RedPill detecta actividad real del mouse o teclado, pausará temporalmente su actividad y preguntará si deseas detenerla.

Si no se selecciona ninguna opción durante **10 segundos**, RedPill continuará automáticamente.

Puedes detener la simulación mediante:

- El botón **STOP**
- La opción **Sí, detener** cuando se detecte actividad humana

Detener la simulación **no cierra RedPill**.

La aplicación puede permanecer disponible en segundo plano desde:

- El **System Tray** de Windows
- La **barra de menús** de macOS

Desde allí puedes volver a acceder a RedPill y activar nuevamente la simulación sin necesidad de iniciar otra instancia de la aplicación.

Para finalizar RedPill completamente, utiliza la opción de salida de la aplicación.

---

## 🛠️ Desarrollo desde código fuente

Si quieres ejecutar, modificar o compilar RedPill desde su código fuente necesitarás:

- Java 21 LTS
- Gradle 9.7.1
- IntelliJ IDEA recomendado

El proyecto incluye **Gradle Wrapper**, por lo que no es necesario instalar Gradle globalmente.

### Ejecutar desde código fuente

Windows:

```bash
gradlew.bat run
```

Linux / macOS:

```bash
./gradlew run
```

---

## 🔨 Compilación

Para limpiar y compilar el proyecto:

### Windows

```bash
gradlew.bat clean build
```

### Linux / macOS

```bash
./gradlew clean build
```

El JAR generado se encontrará en:

```text
build/libs/
```

Puede ejecutarse utilizando:

```bash
java -jar build/libs/redpill-<version>.jar
```

---

## 📁 Estructura del proyecto

```text
redpill/
├── src/main/java/com/corvindevelop/redpill/
│   ├── RedPillApplication.java
│   │
│   ├── core/
│   │   ├── ActivityListener.java
│   │   ├── HumanActivityMonitor.java
│   │   └── MouseActivityService.java
│   │
│   └── ui/
│       ├── CorvinSignaturePanel.java
│       ├── HumanActivityDialog.java
│       ├── MovementLogPanel.java
│       ├── RedPillButton.java
│       ├── RedPillLogoPanel.java
│       ├── RedPillTheme.java
│       ├── RedPillWindow.java
│       ├── ResourceImages.java
│       └── TrayController.java
│
├── src/main/resources/
│   └── images/
│
├── docs/
│   └── ARCHITECTURE.md
│
├── build.gradle
├── settings.gradle
├── gradlew
└── gradlew.bat
```

---

## 🔴 Filosofía

RedPill pretende mantenerse fiel a una idea sencilla:

- Pequeño
- Simple
- Portable
- Multiplataforma
- Sin telemetría
- Sin servicios innecesarios
- Sin fricción

RedPill no intenta administrar ni modificar el sistema.

Puede permanecer disponible en segundo plano mientras la aplicación está abierta, permitiendo detener y reanudar su función cuando sea necesario.

Al salir completamente de RedPill, no deja servicios ni procesos propios ejecutándose.

---

## 🟦 BluePill

**RedPill** nace como proyecto hermano de **BluePill**.

Ambos persiguen el mismo objetivo utilizando enfoques diferentes:

**BluePill** apuesta por la simplicidad de una pequeña utilidad escrita en Python, diseñada para ejecutarse directamente y mantenerse deliberadamente mínima.

**RedPill** lleva la misma idea a Java mediante una aplicación de escritorio con interfaz gráfica, detección de actividad humana, registro de actividad, configuración y acceso desde el área de notificación del sistema.

Dos píldoras.

La misma Matrix.

---

## 👨‍💻 Autor

Desarrollado por **Corvin Develop**.

---

<p align="center">
  <strong>Corvin Develop</strong>
</p>