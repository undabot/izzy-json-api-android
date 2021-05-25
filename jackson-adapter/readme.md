# Usage

## Add gradle dependency

```groovy
implementation 'com.undabot.izzy-json-api-android:jackson-adapter:<version>'
```

## Create Izzy with Jackson

```kotlin
val izzyConfiguration = IzzyConfiguration(
    arrayOf(ArtistResource::class.java, PersonResource::class.java)
)
val objectMapper = ObjectMapper().apply { /* Your optional setup here */ }
val parser = JacksonParser(izzyConfiguration, objectMapper)

val izzy = Izzy(parser)
```