package com.example.cuidacultivo.data

import com.example.cuidacultivo.R

data class PlagaInfo(
    val nombre: String,
    val alias: List<String>,
    val descripcion: String,
    val sintomas: String,
    val control: String,
    val imagenRes: Int
)

val plagasMap: Map<String, PlagaInfo> = mapOf(

    "Broca_del_cafe" to PlagaInfo(
        nombre = "Broca del Café",
        alias = listOf("broca", "broca del café", "broca del cafetal"),
        descripcion = "La broca del café (Hypothenemus hampei) es un escarabajo que perfora los frutos de café, depositando huevos en su interior, causando pérdida de calidad y cantidad de la producción.",
        sintomas = "Frutos con pequeños agujeros de entrada, Presencia de larvas dentro de los granos, Caída prematura de frutos.",
        control = "Recolección de frutos caídos, Uso de trampas, Aplicación de insecticidas selectivos y manejo integrado de plagas.",
        imagenRes = R.drawable.broca_cafe
    ),
    "Cochinillas_harinosas" to PlagaInfo(
        nombre = "Cochinillas Harinosas",
        alias = listOf("cochinilla harinosa", "cochinillas harinosas"),
        descripcion = "Son insectos cubiertos de una sustancia cerosa blanca que se alimentan de la savia, debilitando la planta y favoreciendo hongos saprófitos.",
        sintomas = "Manchas blancas algodonosas en hojas y tallos, Amarillamiento de hojas, Crecimiento lento y debilitado.",
        control = "Uso de depredadores naturales, Aplicación de aceites o jabones insecticidas, Retiro manual de colonias concentradas.",
        imagenRes = R.drawable.cochinillas_harinosas
    ),
    "Minador_de_la_hoja" to PlagaInfo(
        nombre = "Minador de la Hoja",
        alias = listOf("minador de la hoja", "minador hoja"),
        descripcion = "Insecto cuyas larvas se alimentan del interior de las hojas, dejando galerías que reducen la fotosíntesis y el vigor de la planta.",
        sintomas = "Galerías o túneles blancos en hojas, Hojas amarillas o secas, Deformación de hojas y brotes.",
        control = "Eliminación de hojas afectadas, Uso de insecticidas específicos, Fomento de enemigos naturales.",
        imagenRes = R.drawable.minador_hoja
    ),
)
