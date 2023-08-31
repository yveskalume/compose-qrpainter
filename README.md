# Jetpack Compose QRPainter

The Jetpack Compose QRPainter is a library that allows you to generate QR codes in Jetpack Compose.

## Installation

Setup the maven repository

```kotlin
repositories {
    maven { uri("https://jitpack.io") }
}
```
To use the library, add the following dependency to your project's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.github.yveskalume.compose-qrpainter:0.0.1")
}
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