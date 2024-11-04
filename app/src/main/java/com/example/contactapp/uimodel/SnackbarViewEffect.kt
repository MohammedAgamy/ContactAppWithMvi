package com.example.contactapp.uimodel

sealed class SnackbarViewEffect {
    data class ShowSnackbarView(val massage:String , val action:String? = null):SnackbarViewEffect()
}