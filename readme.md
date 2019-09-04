# JSON-API Docs

# Izzy 
A JSON API implementation written in Kotlin, supporting your favorite JSON parsers!


## What is Izzy?

Izzy is a 🐶. Also, Izzy is a JSON API standard implementation in Kotlin. 
It’s a library that allows you to plug in your JSON parser of choice, say which types are JSON-API types and it will automagically serialise and deserialise your objects for you from and to JSON API compliant forms. 
We’ve built it to work together with your favourite JSON parsers, be it Gson, Jackson, or Moshi (in progress) whatever you choose - instead of forcing a parser on you.

## How to set it up:

1. Add the base parser to your build.gradle
    ```
    TODO insert gradle code here, maven code also?
    ```

2. Add a JSON-Parser module to your build.gradle (or implement your own/open a PR with one)
    ```
    TODO insert gradle code here, maven code also?
    ```

3. Register the types you want or need.
    - When you are setting up your instance of `Izzy`, you need to pass the `IzzyConfiguration` a list of classes (or just one) you're going to use with Izzy.
    - For example, when using `Izzy` with Jackson, you'd create an instance like this:
	```kotlin
	Izzy(
	    JacksonParser(
	        IzzyConfiguration(
	            ArticleResource::class.java,
	            PersonResource::class.java
	        )
	    )
	 )
	```

4. Let your models extend `IzzyResource` and annotate them using `@Type(“yourTypeHere”)`
    - relationships must be annotated with `@Relationship(“nameGoesHere”)`

    For example, we'll be using the Article & Person classes from [jsonapi.org](https://jsonapi.org/examples/) samples

    ```kotlin
    @Type("articles")
    data class ArticleResource(
        var title: String? = null,
        var body: String? = null,
        var created: String? = null,
        var updated: String? = null
    ): IzzyResource {
        @Relationship("coauthors") var coauthors: List<PersonResource>? = emptyArray()
        @Relationship("author") var authors: PersonResource? = null
    }
    ```

5. Add a Retrofit plugin if you use Retrofit (or don’t if you don’t)

And you’re ready to be JSON-API Compliant!


## Components:

**`IzzyResource`**
- the base resource class
- ensures your objects has an `ID`,
- contains `meta` and `links` by default if available
- ***all your resources must extend `IzzyResource`***

**`JsonDocument`**
- a wrapper class containing your top-level members: `links`, `meta`, and `data` or `error`
- all `Izzy` deserialization methods return a `JsonDocument` from which you can extract
the data

**`Izzy`**
- instance of your deserializer to which you pass your Json Parser with Izzy Configuration

**`IzzyConfiguration`**
- configuration of the Izzy Parser. Holds the Classes you'll use with the `Izzy` (classes annotated with `@Type`)

**`JsonParser`**
- an interface that wraps popular JSON Parsing libraries - For now, you have modules you can include
if you use Jackson and Gson in your project (Moshi on the way...)

**Custom JsonParser**
- To implement your own `JsonParser`, just implement the `IzzyJsonParser` and `JsonElements`
interfaces and you can use your preferred parser

- Make sure to add test for it - you can extend existing test classes which will ensure that implementation is valid. (`JsonElementsShould` & `IzzyJsonParserShould`)

### Annotations:

**`@Type(val typeName: String)`**
- annotation that associates this class with it’s JSON API type.
- the argument is the JSON API Type of this object.

**`@Relationship(val name: String)`**
- annotation that associates the field with a JSON API relationship.
- Izzy's relationships can be set on both single and Collection based objects.

**`@Nullable`**
- used when field should be serialized to `null` for `null` value
- if not added to field, `null` values will be omitted from json


## Under the hood
Under the hood, Izzy uses reflection to find your relationships, deserialize them and bind them to the resource.

## ! Limitations !

### Deserialization
- relationship `links` are not yet supported

### Serialization
- custom attributes naming is not used when attributes are serialized
    - parser is currently using field name directly and not property from `@SerializedName` or `@JsonProperty`
    - e.g. `@SerializedName("custom_name") var name: String` will be serialized to `name`
- `links` serialization is not supported currently on any level
- `meta` serialization is not supported currently on any level

# Example - using Jackson and Retrofit

**Define resource models**

```kotlin
import com.fasterxml.jackson.annotation.JsonProperty
import com.undabot.izzy.annotations.Nullable
import com.undabot.izzy.annotations.Relationship
import com.undabot.izzy.annotations.Type
import com.undabot.izzy.models.IzzyResource

@Type("articles")
data class ArticleResource(
    // Default values are set because an empty constructor is required
    @JsonProperty("title") var title: String? = null,
    // Field will be serialized to `null` if value is `null` at `@Nullable` annotated field
    @Nullable @JsonProperty("description") var description: String? = null,
    @JsonProperty("keywords") var keywords: ArrayList<String>? = null,
    // non-resource object within attributes
    @JsonProperty("custom_object") var customObject: CustomObject? = null
): IzzyResource() {

    @Relationship("coauthors") var coauthors: List<Person>? = null
    @Relationship("author") var author: Person? = null
}

@Type("persons")
data class PersonResource(
    @JsonProperty("name") var name: String? = null
): IzzyResource() {

    @Relationship("favorite_article") var favoriteArticle: ArticleResource? = null
}

```

**Initialize Izzy parser**

```kotlin
// Register all resources in Izzy Configuration
val izzyConfiguration = IzzyConfiguration(
    arrayOf(
        ArtistResource::class.java,
        PersonResource::class.java
    )
)
val parser = JacksonParser(izzyConfiguration) // or GsonParser when Gson is used
// Initialize Izzy with parser
val izzy = Izzy(parser)
```

**Initialize `IzzyRetrofitConverter`**

```kotlin
val izzyRetrofitConverter = IzzyRetrofitConverter(izzy)
```

**Add as Converter factory to `Retrofit` and create your api service**

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://undabot.com")
    .client(okHttpClient)
    .addConverterFactory(izzyRetrofitConverter)
    .build

val apiService = retrofit.create(ApiService::class.java)
```

**Api service example**

```kotlin
interface ApiService {

    @GET("articles")
    fun articles(): Response<JsonDocument<List<ArticleResource>>>

    @GET("article/{id}")
    fun article(@Path("id") id: String): Response<JsonDocument<ArticleResource>>

    @GET("person/{id}")
    fun person(@Path("id") id: String): Response<JsonDocument<PersonResource>>

    @POST("article")
    fun createArticle(
        @Body articleBody: ArticleResource
    ): Response<JsonDocument<ArticleResource>>
}
```