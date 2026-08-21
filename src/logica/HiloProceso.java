package logica;

import modelo.Proceso;

/**
 * Hilo que simula la ejecución de un proceso durante el tiempo
 * indicado en 'duracion' (en segundos). Al finalizar, notifica
 * al Planificador para liberar la memoria ocupada.
 */
public class HiloProceso extends Thread {

    private final Proceso proceso;
    private final Planificador planificador;

    public HiloProceso(Proceso proceso, Planificador planificador) {
        this.proceso = proceso;
        this.planificador = planificador;
        // Le damos un nombre descriptivo al hilo, útil para depuración
        this.setName("Hilo-" + proceso.getNombre());
    }

    @Override
    public void run() {
        try {
            // Simula el tiempo de ejecución del proceso (CPU ocupada)
            Thread.sleep(proceso.getDuracion() * 1000L);
        } catch (InterruptedException e) {
            // Si el hilo es interrumpido, se restaura el estado de interrupción
            Thread.currentThread().interrupt();
        } finally {
            // Pase lo que pase, el proceso debe liberar su memoria al terminar
            planificador.finalizarProceso(proceso);
        }
    }
}