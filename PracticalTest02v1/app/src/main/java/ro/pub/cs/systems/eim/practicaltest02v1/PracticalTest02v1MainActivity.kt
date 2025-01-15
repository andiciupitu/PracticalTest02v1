package ro.pub.cs.systems.eim.practicaltest02v1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PracticalTest02v1MainActivity : AppCompatActivity() {

    private lateinit var prefixEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var suggestionReceiver: SuggestionReceiver
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v1_main)

        // Inițializare UI
        resultTextView = findViewById(R.id.resultTextView)

        prefixEditText = findViewById(R.id.prefixEditText)
        searchButton = findViewById(R.id.searchButton)

        // Configurare BroadcastReceiver
        suggestionReceiver = SuggestionReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            registerReceiver(
                suggestionReceiver,
                IntentFilter("com.example.AUTOCOMPLETE_SUGGESTION"),
                Context.RECEIVER_NOT_EXPORTED
            )
        }
        /*else {
            registerReceiver(
                suggestionReceiver,
                IntentFilter("com.example.AUTOCOMPLETE_SUGGESTION")
            )
        }*/

        // Configurare buton de căutare
        searchButton.setOnClickListener {
            val query = prefixEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                fetchAutocompleteSuggestions(query)
            } else {
                Toast.makeText(this, "Introduceți un prefix!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchAutocompleteSuggestions(query: String) {
        RetrofitInstance.api.getAutocompleteSuggestions(query = query).enqueue(object : Callback<List<Any>> {
            override fun onResponse(call: Call<List<Any>>, response: Response<List<Any>>) {
                if (response.isSuccessful) {
                    Log.d("AutocompleteResponse", "Răspuns complet: ${response.body().toString()}")

                    val result = response.body() as? List<Any>
                    val suggestions = result?.getOrNull(1) as? List<String>
                    Log.d("ParsedSuggestions", "Sugestii parsate: ${suggestions.toString()}")

                    if (suggestions != null && suggestions.size >= 3) {
                        val thirdSuggestion = suggestions[2]
                        Log.d("ThirdSuggestion", "A treia sugestie: $thirdSuggestion")
                        sendThirdSuggestionBroadcast(thirdSuggestion)
                    } else {
                        Log.d("ThirdSuggestion", "Nu există o a treia sugestie.")
                    }
                } else {
                    Log.e("AutocompleteError", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Any>>, t: Throwable) {
                Log.e("AutocompleteFailure", "Failure: ${t.message}")
            }
        })
    }

    private fun sendThirdSuggestionBroadcast(thirdSuggestion: String) {
        val intent = Intent("com.example.AUTOCOMPLETE_SUGGESTION")
        intent.setPackage(packageName) // Asigură că broadcast-ul este destinat doar aplicației
        intent.putExtra("thirdSuggestion", thirdSuggestion)
        sendBroadcast(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(suggestionReceiver)
    }

    // Receiver pentru gestionarea sugestiilor
    inner class SuggestionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val thirdSuggestion = intent.getStringExtra("thirdSuggestion")
            if (thirdSuggestion != null) {
                Toast.makeText(context, "Sugestia primită: $thirdSuggestion", Toast.LENGTH_SHORT).show()
                resultTextView.setText(thirdSuggestion)
            }
        }
    }
}
