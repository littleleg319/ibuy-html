package ibuy.html.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import ibuy.html.beans.Product;
import ibuy.html.beans.User;
import ibuy.html.utilities.ConnectionHandler;
import ibuy.html.dao.ProductDAO;
import ibuy.html.dao.UserDAO;
/**
 * Servlet implementation class GotoHome
 */
@WebServlet("/GoToHome")
public class GotoHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private TemplateEngine templateEngine;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GotoHome() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    //inizializzazione connessione e context
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//Check Login fatto con filtro
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		List<Product> recent_prod = new ArrayList<Product>();
		ProductDAO products = new ProductDAO(connection);
		try {
			int myprods = products.findmissingProd(user.getId());
			if (myprods == 0) {
				recent_prod = products.findProductsByDefaultCat(myprods);
				// Redirect to the Home page and add missions to the parameters
				String path = "/WEB-INF/Home.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("products", recent_prod);
				templateEngine.process(path, ctx, response.getWriter());
			}
			else {
				recent_prod = products.findLastProductByUser(user.getId());
				List<Product> default_prod = new ArrayList<Product>();
				default_prod = products.findProductsByDefaultCat(myprods);
				String path = "/WEB-INF/Home.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("products", recent_prod);
				ctx.setVariable("default_prods",default_prod);
				templateEngine.process(path, ctx, response.getWriter());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover your recent seen products");
			return;
		}
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
