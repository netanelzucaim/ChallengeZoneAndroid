package com.idz.colman24class2.utils.extensions

import com.google.firebase.Timestamp
import java.util.Date

val Long.toFirebaseTimestamp: Timestamp
    get() = Timestamp(Date(this))