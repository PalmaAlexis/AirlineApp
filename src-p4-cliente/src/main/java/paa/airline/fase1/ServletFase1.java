package paa.airline.fase1;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/ServletFase1")
public class ServletFase1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ServletFase1() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Servidor:").append(request.getContextPath());
	}

}
