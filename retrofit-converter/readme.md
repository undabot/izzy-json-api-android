# Usage

## Add gradle dependency

```groovy
implementation 'com.undabot.izzy-json-api-android:retrofit-converter:<version>'
```

## Create Izzy Retrofit converter

```kotlin
val izzyConfiguration = IzzyConfiguration(
    arrayOf(ArtistResource::class.java, PersonResource::class.java)
)
val parser = IzzyJsonParser(izzyConfiguration) // e.g. JacksonParser or GsonParser
val izzy = Izzy(parser)

val izzyRetrofitConverter = IzzyRetrofitConverter(izzy)
```

## Add as Converter factory to Retrofit

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://undabot.com")
    .client(okHttpClient)
    .addConverterFactory(izzyRetrofitConverter)
    .build
```