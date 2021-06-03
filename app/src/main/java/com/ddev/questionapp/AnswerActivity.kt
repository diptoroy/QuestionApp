package com.ddev.questionapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_answer.*
import kotlinx.android.synthetic.main.activity_question_add.*
import java.util.*

class AnswerActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var dbupdate: DocumentReference
    private lateinit var storageReference: StorageReference
    private lateinit var q: String
    private lateinit var a: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer)
        val bundle:Bundle? = intent.extras
        q = bundle!!.getString("question").toString()
        question_answer_edit_text.text = Editable.Factory.getInstance().newEditable(q)
        a = bundle!!.getString("answer").toString()
        answer_answer_edit_text.text = Editable.Factory.getInstance().newEditable(a)

        db = FirebaseFirestore.getInstance()
        var question = QuestionData()

        answer_btn.setOnClickListener {
            val answer: String = answer_answer_edit_text.text.toString().trim()
            progressBar3.visibility = View.VISIBLE
            val time = System.currentTimeMillis()
            val questionData = QuestionData(q, answer, question.userId, time)
                db.collection("question").document(q).update("answer",questionData.answer)
                    .addOnCompleteListener{
                        if (it.isSuccessful) {
                            progressBar3.visibility = View.INVISIBLE
                            Log.d("Answer", "${it.result}")
                        }else{
                            progressBar3.visibility = View.VISIBLE
                            Log.d("error", "${it.exception}")
                        }
                    }
            }



    }

//    private fun addQuestion(question: String, answer: String) {
//        val userId: String = "6gn2J0yCsMXqYlefyxU84GDthve2"
//        progressBar.visibility = View.VISIBLE
//        val time = System.currentTimeMillis()
//
//        val questionData = QuestionData(question, answer, userId, time)
//        dbupdate.collection("question").document("").update(questionData).addOnCompleteListener {task ->
//            if (task.isSuccessful){
//                progressBar.visibility = View.INVISIBLE
//                val intent = Intent(this,QuestionActivity::class.java)
//                startActivity(intent)
//                Log.d("question", "$task")
//            }else{
//                progressBar.visibility = View.VISIBLE
//            }
//        }
//
//    }
}

