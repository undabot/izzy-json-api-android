## Usage

**Add gradle dependency**

```
TODO add maven
```


**Create Izzy with Gson**

```kotlin
val izzyConfiguration = IzzyConfiguration(
    arrayOf(ArtistResource::class.java, PersonResource::class.java)
)
val gson = Gson() // Your optional gson setup here
val parser = GsonParser(izzyConfiguration, gson)

val izzy = Izzy(parser)
```