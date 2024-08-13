# MovieFinder 
## Overview

MovieFinder is an Android application designed to help users discover movies and TV series. The app provides detailed information about movies, TV shows, and allows users to search for titles using data fetched from the [The Movie Database (TMDb) API](https://developer.themoviedb.org/docs/getting-started). The app is built using Jetpack Compose, offering a modern and flexible UI experience.

## Features

- **Discover Movies and Tv Series**: Browse through popular, trending, and top-rated movies and TV shows.
- **Detailed Information**: View comprehensive details including plot summaries, release dates, ratings, cast, and crew information.
- **Search**: Quickly search for specific movies or TV series by title.
- **Save Favorites**: Save your favorite movies and TV series to local storage, so you can easily access them anytime, even offline.

## Technologies Used
- **Programming language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Networking**: Retrofit and OkHttpClient for API calls
- **Dependency Injection**: Dagger-Hilt
- **Asynchronous Programming**: Flow over Coroutine for managing asynchronous data streams
- **Local Storage**: Room Database for saving favorite movies and TV series
- **API**: Public api of The Movie Database (TMDb). Credit: [TMDb](https://developer.themoviedb.org/docs/getting-started)

## Requirements
- **Android version**: Android 5.0 (Lollipop) or higher
- **Access Token**: To run the app, you need an Access Token from [TMDb](https://developer.themoviedb.org/docs/getting-started).

## Setup Instructions

1. **Clone the Repository:**
    ```bash
    git clone https://github.com/HienDao14/Movie-Discover.git
    cd MovieExplorer
    ```
2. **Get TMDb Access Token:**
- Visit [TMDb API](https://developer.themoviedb.org/docs/getting-started).
- Sign up or log in to your account.
- Generate a new Access Token.
3. **Add Access Token:**
- Create a file named `**hiendao.properties**` in the root directory of the project.
- Add your TMDb Access Token to this file:
    ```
    ACCESS_TOKEN = "your_access_token_here"
    ```
4. **Build the project:**
- Open the project in Android Studio.
- Sync the project with Gradle files.
- Build and run the app on an emulator or physical device.

## Usage
- **Home Screen:** Browse popular and trending movies and TV series.
- **Discover Screen:** 

    &nbsp;- Explore movies and TV series through different categories such as Popular, Top Rated, Upcoming and Revenue.
    &nbsp;- Filter content by genres, allowing you to find movies or TV series that match your interests, such as Action, Comedy, Drama, etc.
    &nbsp;- Discover new content based on trending topics and user ratings.
- **Details Screen:** Tap on a movie or TV series to view more information.
- **Search:** Use the search bar to find specific titles.
- **Favorites:** Save your favorite movies and TV series for quick access later. You can view your saved favorites in the "Favorites" section.

## Get app
You can download the latest APK for MovieFinder from [this link](https://github.com/HienDao14/Movie-Discover/releases/tag/Release)


## License
This project is licensed under the MIT License.

## Contact
For any inquiries or issues, please contact:

- Name: HienDao
- Email: hientto1234@gmail.com
- Github: [HienDao14](https://github.com/HienDao14)
