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
import ibuy.html.beans.User;
import ibuy.html.dao.ProductDAO;
import ibuy.html.dao.SupplierDAO;
import ibuy.html.utilities.ConnectionHandler;

/**
 * Servlet implementation class ShoppingCart
 */
@WebServlet("/ShoppingCart")
public class ShoppingCart extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection = null;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShoppingCart() {
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
			//Dichiarazioni variabili
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			HttpSession session = req.getSession();
			//carrello vuoto
			if (session.getAttribute("cart") == null || session.getAttribute("items") == null) {
				String path = "/WEB-INF/cart.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				ctx.setVariable("MessageKo", "Your shopping cart is empty....Hurry up to fill it up!");
				templateEngine.process(path, ctx, response.getWriter());
				return;
			} else {
				String path = "/WEB-INF/cart.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		//Dichiarazione Parametri
		Integer suppid = null;
		String prodid = null;
		String suppl_name = null;
		Integer qta = null;
		Float price = (float) 0.00;
		Float totalPrice = (float) 0.00;
		Float fee = (float) 0.00;
		Float freeship = (float) 0.00;
		List<CartItem> items = new ArrayList<CartItem>();
		List<Cart> cart_items = new ArrayList<Cart>();
		SupplierDAO range = new SupplierDAO(connection);
		Integer articles = 0;
		ProductDAO prodotto = new ProductDAO(connection);
		String prod_name = null;
		String keyword = null;
		
		//retrieve dalla richiesta dei parametri
		suppid=	Integer.parseInt(request.getParameter("supplierid"));
		price=Float.parseFloat(request.getParameter("price"));
		prodid=StringEscapeUtils.escapeJava(request.getParameter("productid"));
		freeship=Float.parseFloat(request.getParameter("freeship"));
		suppl_name=StringEscapeUtils.escapeJava(request.getParameter("suppl_name"));
		keyword=StringEscapeUtils.escapeJava(request.getParameter("keyword"));
		try {
			qta=Integer.parseInt(request.getParameter("qta"));
			if (qta == null || qta <= 0) {
				String path;
				path = "errorPage.html";
				response.sendRedirect(path);
		} 
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ops....Something went wrong");
			return;
		}
		
		
		//cerco il nome dell'elemento
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

		
		//non ho oggetti nel carrello
		if(s.getAttribute("cart") == null || s.getAttribute("items") == null) { 
			totalPrice=qta*price;
			try {
				if(totalPrice < freeship)
				fee=range.CalculateShippingCost(qta, suppid);
				else 
					fee = (float) 0;
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
			cart.setName(suppl_name);
			cart.setTotalQta(qta);
			cart.setFreeShip(freeship);
			cart_items.add(cart);
		} else { 
			// un carrello già esiste
			cart_items= (List<Cart>) s.getAttribute("cart");
			items = (List<CartItem>) s.getAttribute("items");
			boolean incart = false;
			boolean initem = false;
			
			// verifico se ho un carrello per lo stesso fornitore e prodotto o no
			for (Cart c : cart_items) {
				if (c.getSupplierId() == suppid) {
					for (CartItem n : items) {
							if ((n.getProductId().equals(prodid))&&(n.getSupplierId()==c.getSupplierId())) {
								initem = true;
							}
						}
						incart = true;
					}
				}
			//ho già l'articolo nel carrello di quel fornitore nel carrello
			//quindi aggiorno solo la Quantità
			if (initem && incart) {
						for (CartItem cm : items) {
							if(cm.getSupplierId()==suppid) {
								if (cm.getProductId().equals(prodid)) {
									cm.setQta(cm.getQta()+qta);
							}
					} 
				}
			}	
			//ho articoli del fornitore, ma non questo articolo 
			else if (incart && !initem) { 
					for (Cart ci: cart_items) { 
						if (ci.getSupplierId()==suppid) {
							//cerco il carrello del fornitore e aggiungo l'item 
							//di questo articolo
							CartItem add = new CartItem();
							add.setName(prod_name);
							add.setPrice(price);
							add.setProductId(prodid);
							add.setQta(qta);
							add.setSupplierId(suppid);
							items.add(add);
							ci.setTotalQta(ci.getTotalQta()+qta);
							}	
						}
					} 
			//ho un carrello, ma non per questo fornitore
			else if(!incart && !initem) {
				//creo l'item per l'oggetto e lo aggiungo agli items nel mio 
				//carrello complessivo
				CartItem item1 = new CartItem();
				item1.setProductId(prodid);
				item1.setQta(qta);
				item1.setPrice(price);
				item1.setName(prod_name);
				item1.setSupplierId(suppid);
				items.add(item1);
				//creo il carrello per questo fornitore e lo aggiungo al carrello
				//complessivo
				Cart cart1 = new Cart();
				cart1.setSupplierId(suppid);
				cart1.setFee(fee);
				cart1.setTotalCost(totalPrice);		
				cart1.setName(suppl_name);
				cart1.setTotalQta(qta);
				cart1.setFreeShip(freeship);
				cart_items.add(cart1);
			}
			//aggiornati i dati nei carrelli, aggiorno/calcolo i prezzi totali e le spese di spedizione
			for (Cart c2 : cart_items) {
				articles = 0;
				totalPrice = (float) 0;
				for (CartItem c3 : items) {
					if (c2.getSupplierId()==c3.getSupplierId()) {
					articles=articles+c3.getQta();
					totalPrice= totalPrice + (c3.getQta()*c3.getPrice());
					c2.setTotalCost(totalPrice);
					c2.setTotalQta(articles);
							if (totalPrice < c2.getFreeShip()) {
										try {
												fee=range.CalculateShippingCost(articles, suppid);
												c2.setFee(fee);
												} catch (SQLException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
														}
							} else {
								fee = (float) 0;
								c2.setFee(fee);
								}
						}
					}
			}
		}
		String path = "/WEB-INF/cart.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		request.getSession().setAttribute("cart", cart_items);
		request.getSession().setAttribute("items", items);
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
