package modelo;

/**
 * Controla la memoria RAM del sistema simulado.
 * Los métodos que modifican la memoria disponible son 'synchronized'
 * para garantizar exclusión mutua cuando varios procesos (hilos)
 * intentan asignar o liberar memoria al mismo tiempo.
 */
public class GestorMemoria {

    private final int memoriaTotal;   // en MB (1024 = 1GB)
    private int memoriaDisponible;

    public GestorMemoria(int memoriaTotalMB) {
        this.memoriaTotal = memoriaTotalMB;
        this.memoriaDisponible = memoriaTotalMB;
    }

    /**
     * Intenta asignar memoria a un proceso.
     * @param cantidad memoria requerida en MB
     * @return true si se pudo asignar, false si no hay espacio suficiente
     */
    public synchronized boolean asignar(int cantidad) {
        if (cantidad <= memoriaDisponible) {
            memoriaDisponible -= cantidad;
            return true;
        }
        return false;
    }

    /**
     * Libera memoria previamente asignada a un proceso.
     * @param cantidad memoria a liberar en MB
     */
    public synchronized void liberar(int cantidad) {
        memoriaDisponible += cantidad;
        // Por seguridad, nunca debería superar el total
        if (memoriaDisponible > memoriaTotal) {
            memoriaDisponible = memoriaTotal;
        }
    }

    public synchronized int getMemoriaDisponible() {
        return memoriaDisponible;
    }

    public int getMemoriaTotal() {
        return memoriaTotal;
    }

    public synchronized int getMemoriaUsada() {
        return memoriaTotal - memoriaDisponible;
    }

    public synchronized double getPorcentajeUso() {
        return (getMemoriaUsada() * 100.0) / memoriaTotal;
    }
}