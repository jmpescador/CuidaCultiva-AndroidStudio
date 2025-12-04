package com.example.cuidacultivo.data

data class PlagaInfo(
    val nombre: String,
    val alias: List<String>,
    val descripcion: String,
    val sintomas: String,
    val control: String
)

val plagasMap: Map<String, PlagaInfo> = mapOf(

    "Acaros_mora" to PlagaInfo(
        nombre = "Acaros de la Mora",
        alias = listOf("ácaros", "ácaros mora"),
        descripcion = "Los ácaros de la mora son diminutos artrópodos pertenecientes a la familia Eriophyidae. Su tamaño es microscópico, por lo que no se observan a simple vista; suelen verse únicamente con lupa o microscopio. Tienen forma alargada, vermiforme, y un color que varía entre crema y translúcido. Estos ácaros se alojan preferentemente en las hojas jóvenes, brotes tiernos y, en ocasiones, en los frutos, donde se alimentan de los tejidos vegetales. Su actividad aumenta en condiciones cálidas y secas, favoreciendo infestaciones rápidas durante la primavera y el verano.",
        sintomas = "Hojas deformadas o arrugadas, Decoloración, Crecimiento reducido, Superficie de la hoja áspera o rugosa.",
        control = "Monitoreo constante, Poda y eliminación de partes afectadas, Manejo del entorno, Tratamientos biológicos, Control químico, Fomento de fauna benéfica."
    ),

    "Antracnosis_mora" to PlagaInfo(
        nombre = "Antracnosis de la Mora",
        alias = listOf("antracnosis mora", "antracnosis de mora"),
        descripcion = "La antracnosis de la mora es una enfermedad fúngica causada principalmente por hongos del género Colletotrichum, que afectan hojas, tallos, flores y frutos. Estos patógenos se desarrollan con facilidad en ambientes húmedos, lluviosos y cálidos, especialmente cuando hay mala ventilación en el cultivo. El hongo puede sobrevivir en restos de plantas, en la corteza y en el suelo, lo que permite que la enfermedad reaparezca cada temporada si no se aplican medidas de manejo adecuadas.",
        sintomas = "Manchas hundidas y oscuras, Pequeñas lesiones circulares, Lesiones hundidas en los frutos, Frutos que no maduran correctamente.",
        control = "Uso de fungicidas específicos, Eliminación de restos vegetales afectados, Manejo de humedad y ventilación adecuada."
    ),

    "Antracnosis" to PlagaInfo(
        nombre = "Antracnosis del Café",
        alias = listOf("antracnosis"),
        descripcion = "La antracnosis del café es una enfermedad fúngica causada por especies del género Colletotrichum, siendo Colletotrichum kahawae una de las más relevantes en ciertos países productores. Este hongo afecta hojas, ramas, flores y especialmente frutos, en los cuales puede provocar pérdidas significativas en la producción. El patógeno prospera en ambientes húmedos, con lluvias frecuentes y temperaturas moderadas a cálidas. También puede sobrevivir en restos vegetales y tejidos infectados, lo que facilita su dispersión entre temporadas.",
        sintomas = "Manchas oscuras y hundidas, Frutos momificados, Cancros en ramas jóvenes.",
        control = "Realizar podas de formación y ventilación, Eliminar frutos secos y ramas afectadas, Implementar sombra adecuada."
    ),

    "Aranita_roja" to PlagaInfo(
        nombre = "Arañita Roja",
        alias = listOf("arañita roja", "aranita roja"),
        descripcion = "La arañita roja es un ácaro muy pequeño que se alimenta de la savia de las hojas, debilitando la planta. Se reproduce rápidamente en condiciones cálidas y secas.",
        sintomas = "Hojas con puntos amarillos o blanquecinos, Telarañas finas en el envés de las hojas, Marchitez y caída prematura de hojas.",
        control = "Monitoreo constante, Aplicación de acaricidas o aceites vegetales, Mantener humedad adecuada y ventilación en el cultivo."
    ),

    "Botrytis_mora" to PlagaInfo(
        nombre = "Botrytis de la Mora",
        alias = listOf("botrytis", "botrytis mora"),
        descripcion = "Botrytis cinerea es un hongo que provoca la pudrición gris de los frutos y flores de la mora, favorecido por humedad alta y mala ventilación.",
        sintomas = "Frutos blandos y con manchas grises, Flores marchitas, Presencia de moho gris en frutos y hojas.",
        control = "Eliminación de frutos y flores afectadas, Mejora de ventilación, Aplicación de fungicidas preventivos y correctivos."
    ),

    "Broca_del_cafe" to PlagaInfo(
        nombre = "Broca del Café",
        alias = listOf("broca", "broca del café", "broca del cafetal"),
        descripcion = "La broca del café (Hypothenemus hampei) es un escarabajo que perfora los frutos de café, depositando huevos en su interior, causando pérdida de calidad y cantidad de la producción.",
        sintomas = "Frutos con pequeños agujeros de entrada, Presencia de larvas dentro de los granos, Caída prematura de frutos.",
        control = "Recolección de frutos caídos, Uso de trampas, Aplicación de insecticidas selectivos y manejo integrado de plagas."
    ),

    "Chinche_chamusquina" to PlagaInfo(
        nombre = "Chinche Chamusquina",
        alias = listOf("chinche", "chinche chamusquina", "chinche de la hoja"),
        descripcion = "La chinche chamusquina es un insecto que se alimenta de la savia de hojas y brotes, provocando quemaduras y deformaciones.",
        sintomas = "Hojas con manchas secas o quemadas, Deformación de brotes, Disminución del crecimiento de la planta.",
        control = "Monitoreo de poblaciones, Aplicación de insecticidas específicos, Eliminación de hojas y brotes gravemente afectados."
    ),

    "Cochinilla_verde" to PlagaInfo(
        nombre = "Cochinilla Verde",
        alias = listOf("cochinilla verde", "cochinilla"),
        descripcion = "La cochinilla verde es un insecto que se alimenta de la savia de la planta, segregando melaza que favorece la aparición de fumagina.",
        sintomas = "Presencia de insectos verdes en tallos y hojas, Hojas pegajosas por melaza, Crecimiento debilitado de la planta.",
        control = "Control biológico con depredadores naturales, Aplicación de jabones insecticidas o aceites vegetales, Eliminación de partes muy afectadas."
    ),

    "Cochinillas_harinosas" to PlagaInfo(
        nombre = "Cochinillas Harinosas",
        alias = listOf("cochinilla harinosa", "cochinillas harinosas"),
        descripcion = "Son insectos cubiertos de una sustancia cerosa blanca que se alimentan de la savia, debilitando la planta y favoreciendo hongos saprófitos.",
        sintomas = "Manchas blancas algodonosas en hojas y tallos, Amarillamiento de hojas, Crecimiento lento y debilitado.",
        control = "Uso de depredadores naturales, Aplicación de aceites o jabones insecticidas, Retiro manual de colonias concentradas."
    ),

    "Escamas" to PlagaInfo(
        nombre = "Escamas",
        alias = listOf("escamas"),
        descripcion = "Las escamas son insectos chupadores que se fijan a tallos, ramas y hojas, alimentándose de la savia y debilitando la planta.",
        sintomas = "Protuberancias duras o blandas en tallos y hojas, Hojas amarillas, Presencia de melaza y fumagina.",
        control = "Control biológico con depredadores específicos, Aplicación de aceites horticulturales, Eliminación manual de colonias grandes."
    ),

    "Mildeo_polvose_mora" to PlagaInfo(
        nombre = "Mildiu Polvoso de la Mora",
        alias = listOf("mildeo", "mildeo polvoso", "mildeo mora"),
        descripcion = "Enfermedad fúngica causada por Oidium spp. que afecta hojas y frutos de la mora, favorecida por alta humedad y baja ventilación.",
        sintomas = "Polvo blanco o gris en hojas, Brotes deformados, Hojas marchitas y caída prematura.",
        control = "Aplicación de fungicidas específicos, Mejorar ventilación, Evitar exceso de humedad en el cultivo."
    ),

    "Minador_de_la_hoja" to PlagaInfo(
        nombre = "Minador de la Hoja",
        alias = listOf("minador de la hoja", "minador hoja"),
        descripcion = "Insecto cuyas larvas se alimentan del interior de las hojas, dejando galerías que reducen la fotosíntesis y el vigor de la planta.",
        sintomas = "Galerías o túneles blancos en hojas, Hojas amarillas o secas, Deformación de hojas y brotes.",
        control = "Eliminación de hojas afectadas, Uso de insecticidas específicos, Fomento de enemigos naturales."
    ),

    "Nematodos_del_cafe" to PlagaInfo(
        nombre = "Nematodos del Café",
        alias = listOf("nematodos", "nematodos del café"),
        descripcion = "Pequeños gusanos que se alimentan de raíces, causando daño a la absorción de agua y nutrientes, debilitando la planta.",
        sintomas = "Raíces con nudos o lesiones, Marchitez y amarillamiento de hojas, Crecimiento reducido y menor producción.",
        control = "Rotación de cultivos, Uso de variedades resistentes, Aplicación de nematicidas y compostaje de suelo saludable."
    ),

    "Phytophthora_mora" to PlagaInfo(
        nombre = "Phytophthora de la Mora",
        alias = listOf("phytophthora", "phytophthora mora"),
        descripcion = "Enfermedad causada por hongos del género Phytophthora que provoca podredumbre de raíces, tallos y frutos en moras.",
        sintomas = "Hojas marchitas, Cancros en tallos, Frutos podridos y caída prematura.",
        control = "Drenaje adecuado del suelo, Eliminación de partes infectadas, Aplicación de fungicidas sistémicos y preventivos."
    ),

    "Roya" to PlagaInfo(
        nombre = "Roya del Café",
        alias = listOf("roya", "roya del café"),
        descripcion = "Enfermedad causada por el hongo Hemileia vastatrix, que afecta hojas de café, reduciendo fotosíntesis y productividad.",
        sintomas = "Manchas anaranjadas o amarillas en hojas, Caída prematura de hojas, Debilitamiento general de la planta.",
        control = "Uso de variedades resistentes, Aplicación de fungicidas preventivos, Eliminación de hojas afectadas, Manejo de sombra y ventilación.",
    ),

    "Trips_mora" to PlagaInfo(
        nombre = "Trips de la Mora",
        alias = listOf("trips", "trips mora"),
        descripcion = "Insectos pequeños que se alimentan de hojas y flores, provocando decoloración y deformación de tejidos.",
        sintomas = "Hojas plateadas o descoloridas, Brotes deformados, Flores dañadas y caída de botones florales.",
        control = "Monitoreo y trampas adhesivas, Aplicación de insecticidas específicos, Fomento de enemigos naturales como depredadores de trips."
    )
)
