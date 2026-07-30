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
- **Actualización:** hay una Tercera sesión de revisión, posterior a ésta y a la sesión de mantenimiento de repo, que retomó dos de las "Ideas pendientes" de aquí (`divides()` inconsistente y `setPolCoord()` sin purificar fase). Ver la sección "TERCERA SESIÓN DE REVISIÓN" al final del documento para el estado más reciente antes de asumir que esta sección es el final del hilo.

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
4. **Verificar ANTES de fiarse**: fichero de prueba suelto en el scratchpad, comparado contra el comportamiento matemáticamente esperado y contra el código ORIGINAL sin tocar (`git show HEAD:ruta > fichero`, compilado aparte en `/tmp/origbuild`). No hace falta pedir permiso para estos pasos de verificación (compilar, escribir ficheros de prueba, ejecutar comparaciones) — permiso amplio concedido en la Sexta sesión.
5. Ejecutar la batería de regresión rápida y confirmar `exit:0` en todos; comparar la salida línea a línea contra el build original para descartar regresiones no relacionadas con el fix (ver nota sobre ruido no determinista más abajo).
6. **Subir el campo `VERSION` de `Complex.java`** (línea ~76, formato `"X.Y (YYYY_MMDD_HHMM)"`, con `date +"%Y_%m%d_%H%M"` para la fecha/hora) como parte del mismo cambio — **regla nueva de la Sexta sesión, aplicar en todo commit que toque `Complex.java` de ahora en adelante, sin que el usuario tenga que pedirlo cada vez**. Solo el número+fecha, sin changelog retroactivo dentro del fichero (el detalle vive en el mensaje de commit y en este documento).
7. `git diff --cached --stat` **antes** de `git add` y otra vez **después** — confirmar que el único fichero que entra es `src/com/ipserc/arith/complex/Complex.java`.
8. `git commit` con mensaje largo y explicativo, terminado en `Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>`.
9. Reportar al usuario en pocas frases qué se hizo y preguntar cómo seguir.

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

- **`rePartNull()`/`imPartNull()` son los predicados canónicos de "pureza"** (¿es esta componente despreciable frente a la otra?, ratio `ZERO_THRESHOLD*CORRECTION_FACTOR`). Ya se usaban dentro de `setRecCoord()` (conversión polar→rectangular) para purificar `rep`/`imp`. La asimetría con `setPolCoord()` (rectangular→polar, que no purificaba `pha`) detectada en la sesión anterior se corrigió en la Tercera sesión de revisión (commit `a39f99a`, ver esa sección) reutilizando el mismo patrón de `toStringPol()`.
- **`ZERO_THRESHOLD` (dinámico, conmuta con `EXACT`) vs `ZERO_THRESHOLD_APPROX` (fijo, más laxo):** el patrón correcto en todo el fichero es usar `ZERO_THRESHOLD` por defecto para que el comportamiento respete el flag `EXACT`; `ZERO_THRESHOLD_APPROX` es un modo opt-in (activar con `EXACT=false`), no algo que deba colarse incondicionalmente en el camino "exacto". Antes de esta sesión, 3 de los 4 formateadores lo hacían mal.
- **Patrón de bug a vigilar: comparar un módulo/magnitud (siempre ≥0) contra un límite que puede ser negativo.** Así se encontró el bug de `integrate(double,double,...)`: `uplimit > point.mod` nunca puede ser cierto si `uplimit<0`, sea cual sea el punto. Si se ve una condición de bucle que mezcla `.mod` con un límite con signo, sospechar.
- **Identidad `cos(atan(x)) == 1/sqrt(1+x²)`** es útil para reconocer cuándo un cálculo con `atan`/`cos` encadenados es en realidad una fórmula lineal disfrazada (así se simplificó `stepRe`/`stepIm` en `integrateRE`/`integrateIM` a `vector.rep*precision`/`vector.imp*precision`).
- **Comparar `sin(θ)²+cos(θ)²==1` (u otras identidades trigonométricas) con igualdad EXACTA tras calcular con `Math.sin`/`Math.cos` es frágil:** medido, falla ~22% de las veces por 1-2 ULP de ruido en los propios senos/cosenos. Nunca usar esto como criterio de igualdad; usar `equals(Complex)` (tolerancia sobre rep/imp) o una comparación con tolerancia explícita.
- **`equals(Complex)`/`equals(double,double)`** (tolerancia `ZERO_THRESHOLD*CORRECTION_FACTOR` sobre rep/imp) es el criterio de igualdad "con tolerancia" ya establecido y confiable en toda la clase — úsalo en vez de reinventar comparaciones ad-hoc (mod, fase, identidades trigonométricas) para "¿son estos dos complejos el mismo valor?".
- ~~**División por `Complex.ZERO` produce un estado interno inconsistente**~~ → **Resuelto en la Tercera sesión de revisión (commit `20a4bb3`), ver esa sección.** (Descubierto al analizar `cosc(0)=cos(0)/0` en la sesión anterior.)
- **Antes de compilar un test file que falla con "cannot find symbol" para un método que SÍ existe en el fichero actual**, comprobar si el test file tiene ediciones locales sin commitear que asumen una versión distinta del `.java` que se está usando como referencia (`git show HEAD:...` puede no coincidir con el working tree si el test file — no `Complex.java` — está modificado). Pasó con `TestComplex01.java` y el método `logbase`/`logb`.
- **`Edit` preserva CRLF; herramientas de shell tipo `sed -i`/`perl -i` en Git Bash no** (ver incidente arriba). Si hay que tocar espacio en blanco al final de una línea, es más seguro aceptar un pequeño diff cosmético residual que arriesgarse con `sed`.

## Bugs conocidos, documentados en el propio Javadoc de `Complex.java`, sin corregir (a propósito)

De la sesión anterior (sin cambios): `zeta_riemann_siegel(s)` (da `0.0` para `|Im(s)|<2π`, no usada por nadie) y `zeta_analytic_continuation(s)` (no converge en tiempo práctico para `Re(s)≲0.7`, estructural). **Cuidado: no llamar `zeta_analytic_continuation` con `Re(s)` pequeño sin timeout.**

Nuevos de esta sesión:
- `limit_inf`: el bucle interior `while (result2.mod/result.mod != 1)` no tiene cota de iteraciones (a diferencia del bucle externo de `limit()`, que sí está acotado). Si el ratio nunca cae exactamente en 1.0, sigue doblando `point.mod` indefinidamente. No arreglado (cambiaría qué se considera "convergente").
- `cosc(z)=cos(z)/z` en `z=0`: polo genuino sin valor límite único (depende de la dirección de aproximación), documentado que no lleva guarda a propósito. El estado inconsistente que esto producía en `divides()` (`rep=NaN, imp=NaN` junto con `mod=Infinity`) se corrigió en la Tercera sesión (commit `20a4bb3`): ahora `cosc(0)` da el estado consistente `rep=NaN, imp=NaN, mod=Infinity, pha=NaN` (magnitud infinita, dirección explícitamente indefinida) en vez de una `pha` finita engañosa.
- `equalsred__(double,double,int)`: recibe `numDecs` pero nunca lo usa en el cuerpo — bug latente propio, pero el método está muerto (sin callers), así que no se ha corregido.
- `toStringGNUPlot`: su purga de cero es incondicional (no respeta el flag `FORMAT_NBR`), a diferencia de los otros 3 formateadores que sí lo respetan. Detectado durante la Fase 1, señalado al usuario, decisión de cambiarlo o no queda pendiente (no se tocó).

## Ideas pendientes / no exploradas (candidatas para continuar)

Actualizado tras esta sesión — lo que YA se cubrió (`sinc`/`cosc`/`tanc`, `chebyshev`, `integrate*`/`derivative`, `limit`/`limit_inf`, `round`/`trunc`/`getDecPart`/`getIntPart`, métodos `*Red__`/`sqrroot__`) se ha quitado de esta lista. Lo que sigue sin tocar:

- **Estado estático mutable global** (`EXACT`, `PRECISION`, `ZERO_THRESHOLD*`, `REPRESENTATION`, `FORMAT_NBR`, `randomNbr`...) — no es thread-safe. Preguntado explícitamente esta sesión y el usuario decidió dejarlo **fuera de alcance**: es un cambio arquitectónico grande (candidato a `ThreadLocal` o config por instancia) que merece su propio plan dedicado, no mezclarse con una revisión puntual.
- ~~`divides()` produce un estado interno inconsistente al dividir por `Complex.ZERO`~~ → **Resuelto en la Tercera sesión de revisión (29 julio 2026), commit `20a4bb3`, ver sección siguiente.**
- ~~`setPolCoord()` no purifica la fase a valores alineados a los ejes~~ → **Resuelto en la Tercera sesión de revisión (29 julio 2026), commit `a39f99a`, ver sección siguiente.**
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

# TERCERA SESIÓN DE REVISIÓN DE `Complex.java` — 29 julio 2026 (retoma la lista de "Ideas pendientes")

> Esta sección continúa la revisión de `Complex.java` de las dos primeras sesiones (arriba), después de la sesión de mantenimiento de repo (line-endings + versionado, sección anterior). Se retomó picoteando la lista de "Ideas pendientes" de la sesión 2, de una en una, con el mismo workflow de siempre (resumen antes de cada fase, confirmación del usuario, `Edit` únicamente, compilar y verificar contra el build anterior antes de fiarse, batería de regresión, commit único con solo `Complex.java`).

## Qué se hizo

1. **`divides(Complex)` — estado interno inconsistente al dividir por complejo cero** (commit `20a4bb3`). Al dividir por un complejo de módulo 0 (p.ej. `Complex.ZERO`), la fórmula rectangular anulaba siempre el numerador de `rep`/`imp` (dando `NaN` sin importar la magnitud del numerador), mientras `mod` se calculaba de forma independiente y correcta (`Infinity` si el numerador≠0, `NaN` si también es 0), y `pha` quedaba en un valor finito sin sentido derivado de la fase del propio cero. Resultado: hasta 4 campos contradictorios en el mismo objeto (p.ej. `mod=Infinity` con una `pha` finita). Fix: detectar explícitamente `that.mod==0` y devolver un estado consistente — `rep=imp=mod=pha=NaN` si también el numerador es cero (0/0 indeterminado), o `rep=imp=pha=NaN, mod=Infinity` si el numerador es no-cero (magnitud bien definida, dirección indefinida). Verificado con un test aparte en el scratchpad comparando explícitamente contra el build anterior en los 4 casos de división por cero, más un par de sanity checks de división normal (bit a bit idénticos). Batería de regresión sin cambios numéricos.
2. **`setPolCoord()` — asimetría con `setRecCoord()`, la fase no se purificaba** (commit `a39f99a`). `setRecCoord()` (polar→rectangular) purifica `rep`/`imp` a `0.0` exacto vía `rePartNull()`/`imPartNull()` cuando la componente es despreciable frente a la otra; `setPolCoord()` (rectangular→polar) no tenía el equivalente para `pha` — quedaba en un residuo sin sentido (p.ej. `atan2(1e-14,1000)≈1e-17` en vez de `0.0`). Fix: reutilizar el mismo patrón que ya usaba `toStringPol()` (línea ~1159-1160) para snapear `pha` al eje más cercano (`0`, `±HALF_PI`, `PI`) justo tras calcularla con `atan2`, sin tocar nunca `rep`/`imp`. Verificado con un test aparte: purificación correcta en los 4 cuadrantes cerca de cada eje, ausencia de purificación en un caso justo fuera del umbral (ratio `1e-3`), ida-y-vuelta rectangular→polar→rectangular sin cambios (ya lo purificaba `setRecCoord()` de forma independiente), y una multiplicación encadenada (50×`timesEq`) con `rep`/`imp` resultantes bit a bit idénticos antes/después del fix (solo cambia la última cifra de la `pha` interna, ruido de redondeo eliminado). Batería de regresión sin cambios numéricos.

Ambos commits, como en las sesiones anteriores, tocan **solo** `src/com/ipserc/arith/complex/Complex.java` — verificado con `git diff --cached --stat` antes y después de cada `git add`.

## Ideas pendientes que quedaron fuera de esta sesión (sin cambios respecto a la lista de la sesión 2)

- `toStringGNUPlot` no respeta `FORMAT_NBR` (incondicional).
- Estado estático mutable global no thread-safe (`EXACT`, `PRECISION`, `ZERO_THRESHOLD*`, `REPRESENTATION`, `FORMAT_NBR`, `randomNbr`...) — sigue fuera de alcance por decisión consciente (cambio arquitectónico grande).
- `randomNbr` como único `Random` estático compartido (fuente del ruido no determinista en tests de regresión, ver sección de sesión 2).
- `System.exit(1)` dentro de `setComplex` ante parseo inválido.
- Trabajo de layout `double[]` / Vector API (`jdk.incubator.vector`) — no iniciado.
- `MatrixComplex.java` y `VectorComplex.java` — no tocados ni revisados (y `MatrixComplex.java` sigue con cambios locales sin commitear del usuario).
- Reestructuración arquitectónica de `Complex.java` (separar aritmética/parsing/formato/cajas ASCII/integración/límites) — no abordada.
- Limpieza de los 88 ficheros con line-endings+contenido mezclados (sesión de mantenimiento) — sigue pendiente, sin avances en esta sesión.

No se ha propuesto continuar con ninguna de estas; el usuario decidió parar aquí por hoy.

---

# CUARTA SESIÓN DE REVISIÓN DE `Complex.java` — 29 julio 2026 (retoma la lista de "Ideas pendientes")

> Continúa picoteando la lista de "Ideas pendientes" de la sesión 2/3, mismo workflow de siempre. Al retomar, se preguntó al usuario cuál de las ideas pendientes abordar (con `AskUserQuestion`) y eligió la más pequeña y acotada.

## Qué se hizo

1. **`toStringGNUPlot()` no respetaba el flag `FORMAT_NBR`** (commit `a5d6a99`). Purgaba `rep`/`imp` a `0.0` (vía `rePartNull()`/`imPartNull()`) de forma incondicional, a diferencia de `toStringRec`/`toStringRecI`/`toStringPol`, que solo purgan si `FORMAT_NBR` está activo — era el único de los 4 formateadores que no se podía desactivar con `setFormatOFF()`. Fix: envolver el chequeo de pureza en `if (FORMAT_NBR) { ... }`, igual que los otros 3. Verificado con test suelto en el scratchpad (`z1=(1000.0, 1e-9)`, `z2=(1e-9, 1000.0)`, ratio `1e-12` por debajo de `ZERO_THRESHOLD*CORRECTION_FACTOR=1e-11` con `EXACT=true`): en el build original la purga ocurría igual con `FORMAT_NBR` on/off/default (bug confirmado); en el corregido solo tras `setFormatON()`. Batería de regresión (`TestComplex01/07`, `TestGamma01`, `TestZeta01`) exit 0 en las 4, sin diferencias numéricas salvo el ruido no determinista ya documentado de `TestZeta01`.

Este commit, como en las sesiones anteriores, toca **solo** `src/com/ipserc/arith/complex/Complex.java` — verificado con `git diff --cached --stat` antes y después del `git add`.

## Nota de calibración de umbral (aprendida esta sesión)

Al escribir el test de verificación, un primer intento con `(1000.0, 1e-4)` (el mismo par usado en la sesión 1 para el bug de `ZERO_THRESHOLD_APPROX` fijo) **no disparaba** el chequeo de pureza de `rePartNull()`/`imPartNull()`: con `EXACT=true` (valor por defecto), el umbral real es `ZERO_THRESHOLD*CORRECTION_FACTOR = ZERO_THRESHOLD_EXACT*10 = (PRECISION*10)*10 = 1e-11`, y el ratio `1e-4/1000=1e-7` queda muy por encima. Hubo que bajar a `(1000.0, 1e-9)` (ratio `1e-12`) para que la purga se activara. **Si en el futuro se verifica cualquier purga de pureza (`rePartNull`/`imPartNull`) con `EXACT=true`, usar un ratio bastante por debajo de `1e-11`, no `1e-7`** — ese umbral más laxo (`ZERO_THRESHOLD_APPROX≈3.16e-7`) solo aplica en modo `EXACT=false`.

## Ideas pendientes que quedaron fuera de la primera parte de esta sesión (retomadas después, ver más abajo)

- Estado estático mutable global no thread-safe (`EXACT`, `PRECISION`, `ZERO_THRESHOLD*`, `REPRESENTATION`, `FORMAT_NBR`...) — sigue fuera de alcance por decisión consciente. (`randomNbr` ya no forma parte de este grupo, ver siguiente commit.)
- `System.exit(1)` dentro de `setComplex` ante parseo inválido.
- Trabajo de layout `double[]` / Vector API (`jdk.incubator.vector`) — no iniciado.
- `MatrixComplex.java` y `VectorComplex.java` — no tocados ni revisados (y `MatrixComplex.java` sigue con cambios locales sin commitear del usuario).
- Reestructuración arquitectónica de `Complex.java` (separar aritmética/parsing/formato/cajas ASCII/integración/límites) — no abordada.
- Limpieza de los 88 ficheros con line-endings+contenido mezclados (sesión de mantenimiento) — sigue pendiente.

## Continuación de la Cuarta sesión: `randomNbr` → `ThreadLocalRandom` (mismo día, 29 julio 2026)

2. **`randomNbr` (único `Random` estático compartido) sustituido por `ThreadLocalRandom`** (commit `6131af8`). Usado solo en `boxTitleRandom()`/`boxTextRandom()` (selección aleatoria del estilo de caja ASCII, puramente cosmético). Se eliminó el campo `private static Random randomNbr` y el `import java.util.Random`, sustituyendo cada `randomNbr.nextInt(7)+1` por `ThreadLocalRandom.current().nextInt(7)+1`; se añadió `import java.util.concurrent.ThreadLocalRandom`. Verificado con test suelto en el scratchpad: 500 llamadas a cada método siguen dando las 7 variantes; smoke test con 8 hilos concurrentes (1000 llamadas c/u) sin excepciones. Batería de regresión exit 0 en las 4, sin diferencias numéricas salvo el ruido ya documentado de `TestZeta01` — nota: ese mismo ruido (aleatorio en cada corrida, no relacionado con `randomNbr`, viene de los puntos de prueba de zeta) sigue presente porque `TestZeta01` tiene su propia fuente de aleatoriedad independiente, no relacionada con el campo eliminado.

Ambos commits de esta sesión (`a5d6a99`, `6131af8`) tocan **solo** `src/com/ipserc/arith/complex/Complex.java`.

## Continuación de la Cuarta sesión: `System.exit(1)` → `IllegalArgumentException` en `setComplex` (mismo día, 29 julio 2026)

3. **`System.exit(1)` en `setComplex(String)`/`setComplex(char,double,double)` sustituido por `IllegalArgumentException`** (commit `bd1b3fd`). Había 4 puntos (`System.err.println(...); System.exit(1);`): 3 en `setComplex(String)` (parseo polar no reconocido, selector rectangular sin caso válido, patrón rectangular no reconocido), alcanzables desde cualquier caller externo con un string mal formado; 1 en `setComplex(char,double,double)` (privado), en la práctica inalcanzable porque todos sus llamadores internos pasan siempre `'C'`/`'P'`/`'c'`/`'p'` literal — corregido igualmente por consistencia. Terminar la JVM entera por un input inválido es inaceptable para una clase de librería (mata cualquier app/hilo que la use por un simple typo, sin poder recuperarse). Fix: `throw new IllegalArgumentException(mensaje)` en los 4 puntos, eliminando el `println` redundante (el mensaje ya va en la excepción). Cambio de contrato de la API: antes mataba el proceso, ahora lanza una excepción no comprobada capturable. Verificado con test suelto: 8 inputs válidos (`"3+4i"`, `"2.5|1.0"`, `"i"`, etc.) sin cambios; inputs inválidos (`"abc"`, `"3+4x"`, `"3||4"`) lanzan `IllegalArgumentException` capturable con el mensaje esperado, y el proceso sigue vivo después. Nota: `"3+"` es aceptado como `3.0` por leniencia preexistente del regex, no relacionado con este cambio. Batería de regresión exit 0 en las 4, sin diferencias numéricas.

Los tres commits de esta sesión (`a5d6a99`, `6131af8`, `bd1b3fd`) tocan **solo** `src/com/ipserc/arith/complex/Complex.java`.

## Ideas pendientes actualizadas tras `randomNbr` y `System.exit`

- ~~Estado estático mutable global no thread-safe (`EXACT`, `PRECISION`, `ZERO_THRESHOLD*`, `REPRESENTATION`, `FORMAT_NBR`)~~ → **Resuelto en la Quinta sesión de revisión (30 julio 2026), commit `dccaf1f`, ver sección siguiente.**
- Trabajo de layout `double[]` / Vector API (`jdk.incubator.vector`) — no iniciado.
- `MatrixComplex.java` y `VectorComplex.java` — no tocados ni revisados (y `MatrixComplex.java` sigue con cambios locales sin commitear del usuario).
- Reestructuración arquitectónica de `Complex.java` — no abordada.
- Limpieza de los 88 ficheros con line-endings+contenido mezclados (sesión de mantenimiento) — sigue pendiente.

---

# QUINTA SESIÓN DE REVISIÓN DE `Complex.java` — 30 julio 2026 (estado estático → `ThreadLocal`)

> Retoma la idea pendiente más grande de la lista, aparcada desde la Sesión 2 por su tamaño ("cambio arquitectónico grande, candidato a `ThreadLocal` o config por instancia... merece su propio plan dedicado"). Antes de tocar código se lanzó un fork de exploración para medir el alcance real (call-sites externos, si el uso real depende de que el estado sea compartido entre instancias), y con esos datos se entró en `EnterPlanMode` para acordar el enfoque con el usuario vía `AskUserQuestion` antes de escribir una sola línea — a diferencia de las fases 1-6 anteriores (bugs puntuales acotados), aquí sí se siguió el proceso completo de planificación formal que el propio documento pedía para este ítem.

## Qué se hizo

1. **Los 23 campos estáticos mutables de configuración global** (`EXACT`, `PRECISION`, `ZERO_THRESHOLD_EXACT`, `ZERO_THRESHOLD_APPROX`, `ZERO_THRESHOLD`, `SIGNIFICATIVE`, `DIGITS`, sus 6 `_BCK`, `FORMAT_NBR`, `FIXED_NOTATION`, `SCIENTIFIC_NOTATION`, `MAX_DECIMALS`, sus 4 `_BCK` de formato, `REPRESENTATION`, `REPRESENTATION_BCK`) migrados a `ThreadLocal` (commit `dccaf1f`). Antes eran `private static` compartidos por todas las instancias **y todos los hilos** de la JVM — dos hilos usando `Complex` simultáneamente (p.ej. dos llamadas a `storePrecision()/setPrecision(...)/restorePrecision()` en paralelo) se corrompían mutuamente la configuración. Ahora viven como campos de instancia de una clase anidada `State`, respaldada por un único `ThreadLocal<State>` y un helper privado `state()`: cada hilo tiene su propia copia aislada, pero dentro de un mismo hilo el estado sigue compartido entre todas las instancias `Complex` creadas en él — que es exactamente el patrón que ya usan `MatrixComplex`/`Eigenspace`/`Polynom`/`Laplace`/`Fourier`/`Z`/`Spline` (`storePrecision()` → cambiar modo → calcular con muchas instancias `Complex` anidadas → `restorePrecision()`).
2. **La API pública no cambió**: todos los getters/setters existentes (`exact()`, `getPrecision()`, `setPrecision()`, `setFormatON/OFF`, `setFixedON/OFF`, `setScientificON/OFF`, `setRepres()`/`getRepres()`/`restoreRepres()`, `storePrecision()`/`restorePrecision()`, `digits()`, etc.) conservan firma y comportamiento observable, solo cambia dónde leen/escriben internamente. Confirmado por el fork de exploración previo: de los ~216 ficheros que usan estos métodos, solo 7 son código de librería real (el resto es `TestComplex/*`); ninguno necesitó cambiar una línea.
3. Los campos que son **constantes de fábrica** y nunca se reasignan tras la inicialización (`PRECISION_DEF`, `ZERO_THRESHOLD_EXACT_DEF`, `ZERO_THRESHOLD_APPROX_DEF`, `ZERO_THRESHOLD_DEF`, `SIGNIFICATIVE_DEF`, `DIGITS_DEF`, `MAX_DECIMALS_DEFAULT`) se quedaron fuera de `State`, como `static` planos — confirmado por grep que ningún setter los reasigna, así que son inmutables en la práctica y seguros de compartir entre hilos sin cambios.
4. **Bug real encontrado y corregido durante la propia migración** (no presente en el código original, introducido y arreglado en el mismo commit): orden de inicialización estática. El bloque `State`/`STATE`/`state()` estaba declarado *después* de las constantes `Complex i/j/ZERO/ONE/mONE/PI/DOSPI/TWOPI/HALFPI`, cuyos inicializadores construyen instancias `Complex` que llaman a `state()` durante el propio `<clinit>` de la clase. Como los inicializadores estáticos de Java se ejecutan en orden textual, `STATE` seguía siendo `null` cuando esas constantes se construían, lanzando `NullPointerException` al cargar la clase. Se corrigió moviendo el enum `Representation` y el bloque completo `State`/`STATE`/`state()` (y las constantes `_DEF` de las que depende) a *antes* de esas constantes `Complex`, dejando la restricción de orden documentada en el Javadoc de `State`.
5. **Bug de reentrancia preexistente, documentado pero explícitamente NO corregido** (decisión del usuario, tomada antes de empezar la fase vía `AskUserQuestion`): `storePrecision()`/`restorePrecision()` y `setRepres()`/`restoreRepres()` usan un único slot `_BCK`, no una pila — una llamada `store→restore` anidada dentro de otra, en el *mismo* hilo, sigue pisando el backup de la llamada externa. Esta migración arregla el aislamiento **entre hilos**, no la reentrancia **dentro** de un hilo; queda documentado en el Javadoc de esos 4 métodos con el mismo estilo que otros bugs conocidos-no-corregidos del fichero.

## Verificación (más rigurosa que en fases anteriores, por tratarse de concurrencia)

Además del ciclo habitual (compilar solo, comparar contra build original, batería de regresión), esta fase añadió dos pruebas nuevas porque el patrón de verificación de sesiones anteriores no cubre concurrencia:
- **Test de un solo hilo**: la misma secuencia `storePrecision()→exact(false)/setPrecision(...)→calcular→restorePrecision()` (más `setRepres/restoreRepres` y `setFormatON/setFixedON/restoreFormatStatus`) da resultado **bit a bit idéntico** al build del `HEAD` anterior — confirma que el comportamiento actual de la app (single-threaded) no cambió.
- **Test de concurrencia nuevo**: 8 hilos × 200 iteraciones, cada uno con su propia combinación de `precision`/`exact`/`representación`/`formato`, sin ninguna corrupción cruzada entre hilos ni excepciones — es la prueba que demuestra que la migración realmente resuelve el problema de origen, no solo que no rompe nada en single-thread.
- Batería de regresión estándar (`TestComplex01/07`, `TestGamma01`, `TestZeta01`) exit 0 en las 4, sin diferencias numéricas más allá del ruido no determinista ya documentado de `TestZeta01`.

Este commit, como en las sesiones anteriores, toca **solo** `src/com/ipserc/arith/complex/Complex.java` (242 inserciones, 202 borrados) — verificado con `git diff --stat` antes y después del `git add`.

## Nota de proceso (nueva esta sesión)

A diferencia de las fases 1-6 (bugs puntuales, workflow ligero de "resumen breve + confirmación"), esta fase se trabajó con el proceso completo: fork de exploración de alcance → `AskUserQuestion` para decidir el enfoque técnico (ThreadLocal con API intacta, sin arreglar la reentrancia en esta pasada) → `EnterPlanMode`/`ExitPlanMode` con plan escrito y aprobado explícitamente → implementación delegada a un fork en segundo plano con instrucciones muy explícitas (reglas duras: solo `Edit`, solo `Complex.java`, sin commit hasta terminar verificación) → el usuario activó "modo auto" a mitad de tarea, lo que autorizó al fork a comitear él mismo en cuanto pasara su propia verificación, en vez de esperar una revisión manual del diff en el hilo principal. **Precedente para ideas pendientes igual de grandes** (p.ej. la reestructuración arquitectónica de `Complex.java`): este es el proceso a replicar, no el workflow ligero de fases pequeñas.

## Ideas pendientes actualizadas tras la Quinta sesión

- Trabajo de layout `double[]` / Vector API (`jdk.incubator.vector`) — no iniciado.
- `MatrixComplex.java` y `VectorComplex.java` — no tocados ni revisados (y `MatrixComplex.java` sigue con cambios locales sin commitear del usuario).
- Reestructuración arquitectónica de `Complex.java` — no abordada.
- Limpieza de los 88 ficheros con line-endings+contenido mezclados (sesión de mantenimiento) — sigue pendiente.
- Bug de reentrancia de `_BCK` (single slot, no pila) en `storePrecision`/`restorePrecision`/`setRepres`/`restoreRepres` — documentado en Javadoc, sin corregir.

## Decisión del usuario para la Sexta sesión (al retomar, leer esto primero)

El usuario ha declarado una intención de alcance mayor que las sesiones anteriores: **limpiar/corregir del todo `Complex.java` y luego darle una reestructuración arquitectónica "de programador senior"** (separar responsabilidades en vez de una clase de ~4100 líneas que mezcla aritmética/parsing/formato/cajas ASCII/integración/límites/funciones especiales), y **después** pasar a revisar las demás clases del proyecto (`MatrixComplex.java`, `VectorComplex.java`, etc., no tocadas hasta ahora).

**Orden acordado explícitamente (antes de decidir los detalles, la sesión se cortó porque el usuario tuvo que irse):**
1. **Primero**, terminar los bugs/limpieza puntuales que ya se conocen y están documentados en la lista de arriba (workflow ligero de fases pequeñas, como las Fases 1-6): el bug de reentrancia de `_BCK`, `zeta_riemann_siegel`/`zeta_analytic_continuation` (documentados, ver más arriba en el documento), `limit_inf` sin cota, y revisar por corrección el bloque `boxTitle*`/`boxText*` (nunca revisado, solo se tocó el umbral de cero de `toString*`).
2. **Después**, acometer la reestructuración arquitectónica de `Complex.java` — con el proceso pesado (fork de exploración → `AskUserQuestion`/`EnterPlanMode` → implementación delegada), como se hizo en la Quinta sesión, no el workflow ligero.
3. **Al final**, extender la revisión a las demás clases del proyecto.

**Preguntas que quedaron sin responder al cortar la sesión anterior — YA RESPONDIDAS al retomar (30 julio 2026):**
- Forma de la reestructuración (para cuando se llegue al paso 2, no tocado aún): **split en paquete** — varias clases dentro de `com.ipserc.arith.complex` (aritmética core, parsing, formato, config/`State`, cajas ASCII, integración/límites, funciones especiales), no una reorganización de un único fichero ni dejarlo abierto a que Claude proponga arquitecturas.
- API pública (usada por `MatrixComplex`, `Eigenspace`, `Polynom`, `Laplace`, `Fourier`, `Z`, `Spline` y ~200 test files): **mantenerla intacta** durante el refactor — ninguna firma pública debe romperse; si el diseño interno lo pide, resolver con métodos package-private/delegación en vez de tocar callers.

---

# SEXTA SESIÓN DE REVISIÓN DE `Complex.java` — 30 julio 2026 (paso 1: bugs/limpieza puntuales)

> Continúa el "paso 1" acordado al final de la Quinta sesión (rematar bugs/limpieza puntuales documentados, workflow ligero, antes de la reestructuración arquitectónica). El usuario confirmó al retomar: empezar ya por el paso 1, y adoptar las dos recomendaciones de Claude para las preguntas de alcance del paso 2 (split en paquete + API pública intacta), ver sección anterior.

## Qué se hizo

1. **Limpieza de un falso positivo propio, verificado antes de tocar nada** (commit `1474c56`). Al revisar el punto de la lista de pendientes sobre reentrancia de `_BCK`, Claude creyó detectar un bug nuevo en `zeta(Complex)`: un bloque (`storeFormatStatus()`/`storePrecision()`/`setFixedON(8)`/`exact(true)`) que parecía código vivo camuflado tras un patrón de comentario `/* * /` ... `/* */` que Claude interpretó (mal) como autocerrado línea a línea, lo que habría significado una fuga de estado global permanente en cada llamada a `zeta()`. **El usuario pidió explícitamente verificar comentando el bloque y comparar resultados antes de asumir nada.** Verificado experimentalmente (build con el bloque tal cual vs. build con el bloque eliminado, 8 valores de `s` incluyendo casos frontera, comparando también el estado `EXACT`/`FIXED_NOTATION`/`MAX_DECIMALS` antes/después con `EXACT` forzado a `false`): **resultados idénticos en todos los casos**. Al revisar por qué, se confirmó que el error era de Claude contando caracteres: la línea `/* * /` NO se autocierra (el carácter antes de la barra final es un espacio, no un asterisco contiguo), así que el comentario permanece abierto hasta la siguiente línea `/* */`, que sí cierra con un `*/` contiguo — el bloque entero era comentario válido desde el principio, sin ningún efecto observable. Se eliminó como limpieza (comentario muerto, incluida la variable `_exact_` que ni se leía), sin cambio de comportamiento. **Lección: verificar experimentalmente contra el build original antes de reportar un hallazgo como bug real, tal y como pide el propio workflow — en este caso salvó de "arreglar" algo que no estaba roto.**
2. **Bug de reentrancia de `_BCK` corregido en los 3 pares afectados** (commit `d8a9e85`): `storePrecision()`/`restorePrecision()`, `setRepres()`/`restoreRepres()` y `storeFormatStatus()`/`restoreFormatStatus()` (este último no estaba mencionado explícitamente en la lista de pendientes de la Quinta sesión, pero tenía el bug idéntico — el usuario confirmó incluirlo ya que se tocaba el mismo mecanismo). Los 3 pares usaban un único slot de backup (`*_BCK`) por bloque dentro de `State`; un store/restore anidado dentro de otro en el mismo hilo pisaba el backup de la llamada externa, y el restore externo devolvía el estado al valor intermedio en vez del original. Fix: cada bloque pasa a una pila (`ArrayDeque`) de snapshots inmutables (`PrecisionSnapshot`, `FormatSnapshot`, `Representation`) dentro de la misma `State` per-hilo; `store()` hace `push`, `restore()` hace `poll()` y aplica (o no hace nada si la pila está vacía, igual que antes cuando no había un store previo). Cambio de comportamiento deliberado y sin impacto real: `restoreRepres()` antes hacía un *swap* (togglear entre los dos últimos valores en llamadas consecutivas sin `setRepres()` de por medio); ahora hace un `pop` puro, consistente con los otros dos pares — revisado con grep en todo `src/` que ningún caller depende del toggle (siempre se usa en pares `store→restore`).

## Verificación

- **Caso no anidado** (el único que soportaba el diseño anterior): bit a bit idéntico al build de `HEAD` en los 3 pares.
- **Caso anidado** (el bug en sí): store→cambiar→store anidado→cambiar→restore interno→restore externo devuelve correctamente al valor anterior al cambio externo, no al intermedio — verificado en `storePrecision`, `setRepres` y `storeFormatStatus`.
- **Smoke test de concurrencia**: 8 hilos × 300 iteraciones con anidamiento real de `storePrecision`/`setRepres`, sin corrupción cruzada ni excepciones.
- Batería de regresión (`TestComplex01/07`, `TestGamma01`, `TestZeta01`) exit 0 en las 4, sin diferencias numéricas más allá del ruido no determinista ya documentado.

Ambos commits (`1474c56`, `d8a9e85`) tocan **solo** `src/com/ipserc/arith/complex/Complex.java` — verificado con `git diff --cached --stat` antes y después de cada `git add`.

## Ideas pendientes actualizadas tras esta sesión (paso 1 en curso)

- ~~Bug de reentrada de `_BCK` (single slot, no pila) en `storePrecision`/`restorePrecision`/`setRepres`/`restoreRepres`~~ → **Resuelto en esta sesión, commit `d8a9e85`** (incluye también `storeFormatStatus`/`restoreFormatStatus`). Nota de terminología: el usuario corrigió a Claude — en español se dice "reentrada"/"reentrante", no "reentrancia" (guardado en memoria de Claude para futuras sesiones).
- ~~`limit_inf` sin cota de iteraciones~~ → **Resuelto en esta sesión, commit `270328e`.** Al investigarlo resultó ser más grave de lo documentado: no era solo "muchas iteraciones sin cota", sino un **cuelgue real** — si `func` devuelve `NaN` en un punto de módulo infinito (habitual por `Infinity*0` en identidades trigonométricas), todas las comparaciones del bucle (crecimiento, módulo cero, la propia condición del `while`) se evalúan silenciosamente como "seguir iterando" contra ese `NaN`, sin salida posible. Reproducido con una función sintética: el build de `HEAD` se cuelga de verdad (confirmado con `timeout`, no terminó en 20s); el corregido termina en 995 llamadas. Fix: cortar el bucle en cuanto `func` devuelve `NaN` (mismo predicado `isNaN()` que usa `limit()`) + cota dura `LIM_INF_MAX_ITER=2000` como red de seguridad (deliberadamente muy por encima de las ~992 duplicaciones necesarias para desbordar `point.mod` desde `LIM_INF`, así que no cambia ningún resultado que ya convergiera). Verificado bit a bit idéntico al original en 5 casos normales (convergentes y divergentes).
- ~~`zeta_riemann_siegel(s)`/`zeta_analytic_continuation(s)`~~ → **Eliminados en esta sesión, commit `a62e012`.** Se consideró implementar la fórmula real de Riemann-Siegel (con términos de corrección asintótica) o acelerar la convergencia del segundo, pero: (a) `zeta_riemann_siegel` requeriría un trabajo de implementación numérica serio y arriesgado (términos de corrección difíciles de transcribir/validar sin tablas de ceros de zeta de referencia) para un método sin ningún caller real; (b) la versión acelerada de la fórmula de `zeta_analytic_continuation` (Euler/Hasse-Sondow) **ya existe** en el propio fichero como `zeta_havil` (por eso `zeta()` la usa a ella). Sin callers reales (confirmado con grep en todo `src/`) y sin aportar nada por encima de `zeta_havil`, se eliminaron en vez de mantenerlos documentados indefinidamente — decisión del usuario ("no aportan nada").
- ~~Bloque `boxTitle*`/`boxText*` (ASCII art) — nunca revisado por corrección~~ → **Resuelto en esta sesión, commit `80b731f`.** Primera revisión real del bloque encontró 3 bugs: `makeBoxTitle`/`makeBoxText` no garantizaban hueco para su overhead mínimo (4 y 2 caracteres respectivamente), rompiendo la alineación de la caja cuando `size` quedaba a 1-3 caracteres del título/texto (verificado: build de `HEAD` da líneas de longitud distinta dentro de la misma caja para esos casos, confirmado con un test que compara longitudes); y `boxTextRandom()` llamaba a `boxTitle1` en su rama de repliegue (inalcanzable hoy, copy-paste). Riesgo real confirmado antes de arreglar: varios tests pasan textos dependientes de valores en tiempo de ejecución (`chrono.toString()`, `dim+"x"+dim`) con `boxSize` fijo, así que el bug podía dispararse de forma intermitente según la longitud exacta resultante. Fix con `Math.max` para garantizar el hueco mínimo, sin cambiar ningún caso que ya funcionara (verificado byte a byte idéntico). **Con este commit se completan todos los puntos del paso 1 acordado al final de la Quinta sesión — el siguiente paso es el 2 (reestructuración arquitectónica).**
- Trabajo de layout `double[]` / Vector API (`jdk.incubator.vector`) — no iniciado. Fuera del paso 1 (candidato para más adelante).
- `MatrixComplex.java` y `VectorComplex.java` — no tocados ni revisados (paso 3, más adelante).
- Reestructuración arquitectónica de `Complex.java` — **en curso, ver sección "PASO 2" más abajo.**
- Limpieza de los 88 ficheros con line-endings+contenido mezclados (sesión de mantenimiento) — sigue pendiente, sin relación con el plan de `Complex.java`.

---

# PASO 2 — REESTRUCTURACIÓN ARQUITECTÓNICA DE `Complex.java` (Sexta sesión, 30 julio 2026)

> Arranca con el plan escrito y aprobado en `EnterPlanMode`/`ExitPlanMode` (proceso "pesado", como en la migración a `ThreadLocal` de la Quinta sesión), precedido de un fork de exploración que mapeó el acoplamiento real entre las secciones de `Complex.java` antes de diseñar el split — evitando un plan genérico. El plan completo queda guardado en `C:\Users\josel\.claude\plans\shimmying-scribbling-umbrella.md`.

## Decisiones de alcance (confirmadas con el usuario antes de escribir código)

- **Forma de la reestructuración**: split en paquete `com.ipserc.arith.complex` — varias clases *package-private* (config/`State`, parsing, formato, cajas ASCII, funciones especiales, cálculo), no una reorganización de un único fichero.
- **API pública**: la de `Complex` debe permanecer **100% intacta** (usada por `MatrixComplex`, `Eigenspace`, `Polynom`, `Laplace`, `Fourier`, `Z`, `Spline` y ~200 test files). Cada método que se mueve a otra clase deja en `Complex` un **delegador público de una línea** con la firma exacta actual.

## Hallazgo clave del fork de exploración de acoplamiento (antes de diseñar el split)

- **Solo el bloque `BOXES & TITLES` tiene acoplamiento cero** con el resto de la clase (ningún método toca campos privados de `Complex` ni `state()`) — la extracción más segura, elegida como Fase 2.1 para validar el patrón.
- Todo lo demás tiene dependencias reales: `FUNCTIONS`/`TRIGONOMETRICS`/`INTEGRATION & DERIVATION`/`LIMITS`/`ROUND` tocan **campos privados directamente** (`z.rep`, `point.mod`, etc.) de instancias `Complex` recibidas como parámetro, no solo de `this` — mover ese código exige reescribir esos accesos a los getters públicos ya existentes (`rep()`, `imp()`, `mod()`, `pha()`).
- `state()` lo llaman Presentación, Setters, Boolean Ops y Funciones.
- `normalizePhase()` (bajo el banner "PRESENTATION" pero es en realidad un invariante de aritmética) lo usan tanto Presentación como la aritmética core in-place — debe quedarse en el núcleo (`Complex.java`), no moverse al formateador.

## Arquitectura objetivo (6 nuevas clases *package-private* + `Complex.java` como núcleo)

1. `ComplexState` — config/precisión (`State`/`PrecisionSnapshot`/`FormatSnapshot`/`ThreadLocal`, `exact()/precision()/storePrecision()/setRepres()/...`).
2. `ComplexBoxArt` — cajas ASCII (**Fase 2.1, ya hecha**).
3. `ComplexParser` — parseo por regex de `setComplex(String)`.
4. `ComplexFormat` — `toStringRec/Pol/GNUPlot`, `printRec/Pol`.
5. `ComplexFunctions` — `power/sqrt/exp/log/gamma*/zeta*/binomialCoef/factorial` + trigonometría.
6. `ComplexCalculus` — `integrate*/derivative` + `limit*`.

`Complex.java` (núcleo, permanece): campos privados, constructores, getters, `copy()`, operaciones unarias/booleanas/aritméticas (allocantes e in-place), `round`/`trunc`, y `normalizePhase()`.

Plan de ejecución: **una fase por commit, verificada y confirmada con el usuario antes de la siguiente** (no las 6 de golpe) — de menor a mayor riesgo/acoplamiento. Fases 2.5/2.6 (funciones especiales/trigonometría, cálculo) son las más grandes y con más reescritura mecánica `campo`→`campo()`; el plan contempla replicar el proceso pesado de la Quinta sesión (posible fork en segundo plano) para esas dos.

## Fase 2.1 — `ComplexBoxArt` (commit `aab0fd3`)

Extraído verbatim el bloque `BOXES & TITLES` completo (`boxTitle1..7`, `boxText1..7`, `makeBoxTitle`, `makeBoxText`, `repeat`, `boxTitleRandom/boxTextRandom`, `printBoxTitle/printBoxText`) a la nueva clase package-private `ComplexBoxArt`, incluyendo los 3 fixes de la fase de revisión anterior de esta misma sesión (los `Math.max` de `makeBoxTitle`/`makeBoxText` y el copy-paste de `boxTextRandom`). `Complex.java` mantiene cada método público como delegador de una línea. Se eliminó el import `ThreadLocalRandom` de `Complex.java` (quedó sin uso).

Verificado: compila junto con `Complex.java`; un test suelto que imita el patrón de uso externo real (`Syseq.java` llama `Complex.repeat(...)`) da la misma salida determinista que el build original. Batería de regresión exit 0 en las 4, sin diferencias numéricas. `Syseq.java` no se pudo compilar de forma aislada por un problema de entorno preexistente y ajeno a este cambio (`Polynom.java` depende de la librería externa `JavaPlot` no presente, y `VectorComplex.java` tiene un error de sintaxis en trabajo local sin commitear del usuario, confirmado con `git status`) — cubierto por el test de uso externo equivalente.

**Incidente sin explicación durante esta fase**: el campo `VERSION` del fichero de trabajo apareció con un sufijo `" (commit 5475774)"` que Claude no añadió intencionadamente (no hay hooks de git, ni filtros `.gitattributes`, ni el fork de exploración de solo lectura lanzado antes de esta fase pueden haberlo escrito). Corregido antes de compilar/verificar/commitear (`VERSION` final: `1.11 (2026_0730_1916)`). Si vuelve a pasar en fases futuras, revisar con más detalle antes de asumir que es inofensivo.

## Fase 2.2 — `ComplexState` (commit `9d634f0`)

Movida toda la maquinaria de configuración per-hilo (clase `State` anidada, `PrecisionSnapshot`/`FormatSnapshot`, el `ThreadLocal`, las constantes `_DEF`, y los ~50 métodos de `exact()/precision()/storePrecision()/setFormatON()/setFixedON()/setScientificON()/setRepres()/getRepres()/restoreRepres()/etc.`) a la nueva clase package-private `ComplexState`. `Complex.java` mantiene cada método público como delegador de una línea.

Detalles de diseño importantes:
- **`Complex.Representation`** (el enum público, referenciado externamente como `Complex.setRepres(Complex.Representation.POLAR)` en tests) **se queda declarado en `Complex.java`** — solo la configuración `State` que lo usa se movió, no el tipo en sí. Si se hubiera movido el enum entero a `ComplexState`, se habría roto la API pública (el nombre cualificado cambiaría de `Complex.Representation` a `ComplexState.Representation`).
- `showPrecision()` se queda en `Complex.java` (mezcla valores de `ComplexState` con `LIM_INF`/`LIM_NUMDECS`/`LIM_PRECISION`, constantes propias de `Complex` no relacionadas con el estado de precisión), pero ahora llama a los getters públicos ya delegados en vez de tocar `state()` directamente.
- `normalizePhase()`/`chr()`/`formatNbr()` se quedan en `Complex.java` por ahora (Fase 2.4 moverá `formatNbr`/`chr` junto con el resto de presentación); sus llamadas a `state()` se reescribieron a los getters package-private de `ComplexState`, incluyendo un getter nuevo `representation()` (devuelve el enum, a diferencia de `getRepres()` que devuelve el `String`) para los `switch` de `toString()`/`toStringPol()`.
- Todos los call-sites que quedan en `Complex.java` (`setCre()`, la familia `toString*`/`formatNbr`, `equals`/`isZero`/`rePartNull`/`imPartNull`, y 2 sitios en `FUNCTIONS`) pasan de `state().CAMPO` a `ComplexState.getter()` — ningún acceso directo a campos de `State` fuera de `ComplexState.java`.

Verificado: compila limpio junto con `ComplexBoxArt.java`; test suelto que cubre precisión/exacto, el anidamiento real de `storePrecision`/`restorePrecision` y de `setRepres`/`restoreRepres` (confirma que el fix de reentrada de esta misma sesión sigue funcionando tras la extracción), los 3 flags de formato afectando de verdad a `toStringRec()`, y un smoke test de concurrencia (8 hilos × 300 iteraciones) sin fallos; test separado que imita el patrón de uso real de `MatrixComplex`/`Eigenspace`/etc. (`storePrecision()`→cambiar modo→calcular→`restorePrecision()`) con salida idéntica al build anterior. Batería de regresión exit 0 en las 4, sin diferencias numéricas. `TestBase01/02` no se pudieron compilar de forma aislada por la misma limitación de entorno preexistente (dependen de `MatrixComplex`/`VectorComplex`, que a su vez dependen de `JavaPlot` no presente) — cubierto por el test de patrón externo equivalente.

Nota metodológica: al comparar contra `/tmp/origbuild`, la sección de anidamiento de `storePrecision` mostró una diferencia esperada — ese build de comparación es una instantánea muy temprana de esta misma sesión (de antes del fix de reentrada ya commiteado hoy), no un problema de esta fase. Las demás secciones (precisión básica, `setRepres` anidado, formato, concurrencia) fueron idénticas.

## Fase 2.3 — `ComplexParser` (commit `7c3721f`)

Movida la lógica de parseo por regex de `setComplex(String)` (patrones `REC_PATTERN`/`POL_PATTERN` precompilados + el `switch` de interpretación de grupos capturados) a la nueva clase package-private `ComplexParser`, con un método `parse(String)` que devuelve un resultado inmutable (`Parsed`: indica si es polar o rectangular + los dos valores). `Complex.setComplex(String)` mantiene su firma pública exacta; ahora solo llama a `ComplexParser.parse(numC)`, asigna los campos según el resultado, y llama a `setRecCoord()`/`setPolCoord()` (que se quedan en el núcleo, mutan `this` directamente). El resto de `INITIALIZERS & SETTERS` (`setCre`, `setPolCoord`, `setRecCoord`, `setComplex(char,double,double)`, `setComplexRec/Pol`, `setComplexRandom*`, `integrize`) se queda sin tocar — son métodos que mutan la instancia, no lógica de parsing.

Verificado: compila junto con `ComplexState.java`/`ComplexBoxArt.java`. Test suelto con 17 entradas válidas (rectangular, polar, casos frontera como `"i"`/`"-i"`/`"3+"`/notación científica) y 4 inválidas (esperando `IllegalArgumentException`) — salida idéntica byte a byte al build original, incluidos los mensajes de excepción exactos (solo difieren las líneas del stack trace, que apuntan al nuevo fichero, como es de esperar). Batería de regresión exit 0 en las 4, sin diferencias numéricas.

## Próximos pasos

Al retomar, la Fase 2.4 (`ComplexFormat`, la presentación/`toString*`) es la siguiente — confirmar con el usuario antes de empezar, siguiendo el mismo patrón de resumen+confirmación de todas las fases anteriores.

---

*Última actualización de este bloque: sesión del 30 julio 2026. Sección "Complex.java" (sesión 1-2) congelada tras el commit `72fd463`; sección "Mantenimiento de repositorio" añadida tras los commits `75c95a1` y `ef7bfc2` (tag `v1.0`); sección "Tercera sesión de revisión" añadida tras los commits `20a4bb3` y `a39f99a`; sección "Cuarta sesión de revisión" añadida tras los commits `a5d6a99`, `6131af8` y `bd1b3fd`; sección "Quinta sesión de revisión" añadida tras el commit `dccaf1f`; sección "Sexta sesión de revisión" añadida tras los commits `1474c56` y `d8a9e85` (paso 1 en curso, quedan 3 puntos: `zeta_riemann_siegel`/`zeta_analytic_continuation`, `limit_inf` sin cota, revisión de `boxTitle*`/`boxText*`).*
