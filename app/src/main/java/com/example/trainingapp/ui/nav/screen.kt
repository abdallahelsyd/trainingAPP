package com.example.trainingapp.ui.nav

sealed class Screen(val route: String) {
    object Library : Screen("library")

    // The detail route requires a bookId argument
    object Detail : Screen("detail/{bookId}") {
        fun createRoute(bookId: String) = "detail/$bookId"
    }
}