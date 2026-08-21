package logica;

import modelo.GestorMemoria;
import modelo.Proceso;
import modelo.Proceso.Estado;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Planificador (Scheduler) del simulador.
 * Administra la cola de procesos en espera y la lista de procesos
 * en ejecución, coordinando con el GestorMemoria para decidir
 * cuándo un proceso puede ejecutarse.
 */
public class Planificador {

    private final GestorMemoria gestorMemoria;

    // Cola FIFO de procesos esperando memoria disponible
    private final Queue<Proceso> colaEspera = new LinkedList<>();

    // Lista de procesos actualmente en ejecución (thread-safe para lectura desde la GUI)
    private final List<Proceso> procesosEnEjecucion = new CopyOnWriteArrayList<>();

    public Planificador(GestorMemoria gestorMemoria) {
        this.gestorMemoria = gestorMemoria;
    }

    /**
     * Intenta agregar un nuevo proceso al sistema.
     * Si hay memoria suficiente, se ejecuta de inmediato.
     * Si no, queda en la cola de espera.
     */
    public synchronized void agregarProceso(Proceso proceso) {
        if (gestorMemoria.asignar(proceso.getMemoriaRequerida())) {
            iniciarProceso(proceso);
        } else {
            proceso.setEstado(Estado.EN_ESPERA);
            colaEspera.add(proceso);
        }
    }

    /**
     * Marca el proceso como en ejecución, lo agrega a la lista visible
     * y lanza el hilo que simula su tiempo de ejecución.
     */
    private void iniciarProceso(Proceso proceso) {
        proceso.setEstado(Estado.EN_EJECUCION);
        procesosEnEjecucion.add(proceso);

        HiloProceso hilo = new HiloProceso(proceso, this);
        hilo.start();
    }

    /**
     * Llamado por HiloProceso cuando un proceso termina su ejecución.
     * Libera su memoria y revisa si algún proceso en cola ya puede iniciar.
     */
    public synchronized void finalizarProceso(Proceso proceso) {
        proceso.setEstado(Estado.TERMINADO);
        procesosEnEjecucion.remove(proceso);
        gestorMemoria.liberar(proceso.getMemoriaRequerida());

        revisarCola();
    }

    /**
     * Recorre la cola de espera en orden e intenta asignar memoria
     * a los procesos que ya puedan ejecutarse.
     */
    private void revisarCola() {
        Queue<Proceso> pendientes = new LinkedList<>();

        while (!colaEspera.isEmpty()) {
            Proceso p = colaEspera.poll();
            if (gestorMemoria.asignar(p.getMemoriaRequerida())) {
                iniciarProceso(p);
            } else {
                pendientes.add(p);
            }
        }
        colaEspera.addAll(pendientes);
    }

    public GestorMemoria getGestorMemoria() {
        return gestorMemoria;
    }

    /** Devuelve una copia segura de la cola de espera, para mostrar en la GUI. */
    public synchronized List<Proceso> getColaEspera() {
        return new LinkedList<>(colaEspera);
    }

    /** Devuelve la lista de procesos actualmente en ejecución. */
    public List<Proceso> getProcesosEnEjecucion() {
        return procesosEnEjecucion;
    }
}