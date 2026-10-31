import java.util.Locale;
import java.util.Scanner;

public class Matrices {

    /** Tamano maximo permitido (N x N) para no saturar la consola. */
    static final int MAX_DIMENSION = 10;

    /** Tolerancia para comparar numeros double (evita errores de redondeo). */
    static final double EPS = 1e-9;

    /** Denominador maximo que se prueba al mostrar un numero como fraccion. */
    static final int MAX_DENOMINADOR = 1000;

    static final String LINEA = "==============================================";
   static final String SUBLINEA = "----------------------------------------------";

    /** Un unico Scanner para toda la aplicacion (evita problemas de buffer). */
    static final Scanner SC = new Scanner(System.in);
    
    public static void main(String[] args) {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = leerEnteroEnRango("Elija una opcion (1-5): ", 1, 5);
            if (opcion == 1) {
                crearIdentidad();
            } else if (opcion == 2) {
                verificarIdentidad();
            } else if (opcion == 3) {
                transformarEnIdentidad();
            } else if (opcion == 4) {
                transformacionLineal();
            } else {
                salir = true;
            }
        }
        System.out.println();
        System.out.println(LINEA);
        System.out.println("  Programa finalizado. Hasta pronto!");
        System.out.println(LINEA);
    }

    static void mostrarMenu() {
        System.out.println();
        System.out.println(LINEA);
        System.out.println("          MENU PRINCIPAL - MATRIZ IDENTIDAD");
        System.out.println(LINEA);
        System.out.println("  1. Crear la matriz identidad I_n");
        System.out.println("  2. Verificar si una matriz es identidad");
        System.out.println("  3. Transformar una matriz en identidad");
        System.out.println("  4. Matriz de una transformacion lineal T");
        System.out.println("  5. Salir");
        System.out.println(LINEA);
    }
    
    //  OPCION 1: CREAR LA MATRIZ IDENTIDAD

    /**
     * Pide el tamano N (entre 1 y MAX_DIMENSION), construye la matriz
     * identidad y la muestra con sus datos algebraicos.
     */
    static void crearIdentidad() {
        titulo("CREAR MATRIZ IDENTIDAD");
        int n = leerDimension("Tamano N de la identidad (1-" + MAX_DIMENSION + "): ");

        // new double[n][n] empieza con todo en 0; solo falta poner 1 en la diagonal
        double[][] identidad = new double[n][n];
        for (int k = 0; k < n; k++) {
            identidad[k][k] = 1;
        }

        System.out.println();
        System.out.println(LINEA);
        System.out.println("  MATRIZ IDENTIDAD I_" + n);
        System.out.println(LINEA);
        System.out.println("Dimension : " + n + " x " + n + "  (matriz cuadrada)");
        System.out.println("Dominio   : R^" + n);
        System.out.println("Codominio : R^" + n);
        System.out.println("Definicion: a_ij = 1 si i = j, a_ij = 0 si i != j");
        System.out.println("Transformacion: T(x) = x  (deja cada vector igual)");
        System.out.println();
        imprimirMatriz(identidad);
        System.out.println();
        System.out.println("Las columnas son los vectores canonicos e_1, ..., e_" + n + ".");
    }
    
    //  OPCION 2: VERIFICAR SI UNA MATRIZ ES IDENTIDAD
    

    /**
     * Pide las dimensiones y los elementos de una matriz (decimales o
     * fracciones) y comprueba si cumple la definicion de identidad.
     */
    static void verificarIdentidad() {
        titulo("VERIFICAR MATRIZ IDENTIDAD");
        int m = leerDimension("Numero de filas (1-" + MAX_DIMENSION + "): ");
        int n = leerDimension("Numero de columnas (1-" + MAX_DIMENSION + "): ");

        System.out.println("Ingrese cada elemento (decimal o fraccion, ej. 1, 0, 4/4):");
        System.out.println(SUBLINEA);
        double[][] A = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = leerNumero("  a[" + (i + 1) + "][" + (j + 1) + "] = ");
            }
        }

        System.out.println();
        System.out.println(LINEA);
        System.out.println("  VERIFICACION");
        System.out.println(LINEA);
        System.out.println("Dimension: " + m + " x " + n);
        System.out.println();
        imprimirMatriz(A);
        System.out.println();

        // Una identidad debe ser cuadrada
        if (m != n) {
            System.out.println("Resultado: NO es identidad (la matriz no es cuadrada: "
                    + m + " x " + n + ").");
            return;
        }

        // Recorre toda la matriz y reporta las posiciones que incumplen la definicion
        int errores = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double esperado = (i == j) ? 1 : 0;
                if (Math.abs(A[i][j] - esperado) > EPS) {
                    if (errores < 5) {
                        System.out.println("  a[" + (i + 1) + "][" + (j + 1) + "] = "
                                + formatear(A[i][j]) + "  (deberia ser " + (int) esperado + ")");
                    }
                    errores++;
                }
            }
        }

        if (errores == 0) {
            System.out.println("Resultado: SI es la matriz identidad I_" + n + ".");
            System.out.println("Dominio: R^" + n + "   Codominio: R^" + n);
        } else {
            if (errores > 5) {
                System.out.println("  ... y " + (errores - 5) + " mas.");
            }
            System.out.println("Resultado: NO es identidad (" + errores + " elemento(s) incorrecto(s)).");
        }
    }
    
    //  OPCION 3: TRANSFORMAR UNA MATRIZ EN IDENTIDAD (GAUSS-JORDAN)
    /**
     * Pide una matriz cuadrada n x n (decimales o fracciones) y la lleva a la
     * identidad usando operaciones elementales de fila (metodo de Gauss-Jordan).
     *
     * Para cada columna c (de izquierda a derecha):
     *   1. Busca un pivote: la primera fila, desde la fila c hacia abajo, con un
     *      elemento distinto de 0 en esa columna. Si no existe, la matriz es
     *      singular y NO se puede transformar en la identidad.
     *   2. Si el pivote no esta en la fila c, intercambia filas.
     *   3. Divide la fila c entre el pivote para que quede un 1 en la diagonal.
     *   4. A cada otra fila le resta (su elemento de la columna c) * fila c,
     *      para dejar ceros en el resto de la columna.
     * Si se completan todas las columnas, la matriz queda convertida en I_n.
     */
    static void transformarEnIdentidad() {
        titulo("TRANSFORMAR UNA MATRIZ EN IDENTIDAD");
        System.out.println("Solo una matriz cuadrada (n x n) puede llegar a ser la identidad.");
        int n = leerDimension("Tamano n de la matriz (1-" + MAX_DIMENSION + "): ");

        double[][] A = new double[n][n];
        System.out.println("Ingrese cada elemento (decimal o fraccion):");
        System.out.println(SUBLINEA);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = leerNumero("  a[" + (i + 1) + "][" + (j + 1) + "] = ");
            }
        }

        boolean verPasos = leerSiNo("Mostrar cada paso? (s/n): ");

        System.out.println();
        System.out.println(LINEA);
        System.out.println("  PROCESO DE GAUSS-JORDAN");
        System.out.println(LINEA);
        System.out.println("Matriz original (" + n + " x " + n + "):");
        imprimirMatriz(A);

        int pasos = 0;
        boolean posible = true;

        for (int c = 0; c < n && posible; c++) {
            // 1) Buscar el pivote en la columna c (desde la fila c hacia abajo)
            int filaPivote = -1;
            for (int r = c; r < n; r++) {
                if (Math.abs(A[r][c]) > EPS) {
                    filaPivote = r;
                    break;
                }
            }
            if (filaPivote == -1) {
                System.out.println();
                System.out.println("En la columna " + (c + 1) + " no hay pivote (todo es 0 desde la fila "
                        + (c + 1) + " hacia abajo).");
                posible = false;
            } else {
                // 2) Intercambiar filas si el pivote no esta en la fila c
                if (filaPivote != c) {
                    double[] temp = A[c];
                    A[c] = A[filaPivote];
                    A[filaPivote] = temp;
                    pasos++;
                    registrarPaso(verPasos, pasos, "F" + (c + 1) + " <-> F" + (filaPivote + 1), A);
                }

                // 3) Convertir el pivote en 1 dividiendo toda la fila entre el pivote
                double pivote = A[c][c];
                if (Math.abs(pivote - 1) > EPS) {
                    for (int j = 0; j < n; j++) {
                        A[c][j] = A[c][j] / pivote;
                    }
                    A[c][c] = 1; // evita resultados como 0.9999999
                    pasos++;
                    registrarPaso(verPasos, pasos,
                            "F" + (c + 1) + " = F" + (c + 1) + " / " + conParentesis(pivote), A);
                }

                // 4) Hacer ceros en el resto de la columna c
                for (int r = 0; r < n; r++) {
                    if (r != c && Math.abs(A[r][c]) > EPS) {
                        double f = A[r][c];
                        for (int j = 0; j < n; j++) {
                            A[r][j] = A[r][j] - f * A[c][j];
                        }
                        A[r][c] = 0;
                        pasos++;
                        String signo = (f > 0) ? " - " : " + ";
                        double abs = Math.abs(f);
                        String coef = (Math.abs(abs - 1) < EPS) ? "" : conParentesis(abs) + "*";
                        registrarPaso(verPasos, pasos, "F" + (r + 1) + " = F" + (r + 1) + signo
                                + coef + "F" + (c + 1), A);
                    }
                }
            }
        }

        System.out.println();
        System.out.println(LINEA);
        System.out.println("  RESULTADO");
        System.out.println(LINEA);
        if (posible) {
            if (pasos == 0) {
                System.out.println("La matriz ya era la identidad I_" + n + "; no hizo falta ninguna operacion.");
            } else {
                System.out.println("Se necesitaron " + pasos + " operaciones elementales de fila.");
            }
            System.out.println("Matriz final (identidad I_" + n + "):");
            imprimirMatriz(A);
            System.out.println();
            System.out.println("Como se llego a la identidad, la matriz es invertible.");
        } else {
            System.out.println("NO se puede transformar en la identidad: la matriz es singular (no invertible).");
            System.out.println("Matriz en el ultimo paso:");
            imprimirMatriz(A);
        }
    }

    /** Limpia la matriz y, si el usuario quiere ver los pasos, imprime la operacion y la matriz. */
    static void registrarPaso(boolean ver, int numero, String operacion, double[][] A) {
        limpiar(A);
        if (ver) {
            System.out.println();
            System.out.println("Paso " + numero + ":  " + operacion);
            imprimirMatriz(A);
        }
    }

    /** Convierte en 0 los valores casi cero (por redondeo) para que no aparezcan como -0 o 1e-17. */
    static void limpiar(double[][] A) {
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                if (Math.abs(A[i][j]) < EPS) {
                    A[i][j] = 0;
                }
            }
        }
    }

    /** Devuelve el numero formateado; si es fraccion o negativo lo encierra entre parentesis. */
    static String conParentesis(double v) {
        String s = formatear(v);
        if (s.indexOf('/') >= 0 || s.startsWith("-")) {
            return "(" + s + ")";
        }
        return s;
    }
    
    //  OPCION 4: MATRIZ DE UNA TRANSFORMACION LINEAL T: R^n -> R^m

    /**
     * Pide los coeficientes de cada componente de T: R^n -> R^m y arma su
     * matriz de representacion respecto a las bases canonicas.
     * La columna j de la matriz es T(e_j), donde e_j es el j-esimo vector de
     * la base canonica del dominio (beta). Como la base del codominio (gamma)
     * tambien es la canonica, las coordenadas de T(e_j) son sus propias entradas.
     */
    static void transformacionLineal() {
        titulo("MATRIZ DE UNA TRANSFORMACION LINEAL");
        int n = leerDimension("Dimension del dominio n, T: R^n (1-" + MAX_DIMENSION + "): ");
        int m = leerDimension("Dimension del codominio m, T: R^m (1-" + MAX_DIMENSION + "): ");

        System.out.println();
        System.out.println("T(" + variablesTexto(n) + ") = (T_1, ..., T_" + m + ")");
        System.out.println("Ejemplo: T(x,y) = (x + y, x - y, 3y) tiene coeficientes");
        System.out.println("  T_1: 1 y 1    T_2: 1 y -1    T_3: 0 y 3");
        System.out.println("Ingrese el coeficiente de cada variable en cada componente T_i.");
        System.out.println("Puede escribir decimales (2.5) o fracciones (3/4). Use 0 si no aparece.");
        System.out.println(SUBLINEA);

        // A[i][j] = coeficiente de la variable j en la componente T_(i+1)
        double[][] A = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = leerNumero("  Coeficiente de " + nombreVar(j, n) + " en T_" + (i + 1) + ": ");
            }
        }

        mostrarResumenTransformacion(A, n, m);
        ofrecerEvaluacion(A, n);
    }

    /** Imprime la formula de T, las bases, la imagen de cada vector de la base y la matriz. */
    static void mostrarResumenTransformacion(double[][] A, int n, int m) {
        System.out.println();
        System.out.println(LINEA);
        System.out.println("  RESULTADO");
        System.out.println(LINEA);
        System.out.println("Transformacion : T: R^" + n + " -> R^" + m);
        System.out.println("Dominio        : R^" + n + "   beta  = " + baseCanonicaTexto(n));
        System.out.println("Codominio      : R^" + m + "   gamma = " + baseCanonicaTexto(m));
        System.out.println("Dimension de la matriz: " + m + " x " + n + "  (m filas, n columnas)");
        System.out.println();
        System.out.println("Formula:");
        System.out.println("  " + formulaT(A));
        System.out.println();
        System.out.println("Imagen de cada vector de la base beta:");
        for (int j = 0; j < n; j++) {
            System.out.println("  T" + vectorATexto(vectorCanonico(n, j)) + " = "
                    + vectorATexto(columna(A, j)) + "   -> columna " + (j + 1));
        }
        System.out.println("(gamma es la base canonica: las coordenadas (a, b, c, ...) de cada");
        System.out.println(" imagen coinciden con sus entradas, y forman una columna.)");
        System.out.println();
        System.out.println("Matriz de la transformacion (columna j = T(e_j)):");
        imprimirMatriz(A);
        if (m == n && esIdentidad(A)) {
            System.out.println();
            System.out.println("La matriz es la identidad I_" + n + ": T es la transformacion identidad, T(x) = x.");
        }
    }

    /** Pregunta si se quiere evaluar T(v) y, de ser asi, calcula (matriz) * v. */
    static void ofrecerEvaluacion(double[][] A, int n) {
        System.out.println();
        while (leerSiNo("Desea evaluar T en un vector de R^" + n + "? (s/n): ")) {
            double[] v = new double[n];
            for (int j = 0; j < n; j++) {
                v[j] = leerNumero("  " + nombreVar(j, n) + " = ");
            }
            double[] w = multiplicar(A, v);
            System.out.println("  T" + vectorATexto(v) + " = " + vectorATexto(w));
            System.out.println();
        }
    }

    /** Construye el texto "T(x,y) = (x + y, x - y, 3y)" a partir de la matriz. */
    static String formulaT(double[][] A) {
        int m = A.length;
        int n = A[0].length;
        StringBuilder sb = new StringBuilder("T(" + variablesTexto(n) + ") = (");
        for (int i = 0; i < m; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(componenteATexto(A[i]));
        }
        return sb.append(")").toString();
    }

    /** Convierte una fila de coeficientes en una expresion como "2x - (3/4)y". */
    static String componenteATexto(double[] fila) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < fila.length; j++) {
            double c = fila[j];
            if (Math.abs(c) < EPS) {
                continue; // omite terminos con coeficiente 0
            }
            double abs = Math.abs(c);
            String coef = (Math.abs(abs - 1) < EPS) ? "" : conParentesis(abs); // 1x se escribe x
            String termino = coef + nombreVar(j, fila.length);
            if (sb.length() == 0) {
                sb.append(c < 0 ? "-" : "").append(termino);
            } else {
                sb.append(c < 0 ? " - " : " + ").append(termino);
            }
        }
        return sb.length() == 0 ? "0" : sb.toString();
    }

    /** Nombre de la variable j (desde 0): x, y, z si hay hasta 3 variables; si no, x1, x2, ... */
    static String nombreVar(int j, int n) {
        if (n <= 3) {
            return "xyz".substring(j, j + 1);
        }
        return "x" + (j + 1);
    }

    /** Lista de variables separadas por coma: "x,y" o "x1,x2,x3,x4". */
    static String variablesTexto(int n) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < n; j++) {
            if (j > 0) {
                sb.append(",");
            }
            sb.append(nombreVar(j, n));
        }
        return sb.toString();
    }

    /** Vector canonico e_j (desde 0) de R^k: un 1 en la posicion j y ceros en el resto. */
    static double[] vectorCanonico(int k, int j) {
        double[] e = new double[k];
        e[j] = 1;
        return e;
    }

    /** Texto de la base canonica de R^k: {(1, 0), (0, 1)}; si k > 4 se abrevia {e_1, ..., e_k}. */
    static String baseCanonicaTexto(int k) {
        if (k > 4) {
            return "{e_1, ..., e_" + k + "}";
        }
        StringBuilder sb = new StringBuilder("{");
        for (int j = 0; j < k; j++) {
            if (j > 0) {
                sb.append(", ");
            }
            sb.append(vectorATexto(vectorCanonico(k, j)));
        }
        return sb.append("}").toString();
    }

    /** Producto matriz por vector: devuelve A * v. */
    static double[] multiplicar(double[][] A, double[] v) {
        double[] w = new double[A.length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < v.length; j++) {
                w[i] += A[i][j] * v[j];
            }
        }
        return w;
    }

    /** Extrae la columna j de A. */
    static double[] columna(double[][] A, int j) {
        double[] c = new double[A.length];
        for (int i = 0; i < A.length; i++) {
            c[i] = A[i][j];
        }
        return c;
    }

    /** Texto "(a, b, c)" para un vector. */
    static String vectorATexto(double[] v) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < v.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(formatear(v[i]));
        }
        return sb.append(")").toString();
    }

    /** Indica si una matriz es la identidad (cuadrada, 1 en la diagonal y 0 fuera de ella). */
    static boolean esIdentidad(double[][] A) {
        if (A.length != A[0].length) {
            return false;
        }
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A.length; j++) {
                double esperado = (i == j) ? 1 : 0;
                if (Math.abs(A[i][j] - esperado) > EPS) {
                    return false;
                }
            }
        }
        return true;
    }
    
    //  LECTURA SEGURA Y VALIDACION

    /** Lee una linea completa. Si la entrada se cierra, termina el programa sin error. */
    static String leerLinea(String prompt) {
        System.out.print(prompt);
        if (!SC.hasNextLine()) {
            System.out.println();
            System.out.println("Entrada finalizada. Saliendo del programa.");
            System.exit(0);
        }
        return SC.nextLine();
    }

    /**
     * Lee un entero entre min y max (ambos incluidos).
     * Repite hasta recibir un valor valido y distingue "no es numero"
     * de "fuera de rango".
     */
    static int leerEnteroEnRango(String prompt, int min, int max) {
        while (true) {
            String t = leerLinea(prompt).trim();
            // Solo digitos: descarta letras, simbolos, negativos, decimales y vacio
            if (!t.matches("\\d+")) {
                System.out.println("  [!] Entrada invalida. Escriba un numero entero positivo (sin letras ni simbolos).");
                continue;
            }
            int v;
            try {
                v = Integer.parseInt(t);
            } catch (NumberFormatException e) { // numero demasiado grande para int
                v = Integer.MAX_VALUE;
            }
            if (v < min || v > max) {
                System.out.println("  [!] El valor debe estar entre " + min + " y " + max + ".");
                continue;
            }
            return v;
        }
    }

    /** Lee una dimension: entero entre 1 y MAX_DIMENSION. */
    static int leerDimension(String prompt) {
        return leerEnteroEnRango(prompt, 1, MAX_DIMENSION);
    }

    /** Lee un numero real (decimal o fraccion) repitiendo hasta que sea valido. */
    static double leerNumero(String prompt) {
        while (true) {
            String t = leerLinea(prompt);
            try {
                return parsearNumero(t);
            } catch (NumberFormatException e) {
                System.out.println("  [!] " + e.getMessage());
            }
        }
    }

    /** Lee una respuesta si/no (s, si, n, no) repitiendo hasta que sea valida. */
    static boolean leerSiNo(String prompt) {
        while (true) {
            String t = leerLinea(prompt).trim().toLowerCase();
            if (t.equals("s") || t.equals("si")) {
                return true;
            }
            if (t.equals("n") || t.equals("no")) {
                return false;
            }
            System.out.println("  [!] Responda con 's' o 'n'.");
        }
    }

    // ===============================================================
    //  PARSEO DE DECIMALES Y FRACCIONES
    // ===============================================================

    /**
     * Convierte un String escrito por el usuario en un double.
     *
     * Formatos aceptados:
     *   - Enteros y decimales: "5", "-2", "2.5", "-0.75", ".5", "1,5" (la coma cuenta como punto)
     *   - Fracciones:          "3/4", "-1/2", "5/2", "4/4" (tambien con espacios: "3 / 4")
     *
     * Funcionamiento:
     *   1. Quita los espacios y rechaza la cadena vacia.
     *   2. Si contiene '/', la divide en numerador y denominador, convierte cada
     *      parte como decimal y devuelve numerador / denominador (rechaza
     *      denominador 0 y mas de una barra).
     *   3. Si no, la convierte directamente como decimal.
     * Se valida con una expresion regular antes de Double.parseDouble para que
     * no se acepten textos raros como "NaN", "Infinity" o "1e5".
     *
     * @throws NumberFormatException con un mensaje claro si la entrada no es valida
     */
    static double parsearNumero(String texto) {
        if (texto == null || texto.trim().length() == 0) {
            throw new NumberFormatException("La entrada esta vacia. Escriba un numero (ej. 1, 0.5 o 3/4).");
        }
        String s = texto.replace(" ", "");

        int barra = s.indexOf('/');
        if (barra >= 0) {
            if (barra != s.lastIndexOf('/')) {
                throw new NumberFormatException("Fraccion invalida: solo se permite una barra '/'.");
            }
            double num = parsearDecimal(s.substring(0, barra), texto);
            double den = parsearDecimal(s.substring(barra + 1), texto);
            if (Math.abs(den) < EPS) {
                throw new NumberFormatException("El denominador no puede ser 0.");
            }
            return num / den;
        }
        return parsearDecimal(s, texto);
    }

    /** Convierte un entero/decimal simple (sin barra) a double validando su formato. */
    private static double parsearDecimal(String s, String original) {
        s = s.replace(',', '.');
        if (!s.matches("[+-]?(\\d+\\.?\\d*|\\.\\d+)")) {
            throw new NumberFormatException("\"" + original.trim()
                    + "\" no es valido. Use decimales (2.5, -0.75) o fracciones (3/4, -1/2).");
        }
        return Double.parseDouble(s);
    }

    //  FORMATO DE SALIDA (FRACCIONES / DECIMALES)

    /**
     * Devuelve un numero como entero, fraccion simplificada o decimal con 2 decimales.
     *   0.75 -> "3/4"    2.0 -> "2"    -0.5 -> "-1/2"    3.14159 -> "3.14"
     * Prueba denominadores 2, 3, 4, ... hasta MAX_DENOMINADOR; el primero que
     * reproduce el valor es el menor, asi que la fraccion ya sale simplificada.
     */
    static String formatear(double v) {
        if (Math.abs(v) < EPS) {
            return "0";
        }
        double r = Math.rint(v);
        if (Math.abs(v - r) < EPS) {
            return String.valueOf((long) r);
        }
        for (int q = 2; q <= MAX_DENOMINADOR; q++) {
            long p = Math.round(v * q);
            if (Math.abs(v - (double) p / q) < EPS) {
                return p + "/" + q;
            }
        }
        return String.format(Locale.US, "%.2f", v);
    }

    /** Imprime una matriz con columnas alineadas, entre barras verticales. */
    static void imprimirMatriz(double[][] A) {
        int m = A.length;
        int n = A[0].length;
        String[][] s = new String[m][n];
        int ancho = 1;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                s[i][j] = formatear(A[i][j]);
                ancho = Math.max(ancho, s[i][j].length());
            }
        }
        for (int i = 0; i < m; i++) {
            StringBuilder sb = new StringBuilder("  | ");
            for (int j = 0; j < n; j++) {
                sb.append(String.format("%" + ancho + "s", s[i][j]));
                if (j < n - 1) {
                    sb.append("  ");
                }
            }
            sb.append(" |");
            System.out.println(sb);
        }
    }

    /** Imprime un encabezado enmarcado. */
    static void titulo(String texto) {
        System.out.println();
        System.out.println(LINEA);
        System.out.println("  " + texto);
        System.out.println(LINEA);
    }
}

