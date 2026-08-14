# Recetario: cómo volver a ejecutar (y modificar) los experimentos ya hechos

Cada bloque de trabajo del "Rol Física/Mecánica Cuántica" dejó, además de la
clase nueva, una batería de verificación en `src/TestComplex/`, con el
patrón de nombre `Scratch<Clase>Audit01.java` (o, para el primer bloque,
`TestBell01.java`). Estas baterías NO son solo "tests" en el sentido
habitual -- son, literalmente, el registro de los experimentos que ya se
hicieron para verificar cada pieza, con comentarios explicando CADA
hallazgo no obvio. Léelas: son la mejor documentación "viva" que existe de
cada clase, complementaria a los documentos `02`-`15` de este directorio.

## Preparación (una vez por sesión de terminal)

```bash
export PATH="/c/Program Files/Eclipse Adoptium/jdk-21.0.12.8-hotspot/bin:$PATH"
mkdir -p /tmp/quantum_build
javac -d /tmp/quantum_build -sourcepath src \
  $(find src/com/ipserc/arith/quantum src/com/ipserc/arith/matrixcomplex \
        src/com/ipserc/arith/complex src/com/ipserc/arith/polynom -name "*.java")
```

## Catálogo de baterías disponibles

| Fichero | Verifica | Documento relacionado |
|---|---|---|
| `TestBell01.java` | `BellTest` -- CHSH exacto y simulado Monte Carlo, convergencia | `03_BellTest.md` |
| `ScratchQubitsNAudit01.java` | `Qubits` -- kets de n qubits, `operatorOnQubit`, `controlledGate`, GHZ | `02_Qubits.md` |
| `ScratchBellTestNQubitAudit01.java` | `BellTest` generalizado a n qubits, verificación cruzada con `DensityMatrix` | `03_BellTest.md` |
| `ScratchDensityMatrixAudit01.java` | `DensityMatrix` -- traza parcial, entropía de von Neumann | `04_DensityMatrix.md` |
| `ScratchTimeEvolutionAudit01.java` | `TimeEvolution` -- unitariedad, precesión de espín; incluye el bug de `Diagfactor` encontrado en el camino | `05_TimeEvolution.md` |
| `ScratchTeleportationAudit01.java` | `Teleportation` -- las 4 ramas, probabilidad `0.25` uniforme, corrección exacta | `06_Teleportation.md` |
| `ScratchDecoherenceAudit01.java` | `Decoherence` -- completitud de Kraus, casos límite de cada canal | `07_Decoherence.md` |
| `ScratchDecoherenceChainAudit01.java` | `Decoherence.applyChain()` -- combinación/conmutatividad de canales encadenados | `07_Decoherence.md` |
| `ScratchBlochSphereAudit01.java` | `BlochSphere` -- posiciones de libro de texto, ida y vuelta, script de plot | `08_BlochSphere.md` |
| `ScratchDeutschJozsaAudit01.java` | `DeutschJozsa` -- exactitud a precisión `double`, rechazo de `f` inválida | `09_DeutschJozsa.md` |
| `ScratchBernsteinVaziraniAudit01.java` | `BernsteinVazirani` -- recuperación exhaustiva de secretos, el caso constante-vs-secreto-0 | `10_BernsteinVazirani.md` |
| `ScratchGroverAudit01.java` | `Grover` -- probabilidad exacta vs fórmula cerrada, sobre-rotación | `11_Grover.md` |
| `ScratchNoisyTeleportationAudit01.java` | `NoisyTeleportation` -- puente exacto con `Teleportation` a ruido nulo, fidelidad decreciente | `12_NoisyTeleportation.md` |
| `ScratchNoisyDeutschJozsaAudit01.java` | `NoisyDeutschJozsa` -- los 2 hallazgos de dónde importa el ruido | `13_NoisyDeutschJozsa.md` |
| `ScratchNoisyBernsteinVaziraniAudit01.java` | `NoisyBernsteinVazirani` -- fórmulas cerradas de degradación, dónde aterriza la probabilidad perdida | `14_NoisyBernsteinVazirani.md` |
| `ScratchNoisyGroverAudit01.java` | `NoisyGrover` -- invariancia exacta de `bitFlip`, dependencia de `amplitudeDamping` del bit del target | `15_NoisyGrover.md` |

## Ejecutar una batería

```bash
javac -d /tmp/quantum_build -cp /tmp/quantum_build src/TestComplex/TestBell01.java
java -cp /tmp/quantum_build TestComplex.TestBell01
```

Sustituye `TestBell01` por el nombre de la clase que quieras (sin `.java`).
Cada batería imprime una línea `OK`/`FAIL` por comprobación, y termina con
`N/N OK`.

Para ejecutar TODAS de golpe:

```bash
for c in TestBell01 ScratchQubitsNAudit01 ScratchBellTestNQubitAudit01 \
  ScratchDensityMatrixAudit01 ScratchTimeEvolutionAudit01 ScratchTeleportationAudit01 \
  ScratchDecoherenceAudit01 ScratchDecoherenceChainAudit01 ScratchBlochSphereAudit01 \
  ScratchDeutschJozsaAudit01 ScratchBernsteinVaziraniAudit01 ScratchGroverAudit01 \
  ScratchNoisyTeleportationAudit01 ScratchNoisyDeutschJozsaAudit01 \
  ScratchNoisyBernsteinVaziraniAudit01 ScratchNoisyGroverAudit01; do
  echo "=== $c ==="
  javac -d /tmp/quantum_build -cp /tmp/quantum_build "src/TestComplex/$c.java" 2>&1
  java -cp /tmp/quantum_build "TestComplex.$c" | tail -3
done
```

## Cómo construir tu propio experimento a partir de uno existente

La forma más rápida de aprender a usar una clase es copiar el `main()` de
su batería de auditoría y empezar a cambiar números. Receta:

1. Elige la batería relacionada con lo que quieres explorar (tabla de
   arriba).
2. Abre el fichero y localiza el bloque `check(...)` que te interese --
   cada uno tiene un comentario explicando QUÉ comprueba y (a menudo) POR
   QUÉ ese resultado concreto es el esperado.
3. Copia ese fragmento a un fichero nuevo (p. ej.
   `src/TestComplex/MiExperimento01.java`, paquete `TestComplex`), quita el
   framework `check()`/`ok`/`fail` si no lo necesitas, y simplemente
   imprime lo que te interese ver con `System.out.println`.
4. Cambia parámetros: otro `n`, otro canal de ruido, otra probabilidad,
   otro `target`/`secret`... y vuelve a compilar/ejecutar.

Ejemplo mínimo, partiendo de `ScratchGroverAudit01`, para explorar cómo
cambia la probabilidad óptima según crece `n`:

```java
package TestComplex;

import com.ipserc.arith.quantum.Grover;

public class MiExperimentoGrover01 {
    public static void main(String[] args) {
        for (int n = 2; n <= 8; ++n) {
            int target = (1 << n) - 1; // ultimo elemento
            double p = Grover.search(target, n);
            System.out.printf("n=%d (N=%d)  P(target)=%.4f%n", n, 1 << n, p);
        }
        // Cuidado con n grande -- ver la nota de rendimiento en 11_Grover.md
    }
}
```

```bash
javac -d /tmp/quantum_build -cp /tmp/quantum_build src/TestComplex/MiExperimentoGrover01.java
java -cp /tmp/quantum_build TestComplex.MiExperimentoGrover01
```

## Idea de progresión, si quieres ir paso a paso

1. `TestBell01` -- ejecútalo tal cual, lee la salida, relaciónala con
   `03_BellTest.md`.
2. `ScratchQubitsNAudit01` -- juega con `Qubits.ket()`/`operatorOnQubit()`
   sobre registros de distinto tamaño.
3. `ScratchDensityMatrixAudit01` -- calcula la traza parcial de tus propios
   estados entrelazados, mira cuándo sale mixta y cuándo pura.
4. `ScratchTeleportationAudit01` -- teletransporta tus propios `psi`.
5. `ScratchDecoherenceAudit01` + `ScratchBlochSphereAudit01` -- combina
   ambos para VER (con una gráfica) cómo el ruido encoge un estado.
6. `ScratchDeutschJozsaAudit01` -> `ScratchBernsteinVaziraniAudit01` ->
   `ScratchGroverAudit01` -- los 3 algoritmos "de juguete", en el mismo
   orden en que se construyeron.
7. Los 4 `Noisy*` -- una vez cómodo con lo anterior, combina cualquier
   algoritmo con ruido y observa la degradación.

No hay obligación de seguir este orden -- es solo una progresión razonable
si no sabes por dónde empezar.
