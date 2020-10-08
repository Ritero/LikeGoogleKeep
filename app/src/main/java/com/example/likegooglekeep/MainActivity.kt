package com.example.likegooglekeep

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_note_item.view.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

val TAG: String = "MainActivity"
    val db = Firebase.firestore
    val saraksts: MutableList<Main_NoteItem> = mutableListOf<Main_NoteItem>()
    val DETAIL_REQUEST = 1231
    private lateinit var adapter: NoteItemRecyclerAdapter

    public interface myOnClickListener{
        fun onClick( index: Int, id: String, title: String, body:String)
    }
    val EXTRA_TEXT = "ShareActivity"
    public interface mySharingClickListener{
        fun onSharingClick(title: String, body: String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var activeThemeSaved = getPreferences(Context.MODE_PRIVATE).getBoolean("Theme", false)

        if (activeThemeSaved) {
            setTheme(R.style.CustomAppTheme);
        }else{
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_main)

        val state = savedInstanceState
            ?.getParcelable<sarakstsSaved>("state")

        if (state != null) {
            Log.d(TAG, "iegūtais size ir ${state.c[0].c.size}")
            val sarakstaKopoums = state.c[0].c
            Log.d(TAG, "iegūtais size ir ${state}")
            for (note in sarakstaKopoums) {
                Log.d(TAG, "iegūtais parceble ${note.title} un ${note.body}")
                saraksts.add(Main_NoteItem(note.id, note.title, note.body))
            }


        }



    adapter = NoteItemRecyclerAdapter(saraksts, object: myOnClickListener{
        override fun onClick(index: Int, id: String, title:String, body: String) {

            val intent = Intent(this@MainActivity, NoteEditActivity::class.java)
            Log.d(TAG, "Savojam aktīvo tēmu" + activeThemeSaved)
            intent.putExtra("activeThemeSaved", activeThemeSaved)
            intent.putExtra("index", index )
            intent.putExtra("noteId", id)
            intent.putExtra("labots", true)
            intent.putExtra("title", title)
            intent.putExtra("body", body)
            startActivityForResult(intent, DETAIL_REQUEST)

        }

    },object: mySharingClickListener{
        override fun onSharingClick(title: String, body: String) {
            Log.d(TAG, "aiziet šis onsharings")

//            val sendIntent: Intent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
//                type = "text/plain"
//            }
//
//            val shareIntent = Intent.createChooser(sendIntent, null)
//            startActivity(shareIntent)

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "")
                type = "text/plain"
            }
            startActivity(sendIntent)

//            val sendIntent = Intent().apply {this@MainActivity
//                action = Intent.ACTION_SEND
//                data = Uri.parse("mailto")
//                putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
//                putExtra(Intent.EXTRA_SUBJECT, title)
//                putExtra(Intent.EXTRA_TEXT, body)
//            }
//
//
//            if (intent.resolveActivity(packageManager) != null) {
//                Log.d(TAG, "esam ifā")
//                startActivity(sendIntent)
//            }else{
//                Log.d(TAG, "cant do this" )
//            }
        }
    })
        main_recyclerView_notes.adapter = adapter
        main_buttonAdd.setOnClickListener{

            val intent = Intent(this, NoteEditActivity::class.java)
            intent.putExtra("activeThemeSaved", activeThemeSaved)
            startActivityForResult(intent, DETAIL_REQUEST)

        }

    }
    data class Note(
        val title: String = "",
        val body: String = ""

    )



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "saglabāsim parceble ")
        var emtyList = mutableListOf<sarakstsSaved>()

        var sarakstsSavedItem = mutableListOf<sarakstsSaved>()

        for (ieraksts in saraksts) {

            sarakstsSavedItem.add(sarakstsSaved(ieraksts.id,ieraksts.title,ieraksts.bodyText,emtyList))

        }

        val state = sarakstsSaved(

            "id",
            "noteBody",
            "body",
            listOf(sarakstsSaved("","","",sarakstsSavedItem))
        )
        outState.putParcelable("state", state)
    }


@Parcelize
data class sarakstsSaved(
    val id: String,
    val title: String,
    val body: String,
    val c: List<sarakstsSaved>
): Parcelable



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == DETAIL_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.let {

                data?.let{
                    val index = data.getIntExtra("index", 0)
                    var noteId = data.getStringExtra("noteId")
                    if (noteId == null) {
                       noteId = (title.substring(title.length-1)+Random.nextInt(0,100))
                    }
                    val title = data.getStringExtra("replayTitle")
                    val body = data.getStringExtra("replayBody")
                    val labots = data.getBooleanExtra("labots", false)

                    if (labots) {
                        Log.d(TAG, labots.toString())
                        Log.d(TAG, saraksts.toString())

                        saraksts[index].bodyText = body
                        saraksts[index].title = title

                        Log.d(TAG, saraksts[index].bodyText)
                        adapter.notifyDataSetChanged()
             }
                    else{

                        saraksts.add(Main_NoteItem(noteId, title,body))
                        adapter.notifyDataSetChanged()
                    }
                }

            }
        }

    }
    //---------

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        var activeTheme = resources.getString(R.string.defaultTheme)
var activeThemeBool = false
        val intent = Intent(this, MainActivity::class.java)
    when (item.itemId){
        R.id.defaultTheme -> {
            Log.d(TAG, "default theme")
            theme.applyStyle(R.style.AppTheme, true)
            setTheme(R.style.AppTheme);
            activeThemeBool = false

        }
        R.id.customTheme -> {
            Log.d(TAG, "custom theme")
            setTheme(R.style.CustomAppTheme);
            activeTheme = resources.getString(R.string.customtTheme)
            activeThemeBool = true
        }
    }
        getPreferences(Context.MODE_PRIVATE) .edit()
            .putBoolean("Theme", activeThemeBool) .apply()

        startActivity(intent);
        finish()
        Toast.makeText(this, (resources.getString(R.string.themeText) + " " + activeTheme),Toast.LENGTH_SHORT).show()
        return super.onOptionsItemSelected(item)
    }

    class NoteItemRecyclerAdapter(private val items: MutableList<Main_NoteItem>, val listener: myOnClickListener, val sharinListener: mySharingClickListener):RecyclerView.Adapter<NoteItemRecyclerAdapter.NoteViewHolder>() {

        class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.main_note_item, parent, false)
            return NoteViewHolder(view)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
            val item = items[position]
            val context = holder.itemView.context
            holder.itemView.noteItemTitle.text = item.title
            holder.itemView.noteItemText.text = item.bodyText
            holder.itemView.setOnClickListener {

                sharinListener.onSharingClick(item.title, item.bodyText)
            }
            holder.itemView.noteImageViewEdit.setOnClickListener {

                listener.onClick(position, item.id, item.title, item.bodyText)

            }

                holder.itemView.noteImageViewDelete.setOnClickListener {
                    Toast.makeText(context, R.string.Main_resourceDeleted, Toast.LENGTH_SHORT)
                        .show()
                    items.removeAt(position)
                    notifyDataSetChanged()
                }

            }
        }

}