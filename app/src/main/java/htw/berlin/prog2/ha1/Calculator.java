package htw.berlin.prog2.ha1;

/**
 * Eine Klasse, die das Verhalten des Online Taschenrechners imitiert, welcher auf
 * https://www.online-calculator.com/ aufgerufen werden kann (ohne die Memory-Funktionen)
 * und dessen Bildschirm bis zu zehn Ziffern plus einem Dezimaltrennzeichen darstellen kann.
 * Enthält mit Absicht noch diverse Bugs oder unvollständige Funktionen.
 */
public class Calculator {

    private String screen = "0"; // aktuelle Zahl auf Screen

    private double latestValue; // Wert bei der letzten Berechnung

    private String latestOperation = "";

    private boolean clearPressedOnce = false; // Default: noch nicht gedrückt

    private double lastOperand = 0.0; // lastValue + Operand = neue lastValue

    private boolean repeatEquals = false; // Default: "=" noch nicht gedrückt


    /**
     * @return den aktuellen Bildschirminhalt als String
     */
    public String readScreen() {
        return screen;
    }

    /**
     * Empfängt den Wert einer gedrückten Zifferntaste. Da man nur eine Taste auf einmal
     * drücken kann muss der Wert positiv und einstellig sein und zwischen 0 und 9 liegen.
     * Führt in jedem Fall dazu, dass die gerade gedrückte Ziffer auf dem Bildschirm angezeigt
     * oder rechts an die zuvor gedrückte Ziffer angehängt angezeigt wird.
     * @param digit Die Ziffer, deren Taste gedrückt wurde
     */
    public void pressDigitKey(int digit) {
        if(digit > 9 || digit < 0) throw new IllegalArgumentException(); // gültige Zahl eingeben

        if(screen.equals("0") || latestValue == Double.parseDouble(screen)) screen = ""; // Screen wird leer gesetzt für eine neue Zahl

        screen = screen + digit;
    }

    /**
     * Empfängt den Befehl der C- bzw. CE-Taste (Clear bzw. Clear Entry).
     * Einmaliges Drücken der Taste löscht die zuvor eingegebenen Ziffern auf dem Bildschirm
     * so dass "0" angezeigt wird, jedoch ohne zuvor zwischengespeicherte Werte zu löschen.
     * Wird daraufhin noch einmal die Taste gedrückt, dann werden auch zwischengespeicherte
     * Werte sowie der aktuelle Operationsmodus zurückgesetzt, so dass der Rechner wieder
     * im Ursprungszustand ist.
     */
    public void pressClearKey() {

        if(!clearPressedOnce) { // Wenn C noch nicht gedrückt
            screen = "0";
            clearPressedOnce = true;
        } else { // Wenn C bereits einmal gedrückt und nochmal gedrückt wird
            screen = "0"; // Screen auf 0 gesetzt
            latestOperation = ""; // Operation wird gelöscht
            latestValue = 0.0; // Letzter berechneter Wert wird gelöscht
            clearPressedOnce = false; // C auf default gesetzt
        }

    }

    /**
     * Empfängt den Wert einer gedrückten binären Operationstaste, also eine der vier Operationen
     * Addition, Substraktion, Division, oder Multiplikation, welche zwei Operanden benötigen.
     * Beim ersten Drücken der Taste wird der Bildschirminhalt nicht verändert, sondern nur der
     * Rechner in den passenden Operationsmodus versetzt.
     * Beim zweiten Drücken nach Eingabe einer weiteren Zahl wird direkt das aktuelle Zwischenergebnis
     * auf dem Bildschirm angezeigt. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * @param operation "+" für Addition, "-" für Substraktion, "x" für Multiplikation, "/" für Division
     */
    public void pressBinaryOperationKey(String operation)  {
        latestValue = Double.parseDouble(screen); // Wert gespeichert für die nächste Berechnung
        latestOperation = operation; // Operation-Eingabe
    }

    /**
     * Empfängt den Wert einer gedrückten unären Operationstaste, also eine der drei Operationen
     * Quadratwurzel, Prozent, Inversion, welche nur einen Operanden benötigen.
     * Beim Drücken der Taste wird direkt die Operation auf den aktuellen Zahlenwert angewendet und
     * der Bildschirminhalt mit dem Ergebnis aktualisiert.
     * @param operation "√" für Quadratwurzel, "%" für Prozent, "1/x" für Inversion
     */
    public void pressUnaryOperationKey(String operation) {
        latestValue = Double.parseDouble(screen); // Wert von Screen gespeichert
        latestOperation = operation; // Operation
        var result = switch(operation) { // Mit "var" erkennt Compiler intern sofort Datentyp (-> double)
            case "√" -> Math.sqrt(Double.parseDouble(screen)); // "Double.parseDouble(screen)" ersetzbar durch "latestValue"
            case "%" -> Double.parseDouble(screen) / 100;
            case "1/x" -> 1 / Double.parseDouble(screen);
            default -> throw new IllegalArgumentException();
        };
        screen = Double.toString(result);
        if(screen.equals("NaN")) screen = "Error"; // "NaN" = Not a Number -> kein gültiger Zahlenwert
        if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10); // rundet

    }

    /**
     * Empfängt den Befehl der gedrückten Dezimaltrennzeichentaste, im Englischen üblicherweise "."
     * Fügt beim ersten Mal Drücken dem aktuellen Bildschirminhalt das Trennzeichen auf der rechten
     * Seite hinzu und aktualisiert den Bildschirm. Daraufhin eingegebene Zahlen werden rechts vom
     * Trennzeichen angegeben und daher als Dezimalziffern interpretiert.
     * Beim zweimaligem Drücken, oder wenn bereits ein Trennzeichen angezeigt wird, passiert nichts.
     */
    public void pressDotKey() {
        if(!screen.contains(".")) screen = screen + ".";
    }

    /**
     * Empfängt den Befehl der gedrückten Vorzeichenumkehrstaste ("+/-").
     * Zeigt der Bildschirm einen positiven Wert an, so wird ein "-" links angehängt, der Bildschirm
     * aktualisiert und die Inhalt fortan als negativ interpretiert.
     * Zeigt der Bildschirm bereits einen negativen Wert mit führendem Minus an, dann wird dieses
     * entfernt und der Inhalt fortan als positiv interpretiert.
     */
    public void pressNegativeKey() {
        screen = screen.startsWith("-") ? screen.substring(1) : "-" + screen; // wenn ja: schneidet das 0. Zeichen ab "-"
    }

    /**
     * Empfängt den Befehl der gedrückten "="-Taste.
     * Wurde zuvor keine Operationstaste gedrückt, passiert nichts.
     * Wurde zuvor eine binäre Operationstaste gedrückt und zwei Operanden eingegeben, wird das
     * Ergebnis der Operation angezeigt. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * Wird die Taste weitere Male gedrückt (ohne andere Tasten dazwischen), so wird die letzte
     * Operation (ggf. inklusive letztem Operand) erneut auf den aktuellen Bildschirminhalt angewandt
     * und das Ergebnis direkt angezeigt.
     */
    public void pressEqualsKey() {
        double current = repeatEquals ? lastOperand : Double.parseDouble(screen); // Wurde schon "=" einmal gedrückt?

        var result = switch(latestOperation) {
            case "+" -> latestValue + current; // letztes Ergebnis + Operand
            case "-" -> latestValue - current;
            case "x" -> latestValue * current;
            case "/" -> latestValue / current;
            default -> throw new IllegalArgumentException();
        };

        latestValue = result; // letztes Ergebnis gespeichert
        lastOperand = current; // Wichtig: letzter Operand gespeichert
        repeatEquals = true; // = wurde bereits gedrückt

        screen = Double.toString(result);
        if(screen.equals("Infinity")) screen = "Error"; // Infinity = Absturz: Eine Zahl zu groß oder Division durch 0
        if(screen.endsWith(".0")) screen = screen.substring(0,screen.length()-2); // Keine Nachkommastellen nötig bei ".0"
        if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10); // Zahllänge eingrenzen
    }
}
