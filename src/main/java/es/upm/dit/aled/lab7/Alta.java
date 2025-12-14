package es.upm.dit.aled.lab7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;



/**
 * Servlet implementation class Alta
 */
@WebServlet("/alta")
public class Alta extends HttpServlet {
	/*
	 * La única instancia de PacienteRepository se crea y se almacena en el método init() del Servlet Alta.java
	 */
    @Override
    public void init() {
    	if(getServletContext().getAttribute("repo") == null )
    		getServletContext().setAttribute("repo", new PacienteRepository(getServletContext()));
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    InputStream file = getServletContext().getResourceAsStream("/alta.html");
		InputStreamReader reader1 = new InputStreamReader(file);
		BufferedReader html = new BufferedReader(reader1);

		String pagina = "", linea;
		while((linea = html.readLine()) != null)
			pagina += linea;

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.println(pagina);
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String nombre = request.getParameter("nombre");
		String apellidos = request.getParameter("apellidos");
		String dni = request.getParameter("dni");
		
		//Recupera un stream al fichero
		InputStream file = getServletContext().getResourceAsStream("/alta.html");
		InputStreamReader reader1 = new InputStreamReader(file);
		BufferedReader html = new BufferedReader(reader1);
		//Guarda el contenido del fichero en un String
		String pagina = "", linea;
		while((linea = html.readLine()) != null)
		pagina += linea;
		
		// Nos creamos un repositorio de pacientes (rellenamos el mensaje) --> Necesario para obtener los métodos de la clase
		// PacienteRespository
		PacienteRepository repositorio = (PacienteRepository) getServletContext().getAttribute("repo");
		
		if(repositorio.findByDni(dni) != null) {
			pagina = pagina.replace("<h2></h2>", "<h2>El paciente con el dni " + dni + " ya existe</h2>");
		} else {
			Paciente p = new Paciente(nombre,apellidos,dni);
			repositorio.addPaciente(p);
			pagina = pagina.replace("<h2></h2>", "<h2 style='color:green;'>El paciente con el DNI " + dni + " se ha añadido correctamente.</h2>");
		}
		
		// Devolvemos al cliente el contenido de la nueva página (enviamos la respuesta)
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(pagina);
		out.close();
		
		
	}
}
