package es.upm.dit.aled.lab7;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Servlet implementation class Baja
 */
@WebServlet("/baja")
public class Baja extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/*
	 * La única instancia de PacienteRepository se crea y se almacena en el método init() del Servlet Alta.java
	 */
    @Override
    public void init() {
    	if(getServletContext().getAttribute("repo") == null )
    		getServletContext().setAttribute("repo", new PacienteRepository(getServletContext()));
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Baja() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    //Leemos el contenido del fichero
		InputStream file = getServletContext().getResourceAsStream("/baja.html");
			InputStreamReader reader1 = new InputStreamReader(file);
			BufferedReader html = new BufferedReader(reader1);

			String pagina = "", linea;
			while((linea = html.readLine()) != null)
				pagina += linea;
			
			PacienteRepository repo = (PacienteRepository) getServletContext().getAttribute("repo");
			String options = "";
			for(Paciente p : repo.getPacientes()) {
				options += "<option value='"+p.getDni()+"'>"+p.getNombre()+" "+p.getApellido()+"</option>\n";
			}
			// Actualizamos página:
			pagina = pagina.replace("<option></option>", options);
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");
			out.println(pagina);
			out.close();
		}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Recuperamos el parámetro seleccionado por el usuario (DNI en este caso)
		String dni = request.getParameter("paciente");
		
		//Leemos el contenido del fichero
				InputStream file = getServletContext().getResourceAsStream("/baja.html");
					InputStreamReader reader1 = new InputStreamReader(file);
					BufferedReader html = new BufferedReader(reader1);
					String pagina = "", linea;
					while((linea = html.readLine()) != null)
						pagina += linea;
					
			PacienteRepository repo = (PacienteRepository) getServletContext().getAttribute("repo");
			String mensaje = "";
			// Comprobamos si el DNI del paciente está almacenado, y en caso de que lo esté lo borramos
			if(repo.findByDni(dni) != null) {
				repo.removePaciente(dni);
				mensaje += "<h2 style='color:green;'>El paciente con el DNI " + dni + " ha sido eliminado correctamente.</h2>";
			} else {
				mensaje += "<h2 style='color:red;'>Error. El paciente no se ha podido eliminar.</h2>";
			}
			pagina = pagina.replace("<h2></h2>", mensaje);
			
			// Falta por rellenar la lista de pacientes actualizada (la cual desaparece si no le decimos nada)
			String options = "";
			for(Paciente p : repo.getPacientes()) {
				options += "<option value='"+p.getDni()+"'>"+p.getNombre()+" "+p.getApellido()+"</option>\n";
			}
			pagina = pagina.replace("<option></option>", options);
					PrintWriter out = response.getWriter();
					response.setContentType("text/html");
					out.println(pagina);
					out.close();
	}

}
