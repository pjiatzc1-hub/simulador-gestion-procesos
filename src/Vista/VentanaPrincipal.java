package vista;

import logica.Planificador;
import modelo.GestorMemoria;
import modelo.Proceso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Random;

/**
 * Ventana principal del simulador.
 * Permite agregar procesos, y muestra en tiempo real:
 * - Procesos en ejecución
 * - Procesos en cola de espera
 * - Estado de la memoria RAM (barra de progreso)
 */
public class VentanaPrincipal extends JFrame {

    private final Planificador planificador;
    private final GestorMemoria gestorMemoria;

    // Componentes del formulario
    private JTextField campoNombre;
    private JSpinner campoMemoria;
    private JSpinner campoDuracion;

    // Tablas
    private DefaultTableModel modeloEjecucion;
    private DefaultTableModel modeloEspera;

    // Barra de memoria
    private JProgressBar barraMemoria;
    private JLabel etiquetaMemoria;

    private final Random random = new Random();

    public VentanaPrincipal(Planificador planificador) {
        this.planificador = planificador;
        this.gestorMemoria = planificador.getGestorMemoria();

        configurarVentana();
        construirInterfaz();
        iniciarTimerActualizacion();
    }

    private void configurarVentana() {
        setTitle("Simulador de Gestión de Procesos en Memoria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // centrar en pantalla
        setLayout(new BorderLayout(10, 10));
    }

    private void construirInterfaz() {
        add(construirPanelFormulario(), BorderLayout.NORTH);
        add(construirPanelTablas(), BorderLayout.CENTER);
        add(construirPanelMemoria(), BorderLayout.SOUTH);
    }

    // ---------- Panel superior: formulario para agregar procesos ----------
    private JPanel construirPanelFormulario() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Nuevo proceso"));

        panel.add(new JLabel("Nombre:"));
        campoNombre = new JTextField(10);
        panel.add(campoNombre);

        panel.add(new JLabel("Memoria (MB):"));
        campoMemoria = new JSpinner(new SpinnerNumberModel(100, 1, 1024, 10));
        panel.add(campoMemoria);

        panel.add(new JLabel("Duración (s):"));
        campoDuracion = new JSpinner(new SpinnerNumberModel(5, 1, 120, 1));
        panel.add(campoDuracion);

        JButton botonAgregar = new JButton("Agregar proceso");
        botonAgregar.addActionListener(e -> agregarProcesoDesdeFormulario());
        panel.add(botonAgregar);

        JButton botonAleatorio = new JButton("Agregar aleatorio");
        botonAleatorio.addActionListener(e -> agregarProcesoAleatorio());
        panel.add(botonAleatorio);

        JButton botonVarios = new JButton("Agregar 5 aleatorios");
        botonVarios.addActionListener(e -> {
            for (int i = 0; i < 5; i++) {
                agregarProcesoAleatorio();
            }
        });
        panel.add(botonVarios);

        return panel;
    }

    // ---------- Panel central: tablas de ejecución y espera ----------
    private JPanel construirPanelTablas() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));

        String[] columnas = {"PID", "Nombre", "Memoria (MB)", "Duración (s)", "Estado"};

        modeloEjecucion = crearModeloNoEditable(columnas);
        JTable tablaEjecucion = new JTable(modeloEjecucion);
        JPanel panelEjecucion = new JPanel(new BorderLayout());
        panelEjecucion.setBorder(BorderFactory.createTitledBorder("Procesos en ejecución"));
        panelEjecucion.add(new JScrollPane(tablaEjecucion), BorderLayout.CENTER);

        modeloEspera = crearModeloNoEditable(columnas);
        JTable tablaEspera = new JTable(modeloEspera);
        JPanel panelEspera = new JPanel(new BorderLayout());
        panelEspera.setBorder(BorderFactory.createTitledBorder("Cola de espera"));
        panelEspera.add(new JScrollPane(tablaEspera), BorderLayout.CENTER);

        panel.add(panelEjecucion);
        panel.add(panelEspera);
        return panel;
    }

    private DefaultTableModel crearModeloNoEditable(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // ---------- Panel inferior: estado de memoria ----------
    private JPanel construirPanelMemoria() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Estado de la memoria RAM"));

        barraMemoria = new JProgressBar(0, gestorMemoria.getMemoriaTotal());
        barraMemoria.setStringPainted(true);

        etiquetaMemoria = new JLabel("", SwingConstants.CENTER);

        panel.add(barraMemoria, BorderLayout.CENTER);
        panel.add(etiquetaMemoria, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- Acciones ----------
    private void agregarProcesoDesdeFormulario() {
        String nombre = campoNombre.getText();
        int memoria = (int) campoMemoria.getValue();
        int duracion = (int) campoDuracion.getValue();

        planificador.agregarProceso(new Proceso(nombre, memoria, duracion));
        campoNombre.setText("");
    }

    private void agregarProcesoAleatorio() {
        int memoria = 50 + random.nextInt(450);   // entre 50 y 500 MB
        int duracion = 3 + random.nextInt(10);    // entre 3 y 12 segundos
        planificador.agregarProceso(new Proceso(null, memoria, duracion));
    }

    // ---------- Actualización periódica de la interfaz ----------
    private void iniciarTimerActualizacion() {
        Timer timer = new Timer(500, e -> actualizarInterfaz());
        timer.start();
    }

    private void actualizarInterfaz() {
        actualizarTabla(modeloEjecucion, planificador.getProcesosEnEjecucion());
        actualizarTabla(modeloEspera, planificador.getColaEspera());

        int usada = gestorMemoria.getMemoriaUsada();
        int total = gestorMemoria.getMemoriaTotal();
        barraMemoria.setValue(usada);
        etiquetaMemoria.setText(String.format("%d MB usados de %d MB (%.1f%%) — %d MB disponibles",
                usada, total, gestorMemoria.getPorcentajeUso(), gestorMemoria.getMemoriaDisponible()));
    }

    private void actualizarTabla(DefaultTableModel modelo, Iterable<Proceso> procesos) {
        modelo.setRowCount(0); // limpiar filas actuales
        for (Proceso p : procesos) {
            modelo.addRow(new Object[]{
                    p.getPid(), p.getNombre(), p.getMemoriaRequerida(),
                    p.getDuracion(), p.getEstado()
            });
        }
    }
}