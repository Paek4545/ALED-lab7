package es.upm.dit.aled.lab7;

import jakarta.servlet.ServletContext;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteRepository {

    private final List<Paciente> pacientes = new ArrayList<>();
    private final File dataFile;

    public PacienteRepository(ServletContext context) {
        // Ruta relativa dentro del WAR (funciona en Tomcat local y en Azure)
        String path = context.getRealPath("/WEB-INF/data/pacientes.txt");
        this.dataFile = new File(path);

        // Crear carpeta si no existe
        File parent = dataFile.getParentFile();
        if (!parent.exists()) parent.mkdirs();

        // Cargar datos existentes
        load();
    }

    // ---------------------------
    // Lectura de datos
    // ---------------------------
    private void load() {
        if (!dataFile.exists()) {
            return; // No hay fichero todavía
        }

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Esperamos líneas del tipo: nombre;apellido;dni
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    Paciente p = new Paciente(parts[0], parts[1], parts[2]);
                    pacientes.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------
    // Escritura de datos
    // ---------------------------
    private void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(dataFile, false))) {
            for (Paciente p : pacientes) {
                pw.println(p.getNombre() + ";" +
                           p.getApellido() + ";" +
                           p.getDni());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------------------
    // Métodos públicos de acceso
    // ---------------------------
    // Obtenemos la lista de pacientes a partir de su método getPacientes()
    public List<Paciente> getPacientes() {
        return new ArrayList<>(pacientes);
    }
    
    // Para añadir pacientes, se usa el método público addPaciente(Paciente p)
    public void addPaciente(Paciente p) {
        pacientes.add(p);
        save();
    }

    // Para eliminar pacientes, se usa el método público removePaciente(Paciente p)
    public void removePaciente(String dni) {
        pacientes.removeIf(p -> p.getDni().equals(dni));
        save();
    }

    public Paciente findByDni(String dni) {
        return pacientes.stream()
                .filter(p -> p.getDni().equals(dni))
                .findFirst().orElse(null);
    }
    public boolean updatePaciente(String dniOriginal, Paciente pacienteActualizado) {
        for (int i = 0; i < pacientes.size(); i++) {
            Paciente p = pacientes.get(i);
            
            // 1. Buscamos el paciente por el DNI original
            if (p.getDni().equals(dniOriginal)) {
                
                // 2. Reemplazamos el paciente en esa posición con el objeto actualizado
                pacientes.set(i, pacienteActualizado);
                
                // 3. Guardamos la lista completa en el fichero (sobrescribe todo el archivo)
                save(); 
                
                return true; // Éxito en la actualización
            }
        }
        // El paciente con el dniOriginal no fue encontrado
        return false;
    }
}
