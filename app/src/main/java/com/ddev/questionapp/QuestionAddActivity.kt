package com.ddev.questionapp

import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_answer.*
import kotlinx.android.synthetic.main.activity_question_add.*
import kotlinx.android.synthetic.main.activity_question_add.question_image
import kotlinx.android.synthetic.main.question_row.*
import java.util.*


class QuestionAddActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private lateinit var resultUri: Uri
    private val admin: String = "7syKDKkrdTYMYbmpyRxpdmkLqAg2"
    private lateinit var  questionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_add)

        val bundle:Bundle? = intent.extras
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        if (mAuth.currentUser.uid == admin){
            answer_edit_text.visibility = View.VISIBLE
        }else if(mAuth.currentUser.uid != admin){
            answer_edit_text.visibility = View.INVISIBLE
        }

        if (bundle != null){
            submit_btn.text = "Update"
            val question: String = bundle!!.getString("question").toString()
            question_edit_text.text = Editable.Factory.getInstance().newEditable(question)
            val answer: String = bundle!!.getString("answer").toString()
            answer_edit_text.text = Editable.Factory.getInstance().newEditable(answer)
            questionId = bundle!!.getString("questionId").toString()
            val qImage: String = bundle!!.getString("image").toString()
            Glide.with(this).load(bundle!!.getString(qImage)).into(question_image);
        }else{
            submit_btn.text = "Submit"
        }

        question_image.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(this);
        }

        submit_btn.setOnClickListener {
            val question: String = question_edit_text.text.toString().trim()
            val answer: String = answer_edit_text.text.toString().trim()
            if (bundle != null){
                delete_btn.visibility = View.VISIBLE
                val qId: String = questionId
                Log.d("question id is","$qId")
                updateQuestion(qId, question, answer)
            }else{
                delete_btn.visibility = View.INVISIBLE
                val qId: String = UUID.randomUUID().toString()
                addQuestion(qId,question, answer)
            }

        }

        delete_btn.setOnClickListener {
            deleteQuestion(questionId)
        }
        

    }

    private fun deleteQuestion(questionId: String) {
        db.collection("Question").document(questionId).delete().addOnCompleteListener {
            progressBar2.visibility = View.VISIBLE
            if (it.isSuccessful){
                progressBar2.visibility = View.INVISIBLE
                val intent = Intent(this,QuestionActivity::class.java)
                startActivity(intent)
                Log.d("Deleted!","$it")
                Toast.makeText(this, "Data Updated!!", Toast.LENGTH_SHORT).show();
            }else{
                progressBar2.visibility = View.VISIBLE
                Log.d("error","${it.exception}")
            }
        }
    }

    private fun updateQuestion(qId: String, question: String, answer: String) {
        progressBar2.visibility = View.VISIBLE
        db.collection("Question").document(qId)
            .update("question",question,"answer",answer)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    progressBar2.visibility = View.INVISIBLE
                    val intent = Intent(this,QuestionActivity::class.java)
                    startActivity(intent)
                    Log.d("updated!","$it")
                    Toast.makeText(this, "Data Updated!!", Toast.LENGTH_SHORT).show();
                    finish()
                }else{
                    progressBar2.visibility = View.VISIBLE
                    Log.d("error","${it.exception}")
                }
            }

    }

    private fun addQuestion(qId: String, question: String, answer: String) {
        val userId: String = mAuth.currentUser.uid
        progressBar2.visibility = View.VISIBLE
        val time = System.currentTimeMillis()

        val questionData = QuestionData(question, answer, userId, time, qId)

        db.collection("Question").document(qId).set(questionData).addOnCompleteListener {
            if (it.isSuccessful) {
                progressBar2.visibility = View.INVISIBLE
                val intent = Intent(this, QuestionActivity::class.java)
                startActivity(intent)
                finish()
                Log.d("question", "$it")
            } else {
                progressBar2.visibility = View.VISIBLE
            }
        }


//        var imagePath: StorageReference =
//            storageReference.child("question_image").child("$qId.jpg")
//
//            imagePath.putFile(resultUri).addOnCompleteListener { image ->
//                if (image.isSuccessful) {
//                    val downloadUri = image.result
//                    val imagePath: String = downloadUri.toString()
//                    val questionData = QuestionData(question, answer, userId, time, qId,imagePath)
//                    db.collection("Question").add(questionData).addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            progressBar2.visibility = View.INVISIBLE
//                            val intent = Intent(this, QuestionActivity::class.java)
//                            startActivity(intent)
//                            Log.d("question", "$task")
//                        } else {
//                            progressBar2.visibility = View.VISIBLE
//                        }
//                    }
//                }
//            }

//
//        val questionImage = storageReference.child("images/question.jpg")
//        questionImage.putFile(resultUri).addOnCompleteListener{
//
//        }.addOnFailureListener{
//
//        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                resultUri = result.uri
                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,resultUri)
                question_image.setImageBitmap(bitmap)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
}