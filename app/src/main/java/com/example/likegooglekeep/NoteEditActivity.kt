package com.example.likegooglekeep

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_note_edit.*

class NoteEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activeThemeSaved = intent.getBooleanExtra("activeThemeSaved", false)
        Log.d("MainActivity", "tēma " + activeThemeSaved  )
        if (activeThemeSaved) {
            setTheme(R.style.CustomAppTheme);

        }
        setContentView(R.layout.activity_note_edit)




        if (intent.getBooleanExtra("labots",false)){

            noteEdit_editTextTitle.setText(intent.getStringExtra("title"))
            noteEdit_edittextBody.setText(intent.getStringExtra("body"))
            noteEdit_buttonAdd.setText(R.string.NoteEdit_buttonEdit)

        }


        noteEdit_buttonAdd.setOnClickListener {

           val title = noteEdit_editTextTitle.text.toString()
            val body = noteEdit_edittextBody.text.toString()



            if (!title.isEmpty() && !body.isEmpty()) {

                val activityResult = Intent().apply {
                    putExtra("noteId", intent.getStringExtra("noteId"))
                    putExtra("index", intent.getIntExtra("index",0))
                    putExtra("labots", intent.getBooleanExtra("labots", false))
                    putExtra("replayTitle", title)
                    putExtra("replayBody", body)
                }
                setResult(Activity.RESULT_OK, activityResult)
                finish()
            }else{
                Toast.makeText(this, R.string.toast_aizpildietLaukus, Toast.LENGTH_SHORT).show()
            }

        }

        noteEdit_buttonCancel.setOnClickListener {
            //cancel
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

    }
}