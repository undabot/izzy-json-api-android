# Usage

## Add gradle dependency**

```groovy
implementation 'com.undabot.izzy-json-api:gson-adapter:<version>'
```

## Create Izzy with Gson**

```kotlin
val izzyConfiguration = IzzyConfiguration(
    arrayOf(ArtistResource::class.java, PersonResource::class.java)
)
val gson = Gson() // Your optional gson setup here
val parser = GsonParser(izzyConfiguration, gson)

val izzy = Izzy(parser)
```