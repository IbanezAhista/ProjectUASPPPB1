package ibanez.pppb1.projectuas.network

import ibanez.pppb1.projectuas.database.Note
import ibanez.pppb1.projectuas.database.NoteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("note_table")
    suspend fun createNote(@Body note: Note): Response<Map<String, String>>

    @GET("note_table")
    suspend fun getNote(): Response<List<NoteResponse>>

    @POST("note_table/{id}")
    suspend fun updateNote(
        @Path("id") id: String,
        @Body note: Note
    ): Response<Note>

    @DELETE("note_table/{id}")
    suspend fun deleteNote(
        @Path("id") id: String
    ): Response<Void>
}
