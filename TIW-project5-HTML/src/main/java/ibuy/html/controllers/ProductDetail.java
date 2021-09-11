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

import ibuy.html.beans.PriceRange;
import ibuy.html.beans.Product;
import ibuy.html.beans.Supplier;
import ibuy.html.beans.User;
import ibuy.html.dao.ProductDAO;
import ibuy.html.dao.SupplierDAO;
import ibuy.html.utilities.ConnectionHandler;

/**
 * Servlet implementation class ProductDetail
 */
@WebServlet("/ProductDetail")
public class ProductDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProductDetail() {
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
		templateResolver.setCharacterEncoding("UTF-8");
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Dichiaro variabili e prendo la sessione utente
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		List<Supplier> suppliers = new ArrayList<Supplier>();
		List<Product> prods_list = new ArrayList<Product>();
		List<PriceRange> range = new ArrayList<PriceRange>();
		ProductDAO products = new ProductDAO(connection);
		SupplierDAO supp = new SupplierDAO(connection);
		String product = null;
		String keyword = null;
		Product prod = new Product();
		String category = null;
		try { //Cerco dettagli prodotto 
			product = StringEscapeUtils.escapeJava(request.getParameter("code"));
			keyword = StringEscapeUtils.escapeJava(request.getParameter("keyword"));
			category = StringEscapeUtils.escapeJava(request.getParameter("category"));
			prod = products.findProductDetails(product); 					
			suppliers = supp.findSupplierDetails(product);
			int[] supid = supp.findSupplierIds(product);
			range = supp.findShippingRanges(supid);
			if (category == "Initial" || category == "") {
				//Non ho filtrato per categoria, ma solo per parola chiave
			prods_list = products.findProductsByKey(keyword);
			} else 
				//Ho filtrato per categoria e parola chiave
				prods_list = products.findProductsByCategory(category, keyword);
			if (keyword == null || prods_list == null || prod == null || suppliers == null) {
					String path;
					path = "errorPage.html";
					response.sendRedirect(path);
				} else {	
							products.UpdateProductSeen(user.getId(),product);
							String path = "/WEB-INF/Results.html";
							ServletContext servletContext = getServletContext();
							final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
							ctx.setVariable("products", prods_list);
							ctx.setVariable("prod_details", prod);
							ctx.setVariable("suppl_details", suppliers);
							ctx.setVariable("keyword", keyword);
							ctx.setVariable("ranges", range);
							ctx.setVariable("category", category);
							templateEngine.process(path, ctx, response.getWriter());
							}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//	e.printStackTrace();
			String path;
			path = "errorPage.html";
			response.sendRedirect(path);
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
