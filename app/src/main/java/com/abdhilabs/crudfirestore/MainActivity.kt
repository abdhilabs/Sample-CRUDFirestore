package com.abdhilabs.crudfirestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdhilabs.crudfirestore.adapter.NoteAdapter
import com.abdhilabs.crudfirestore.model.Note
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private var mAdapter: NoteAdapter? = null

    private var firestoreDB: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firestoreDB = FirebaseFirestore.getInstance()

        loadNotesList()

        firestoreListener = firestoreDB!!.collection("notes")
            .addSnapshotListener(EventListener { documentSnapshot, e ->
                if (e != null) {
                    Log.e(TAG, "Listen Failed!", e)
                    return@EventListener
                }
                val noteList = mutableListOf<Note>()


                    for (doc in documentSnapshot!!) {
                        val note = doc.toObject(Note::class.java)
                        note.id = doc.id
                        noteList.add(note)
                    }
                mAdapter = NoteAdapter(noteList, applicationContext, firestoreDB!!)
                rvList.adapter = mAdapter
            })
    }

    override fun onDestroy() {
        super.onDestroy()

        firestoreListener!!.remove()
    }

    private fun loadNotesList() {
        firestoreDB!!.collection("notes")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val noteList = mutableListOf<Note>()

                    for (doc in task.result!!) {
                        val note = doc.toObject<Note>(Note::class.java)
                        note.id = doc.id
                        noteList.add(note)
                    }
                    mAdapter = NoteAdapter(noteList, applicationContext, firestoreDB!!)
                    rvList.apply {
                        layoutManager = LinearLayoutManager(applicationContext)
                        itemAnimator = DefaultItemAnimator()
                        adapter = mAdapter
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.exception)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item != null){
            if (item.itemId == R.id.addNote){
                val intent = Intent(this,NoteActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
