package io.github.nullptrx.files.example.coil

import coil.request.ImageRequest
import coil.transition.CrossfadeTransition

fun ImageRequest.Builder.fadeIn(durationMillis: Int): ImageRequest.Builder =
  apply {
    placeholder(android.R.color.transparent)
    transitionFactory(CrossfadeTransition.Factory(durationMillis, true))
  }
