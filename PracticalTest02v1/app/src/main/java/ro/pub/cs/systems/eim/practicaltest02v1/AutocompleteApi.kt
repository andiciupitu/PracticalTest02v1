package ro.pub.cs.systems.eim.practicaltest02v1

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AutocompleteApi {
    @GET("complete/search")
    fun getAutocompleteSuggestions(
        @Query("client") client: String = "chrome",
        @Query("q") query: String
    ): Call<List<Any>>
}
