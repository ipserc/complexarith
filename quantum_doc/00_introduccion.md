# `com.ipserc.arith.quantum` -- guía de aprendizaje desde cero

Este directorio documenta, módulo a módulo, todo lo construido en el paquete
`com.ipserc.arith.quantum` del proyecto ComplexArith: 15 clases Java que
implementan (mediante simulación numérica clásica, no un ordenador cuántico
real) los conceptos y algoritmos fundamentales de la computación y la
información cuántica.

Está escrito asumiendo que partes de cero en mecánica cuántica -- no se da
por supuesto ningún conocimiento previo de la materia. Sí se aprovecha que
tienes una base universitaria sólida en tratamiento de señal (Ingeniería de
Telecomunicación), así que en varios puntos se establece el paralelismo con
ideas que ya conoces de ahí (vectores complejos, bases ortonormales,
proyecciones, operadores lineales) para anclar los conceptos nuevos.

## Cómo está organizado este directorio

| Fichero | Contenido |
|---|---|
| `00_introduccion.md` | Este documento: mapa general, cómo compilar/ejecutar, orden de lectura recomendado. |
| `01_conceptos_basicos.md` | Primer de mecánica cuántica para el código de este paquete: qubit, superposición, medida, entrelazamiento, matriz densidad, canal de ruido -- cada concepto ligado a CÓMO se representa en Java aquí. **Léelo antes que cualquier módulo concreto.** |
| `02_Qubits.md` | `Qubits.java` -- el vocabulario básico: kets, puertas, estados entrelazados canónicos. |
| `03_BellTest.md` | `BellTest.java` -- el experimento CHSH, violación de la desigualdad de Bell. |
| `04_DensityMatrix.md` | `DensityMatrix.java` -- formalismo de matriz densidad, traza parcial, entropía de von Neumann. |
| `05_TimeEvolution.md` | `TimeEvolution.java` -- evolución temporal unitaria, ecuación de Schrödinger. |
| `06_Teleportation.md` | `Teleportation.java` -- teletransportación cuántica. |
| `07_Decoherence.md` | `Decoherence.java` -- canales de ruido (operadores de Kraus), incluida la cadena de varios canales. |
| `08_BlochSphere.md` | `BlochSphere.java` -- visualización 3D de un qubit (esfera de Bloch). |
| `09_DeutschJozsa.md` | `DeutschJozsa.java` -- primer algoritmo cuántico con ventaja demostrable. |
| `10_BernsteinVazirani.md` | `BernsteinVazirani.java` -- recuperación de un secreto oculto con una sola consulta. |
| `11_Grover.md` | `Grover.java` -- búsqueda cuántica, ventaja cuadrática. |
| `12_NoisyTeleportation.md` | `NoisyTeleportation.java` -- teletransportación + ruido. |
| `13_NoisyDeutschJozsa.md` | `NoisyDeutschJozsa.java` -- Deutsch-Jozsa + ruido. |
| `14_NoisyBernsteinVazirani.md` | `NoisyBernsteinVazirani.java` -- Bernstein-Vazirani + ruido. |
| `15_NoisyGrover.md` | `NoisyGrover.java` -- Grover + ruido. |
| `16_experimentos_guiados.md` | Recetario paso a paso: cómo volver a ejecutar (y modificar) los experimentos ya hechos, con los comandos exactos. |
| `17_limites_y_extensiones.md` | Nota conceptual (sin clase Java asociada): qué partículas/grados de libertad puede representar este paquete hoy (espín en dimensión finita), cuáles no (grados de libertad continuos, estadística fermión/bosón de partículas idénticas), y cómo de extensible sería cada hueco en el futuro. |
| `18_QFT.md` | `QFT.java` -- la Transformada Cuántica de Fourier, construida por puertas elementales y verificada contra su fórmula literal. |

## Orden de lectura recomendado

El paquete se construyó en este orden, y cada clase se apoya en las
anteriores -- seguirlo en el mismo orden es la ruta de menor resistencia:

```
01 Conceptos básicos
  |
02 Qubits (vocabulario: kets, puertas, estados)
  |
  +-- 03 BellTest (primer experimento: usa Qubits)
  |
  +-- 04 DensityMatrix (formalismo alternativo: usa Qubits)
  |     |
  |     +-- 07 Decoherence (ruido: usa DensityMatrix)
  |           |
  |           +-- 08 BlochSphere (visualización: usa Decoherence)
  |
  +-- 05 TimeEvolution (evolución: usa Qubits; BellTest.correlation() lo reusa)
  |
  +-- 06 Teleportation (protocolo: usa Qubits)
        |
        +-- 12 NoisyTeleportation (usa Teleportation + Decoherence + DensityMatrix)

09 DeutschJozsa (algoritmo: usa Qubits)
  |
  +-- 10 BernsteinVazirani (reusa el circuito de DeutschJozsa)
  |
  +-- 13 NoisyDeutschJozsa (usa DeutschJozsa + Decoherence + DensityMatrix)
        |
        +-- 14 NoisyBernsteinVazirani (reusa NoisyDeutschJozsa)

11 Grover (algoritmo: usa Qubits, DeutschJozsa.hadamardChain())
  |
  +-- 15 NoisyGrover (usa Grover + Decoherence + DensityMatrix)

18 QFT (usa Qubits directamente: hadamard, phaseGate, controlledGate)
```

Si solo quieres "tocar" algo cuanto antes, el camino más corto y vistoso es:
`01` (conceptos) -> `02` (Qubits) -> `03` (BellTest) -- en media hora tienes
la violación de la desigualdad de Bell corriendo en tu máquina.

`17_limites_y_extensiones.md` queda fuera de este árbol: es un apéndice
conceptual, sin código propio, que se puede leer en cualquier momento tras
`01`.

## Cómo compilar y ejecutar estas clases en tu entorno

Todo lo de abajo asume PowerShell/Git Bash en Windows con el JDK de Eclipse
Adoptium instalado (es el que usa este proyecto). Ajusta la ruta del JDK si
la tuya es distinta.

### 1. Localizar el JDK y añadirlo al PATH de la sesión

```bash
export PATH="/c/Program Files/Eclipse Adoptium/jdk-21.0.12.8-hotspot/bin:$PATH"
javac -version   # debe imprimir algo como "javac 21.0.12"
```

(`javac`/`java` no están en el `PATH` por defecto en este entorno -- hay que
anteponer el `bin` del JDK cada vez que abras una sesión nueva.)

### 2. Compilar el paquete `quantum` y sus dependencias

El paquete `quantum` depende de `com.ipserc.arith.matrixcomplex` (álgebra de
matrices complejas) y `com.ipserc.arith.complex` (números complejos). Para
compilar SOLO lo necesario (evitando el resto del proyecto, que incluye
piezas con dependencias externas no siempre presentes):

```bash
mkdir -p /tmp/quantum_build
javac -d /tmp/quantum_build -sourcepath src \
  $(find src/com/ipserc/arith/quantum src/com/ipserc/arith/matrixcomplex \
        src/com/ipserc/arith/complex src/com/ipserc/arith/polynom -name "*.java")
```

Esto deja las clases compiladas (`.class`) en `/tmp/quantum_build`.

### 3. Ejecutar una batería de verificación ya escrita

Todas las baterías de verificación ("audits") de este paquete viven en
`src/TestComplex/Scratch*Audit01.java` (mismo directorio de siempre para los
tests exploratorios del proyecto). Para ejecutar, por ejemplo, la de
`BellTest`:

```bash
javac -d /tmp/quantum_build -cp /tmp/quantum_build src/TestComplex/TestBell01.java
java -cp /tmp/quantum_build TestComplex.TestBell01
```

Cada batería imprime una línea `OK`/`FAIL` por comprobación y termina con un
resumen `N/N OK`. Ver `16_experimentos_guiados.md` para el catálogo completo
de baterías disponibles y qué comprueba cada una.

### 4. Escribir tu propio experimento

Crea un fichero `.java` nuevo (por ejemplo en `src/TestComplex/`, o en
cualquier carpeta de scratch), con un `main()`, importa las clases que
necesites de `com.ipserc.arith.quantum` y `com.ipserc.arith.matrixcomplex`, y
compílalo/ejecútalo igual que arriba. Cada documento de módulo trae ejemplos
mínimos que puedes copiar y modificar como punto de partida.

## Convención de qubits usada en todo el paquete

Un detalle que aparece en casi todas las clases y conviene fijar desde el
principio: cuando un estado de varios qubits se construye con
`Qubits.ket(b1, b2, ..., bn)`, el **qubit 0 es el bit MÁS SIGNIFICATIVO**
(el primero del array), igual que se escribiría `|b1 b2 ... bn>` a mano. Esta
convención MSB-first se usa consistentemente en `operatorOnQubit()`,
`controlledGate()`, `partialTrace()`, los oráculos de `DeutschJozsa`/
`BernsteinVazirani`/`Grover`, etc. -- no hace falta memorizarla ahora, cada
módulo la recuerda donde hace falta.
