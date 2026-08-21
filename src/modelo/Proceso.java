package modelo;

/**
 * Representa un proceso dentro del simulador.
 * Contiene los datos básicos (PID, nombre, memoria requerida, duración)
 * y su estado actual dentro del sistema.
 */
public class Proceso {

    // Contador estático para generar PIDs únicos automáticamente
    private static int contadorPID = 1;

    public enum Estado {
        EN_ESPERA,
        EN_EJECUCION,
        TERMINADO
    }

    private final int pid;
    private final String nombre;
    private final int memoriaRequerida; // en MB
    private final int duracion;         // en segundos
    private Estado estado;

    public Proceso(String nombre, int memoriaRequerida, int duracion) {
        this.pid = contadorPID++;
        // Si no se especifica nombre, se genera uno dinámicamente
        this.nombre = (nombre == null || nombre.trim().isEmpty())
                ? "Proceso-" + this.pid
                : nombre;
        this.memoriaRequerida = memoriaRequerida;
        this.duracion = duracion;
        this.estado = Estado.EN_ESPERA;
    }

    public int getPid() {
        return pid;
    }

    public String getNombre() {
        return nombre;
    }

    public int getMemoriaRequerida() {
        return memoriaRequerida;
    }

    public int getDuracion() {
        return duracion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return String.format("[PID:%d] %s | %dMB | %ds | %s",
                pid, nombre, memoriaRequerida, duracion, estado);
    }
}