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
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import ibuy.html.beans.Cart;
import ibuy.html.beans.CartItem;
import ibuy.html.beans.PriceRange;
import ibuy.html.dao.ProductDAO;
import ibuy.html.dao.SupplierDAO;
import ibuy.html.utilities.ConnectionHandler;

/**
 * Servlet implementation class AddToCart
 */
@WebServlet("/AddToCart")
public class AddToCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddToCart() {
        super();
        // TODO Auto-generated constructor stub
    }
    //inizializzazione context
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
		// TODO Auto-generated method stub
		//doGet(request, response);
		//parametri del bean Cart
		Integer suppid = null;
		String prodid = null;
		Integer qta = null;
		Float price = null;
		Float totalPrice = null;
		Float fee = null;
		Float freeship = null;
		
		//retrieve dalla richiesta dei parametri
		suppid=	Integer.parseInt(request.getParameter("supplierid"));
		price=Float.parseFloat(request.getParameter("price"));
		prodid=StringEscapeUtils.escapeJava(request.getParameter("productid"));
		freeship=Float.parseFloat(request.getParameter("freeship"));
		try {
			qta=Integer.parseInt(request.getParameter("qta"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect qta values");
			return;
		}
		
		//Prendo la sessione
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession s = req.getSession();
		List<Cart> cart_items = new ArrayList<Cart>();
		SupplierDAO range = new SupplierDAO(connection);
		
		if(s.getAttribute("cart") == null) { //non ho oggetti nel carrello
			totalPrice=qta*price;
			try {
				if(totalPrice < freeship)
				fee=range.CalculateShippingCost(qta, suppid);
				else 
					fee = freeship;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			List<CartItem> items = new ArrayList<CartItem>();
			CartItem item = new CartItem();
			item.setProductId(prodid);
			item.setQta(qta);
			item.setPrice(price);
			items.add(item);

			Cart cart = new Cart();
			cart.setSupplierId(suppid);
			cart.setFee(fee);
			cart.setTotalCost(totalPrice);		
			cart.setItem(items);
			cart_items.add(cart);
			String path = "/WEB-INF/cart.html";
			request.getSession().setAttribute("cart", cart_items);
			response.sendRedirect(path);
		} else {
			cart_items= (List<Cart>) s.getAttribute("cart");
			int articles = 0;
			for (Cart c : cart_items) {
				if (c.getSupplierId() == suppid) {
					//ho già lo stesso item di quel fornitore nel carrello
					for (CartItem sp : c.getItem()) {
						if (sp.getProductId() == prodid) {
					// Ho già lo stesso elemento nel carrello. Devo aggiornare qta totalcost e fee
							sp.setQta(0); //aggiorno qta
							}
						totalPrice = totalPrice + sp.getQta() * sp.getPrice();
						articles = articles + sp.getQta();
						if (totalPrice < freeship)
							try {
								fee=range.CalculateShippingCost(articles, suppid);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						else 
							fee=freeship;
						}
						c.setFee(fee);
						c.setTotalCost(totalPrice);
					}
				}
			String path = "/WEB-INF/cart.html";
			request.getSession().setAttribute("cart", cart_items);
			response.sendRedirect(path);
			}
		}
		
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
