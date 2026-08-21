/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorgestionprocesos;

import logica.Planificador;
import modelo.GestorMemoria;
import vista.VentanaPrincipal;

import javax.swing.*;

/**
 * Punto de entrada del simulador.
 * Inicializa el gestor de memoria y el planificador,
 * y lanza la interfaz gráfica.
 */
public class Main {

    public static void main(String[] args) {
        // RAM total del sistema: 1 GB = 1024 MB
        GestorMemoria gestorMemoria = new GestorMemoria(1024);
        Planificador planificador = new Planificador(gestorMemoria);

        // Las interfaces Swing deben crearse en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(planificador);
            ventana.setVisible(true);
        });
    }
}