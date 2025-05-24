# Jetpack Compose QRPainter

The Jetpack Compose QRPainter is a library that allows you to generate QR codes in Jetpack Compose.

[![](https://jitpack.io/v/yveskalume/compose-qrpainter.svg)](https://jitpack.io/#yveskalume/compose-qrpainter)

## Installation

Setup the maven repository

To use the library, add the following dependency to your project's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("dev.yveskalume:compose-qrpainter:<version>")
}
```

```toml
compose-qrpainter = { group = "dev.yveskalume", name = "compose-qrpainter", version = "<last-version>" }
```

## Usage

```kotlin
Image(
    painter = rememberQrBitmapPainter(
        content = "https://google.com",
        size = 300.dp,
        padding = 1.dp
    ),
    contentDescription = null
)
```
