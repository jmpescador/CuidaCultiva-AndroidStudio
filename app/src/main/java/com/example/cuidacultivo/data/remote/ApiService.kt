package com.example.cuidacultivo.data.remote

import com.example.cuidacultivo.data.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("/usuarios")  // Solo el endpoint relativo
    suspend fun crearUsuario(@Body usuario: Usuario): Response<Usuario>

    @GET("/usuarios")
    suspend fun obtenerUsuarios(): Response<List<Usuario>>

    @GET("usuarios-cedula")
    suspend fun obtenerPorCedula(@Query("cedula") cedula: String): Response<Usuario>

    @PUT("usuario/cedula/{cedula}")
    suspend fun actualizarUsuarioPorCedula(
        @Path("cedula") cedula: String,
        @Body usuario: Usuario
    ): Response<Usuario>

    // ⭐ AÑADIR ESTE MÉTODO
    @POST("/historial")
    suspend fun enviarHistorial(@Body request: HistorialRequest): Response<Unit>
}
