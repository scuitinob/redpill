<p align="center">
  <img src="src/main/resources/images/redpill-icon.png" alt="RedPill logo" width="300">
</p>

# RedPill

**RedPill** es una aplicación de escritorio en Java inspirada en *The Matrix*.  
Su propósito es mantener el sistema “despierto” simulando actividad mínima del usuario, con una interfaz gráfica simple y control sobre su ejecución.

> *“You take the red pill — you stay in Wonderland.”*

---

## 🧠 ¿Qué hace?

- Mueve el cursor del mouse siguiendo una secuencia de actividad
- Presiona la tecla **SHIFT**
- Repite la acción en intervalos configurables
- Ejecuta una acción inmediatamente al iniciar
- Detecta actividad real del mouse o teclado
- Permite detener o continuar el proceso cuando detecta actividad humana
- Muestra un registro de las acciones realizadas
- Puede permanecer ejecutándose desde el **System Tray**
- Ayuda a evitar que el sistema entre en reposo por inactividad

No espía, no envía información, no persiste ocultamente y no hace nada más.

---

## ⚙️ Requisitos

### Desarrollo

- Java 21 LTS
- Gradle 9.7.1
- IntelliJ IDEA recomendado

El proyecto incluye **Gradle Wrapper**, por lo que no es necesario instalar Gradle globalmente.

### Ejecución desde código fuente

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

## ▶️ Uso

1. Ejecuta **RedPill**
2. Selecciona el intervalo entre acciones
3. Presiona **START**
4. RedPill realizará inmediatamente la primera acción
5. Mientras permanezca activo, repetirá la secuencia según el intervalo seleccionado

Si RedPill detecta movimiento del mouse o actividad del teclado, pausará temporalmente el proceso y preguntará si deseas detenerlo.

Si no se selecciona ninguna opción durante **10 segundos**, RedPill continuará automáticamente.

Para detenerlo manualmente:

- Presiona **STOP**
- Selecciona **Sí, detener** al detectarse actividad humana
- Sal de la aplicación desde el System Tray

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

- Pequeño.
- Simple.
- Portable.
- Multiplataforma.
- Sin telemetría.
- Sin fricción.

RedPill no intenta hackear ni ocultarse del sistema.

Solo lo mantiene despierto.

---

## 🟦 BluePill

**RedPill** nace como proyecto hermano de **BluePill**.

Ambos persiguen el mismo objetivo utilizando enfoques diferentes:

**BluePill** apuesta por la simplicidad de un pequeño script en Python.

**RedPill** lleva la misma idea a Java, incorporando interfaz gráfica, detección de actividad humana, configuración y soporte multiplataforma.

Dos píldoras.  
La misma Matrix.

---

## 👤 Author

**Sergio Cuitiño**

GitHub: [@scuitinob](https://github.com/scuitinob)

Developed under **Corvin Develop**.