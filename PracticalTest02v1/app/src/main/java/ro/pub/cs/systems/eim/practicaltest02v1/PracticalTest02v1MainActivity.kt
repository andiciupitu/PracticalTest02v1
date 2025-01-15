package ro.pub.cs.systems.eim.practicaltest02v1

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PracticalTest02v1MainActivity : AppCompatActivity() {

    private lateinit var prefixEditText: EditText
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practical_test02v1_main)

        // Inițializare UI
        prefixEditText = findViewById(R.id.prefixEditText)
        searchButton = findViewById(R.id.searchButton)

        // Butonul pentru căutare
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
                    // Afișare răspuns complet în Logcat
                    Log.d("AutocompleteResponse", "Răspuns complet: ${response.body().toString()}")

                    // Parsarea stringurilor de autocomplete
                    val result = response.body() as? List<Any>
                    val suggestions = result?.getOrNull(1) as? List<String>
                    Log.d("ParsedSuggestions", "Sugestii parsate: ${suggestions.toString()}")

                    // Afișarea celei de-a treia intrări, dacă există
                    if (suggestions != null && suggestions.size >= 3) {
                        Log.d("ThirdSuggestion", "A treia sugestie: ${suggestions[2]}")
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
}
