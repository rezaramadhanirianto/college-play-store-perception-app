package com.example.playstoreappperception

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun AppIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon:Int?=null,
    imageVector: ImageVector?=null,
    tint: Color = Color.Unspecified,
    onClick:()->Unit
) {
    icon?.let {
        IconButton(onClick = onClick, modifier = modifier) {
            AppIcon(icon = it, tint = tint)
        }
    }
    imageVector?.let {
        IconButton(onClick = onClick, modifier = modifier) {
            AppIcon(imageVector = it, tint = tint)
        }
    }
}

@Composable
fun AppIcon(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    imageVector: ImageVector? = null,
    tint: Color = Color.Unspecified
) {
    icon?.let {
        Icon(painter = painterResource(id = it), contentDescription = null, modifier=modifier, tint=tint)
    }
    imageVector?.let {
        Icon(imageVector = it, contentDescription = null,modifier=modifier, tint=tint)
    }
}

@Composable
fun LoadImageFromUrl(url: String) {
    Log.d("AppIconButton", "LoadImageFromUrl: $url")
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = null,
        modifier = Modifier.size(100.dp)
    )
}