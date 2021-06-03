package com.ddev.questionapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.question_row.view.*
import java.util.*
import kotlin.collections.HashMap

class QuestionAdapter(private val onClickListener: QuestionOnClickListener) :
    RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {
    private var questionList = emptyList<QuestionData>()
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val admin = "7syKDKkrdTYMYbmpyRxpdmkLqAg2"
    private val currentUser: String = mAuth.currentUser.uid
    private lateinit var db: FirebaseFirestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.question_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.question_text.text = questionList[position].question.toString()
        holder.itemView.answer_text.text = questionList[position].answer.toString()
        holder.itemView.time_text.text = questionList[position].time?.let { Utils.getTimeAgo(it) }
        holder.itemView.react_count.text = questionList[position].reactCount.toString()

        db = FirebaseFirestore.getInstance()
        val qId: String? = questionList[position].questionId

        //for admin
        if (currentUser == admin) {

            holder.itemView.setOnClickListener {
                onClickListener.onClick(questionList[position], position)
            }
        }

        //set reacts count
        if (qId != null) {
            val docRef = db.collection("Question").document(qId).collection("Loves")
            docRef.addSnapshotListener { snapshot, e ->
                if (!snapshot!!.isEmpty) {
                    var count: Int = snapshot!!.size()
                    holder.itemView.react_count.text = "$count reacts"

                } else {
                    holder.itemView.react_count.text = "0 reacts"
                }
            }
        }
        //set reacts drawable
        if (qId != null) {
            db.collection("Question").document(qId).collection("Loves").document(currentUser).addSnapshotListener{ documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (documentSnapshot?.exists()!!){
                        holder.itemView.react_btn.setImageResource(R.drawable.ic_baseline_favorite_24_user)
                }else{
                    holder.itemView.react_btn.setImageResource(R.drawable.ic_baseline_favorite_24)
                }

            }
        }


        holder.itemView.react_btn.setOnClickListener {
            db = FirebaseFirestore.getInstance()
            val qId: String? = questionList[position].questionId
//            val time = HashMap<String,Long>()
//            time.put("time",questionList[position].time)
//            if (qId != null) {
//                db.collection("Question").document(qId).collection("Loves").document(currentUser).set(time)
//            }
            //like for user
            if (qId != null) {
                db.collection("Question").document(qId).collection("Loves").document(currentUser)
                    .get()
                    .addOnCompleteListener {
                        if (!it.result?.exists()!!) {
                            val time = HashMap<String, Long>()
                            time.put("time", questionList[position].time)
                            db.collection("Question").document(qId).collection("Loves")
                                .document(currentUser).set(time)

                        } else {
                            db.collection("Question").document(qId).collection("Loves")
                                .document(currentUser).delete()
                        }
                    }
            }


        }


    }


    override fun getItemCount(): Int {
        return questionList.size
    }

    fun setData(newList: List<QuestionData>) {
        notifyDataSetChanged()
        questionList = newList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


}

