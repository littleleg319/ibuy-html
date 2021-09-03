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

import ibuy.html.beans.Cart;
import ibuy.html.beans.CartItem;
import ibuy.html.beans.User;
import ibuy.html.dao.OrderDAO;
import ibuy.html.utilities.ConnectionHandler;

/**
 * Servlet implementation class CreateOrder
 */
@WebServlet("/CreateOrder")
public class CreateOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateOrder() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    //inizializzazione context e connessione
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
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Inizializzazione variabili
		Integer supplierid = null;
		List<Cart> cart_global = new ArrayList<Cart>();
		List<CartItem> items = new ArrayList<CartItem>();
		Cart order = new Cart();
		List<CartItem> myitems = new ArrayList<CartItem>();
		OrderDAO createOrder = new OrderDAO(connection);
		
		//Prendo la sessione
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession s = req.getSession();
		User user = (User) s.getAttribute("user");
		
		//Retrieve dati
		supplierid = Integer.parseInt(request.getParameter("supplierid"));
		cart_global = (List<Cart>) s.getAttribute("cart");
		items = (List<CartItem>) s.getAttribute("items");
		
		//controllo che non siano vuoti
		if (s.getAttribute("cart") == null || s.getAttribute("items") == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ops....Something went wrong");
			return;
		}
		
		//cerco il carrello di cui devo fare l'ordine 
		for (Cart c : cart_global) {
			if (c.getSupplierId() == supplierid)
				order = c;
				}
		//controllo che il mio carrello ci sia
		if (order == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ops....Something went wrong");
			return;
		} else {
			//cerco gli items
				for (CartItem i : items) {
					if(i.getSupplierId() == supplierid)
						myitems.add(i);
				}
				if (myitems == null) {
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ops....Something went wrong");
					return;
				} else {
					Integer esito = createOrder.CreateOrder(user.getId(),myitems, order);
					if (esito == 0) { //tutto ok --> rimuovo il carrello e gli items
						int  i=0;
						int j=0;
						for (i=0; i<cart_global.size();i++) {
							if (cart_global.get(i).getSupplierId() == supplierid)
								cart_global.remove(i);
						}
						for (j=0; j<items.size();j++) {
							if (items.get(j).getSupplierId() == supplierid)
								items.remove(j);
						}
						if (cart_global.isEmpty()) {//non ho più carrelli
							request.getSession().removeAttribute("cart");
							request.getSession().removeAttribute("items");
							} else {
								request.getSession().setAttribute("cart", cart_global);
								request.getSession().setAttribute("items", items);
							}
					} else {
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ops....Something went wrong");
						return;
					}
				}
			
		} 
		String path = "/WEB-INF/Home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("MessageOk", "Thank you! Your order has been successfully received! ");
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
