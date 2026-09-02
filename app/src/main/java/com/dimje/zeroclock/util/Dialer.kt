package com.dimje.zeroclock.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openDialer(number: String) {
    startActivity(
        Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}
