# PopularMovies
This app was created for the [Android Developer Nanodegree](https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801) @[Udacity](https://www.udacity.com).

It is currently under development. Stay tuned for more to come.

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
