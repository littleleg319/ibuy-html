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
		templateResolver.setCharacterEncoding("UTF-8");
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Diachiarazione variabili
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		List<Product> recent_prod = new ArrayList<Product>();
		List<String> categories = new ArrayList<String>();
		ProductDAO products = new ProductDAO(connection);
		String ok_msg = request.getParameter("order_ok");
		
		try {
			int myprods = products.findmissingProd(user.getId());
			if (myprods < 5 ) { 
				//ho visto meno di 5 oggetti --> prendo quelli che ho visto
				recent_prod = products.findLastProductByUser(user.getId());
				List<Product> default_prod = new ArrayList<Product>();
				//il delta lo cerco nella Default Category tra i prodotti in offerta
				default_prod = products.findProductsByDefaultCat(myprods,user.getId());
				//listo le categorie prodotti presenti
				categories = products.findCategories();
				// Caso in cui vado alla Home dopo un ordine effettuato
				if (!(ok_msg == null )) {
					if (ok_msg.equals("true")) {
				String path = "/WEB-INF/Home.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("products", recent_prod);
				ctx.setVariable("default_prods",default_prod);
				ctx.setVariable("categories", categories);
				ctx.setVariable("MessageOk", "Thank you! Your order has been successfully received! ");
				templateEngine.process(path, ctx, response.getWriter());
				} 
				} else { 
					//Vado alla HOME negli altri casi
					String path = "/WEB-INF/Home.html";
					ServletContext servletContext = getServletContext();
					final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
					ctx.setVariable("products", recent_prod);
					ctx.setVariable("default_prods",default_prod);
					ctx.setVariable("categories", categories);
					templateEngine.process(path, ctx, response.getWriter());
				}
			}
			else { //ho visto 5 oggetti
				recent_prod = products.findLastProductByUser(user.getId());
				categories = products.findCategories();
				// Caso in cui vado alla Home dopo un ordine effettuato
				if (!(ok_msg == null )) {
					if (ok_msg.equals("true")) {
				String path = "/WEB-INF/Home.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("products", recent_prod);
				ctx.setVariable("categories", categories);
				ctx.setVariable("MessageOk", "Thank you! Your order has been successfully received! ");
				templateEngine.process(path, ctx, response.getWriter());
				} } else { 
					//Vado alla HOME negli altri casi
					String path = "/WEB-INF/Home.html";
					ServletContext servletContext = getServletContext();
					final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
					ctx.setVariable("products", recent_prod);
					ctx.setVariable("categories", categories);
					templateEngine.process(path, ctx, response.getWriter());
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//	e.printStackTrace();
			String path;
			path = "errorPage.html";
			response.sendRedirect(path);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
