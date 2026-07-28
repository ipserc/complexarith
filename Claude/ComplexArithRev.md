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

- **Repo:** `C:\Users\josel\workspace-eclipse\complexarith_github` (proyecto Eclipse/Java personal, NO es un repo limpio: tiene muchísimos ficheros modificados/sin trackear que son trabajo propio del usuario, ajenos a esta tarea — **nunca tocarlos ni incluirlos en un commit de esta tarea**).
- **Fichero de trabajo único hasta ahora:** `src/com/ipserc/arith/complex/Complex.java` (~3800 líneas). No se ha tocado `MatrixComplex.java`, `VectorComplex.java` ni ningún otro fichero del proyecto.
- **Rama:** `master`. Se hacen commits directos, no hay rama de feature.
- **Último commit de esta tarea:** `80b860e` "Complex: completa in-place en zeta_havil, confirma que no aplica el bug de mONE.power" (28-29 julio 2026). Ver la lista completa de commits más abajo.
- La sesión se paró aquí a petición del usuario ("vamos a parar ya"), no por falta de trabajo pendiente — hay bastante fruta madura sin recoger (ver "Ideas pendientes / no exploradas").

## Permisos concedidos por el usuario (vigentes, no hace falta re-preguntar)

- *"tienes permisos completos para hacer lo que necesites dentro de este proyecto"* — permiso general de trabajo dentro del repo.
- *"Puedes mejorar los algoritmos de cálculo si ves que no dan resultados correctos"* — permiso explícito para corregir bugs matemáticos que se encuentren durante el trabajo, no solo hacer refactor/rendimiento.
- *"tienes permiso para ejecutar todas las pruebas sin consultar"** — no hace falta pedir permiso para compilar/ejecutar tests o benchmarks.
- El usuario prefiere que **cada fase de mejora sea su propio commit**, y que se le pregunte (con `AskUserQuestion`) cuando hay una decisión real de alcance/riesgo (p.ej. "¿arreglo el algoritmo completo o solo documento el bug?"), no para cosas mecánicas.
- El usuario escribe y espera respuesta en **español**.

## Workflow acordado (seguir SIEMPRE, en este orden, en cada fase)

1. Implementar el cambio en `Complex.java` con `Edit`.
2. Compilar solo: `javac -d /tmp/complexbuild -encoding UTF-8 src/com/ipserc/arith/complex/Complex.java` (usar rutas absolutas si `cd` no persiste bien entre llamadas de Bash).
3. **Verificar ANTES de fiarse**: escribir un fichero de prueba suelto en el scratchpad de la sesión, compilarlo contra el jar/clases nuevas, y comparar contra:
   - el comportamiento matemáticamente esperado (valores conocidos: $\zeta(2)=\pi^2/6$, $\zeta(3)=$ Apéry, $\zeta(4)=\pi^4/90$, $\zeta(1/2)\approx-1.4603545$, identidades exactas como $\arcsin(z)+\arccos(z)=\pi/2$, roundtrips como $\sin(\arcsin(z))=z$), **y**
   - el código ORIGINAL sin tocar, compilado aparte desde `git show HEAD:ruta > fichero` en un directorio de build separado (`/tmp/origbuild`), para confirmar que no hay regresión bit-a-bit (salvo ruido de FP esperado en el último dígito).
4. Ejecutar la batería de regresión rápida (ver más abajo) y confirmar `exit:0` en todos.
5. `git diff --cached --stat` **antes** de `git add` y otra vez **después** de `git add` — confirmar que el único fichero que va a entrar en el commit es `src/com/ipserc/arith/complex/Complex.java`. Esto es crítico, ver el incidente de abajo.
6. `git commit` con mensaje largo y explicativo (ver estilo en el historial): qué cambia, por qué, qué se verificó, con números concretos (tiempos, valores). Termina siempre con `Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>`.
7. Reportar al usuario en 2-4 frases qué se hizo y preguntar cómo seguir.

### Incidente real que motiva el paso 5 (no repetir)
En el primer commit de la sesión, `git add src/com/ipserc/arith/complex/Complex.java` + `git commit` **arrastró también** un renombrado (`Vector.java` → `VectorComplex.java`) que el usuario ya tenía staged en el índice de git de una sesión anterior, ajeno a esta tarea. `git commit` siempre commitea TODO el índice, no solo lo que se acaba de `git add`. Se corrigió con `git reset --soft HEAD~1` (deshace el commit, mantiene el índice) seguido de `git reset HEAD -- <ficheros ajenos>` (destaged solo lo ajeno, deja `Complex.java` staged) y se volvió a comitear limpio. Desde entonces, **siempre** comprobar `git diff --cached --stat` antes de commitear.

### Batería de regresión rápida (usar estos, cubren aritmética core + gamma + zeta)
```
cd "C:\Users\josel\workspace-eclipse\complexarith_github\src"
javac -d /tmp/complexbuild -encoding UTF-8 TestComplex/TestComplex01.java TestComplex/TestComplex07.java TestComplex/TestGamma01.java TestComplex/TestZeta01.java
cd /tmp/complexbuild
for t in TestComplex01 TestComplex07 TestGamma01 TestZeta01; do java TestComplex.$t > /dev/null 2>&1; echo "$t exit:$?"; done
```
`TestComplex01-05` cubren aritmética/formato básicos, `TestComplex07` cubre `binomialCoef`, `TestGamma01/02` cubre gamma (`TestGamma02` es lento, ~decenas de segundos, solo usarlo si se toca gamma), `TestZeta01` cubre zeta. El scratchpad de la sesión (temporal, no persiste entre sesiones) se usó en `C:\Users\josel\AppData\Local\Temp\claude\...\scratchpad\` — cualquier fichero de verificación ad-hoc ahí no sobrevive a un reinicio, hay que reescribirlo si hace falta reproducir un test.

## Commits hechos en esta sesión (orden cronológico, todos en `master`, todos tocan solo `Complex.java`)

1. **`08d77b3`** — Optimiza aritmética core: `times`/`divides` reescritos con fórmula rectangular directa (0 llamadas trig, antes 2, reutilizando campos `rep/imp/mod/pha` ya cacheados de ambos operandos vía una factoría privada `raw()`). Corrige `power(Complex)` para el branch cut de `0^z`. Corrige `imPartNull()`/`rePartNull()` (división por cero silenciosa). Añade `equals(Object)`/`hashCode()` (antes solo había `equals(Complex)` sobrecargado, no un override real — cualquier `HashSet`/`HashMap`/`List.contains` habría usado identidad silenciosamente; verificado que no hay uso de ese tipo en el repo, cambio seguro). Precompila los `Pattern` regex de `setComplex(String)`. Quita boxing en `isInfinite()`/`isNaN()`.
2. **`9112552`** — Mutadores in-place `plusEq/minusEq/timesEq/dividesEq` aplicados a `gamma_nemes` y `binomialCoef(Complex,Complex)`.
3. **`93b7b1c`** — In-place en `gamma_fast` (Lanczos, es la implementación por defecto de `gamma()`), `gamma_weiertrass`, `gamma_euler`.
4. **`e88e049`** — In-place en `beta` y `zeta_ext`.
5. **`25b5b88`** — **Bug matemático real corregido**: `binomialCoef(Complex,Complex)` usaba `gamma(n)/gamma(k)/gamma(n-k)` (falta `+1` en cada término, porque `gamma(m)=(m-1)!`) dando p.ej. `C(6,2)=20` en vez de `15`. Corregido a `gamma(n+1)/(gamma(k+1)·gamma(n-k+1))`. `binomialCoef(int,int)` ya era correcto (usa `factorial` directo), el bug estaba aislado al overload `Complex`.
6. **`66325fe`** — **Bug de overflow real corregido**: `arcsin`/`arccos` calculan `z.power(2)` internamente, que desborda a `Infinity` para `|z| > ~1.34e154` (¡`arcsin(1e200)` daba `NaN+NaNi`!). Se probó primero la sustitución de libro de texto `sqrt(1-z)·sqrt(1+z)` en vez de `sqrt(1-z²)` — **rota**: `sqrt(a)·sqrt(b) ≠ sqrt(a·b)` en la rama principal compleja cuando `arg(a)+arg(b)` sale de `(-π,π]`, y esto rompía silenciosamente casos NO extremos como `arccos(1.5)` (signo invertido). Se descartó. En su lugar: guarda `SAFE_SQUARE_LIMIT=1e150` que activa una forma cerrada derivada a mano (`asin(z)≈π/2∓i·ln(2z)`, signo según semiplano de `z`) solo para `|z|` extremo, validada con continuidad exacta en la frontera de la guarda y la identidad `asin(z)+acos(z)=π/2` en los 4 cuadrantes.
7. **`e759428`** — Mismo bug en `arcsinh`/`arccosh`, arreglado reescribiéndolas en términos de `arcsin`/`arccos` vía las identidades exactas `arcsinh(z)=-i·arcsin(iz)`, `arccosh(z)=-i·arccos(z)` (derivadas de las fórmulas YA existentes en el fichero, no de una identidad de libro de texto independiente, para garantizar que la rama coincide). Heredan la guarda automáticamente, sin duplicar la derivación.
   - **Auditadas y confirmadas SIN el bug** (no tocadas): `arctan`, `acotan`, `arctanh`, `acoth` — ninguna eleva `z` al cuadrado (usan cocientes `(a±z)/(b∓z)`), dan valores finitos y coherentes con las asíntotas conocidas para `|z|` extremo, y dan `Infinity` limpio (no `NaN`) en sus polos genuinos (`z=±i` para `arctan`/`acotan`, `z=±1` para `arctanh`/`acoth`), que es el comportamiento correcto.
8. **`847c0bc`** — In-place en `zeta_riemann_siegel` (no usada por nadie más en el fichero) y `zeta_analytic_continuation`. Se documenta en Javadoc un bug real de `zeta_riemann_siegel` (es asintótica; para `|Im(s)|<2π` el corte `N=⌊√(|Im(s)|/2π)⌋` da `N=0` y devuelve `0.0` siempre; incluso donde corre da valores muy alejados de `zeta_havil`) sin arreglarla — no la llama `zeta()` ni nada, no vale la pena rederivar el algoritmo. Se quita un `k.println("k=")` de depuración olvidado en `zeta_analytic_continuation` (inundaba stdout en cada iteración).
9. **`1e8b521`** — Investigado por qué `zeta_analytic_continuation` no converge cerca de un cero de zeta (`s=0.5+14.13i`). Dos causas independientes encontradas:
   - **Causa 1 (corregida):** el signo alternante `(-1)^(k-1)` se calculaba con `Complex.mONE.power(k.minus(1))` — como `mONE` tiene fase `π`, ese ángulo crece sin límite (`π·(k-1)`), así que normalizarlo cuesta `O(k)` por llamada (bucle restando `2π`) y **además** acumula error: a `k=5.000.000` el "signo" ya había derivado a `-0.99999993-3.7e-4i` en vez de `-1+0i` exacto. Sustituido por un `double sign` que se invierte en `O(1)` cada iteración. Medido: `zeta_analytic_continuation(2)` pasa de **11,5s a 0,031s (~370x)**, mismo resultado correcto.
   - **Causa 2 (documentada, NO corregida — es estructural):** el criterio de parada (`z1.equals(z2)`, tolerancia ~1e-12 absoluta) necesita `k ~ (1e12)^(1/Re(s))` términos porque la serie decae como `1/k^Re(s)`. Para `Re(s)=2` son ~1e6 términos (factible); para `Re(s)=0.5` (línea crítica) son **~1e24** — inalcanzable sin importar la velocidad. Confirmado tras arreglar la causa 1 que `zeta_analytic_continuation(0.5)` sigue sin terminar en 20s. No se rehace el criterio de convergencia (cambiaría el algoritmo). No importa en la práctica: `zeta()` usa `zeta_havil` (validada, convergente para todo `s`), no este método.
10. **`80b860e`** — Investigado si el mismo bug de `mONE.power(k)` afecta a `zeta_havil` (usa el mismo patrón). **Conclusión: no aplica** — el bucle interior está acotado a `k≤169` por diseño (`maxN=170`, no un `while` sin límite), y a esa escala `Complex.mONE.power(k)` es exacto (error=0 medido) y su coste es insignificante (~12% del tiempo total de la función). Se completa la única línea que quedaba sin in-place (el `divides` final). De paso se comprobó que `factorial(169)` (usado dentro de `binomialCoef(int,int)`, llamado desde `zeta_havil`) no desborda ni pierde precisión catastrófica — no hay bug ahí.

## Trucos, reglas y patrones específicos de este código (aprendidos a base de medir, no asumir)

- **Patrón `raw()`:** factoría privada `Complex.raw(rep, imp, mod, pha)` que asigna los 4 campos directamente (más `setCre()`) sin pasar por los constructores `'C'`/`'P'` (que siempre recalculan la otra representación vía trig). Se usa en `times`/`divides` para evitar `cos`/`sin`/`atan2`/`hypot` cuando ya se conocen ambas representaciones de los operandos.
- **Mutadores in-place `plusEq/minusEq/timesEq/dividesEq`:** mutan `this` y lo devuelven (fluido), no allocan. **PELIGRO real y ya verificado:** nunca llamarlos sobre una constante estática compartida (`Complex.ONE`, `ZERO`, `mONE`, `PI`, `DOSPI`, `HALFPI`, `i`/`j`) — corrompería esa constante para todo el programa. Patrón seguro: si un bucle necesita un contador/acumulador mutable que "empieza como" una de esas constantes (p.ej. `k` empezando en 1), inicializarlo con `new Complex(1,0)` (o `.copy()`), nunca con `Complex.ONE` directamente. Ya hubo que arreglar este exacto bug en `zeta_re`/`zeta_analytic_continuation` durante el propio refactor in-place.
- **`Complex.mONE.power(k)` para signo alternante `(-1)^k` es una trampa de rendimiento/precisión si `k` no está acotado:** pasa por la fórmula general de potencia compleja (`log`/`exp` + normalización de fase con un bucle `while` que resta `2π`), cuyo coste y error crecen con `k` sin límite. Con `k` acotado (decenas/cientos) es gratis y exacto; con `k` creciendo a millones (bucles `while` de convergencia) es catastrófico (`O(k²)` total, y el "signo" deja de ser exactamente `±1`). Alternativa correcta y barata: llevar un `double sign = 1.0` que se invierte (`sign = -sign`) cada iteración.
- **Rama principal de la raíz cuadrada compleja NO es multiplicativa en general:** `sqrt(a)·sqrt(b) ≠ sqrt(a·b)` cuando `arg(a)+arg(b)` sale de `(-π,π]`. Cualquier "truco" que sustituya `sqrt(f(z))` por un producto de raíces separadas para evitar overflow hay que validarlo exhaustivamente (no solo en el caso extremo que motivó el cambio) porque puede romper silenciosamente el rango normal. Se descubrió así el intento fallido de arreglo de `arcsin`/`arccos`.
- **Técnica de validación de fórmulas de rama que SÍ funcionó:** (1) continuidad exacta en la frontera entre la fórmula vieja y la nueva (mismo valor, `diff=0.0`, en el último punto donde la vieja aún no desborda); (2) una identidad matemática independiente de la rama que debe cumplirse siempre (`asin(z)+acos(z)=π/2`) probada en los 4 cuadrantes; (3) contrastar el salto de signo/discontinuidad encontrado contra el comportamiento YA existente del código sin tocar en el rango moderado (para distinguir "corte de rama esperado" de "bug nuevo introducido").
- **Reutilizar identidades derivadas DEL PROPIO fichero, no de un libro de texto independiente**, cuando se reescribe una función en términos de otra ya arreglada (p.ej. `arcsinh(z)=-i·arcsin(iz)` se derivó algebraicamente a partir de la fórmula de `arcsin` que ya está en `Complex.java`, no de una tabla de identidades externa) — así se garantiza que la convención de rama coincide exactamente, sin sorpresas.
- **`zeta_havil` es el método de referencia validado** para comparar cualquier otra implementación de zeta (`zeta_re` para `Re(s)>2`, `zeta_ext` para `Re(s)<-1`, `zeta_havil` en medio — así decide el dispatcher `zeta()`). Sirve como oráculo de corrección para `zeta_riemann_siegel`/`zeta_analytic_continuation`, que NO están en el camino usado por `zeta()`.
- **Antes de "arreglar" algo, comprobar si se usa de verdad** (`grep` por el nombre del método en todo `src/`). Tanto `zeta_riemann_siegel` como `zeta_analytic_continuation` resultaron ser código experimental aislado que no llama nadie más — cambia mucho el cálculo de riesgo/beneficio de invertir tiempo en rederivar un algoritmo completo.
- **Falsos positivos de regresión:** dos veces durante la sesión un `exit:1` en un test resultó ser un problema de classpath (había recompilado solo el fichero que tocaba y no las clases de test tras un `rm -rf` del directorio de build), no una regresión real. Si un test que antes pasaba falla justo después de un `rm -rf /tmp/complexbuild`, recompilar TODAS las clases de test necesarias antes de asumir que hay un bug.
- **Benchmarks de proceso completo (`java TestGamma02` con timing externo) son ruidosos** (variación del 40%+ vista entre dos ejecuciones idénticas, por carga del sistema/Eclipse en background). Para medir una mejora real: benchmark **dentro del mismo proceso**, con warmup (varias llamadas antes de medir) y varias repeticiones, comparando old-vs-new con el mismo harness.

## Bugs conocidos, documentados en el propio Javadoc de `Complex.java`, sin corregir (a propósito)

- `zeta_riemann_siegel(s)`: da `0.0` para cualquier `s` con `|Im(s)|<2π`, e inexacta incluso donde el bucle corre. No usada por nadie. Documentado, no arreglada.
- `zeta_analytic_continuation(s)`: no converge en tiempo práctico para `Re(s) ≲ 0.7` (necesitaría `~10^24` términos para `Re(s)=0.5`). Estructural, documentado, no arreglada. **Cuidado: no llamar `zeta_analytic_continuation` con `Re(s)` pequeño en un test/benchmark sin timeout — se queda colgado indefinidamente** (ya pasó dos veces en esta sesión).

## Ideas pendientes / no exploradas (candidatas para continuar)

Del informe de auditoría inicial (primera respuesta de la sesión), lo que sigue sin tocar:
- **Estado estático mutable global** (`EXACT`, `PRECISION`, `ZERO_THRESHOLD`, `REPRESENTATION`, `FORMAT_NBR`...) — no es thread-safe, incompatible con paralelización real.
- `randomNbr` es un único `Random` estático compartido (candidato a `ThreadLocalRandom`), usado solo en los métodos `boxTitle*`/`boxText*` (cosmético, bajo impacto).
- `System.exit(1)` dentro de `setComplex` ante parseo inválido — debería lanzar excepción en vez de matar la JVM.
- Métodos `__deprecated__` (`equalsred__`, `isZeroRed__`, `imPartNullRed__`, `rePartNullRed__`, `sqrroot__`) conviven con sus reemplazos — candidatos a eliminar si se confirma que no se usan.
- `round`/`trunc`/`getDecPart`/`getIntPart` usan `BigDecimal`/`String.format`/`Double.parseDouble` para redondeo — mucho más lento que aritmética de coma flotante pura; no se ha tocado.
- **No se ha hecho ningún trabajo de layout `double[]` / Vector API** (`jdk.incubator.vector`) que pedía el prompt original — todo el trabajo de rendimiento ha sido a nivel de objeto `Complex` (in-place, evitar trig), no de vectorización SIMD sobre arrays.
- `MatrixComplex.java` y `VectorComplex.java` no se han tocado ni revisado en absoluto.
- Dentro de `Complex.java`, sin revisar todavía: `sinc`/`cosc`/`tanc`, `chebyshev`/`ChebyshevZero`, `integrate`/`integrateRE`/`integrateIM`/`derivative` (ya tienen algo de in-place de la fase de "bucles calientes" inicial, pero no se han auditado por corrección matemática), las funciones de `limit`/`limit_inf` (usan comparación con tolerancias parecidas a las que causaron bugs en otros sitios — candidatas sospechosas), y todo el bloque de formateo/presentación (`toStringRec`, `toStringPol`, etc., no revisado).
- La clase entera sigue siendo ~3800 líneas mezclando aritmética + parsing + formato + cajas de texto ASCII + integración numérica + límites + funciones especiales — la reestructuración arquitectónica (separar responsabilidades) que pedía el prompt original no se ha abordado, solo el rendimiento/corrección puntual.

---

*Última actualización de este bloque: sesión del 28-29 julio 2026, tras el commit `80b860e`.*
