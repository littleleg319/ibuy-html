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

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import ibuy.html.beans.Product;
import ibuy.html.beans.Supplier;
import ibuy.html.beans.User;
import ibuy.html.dao.ProductDAO;
import ibuy.html.dao.SupplierDAO;
import ibuy.html.utilities.ConnectionHandler;

/**
 * Servlet implementation class ShowResults
 */
@WebServlet("/ShowResults")
public class ShowResults extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowResults() {
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
		//		response.getWriter().append("Served at: ").append(request.getContextPath());
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		List<Product> prods_list = new ArrayList<Product>();
		ProductDAO products = new ProductDAO(connection);
		String keyword = null;
		String code = null;
		code = StringEscapeUtils.escapeJava(request.getParameter("code"));
			try { 
				//Controllo che mi abbiano dato la chiave di ricerca e sono nella prima pagina
				keyword = StringEscapeUtils.escapeJava(request.getParameter("keyword"));
				if (keyword == null & code == null) {
					String path;
					request.getSession().setAttribute("user", user);
					path = getServletContext().getContextPath() + "/GoToHome";
					response.sendRedirect(path);
				} else if (code == null) { 
				//sono nell'overview dei risultati
					prods_list = products.findProductsByKey(keyword); 
					if (prods_list == null) {
						String path;
						request.getSession().setAttribute("user", user);
						path = getServletContext().getContextPath() + "/GoToHome";
						response.sendRedirect(path);
					} else {
					String path = "/WEB-INF/Results.html";
					request.getSession().setAttribute("keyword", keyword);
					ServletContext servletContext = getServletContext();
					final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
					ctx.setVariable("products", prods_list);
					ctx.setVariable("keyword", keyword);
					templateEngine.process(path, ctx, response.getWriter());
					}
				} 
/*				else { //ho scelto un prodotto
				Product prod = new Product();
				List<Supplier> suppliers = new ArrayList<Supplier>();
				SupplierDAO supp = new SupplierDAO(connection);
				prod = products.findProductDetails(code); 
				if (prod == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ops....Something went wrong");
					return;
				} else {					
					suppliers = supp.findSupplierDetails(code);
						if (suppliers == null) {
							response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ops....Something went wrong");
							return;
						} else {
							prods_list = products.findProductsByKey(keyword); 
							if (prods_list == null) {
								response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ops....Something went wrong");
								return;
							} else {
								String path = "/WEB-INF/Results.html";
								ServletContext servletContext = getServletContext();
								final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
								ctx.setVariable("products", prods_list);
								ctx.setVariable("prod_details", prod);
								ctx.setVariable("suppl_details", suppliers);
								templateEngine.process(path, ctx, response.getWriter());
							}
						}			
				}
			}*/
	}catch (SQLException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
			//	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ops....Something went wrong");
			// return;
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
