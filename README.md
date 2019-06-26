# PopularMovies
This app was created for the [Android Developer Nanodegree](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801) @[Udacity](https://www.udacity.com).

It is currently under development. Stay tuned for more to come.

## Stage 1
* Display popular or top rated movies posters in grid
* Settings to switch between popular and top rated movies sort orders
* Clicking movie poster displays movie details

## Stage 2
* Display movie trailers in the movie details
* Open YouTube app or web browser to play trailers
* Display movie reviews in the movie details
* Allow users to mark movie as a favorite
* Add another setting to allow user to sort by favorites
* Persist favorites to local Room database
* Improve application efficiency with Android Architecture Components
* Allow users to share first trailer's YouTube url via movie details

## How to use
This application uses data from [The Movie Database API](https://www.themoviedb.org/documentation/api). 
In order to use it, you must provide your own API key. Once you get the API key, you will need to put
it in the *.gradle* folder in your home directory. It can typically be found in one of the locations below:


* Linux: /home/**\<Username\>**/.gradle
* Mac: /Users/**\<Username\>**/.gradle
* Windows: C:\Users\\**\<Username\>**\\.gradle

Inside this folder, there should be a *gradle.properties* file. If not, you should create one. Inside the
*gradle.properties* file, add the key as follows (including the quotation marks):

`the_movie_db_v3_api_key="api key goes here"`

## Acknowledgements
* [ButterKnife](https://jakewharton.github.io/butterknife/)
* [okhttp](https://square.github.io/okhttp/)
* [Picasso](https://square.github.io/picasso/)
* [Retrofit](https://square.github.io/retrofit/)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxJava2](https://github.com/ReactiveX/RxJava)
* [The Movie DB](https://www.themoviedb.org/?language=en-US)

## Attribution
All movie data is provided by **The Movie DB**

<img src="readme-assets/tmdb.png" width=125 height=49>

## Future Improvements
* Utilize [NetworkBoundResource](https://developer.android.com/jetpack/docs/guide#addendum) to improve fetching/storing of network and local data
* Store images for local movies in local storage
* Convert to Kotlin
* Improve the UI to display current sort order for user
* Use fragments with single activity
* Utilize Navigation architecture component
* Add dependency injection
* Add tests