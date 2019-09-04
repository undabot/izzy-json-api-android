## Usage

**Add gradle dependency**

```
TODO add maven
```


**Create Izzy with Jackson**

```kotlin
val izzyConfiguration = IzzyConfiguration(
    arrayOf(ArtistResource::class.java, PersonResource::class.java)
)
val objectMapper = ObjectMapper().apply { /* Your optional setup here */ }
val parser = JacksonParser(izzyConfiguration, objectMapper)

val izzy = Izzy(parser)
```