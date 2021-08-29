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

import ibuy.html.beans.Cart;
import ibuy.html.beans.CartItem;
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
		String suppl_name = null;
		Integer qta = null;
		Float price = (float) 0.0;
		Float totalPrice = (float) 0.0;
		Float fee = (float) 0.0;
		Float freeship = (float) 0.0;
		List<CartItem> items = new ArrayList<CartItem>();
		Integer articles = 0;
		
		//retrieve dalla richiesta dei parametri
		suppid=	Integer.parseInt(request.getParameter("supplierid"));
		price=Float.parseFloat(request.getParameter("price"));
		prodid=StringEscapeUtils.escapeJava(request.getParameter("productid"));
		freeship=Float.parseFloat(request.getParameter("freeship"));
		suppl_name=StringEscapeUtils.escapeJava(request.getParameter("suppl_name"));
		try {
			qta=Integer.parseInt(request.getParameter("qta"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect qta values");
			return;
		}
		
		//cerco il nome dell'elemento
		ProductDAO prodotto = new ProductDAO(connection);
		String prod_name = null;
		try {
			prod_name = prodotto.findProductDetails(prodid).getName();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
			

			CartItem item = new CartItem();
			item.setProductId(prodid);
			item.setQta(qta);
			item.setPrice(price);
			item.setName(prod_name);
			item.setSupplierId(suppid);
			items.add(item);

			Cart cart = new Cart();
			cart.setSupplierId(suppid);
			cart.setFee(fee);
			cart.setTotalCost(totalPrice);		
			cart.setItem(items);
			cart.setName(suppl_name);
			cart_items.add(cart);
		} else {
			cart_items= (List<Cart>) s.getAttribute("cart");
			items = (List<CartItem>) s.getAttribute("items");
			boolean incart = false;
			boolean initem = false;
			Cart delta = new Cart();
			
			for (Cart c : cart_items) {
				if (c.getSupplierId() == suppid) {
					for (CartItem n : c.getItem()) {
							if (n.getProductId().equals(prodid)) {
								initem = true;
							}
						}
						incart = true;
					}
				}
			//ho già l'articolo nel carrello di quel fornitore nel carrello
			if (initem && incart) {
						for (CartItem cm : items) {
							if(cm.getSupplierId()==suppid) {
								if (cm.getProductId().equals(prodid)) {
									cm.setQta(qta);
							}
					} 
				}
			} else if (incart && !initem) { //ho articoli del fornitore, ma non questo articolo
					for (Cart ci: cart_items) {
						if (ci.getSupplierId()==suppid) {
							CartItem add = new CartItem();
							add.setName(prod_name);
							add.setPrice(price);
							add.setProductId(prodid);
							add.setQta(qta);
							add.setSupplierId(suppid);
							ci.getItem().add(add);
							items.add(add);
							}	
						}
					} else {
				CartItem item1 = new CartItem();
				item1.setProductId(prodid);
				item1.setQta(qta);
				item1.setPrice(price);
				item1.setName(prod_name);
				item1.setSupplierId(suppid);
				items.add(item1);

				Cart cart1 = new Cart();
				cart1.setSupplierId(suppid);
				cart1.setFee(fee);
				cart1.setTotalCost(totalPrice);		
				cart1.setItem(items);
				cart1.setName(suppl_name);
				cart_items.add(cart1);
			}
			
			for (Cart c2 : cart_items) {
				for (CartItem c3 : c2.getItem()) {
					articles=articles+c3.getQta();
					totalPrice= totalPrice + (c3.getQta()*c3.getPrice());
				}
				if (totalPrice < freeship) {
					try {
						fee=range.CalculateShippingCost(articles, suppid);
						c2.setFee(fee);
						c2.setTotalCost(totalPrice);
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			} else {
				fee = freeship;
				c2.setFee(fee);
				c2.setTotalCost(totalPrice);
			}
		}
	}
		String path = "/WEB-INF/cart.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		request.getSession().setAttribute("cart", cart_items);
		request.getSession().setAttribute("items", items);
	//	response.sendRedirect(path);
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
