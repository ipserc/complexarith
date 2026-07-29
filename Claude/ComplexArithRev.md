# ROL Y CONTEXTO
Actúa como un **Principal Java Performance Engineer**, **Doctor en Matemáticas Aplicadas** y **Doctor en Ciencias Físicas y Mecánica Cuántica** especializado en Análisis Complejo y Computación Científica de Alto Rendimiento (HPC). Tu objetivo es auditar, refactorizar, optimizar y reestructurar un módulo/programa en Java dedicado a cálculos aritméticos y geométricos avanzados en el plano complejo ($\mathbb{C}$).

---

# OBJETIVOS PRINCIPALES
1. **Rendimiento Extremo (CPU & Throughput > Memoria):** Prioriza la velocidad de ejecución y baja latencia por encima del ahorro de memoria. Aplica optimizaciones de bajo nivel, reducción de sobrecarga de GC (Garbage Collector), unrolling de bucles, primitive arrays y Vector API si aplica.
2. **Rigor Matemático:** Garantizar la precisión numérica en análisis complejo advanced (funciones trascendentes $e^z, \ln z, z^w$, trigonometría compleja, transformaciones geométricas, cortes de rama y manejo estricto de $\pm\infty$ y $\text{NaN}$).
3. **Arquitectura y Limpieza:** Reestructurar el código siguiendo patrones limpios de Java moderno (Java 17/21+), manteniendo APIs fluidas pero sin sacrificar ni un nanosegundo de rendimiento.

---

# INSTRUCCIONES DE REVISIÓN Y REFRACTORIZACIÓN

## 1. Rendimiento y Optimización de Bajo Nivel
- **Eliminación de Bucles e Inmutabilidad Innecesaria en Hot Paths:** Evita instanciar objetos `Complex` masivamente en bucles críticos. Utiliza APIs in-place o layout de estructuras planas (`double[]` intercalados $Re, Im$ o arreglos paralelos) para aprovechar la memoria caché y la vectorización (SIMD).
- **Aprovechamiento de Funciones Intrínsecas:** Reemplaza operaciones matemáticas costosas con equivalentes altamente optimizados o aproximaciones vectoriales cuando no comprometan la precisión requerida.
- **Evitar Boxing/Unboxing:** Garantizar el uso exclusivo de primitivos (`double`, `long`).
- **Java Vector API / JEPs:** Si el entorno ejecuta Java 17+, sugiere o implementa el uso de `jdk.incubator.vector` para vectorizar operaciones en arreglos de números complejos.

## 2. Precisión y Dominio Matemático ($\mathbb{C}$)
- **Análisis de Cortes de Rama (Branch Cuts):** Revisa explícitamente funciones como $\log(z)$, $\sqrt{z}$, $\text{asin}(z)$, $\text{atan}(z)$ y potencias $z^w$ asegurando que el argumento principal $\text{Arg}(z) \in (-\pi, \pi]$ siga el estándar IEEE 754 / ISO C99.
- **Estabilidad Numérica:** Previene desbordamientos (overflow/underflow) en la magnitud $|z| = \sqrt{x^2 + y^2}$ mediante el algoritmo de Moler-Morrison o `Math.hypot(x, y)`.
- **Geometría en el Plano Complejo:** Revisa las transformaciones complejas (traslaciones, rotaciones, homotecias, transformaciones de Möbius $f(z) = \frac{az+b}{cz+d}$) garantizando que las matrices o representaciones asociadas sean computacionalmente eficientes.

## 3. Estructura y Código Java
- Proporciona un código moderno, modular y autodocumentado.
- Incluye comentarios Javadoc detallados con las fórmulas matemáticas representadas en notación LaTeX/Unicode.
- Diseña una suite de benchmarks teóricos (o arquetipo de JMH - Java Microbenchmark Harness) para verificar las mejoras de latencia/throughput.

---

# ENTREGABLE ESPERADO
1. **Informe de Auditoría Inicial:** Breve resumen de los *bottlenecks* detectados, fallos de precisión matemática o ineficiencias de memoria/CPU en el código actual.
2. **Código Refactorizado y Optimizado:** La implementación completa en Java con comentarios claros sobre dónde y por qué se ganó rendimiento.
3. **Explicación de las Decisiones Matemáticas y de Arquitectura:** Justificación de las técnicas empleadas (ej. vectorización, manejo de branch cuts, evitación de alocaciones).

---

# ESTADO DE LA SESIÓN Y CONTINUACIÓN (leer esto primero al retomar)

> Este bloque es un documento vivo de continuidad. Está escrito para que, si se retoma el trabajo en una sesión nueva sin memoria de esta conversación, se pueda reconstruir todo el contexto, las reglas acordadas y el estado exacto en el que se dejó el trabajo, sin tener que releer todo el historial de git ni redescubrir los mismos bugs.

## Dónde estamos

- **Repo:** `C:\Users\josel\workspace-eclipse\complexarith_github` (proyecto Eclipse/Java personal, NO es un repo limpio: tiene muchísimos ficheros modificados/sin trackear que son trabajo propio del usuario, ajenos a esta tarea — **nunca tocarlos ni incluirlos en un commit de esta tarea**). En esta sesión, además de los ficheros ya conocidos, `src/com/ipserc/arith/matrixcomplex/MatrixComplex.java` apareció con un diff enorme (~1085 inserciones/266 borrados) de trabajo local del usuario sin commitear — **confirmado desde el primer `git status` de la sesión, no algo que se haya causado aquí; nunca usarlo como base de compilación de verificación ni tocarlo**.
- **Fichero de trabajo único hasta ahora:** `src/com/ipserc/arith/complex/Complex.java` (ahora ~4125 líneas, creció por la documentación añadida). No se ha tocado `MatrixComplex.java`, `VectorComplex.java` ni ningún otro fichero del proyecto.
- **Rama:** `master`. Se hacen commits directos, no hay rama de feature.
- **Último commit de esta tarea:** `72fd463` "Complex: documenta la familia *Red__ y sqrroot__ como código muerto confirmado" (29 julio 2026). Con este commit se completó el plan de 6 fases acordado con el usuario para esta segunda sesión de revisión (ver lista completa más abajo).
- Esta sesión completó las 6 fases planificadas de principio a fin — no se paró a mitad, a diferencia de la sesión anterior. Quedan ideas pendientes explícitamente fuera de alcance (ver más abajo), no por falta de tiempo sino por decisión consciente del usuario sobre riesgo/beneficio.

## Permisos y preferencias del usuario (vigentes, no hace falta re-preguntar)

Todo lo de la sesión anterior sigue vigente (permiso completo dentro del repo, permiso para corregir bugs matemáticos encontrados, permiso para ejecutar tests sin preguntar, un commit por fase, preguntar con `AskUserQuestion` solo ante decisiones reales de alcance/riesgo, responder en español). Nuevo esta sesión:

- **El usuario pidió explícitamente: "antes de iniciar cada fase, dame un resumen de lo que se va a hacer en ella"** — antes de tocar código en una fase nueva, dar un resumen breve de qué se va a hacer y esperar confirmación (normalmente basta un "sí, adelante"), no lanzarse directamente a editar.
- **Antes de usar `EnterPlanMode` para planificar una revisión, explorar primero con un agente (`Explore`/fork) las zonas concretas a revisar** para poder priorizar con datos reales (nº de call-sites, código muerto confirmado, bugs visibles) en vez de un plan genérico — así se hizo al arrancar esta sesión y funcionó bien.
- El usuario dio contexto de dominio importante que cambió un diagnóstico a mitad de fase: **`ZERO_THRESHOLD_APPROX` existe porque el usuario detectó que Mathematica (Wolfram) y GNU Octave calculan mal los autovectores por problemas de precisión, y lo montó como un experimento para reproducir/controlar ese problema — "no tenía otra intención"**. No es un requisito de diseño a preservar a toda costa; si un cambio hace el comportamiento por defecto más exacto, el usuario prefiere eso ("prefiero que el cálculo sea lo más exacto posible"). Relevante para cualquier trabajo futuro que toque `ZERO_THRESHOLD_APPROX`/`EXACT`.
- El proyecto se desarrolló originalmente en Linux y se portó a Windows recientemente (el día antes de esta sesión); **el terminador de línea "natural" del proyecto es LF**, aunque el `HEAD` actual del repo tiene CRLF (probablemente artefacto del port). No se ha normalizado esto — se ha mantenido CRLF en todos los commits de esta sesión para no mezclar un cambio de line-endings de todo el fichero con los fixes de contenido. Si el usuario quiere normalizar a LF, debería ser un commit dedicado y aparte.

## Workflow acordado (seguir SIEMPRE, en este orden, en cada fase)

Sin cambios respecto a la sesión anterior, con una adición: **antes de empezar cada fase, dar un resumen breve al usuario y esperar confirmación** (ver preferencias arriba).

1. Dar un resumen breve de la fase y esperar confirmación del usuario.
2. Implementar el cambio en `Complex.java` con `Edit` — **nunca con `sed`/herramientas de shell que reescriban el fichero entero** (ver incidente de line-endings más abajo).
3. Compilar solo: `javac -d /tmp/complexbuild -encoding UTF-8 src/com/ipserc/arith/complex/Complex.java`.
4. **Verificar ANTES de fiarse**: fichero de prueba suelto en el scratchpad, comparado contra el comportamiento matemáticamente esperado y contra el código ORIGINAL sin tocar (`git show HEAD:ruta > fichero`, compilado aparte en `/tmp/origbuild`).
5. Ejecutar la batería de regresión rápida y confirmar `exit:0` en todos; comparar la salida línea a línea contra el build original para descartar regresiones no relacionadas con el fix (ver nota sobre ruido no determinista más abajo).
6. `git diff --cached --stat` **antes** de `git add` y otra vez **después** — confirmar que el único fichero que entra es `src/com/ipserc/arith/complex/Complex.java`.
7. `git commit` con mensaje largo y explicativo, terminado en `Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>`.
8. Reportar al usuario en pocas frases qué se hizo y preguntar cómo seguir.

### Incidente real de esta sesión (no repetir): `sed -i` rompe los line-endings
Al intentar arreglar una única línea con espacio final usando `sed -i 's/.../.../' Complex.java` en Git Bash, `sed` reescribió el fichero ENTERO convirtiendo CRLF→LF, provocando que `git diff` mostrara las ~4100 líneas como cambiadas. Se detectó a tiempo (antes de commitear) porque `git diff --stat` mostró un cambio de tamaño anormal y `file src/.../Complex.java` dejó de decir "with CRLF line terminators". Se corrigió con `git checkout -- <fichero>` (el fichero no tenía cambios previos sin commitear, así que era seguro) y se rehicieron los cambios solo con la herramienta `Edit`, que sí preserva CRLF. **Regla: nunca usar `sed`/`perl -i` sobre `Complex.java` en este entorno; usar siempre `Edit`, y si hace falta tocar espacio en blanco final de una línea, verificar con `cat -A`/`file` antes y después.**

### Ruido no determinista en los tests de regresión (no confundir con regresión real)
Varios ficheros de test (`TestComplex01.java`, `TestZeta01.java`, etc.) usan el `Random` estático compartido (`randomNbr`) para: (a) elegir aleatoriamente el estilo de caja ASCII de los títulos (`boxTitleRandom`), y (b) generar puntos de prueba aleatorios para zeta. Esto significa que **dos ejecuciones del mismo test, sin ningún cambio de código, dan salidas de texto distintas** (estilos de caja distintos, y en `TestZeta01` valores `s=` distintos cada vez). Al comparar `diff` contra una ejecución anterior, filtrar por líneas con contenido numérico real (`grep -E '^[<>].*[0-9]\.[0-9]'`) y no fiarse de un `diff` en bruto — si no hay coincidencia con la parte numérica, no es una regresión, es aleatoriedad esperada del propio test.

### Limitación del entorno: algunos test files no compilan de forma aislada
`TestSurfaceCosc01/02.java` (dependen de `MatrixComplex` + la librería externa `com.panayotis.gnuplot`, no presente en este entorno/classpath) y `TestLimits01/02.java` (dependen de `MatrixComplex`/`Fourier`/`Sigfunc`) no se pueden compilar en un directorio de build aislado como se hace con `TestComplex01/07`/`TestGamma01`/`TestZeta01`. Compilarlos junto con sus dependencias reales tampoco es una opción limpia porque `MatrixComplex.java` tiene cambios locales del usuario sin commitear (ver arriba). **Para verificar zonas del código que solo se ejercitan desde estos test files, escribir un test autocontenido en el scratchpad que llame directamente a los métodos de `Complex` en cuestión**, como se hizo para `cosc`/`tanc`/`chebyshev`/`limit`/`integrate` en esta sesión.

### Batería de regresión rápida (sin cambios respecto a la sesión anterior)
```
cd "C:\Users\josel\workspace-eclipse\complexarith_github\src"
javac -d /tmp/complexbuild -encoding UTF-8 TestComplex/TestComplex01.java TestComplex/TestComplex07.java TestComplex/TestGamma01.java TestComplex/TestZeta01.java
cd /tmp/complexbuild
for t in TestComplex01 TestComplex07 TestGamma01 TestZeta01; do java TestComplex.$t > /dev/null 2>&1; echo "$t exit:$?"; done
```

## Commits hechos en la SESIÓN ANTERIOR (resumen; ver el historial de git para el detalle completo)
`08d77b3`, `9112552`, `93b7b1c`, `e88e049`, `25b5b88`, `66325fe`, `e759428`, `847c0bc`, `1e8b521`, `80b860e` — optimización de aritmética core (in-place, evitar trig redundante), bug de `binomialCoef(Complex,Complex)` (faltaba `+1`), bug de overflow en `arcsin`/`arccos`/`arcsinh`/`arccosh` para `|z|` extremo, bug de rendimiento/precisión de `mONE.power(k)` en `zeta_analytic_continuation`, y auditoría de `zeta_havil`/`zeta_riemann_siegel`. Detalle completo de cada uno en el propio `git log`.

## Commits hechos en ESTA sesión (orden cronológico, todos en `master`, todos tocan solo `Complex.java`)

1. **`1fe4d89`** — Fase 1a: unifica el umbral de cero entre `toStringRec`/`toStringRecI`/`toStringGNUPlot` (usaban `ZERO_THRESHOLD_APPROX` fijo, ignorando el flag `EXACT`) y `toStringPol` (que ya usaba `ZERO_THRESHOLD`, EXACT-aware). Verificado con `z=(1000.0, 1e-4)`: en modo `EXACT=true` antes se perdía la parte imaginaria en rectangular/gnuplot mientras polar mostraba una fase real; con el fix, las 4 representaciones coinciden.
2. **`4effa87`** — Fase 1b (profundización pedida por el usuario tras preguntar "¿podemos establecer un umbral para decidir pureza?"): sustituye el test de razón ad-hoc de los 3 formateadores por los predicados canónicos `rePartNull()`/`imPartNull()` (ya usados internamente por `setRecCoord()`), y amplía `toStringPol` para detectar las 4 orientaciones puras (real+, real-, imaginario+, imaginario-) en vez de solo "fase≈0". Verificado en los 4 cuadrantes con `(±1000, ±1e-4)` y sus inversos; único cambio numérico colateral en la batería de regresión: una fase `-π` de ruido de FP en `gamma_nemes` (TestGamma01) se normaliza a `+π` (la convención canónica).
3. **`c5651d1`** — Fase 2: documenta (sin cambiar comportamiento) por qué `round(Complex,int)` siempre delega en `roundRec` y nunca en `roundPol` (confirmado con los callers reales de `Eigenspace.java`/`Polynom.java`: trabajan en rectangular, es deliberado). Documenta el código muerto confirmado `getDecPart`/`getIntPart`/`trunc(double,int)`/`trunc(Complex,int)`.
4. **`1598554`** — Fase 3: **bug matemático real corregido** en `integrate(double lolimit, double uplimit, func, numDec)` — la condición de parada `while (uplimit > point.mod)` comparaba el límite superior contra el MÓDULO (siempre ≥0) del punto, en vez de su posición real, dando resultados silenciosamente incorrectos (o solo 1 punto evaluado) para límites descendentes o con ambos negativos. Verificado con `f(x)=x` en 5 combinaciones de signos/dirección. También: elimina una evaluación de función completamente desperdiciada (`prevVal` se calculaba y nunca se leía), y simplifica `integrateRE`/`integrateIM` (el cálculo de `stepRe`/`stepIm` vía `atan`/`cos` se reduce algebraicamente a `vector.rep*precision`/`vector.imp*precision` — verificado bit a bit idéntico al original en 9 casos). Documenta el coste `O(10^numDec)` de las 3 variantes de `integrate` y la elección de paso diagonal en `derivative`.
5. **`de2221d`** — Fase 4: **bug matemático real corregido** en `tanc(z)` — se definía como `sinc(z)/cosc(z)`, que por cancelación algebraica daba `tan(z)` en vez de `tan(z)/z`. Redefinido directamente con la misma guarda en `z=0` que `sinc`. Documenta por qué `cosc(z)` NO lleva guarda (polo genuino en `z=0`, no singularidad removible, a diferencia de `sinc`/`tanc`) y su colisión de nombre con `Sigfunc.cosc` (semántica distinta). Verifica `Complex.chebyshev` contra la recurrencia de `Polynom.chebyshev1` en 6 grados × 9 puntos (dentro/fuera de `[-1,1]`, reales y complejos) sin encontrar discrepancia.
6. **`e0a02cd`** — Fase 5: **bug de fiabilidad real corregido** en `limequ` — su comparación de fase (`sin(pha)²+cos(pha)²==1`) falla espuriamente por ruido de FP en ~22% de los ángulos (medido con un barrido -π..π), y era además lógicamente más laxa de lo previsto (aceptaría fases opuestas por π como "iguales"). Sustituido por `limr.equals(liml)`, el mismo criterio de igualdad ya usado como convergencia en `zeta_havil`. Documenta (sin corregir, es cambio de algoritmo) el bucle sin cota de `limit_inf` y `LIM_INF=Integer.MAX_VALUE` como proxy modesto de infinito.
7. **`72fd463`** — Fase 6: documenta (sin eliminar) la familia `*Red__` (`equalsred__`, `isZeroRed__`, `imPartNullRed__`, `rePartNullRed__`) y `sqrroot__(Complex[,int])` como código muerto confirmado (cero callers en todo `src/`), con hallazgos puntuales: `equalsred__(double,double,int)` recibe `numDecs` pero nunca lo usa; `equalsred__(Complex,int)` ya no tiene semántica "reducida", es un wrapper no-op sobre `equals`.

## Trucos, reglas y patrones específicos de este código (aprendidos esta sesión, además de los de la anterior)

- **`rePartNull()`/`imPartNull()` son los predicados canónicos de "pureza"** (¿es esta componente despreciable frente a la otra?, ratio `ZERO_THRESHOLD*CORRECTION_FACTOR`). Ya se usaban dentro de `setRecCoord()` (conversión polar→rectangular) para purificar `rep`/`imp`, pero **NO** hay equivalente en `setPolCoord()` (rectangular→polar) para purificar `pha` a un valor alineado a los ejes — asimetría real, confirmada, dejada fuera de alcance a propósito (ver "Ideas pendientes") por su blast radius (se llama en cada conversión rectangular→polar de toda la clase).
- **`ZERO_THRESHOLD` (dinámico, conmuta con `EXACT`) vs `ZERO_THRESHOLD_APPROX` (fijo, más laxo):** el patrón correcto en todo el fichero es usar `ZERO_THRESHOLD` por defecto para que el comportamiento respete el flag `EXACT`; `ZERO_THRESHOLD_APPROX` es un modo opt-in (activar con `EXACT=false`), no algo que deba colarse incondicionalmente en el camino "exacto". Antes de esta sesión, 3 de los 4 formateadores lo hacían mal.
- **Patrón de bug a vigilar: comparar un módulo/magnitud (siempre ≥0) contra un límite que puede ser negativo.** Así se encontró el bug de `integrate(double,double,...)`: `uplimit > point.mod` nunca puede ser cierto si `uplimit<0`, sea cual sea el punto. Si se ve una condición de bucle que mezcla `.mod` con un límite con signo, sospechar.
- **Identidad `cos(atan(x)) == 1/sqrt(1+x²)`** es útil para reconocer cuándo un cálculo con `atan`/`cos` encadenados es en realidad una fórmula lineal disfrazada (así se simplificó `stepRe`/`stepIm` en `integrateRE`/`integrateIM` a `vector.rep*precision`/`vector.imp*precision`).
- **Comparar `sin(θ)²+cos(θ)²==1` (u otras identidades trigonométricas) con igualdad EXACTA tras calcular con `Math.sin`/`Math.cos` es frágil:** medido, falla ~22% de las veces por 1-2 ULP de ruido en los propios senos/cosenos. Nunca usar esto como criterio de igualdad; usar `equals(Complex)` (tolerancia sobre rep/imp) o una comparación con tolerancia explícita.
- **`equals(Complex)`/`equals(double,double)`** (tolerancia `ZERO_THRESHOLD*CORRECTION_FACTOR` sobre rep/imp) es el criterio de igualdad "con tolerancia" ya establecido y confiable en toda la clase — úsalo en vez de reinventar comparaciones ad-hoc (mod, fase, identidades trigonométricas) para "¿son estos dos complejos el mismo valor?".
- **División por `Complex.ZERO` produce un estado interno inconsistente:** `divides()` calcula `rep`/`imp` (que dan `NaN`, por `0/0`) y `mod` (que da `Infinity`, por `x/0` con `x≠0`) de forma independiente vía la factoría `raw()`, sin verificar consistencia entre ellos — el objeto resultante tiene `rep=NaN, imp=NaN, mod=Infinity` simultáneamente, una combinación contradictoria. Descubierto al analizar `cosc(0)=cos(0)/0`. No se ha arreglado (tocaría el núcleo aritmético usado en todo el fichero); documentado como hallazgo para una futura revisión dedicada de `divides()`.
- **Antes de compilar un test file que falla con "cannot find symbol" para un método que SÍ existe en el fichero actual**, comprobar si el test file tiene ediciones locales sin commitear que asumen una versión distinta del `.java` que se está usando como referencia (`git show HEAD:...` puede no coincidir con el working tree si el test file — no `Complex.java` — está modificado). Pasó con `TestComplex01.java` y el método `logbase`/`logb`.
- **`Edit` preserva CRLF; herramientas de shell tipo `sed -i`/`perl -i` en Git Bash no** (ver incidente arriba). Si hay que tocar espacio en blanco al final de una línea, es más seguro aceptar un pequeño diff cosmético residual que arriesgarse con `sed`.

## Bugs conocidos, documentados en el propio Javadoc de `Complex.java`, sin corregir (a propósito)

De la sesión anterior (sin cambios): `zeta_riemann_siegel(s)` (da `0.0` para `|Im(s)|<2π`, no usada por nadie) y `zeta_analytic_continuation(s)` (no converge en tiempo práctico para `Re(s)≲0.7`, estructural). **Cuidado: no llamar `zeta_analytic_continuation` con `Re(s)` pequeño sin timeout.**

Nuevos de esta sesión:
- `limit_inf`: el bucle interior `while (result2.mod/result.mod != 1)` no tiene cota de iteraciones (a diferencia del bucle externo de `limit()`, que sí está acotado). Si el ratio nunca cae exactamente en 1.0, sigue doblando `point.mod` indefinidamente. No arreglado (cambiaría qué se considera "convergente").
- `cosc(z)=cos(z)/z` en `z=0`: polo genuino sin valor límite único (depende de la dirección de aproximación), documentado que no lleva guarda a propósito — pero el resultado real que produce hoy (`rep=NaN, imp=NaN, mod=Infinity`) es un estado inconsistente del núcleo de división, no arreglado (ver "Trucos" arriba).
- `equalsred__(double,double,int)`: recibe `numDecs` pero nunca lo usa en el cuerpo — bug latente propio, pero el método está muerto (sin callers), así que no se ha corregido.
- `toStringGNUPlot`: su purga de cero es incondicional (no respeta el flag `FORMAT_NBR`), a diferencia de los otros 3 formateadores que sí lo respetan. Detectado durante la Fase 1, señalado al usuario, decisión de cambiarlo o no queda pendiente (no se tocó).

## Ideas pendientes / no exploradas (candidatas para continuar)

Actualizado tras esta sesión — lo que YA se cubrió (`sinc`/`cosc`/`tanc`, `chebyshev`, `integrate*`/`derivative`, `limit`/`limit_inf`, `round`/`trunc`/`getDecPart`/`getIntPart`, métodos `*Red__`/`sqrroot__`) se ha quitado de esta lista. Lo que sigue sin tocar:

- **Estado estático mutable global** (`EXACT`, `PRECISION`, `ZERO_THRESHOLD*`, `REPRESENTATION`, `FORMAT_NBR`, `randomNbr`...) — no es thread-safe. Preguntado explícitamente esta sesión y el usuario decidió dejarlo **fuera de alcance**: es un cambio arquitectónico grande (candidato a `ThreadLocal` o config por instancia) que merece su propio plan dedicado, no mezclarse con una revisión puntual.
- **`setPolCoord()` no purifica la fase** a valores alineados a los ejes (0, ±π/2, π) cuando una componente es despreciable, a diferencia de `setRecCoord()` que sí purifica rep/imp en la dirección contraria. Se decidió conscientemente NO tocarlo esta sesión por su blast radius (se ejecuta en cada conversión rectangular→polar de toda la clase); el síntoma visible (que `toStringPol` no detectaba "casi real negativo"/"casi imaginario") se arregló en la Fase 1 solo a nivel de formateo, sin tocar la conversión estructural subyacente.
- **`divides()` produce un estado interno inconsistente al dividir por `Complex.ZERO`** (`rep`/`imp=NaN` junto con `mod=Infinity`) — descubierto al analizar `cosc(0)`, no arreglado, tocaría el núcleo aritmético.
- `toStringGNUPlot` no respeta `FORMAT_NBR` (incondicional), a diferencia de los otros 3 formateadores — señalado, no resuelto.
- `randomNbr` es un único `Random` estático compartido (candidato a `ThreadLocalRandom`), usado solo en `boxTitle*`/`boxText*` (cosmético, bajo impacto). Nota: esto también es la fuente del ruido no determinista en los tests de regresión (ver arriba).
- `System.exit(1)` dentro de `setComplex` ante parseo inválido — debería lanzar excepción en vez de matar la JVM.
- **No se ha hecho ningún trabajo de layout `double[]` / Vector API** (`jdk.incubator.vector`) que pedía el prompt original.
- `MatrixComplex.java` y `VectorComplex.java` no se han tocado ni revisado (y `MatrixComplex.java` tiene cambios locales sin commitear del usuario, ver arriba).
- El bloque de formateo/presentación (`boxTitle*`/`boxText*`, ASCII art) no se ha revisado por corrección — solo se tocó la lógica de umbral de cero de `toString*`, no el resto del pipeline de formato.
- La clase entera sigue siendo ~4125 líneas mezclando aritmética + parsing + formato + cajas de texto ASCII + integración numérica + límites + funciones especiales — la reestructuración arquitectónica (separar responsabilidades) que pedía el prompt original sigue sin abordarse.
- ~~Line-endings: el `HEAD` actual tiene CRLF pero el "natural" del proyecto (desarrollado en Linux) es LF — no normalizado~~ → **Resuelto en la sesión de mantenimiento del 29 julio 2026, ver sección siguiente.**

---

# SESIÓN DE MANTENIMIENTO DE REPOSITORIO — 29 julio 2026 (independiente de la revisión de `Complex.java`)

> Esta sección documenta trabajo que **no** forma parte del plan de auditoría/refactor de `Complex.java` de las secciones anteriores. Es una sesión distinta, de higiene de repositorio (line-endings + versionado), hecha el mismo día. Las "Ideas pendientes" de `Complex.java` de la sección anterior siguen intactas y sin tocar — no se avanzó en ellas en esta sesión.

## Qué se hizo

1. **Normalización de line-endings a LF** (commit `75c95a1`). El usuario forzó a Eclipse a usar LF como terminador de línea, lo que reescribió el terminador (sin cambios de contenido) en 137 ficheros que tenían CRLF heredado del port Linux→Windows. Se separó cuidadosamente qué ficheros tenían **solo** cambio de terminador (verificado con `git diff -w`, insertions==deletions exactas) de los que mezclaban terminador + contenido real; solo los 137 "puros" entraron en este commit. Se añadió `.gitattributes` (`* text eol=lf`) para fijar LF de forma consistente en el repo hacia adelante, independientemente de `core.autocrlf` local. El usuario confirmó que ya tiene `git config --global core.autocrlf false`.
2. **Versionado a nivel de proyecto** (commit `ef7bfc2`, tag anotado `v1.0`). Hasta ahora la versión solo se llevaba por clase (constante `VERSION = "1.X (YYYY_MMDD_HHMM)"` en cada `.java`, ej. `Complex.java` en `1.9`). Se acordó con el usuario un esquema **secuencial incremental** (no SemVer ni CalVer — no aplican bien a un proyecto sin paquete publicado ni cadencia de release regular) y se añadió un fichero `VERSION` en la raíz del repo con el mismo formato `MAJOR.MINOR (YYYY_MMDD_HHMM)`, ahora en `1.0 (2026_0729_0943)`. Cada versión de proyecto futura se marcará con `VERSION` actualizado + commit + tag `vX.Y` (tag local únicamente; no se empuja al remoto salvo petición explícita).

## Qué queda pendiente / sin tocar

- **88 ficheros con cambios "mezclados"** (terminador de línea + contenido real del usuario en el mismo diff), deliberadamente dejados fuera del commit de normalización LF — siguen sin commitear, tal y como estaban. Incluyen `src/com/ipserc/arith/matrixcomplex/MatrixComplex.java` (ya documentado en la sección anterior como WIP grande del usuario), `.gitignore` (cambio real: comenta `*.class`), los borrados `src/TestComplex/SchurCalc.java` y `src/com/ipserc/arith/vector/Vector.java`, y ~84 ficheros más de `TestComplex/` y de los paquetes `factorization`/`geom`/`matrixcomplex`/`polynom`/`signal`. Para verlos: `git status --short | grep '^ M\|^ D'` tras el estado de este commit. Si se retoma este trabajo, el mismo método de separación (`git diff -w` vacío ⇒ puro terminador) sirve para ir limpiando estos ficheros uno a uno o por lotes, pero cada uno requiere revisar qué parte es contenido real antes de commitear.
- No se ha propuesto ni acordado con el usuario si el `VERSION` de proyecto debe subirse también en cada commit relevante de ahora en adelante, o solo cuando el usuario lo pida explícitamente — asumir esto último salvo que diga lo contrario.
- No se han tocado los tags remotos ni se ha hecho push de nada de esta sesión.

---

*Última actualización de este bloque: sesión del 29 julio 2026. Sección "Complex.java" congelada tras el commit `72fd463`; sección "Mantenimiento de repositorio" añadida tras los commits `75c95a1` y `ef7bfc2` (tag `v1.0`).*
