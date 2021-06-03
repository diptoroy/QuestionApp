package com.ddev.questionapp

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

data class QuestionData(
        val question: String? = null,
        val answer: String? = null,
        val userId: String? = null,
        val time: Long = 0L,
        val questionId: String? = null,
        val reactCount: Int? = null
)
